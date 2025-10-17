package com.example.disruptor;

import com.lmax.disruptor.ExceptionHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DisruptorHandlerException  implements ExceptionHandler {
    @Override
    public void handleEventException(Throwable throwable, long l, Object o) {
     log.info("handleEventException =====>{} ,========>l {},==========>o {}",throwable.getMessage(),l,o);
    }

    @Override
    public void handleOnStartException(Throwable throwable) {
     log.info("handleOnStartException =====>{}",throwable.getMessage());
    }

    @Override
    public void handleOnShutdownException(Throwable throwable) {
        log.info("handleOnShutdownException =====>{}",throwable.getMessage());
    }
}
