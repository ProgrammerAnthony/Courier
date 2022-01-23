package com.courier.core.config;

import com.courier.core.service.CourierTaskInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Anthony
 * @create 2022/1/23
 * @desc
 */
@Component
public class ThreadPoolConfig {

    private static final String COURIER_TASK_THREAD_POOL_PREFIX = "CourierThreadPool_";


    private static final String ALERT_THREAD_POOL_PREFIX = "AlertThreadPool_";

    @Autowired
    private CourierConsistencyConfig tendConsistencyConfiguration;


    @Bean
    public CompletionService<CourierTaskInstance> consistencyTaskPool() {
        LinkedBlockingQueue<Runnable> asyncConsistencyTaskThreadPoolQueue =
                new LinkedBlockingQueue<>(tendConsistencyConfiguration.getThreadPoolQueueSize());
        ThreadPoolExecutor asyncReleaseResourceExecutorPool = new ThreadPoolExecutor(
                tendConsistencyConfiguration.getThreadCorePoolSize(),
                tendConsistencyConfiguration.getThreadCorePoolSize(),
                tendConsistencyConfiguration.getThreadPoolKeepAliveTime(),
                TimeUnit.valueOf(tendConsistencyConfiguration.getThreadPoolKeepAliveTimeUnit()),
                asyncConsistencyTaskThreadPoolQueue,
                createThreadFactory(COURIER_TASK_THREAD_POOL_PREFIX)
        );
        return new ExecutorCompletionService<>(asyncReleaseResourceExecutorPool);
    }


    @Bean
    public ThreadPoolExecutor alertNoticePool() {
        LinkedBlockingQueue<Runnable> asyncAlertNoticeThreadPoolQueue =
                new LinkedBlockingQueue<>(100);
        return new ThreadPoolExecutor(
                3,
                5,
                60,
                TimeUnit.SECONDS,
                asyncAlertNoticeThreadPoolQueue,
                createThreadFactory(ALERT_THREAD_POOL_PREFIX)
        );
    }


    private ThreadFactory createThreadFactory(String threadPoolPrefix) {
        return new ThreadFactory() {
            private AtomicInteger threadIndex = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, threadPoolPrefix + this.threadIndex.incrementAndGet());
            }
        };
    }

}
