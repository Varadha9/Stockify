package com.example.v2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class AppExecutor {

    private static final int CORE_THREADS = 4;

    private static final ExecutorService INSTANCE = new ThreadPoolExecutor(
            CORE_THREADS,
            CORE_THREADS,
            30L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(256),
            r -> {
                Thread t = new Thread(r, "AppExecutor");
                t.setDaemon(true);
                return t;
            },
            // Discard oldest task rather than running on caller (main) thread
            new ThreadPoolExecutor.DiscardOldestPolicy()
    );

    private AppExecutor() {}

    public static ExecutorService get() {
        return INSTANCE;
    }
}
