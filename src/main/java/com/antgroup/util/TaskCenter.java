package com.antgroup.util;

import java.util.concurrent.*;

public class TaskCenter {

    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(1,
            2, 6, TimeUnit.HOURS, new LinkedBlockingQueue<>());

    public static <T> Future<T> addTask(Callable<T> task) {
        return EXECUTOR.submit(task);
    }
    
}
