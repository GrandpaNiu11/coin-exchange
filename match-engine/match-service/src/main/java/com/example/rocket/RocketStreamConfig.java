package com.example.rocket;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBinding({sink.class, Source.class})
public class RocketStreamConfig {
}
