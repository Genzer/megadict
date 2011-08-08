package com.megadict.test.toolbox;

import java.util.*;
import java.util.concurrent.*;

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
        List<T> finalResults = new ArrayList<T>(tasks.size());
        List<Future<T>> temporaryResults = new ArrayList<Future<T>>(tasks.size());

        try {
            for (Callable<T> task : tasks) {
                Future<T> future = executor.submit(task);
                temporaryResults.add(future);
            }            
            executor.awaitTermination(waitingTimeInMillis, TimeUnit.MILLISECONDS);
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
