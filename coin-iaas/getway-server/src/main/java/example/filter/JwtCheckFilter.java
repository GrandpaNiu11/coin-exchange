package example.filter;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.net.URI;
import java.util.List;
import java.util.Set;

@Component
public class JwtCheckFilter implements GlobalFilter, Ordered {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Value("${no.require.urls:/admin/login,/user/gt/register,/user/login,/user/users/register}")
    private Set<String>  noRequireTokenUrls;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        System.out.println("JwtCheckFilter");
        if (!isReuireToken(exchange)) {
            return chain.filter(exchange);
        }
        String token = getUserToken(exchange);
        if (token == null){
            return buildNoAuthoriztionResult(exchange);
        }
        String bearer = token.replace("bearer ", "");
        Boolean haskey = redisTemplate.hasKey(bearer);
        System.out.println("haskey = " + bearer);
        if ( haskey){
            return chain.filter(exchange);
        }

        return buildNoAuthoriztionResult(exchange);
    }

    private Mono<Void> buildNoAuthoriztionResult(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json");
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("error", "NoAuthorization");
        jsonObject.put("message", "token is no error");
        return response.writeWith(Mono.just(response.bufferFactory().wrap(jsonObject.toJSONString().getBytes())));
    }

    private String getUserToken(ServerWebExchange exchange) {
        String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        return  token == null ? null : token.replace("Bearer ", "");


    }

    private boolean isReuireToken(ServerWebExchange exchange) {
        URI uri = exchange.getRequest().getURI();
        if (noRequireTokenUrls.contains(uri.getPath())){
            return false;
        }
        return true;
    }

    @Override
    public int getOrder() {


        return 0;
    }
}
