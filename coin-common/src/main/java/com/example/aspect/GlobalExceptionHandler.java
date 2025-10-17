package com.example.aspect;

import com.baomidou.mybatisplus.extension.api.IErrorCode;
import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.example.model.R;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {



    @ExceptionHandler(value = ApiException.class)
    public R handle(ApiException e) {
        IErrorCode errorCode = e.getErrorCode();
        if (errorCode != null){
            return R.fail(errorCode.getCode(), errorCode.getMsg());
        }
      return R.fail(e.getMessage());
    }

    /**
     * 方法参数校验失败的异常
     **/
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handlerMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        BindingResult bindingResult = exception.getBindingResult();
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            if (fieldError != null) {
                return R.fail(fieldError.getField() + fieldError.getDefaultMessage());
            }
        }
        return R.fail(exception.getMessage());
    }

    /**
     * 对象内部使用Validate 没有校验成功的异常
     **/
    @ExceptionHandler(value = BindException.class)
    public R handlerBindException(BindException bindException) {
        BindingResult bindingResult = bindException.getBindingResult();
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            if (fieldError != null) {
                return R.fail(fieldError.getField() + fieldError.getDefaultMessage());
            }
        }
        return R.fail(bindException.getMessage());
    }
}
