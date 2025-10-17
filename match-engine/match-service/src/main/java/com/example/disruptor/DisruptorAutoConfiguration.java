package com.example.disruptor;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import net.openhft.affinity.AffinityThreadFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

@Configuration
@EnableConfigurationProperties(value = DisruptorProperties.class)
public class DisruptorAutoConfiguration {

    public DisruptorProperties disruptorProperties;

    public DisruptorAutoConfiguration(DisruptorProperties disruptorProperties) {
        this.disruptorProperties = disruptorProperties;
    }
    @Bean
    public EventFactory<OrderEvent> eventFactory() {
        return new EventFactory<OrderEvent>() {
            @Override
            public OrderEvent newInstance() {
                return new OrderEvent();
            }
        };
    }
   @Bean
   public ThreadFactory threadFactory(){
        return  new AffinityThreadFactory("Match-Handler:");
   }


   @Bean
   public WaitStrategy waitStrategy(){
        return new YieldingWaitStrategy();
  }

    @Bean
    public RingBuffer<OrderEvent> ringBuffer(EventFactory<OrderEvent> eventFactory , ThreadFactory threadFactory ,
                                             WaitStrategy waitStrategy, EventHandler<OrderEvent>[] eventHandlers) {
        Disruptor<OrderEvent> disruptor = null;

        ProducerType producerType = ProducerType.SINGLE;
        if (disruptorProperties.isMultiProducer()){
             producerType = ProducerType.MULTI;
        }


        //eventFactory – the factory to create events in the ring buffer. 事件工厂
        //ringBufferSize – the size of the ring buffer, must be power of 2. 大小
        //threadFactory – a ThreadFactory to create threads for processors. (消费者)我们执行线程如何创建
        //producerType – the claim strategy to use for the ring buffer. 生产者类型
        //waitStrategy – the wait strategy to use for the ring buffer.  等待策略
        disruptor = new Disruptor<OrderEvent>(eventFactory, disruptorProperties.getRingBufferSize(), threadFactory, producerType, waitStrategy);
        //设置消费者
        disruptor.handleEventsWith(eventHandlers);
        //每个消费者代表一个交易对 有多少个交易对 就有多少个events  多个handler 并发执行
        disruptor.setDefaultExceptionHandler(new DisruptorHandlerException());

        disruptor.start();
        //优雅关机
        final Disruptor<OrderEvent> disuptorShutdown =disruptor;
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            disuptorShutdown.shutdown();
        },"disuptorShutdownThread"

        ));



        RingBuffer<OrderEvent> ringBuffer = disruptor.getRingBuffer();
        return ringBuffer;
    }

    @Bean
    public DisruptorTemplate disruptorTemplate(RingBuffer<OrderEvent> ringBuffer){
        return new DisruptorTemplate(ringBuffer) ;
    }
}
