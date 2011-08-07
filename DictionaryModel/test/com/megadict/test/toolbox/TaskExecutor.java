package com.megadict.test.toolbox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class TaskExecutor {

    private TaskExecutor() {
    }

    public static void execute(Collection<? extends Runnable> tasks) {
        execute(tasks, DEFAULT_TIMEOUT_IN_MILLIS);
    }

    public static void execute(Collection<? extends Runnable> tasks, long waitingTimeInMillis) {
        ExecutorService executor = Executors.newFixedThreadPool(tasks.size());

        for (Runnable task : tasks) {
            executor.execute(task);
        }

        try {
            executor.awaitTermination(waitingTimeInMillis, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            executor.shutdown();
        }

    }

    public static <T> List<T> executeAndGetResult(Collection<? extends Callable<T>> tasks) {
        return executeAndGetResult(tasks, DEFAULT_TIMEOUT_IN_MILLIS);
    }

    public static <T> List<T> executeAndGetResult(Collection<? extends Callable<T>> tasks, long waitingTimeInMillis) {
        ExecutorService executor = Executors.newFixedThreadPool(tasks.size());
        List<T> finalResults = Collections.emptyList();
        List<Future<T>> temporaryResults = Collections.emptyList();

        try {
            temporaryResults = executor.invokeAll(tasks, waitingTimeInMillis, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            executor.shutdown();
        }
        
        finalResults = new ArrayList<T>(temporaryResults.size());
        
        for (Future<T> result : temporaryResults) {
            try {
                finalResults.add(result.get());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

        return finalResults;
    }

    private static final int DEFAULT_TIMEOUT_IN_MILLIS = 1000;
}
