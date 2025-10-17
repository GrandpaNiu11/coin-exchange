package com.example.aspect;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSON;
import com.example.model.WebLog;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
@Component
@Aspect
@Order(1)
@Slf4j
public class WebLogAspect {

    /**
     * <h2>
     *  定义切入点:
     *  controller 包里面所有类，类里面的所有方法，都有该切面
     * </h2>
     **/
    @Pointcut("execution(* com.example.controller.*.*(..))")
    public void webLog() {
    }

    /**
     * <h2>记录日志的环绕通知</h2>
     * @param
     **/
    @Around("webLog()")
    public Object recodeWebLog(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object result = null;
        WebLog webLog = new WebLog();
        long start = System.currentTimeMillis();
        //执行方法的真实调用
        result = proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());
        long end = System.currentTimeMillis();
        // 请求该接口花费的时间
        webLog.setSpendTime((int) ((end - start) / 1000));
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        //获取安全上下文
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //*************************************weblog信息***********************************************
        //设置URI
        webLog.setUri(request.getRequestURI());
        //设置URL
        String url = request.getRequestURL().toString();
        webLog.setUrl(url);
        //http://ip:port
        webLog.setBasePath(StrUtil.removeSuffix(url, URLUtil.url(url).getPath()));
        //获取用户的id
        webLog.setUsername(authentication == null ? "anonymous" : authentication.getPrincipal().toString());
        //设置ip地址
        webLog.setIp(request.getRemoteAddr());

        //因为我们使用swagger工具，所有必须在方法上添加@ApiOperation(value="")这个注解
        //方法信息描述
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        //类的全路径
        String className = proceedingJoinPoint.getTarget().getClass().getName();
        //方法
        Method method = signature.getMethod();
        ApiOperation apiOperation = method.getAnnotation(ApiOperation.class);
        webLog.setDescription(apiOperation == null ? "no desc " : apiOperation.value());
        webLog.setMethod(className + "." + method.getName());
        webLog.setParameter(getMethodParameter(method, proceedingJoinPoint.getArgs()));
        webLog.setResult(result);
        //************************************************************************************
        log.info("访问日志: " + JSON.toJSONString(webLog, true));
        return result;
    }

    /**
     * <h2>获取方法的参数</h2>
     * @param method
     * @param args
     **/
    private Object getMethodParameter(Method method, Object[] args) {
        Map<String, Object> methodParametersWithValues = new HashMap<>();
        LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();
        //方法名称
        String[] parameterNames = discoverer.getParameterNames(method);
        for (int i = 0; i < parameterNames.length; i++) {
            if (parameterNames[i].equals("password") || parameterNames[i].equals("file")) {
                continue;
            }
            methodParametersWithValues.put(parameterNames[i], args[i]);
        }
        return methodParametersWithValues;
    }

}
