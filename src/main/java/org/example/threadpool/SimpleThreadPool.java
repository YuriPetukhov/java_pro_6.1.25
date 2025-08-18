package org.example.threadpool;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Простой фиксированный пул потоков с очередью на LinkedList.
 * Поддерживает shutdown и ожидание завершения без таймаута.
 */
public class SimpleThreadPool {

    /** Очередь задач (FIFO). Не гарантирует "справедливости" при большом числе воркеров. */
    private final LinkedList<Runnable> queue = new LinkedList<>();
    /** Рабочие потоки. */
    private final List<Worker> workers = new ArrayList<>();
    /** Флаг запрета приёма новых задач. */
    private volatile boolean shutdown = false;
    /** Лэтч для ожидания завершения всех рабочих потоков. */
    private final CountDownLatch terminated;

    /**
     * @param capacity число рабочих потоков (>0)
     */
    public SimpleThreadPool(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("capacity must be > 0");
        this.terminated = new CountDownLatch(capacity);
        for (int i = 0; i < capacity; i++) {
            Worker w = new Worker("pool-worker-" + i);
            workers.add(w);
            w.start();
        }
    }

    /**
     * Поставить задачу в очередь на выполнение.
     * @throws NullPointerException если task == null
     * @throws IllegalStateException если пул уже завершён (shutdown)
     */
    public void execute(Runnable task) {
        if (task == null) throw new NullPointerException("task");
        synchronized (queue) {
            if (shutdown) throw new IllegalStateException("ThreadPool is shut down");
            queue.addLast(task);
            queue.notify();
        }
    }

    /**
     * Инициировать завершение: новые задачи не принимаются.
     * Уже поставленные задачи будут выполнены.
     */
    public void shutdown() {
        synchronized (queue) {
            shutdown = true;
            queue.notifyAll();
        }
    }

    /**
     * Дождаться завершения всех рабочих потоков (без таймаута).
     * Потоки завершатся, когда очередь опустеет и пул закрыт (shutdown).
     */
    public void awaitTermination() {
        try {
            terminated.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /** Текущий размер очереди (для тестов/метрик). */
    public int getQueueSize() {
        synchronized (queue) {
            return queue.size();
        }
    }

    /** Возвращает true после вызова {@link #shutdown()}. */
    public boolean isShutdown() {
        return shutdown;
    }

    /** Возвращает true, когда все worker-потоки завершились. */
    public boolean isTerminated() {
        return terminated.getCount() == 0;
    }

    /**
     * Рабочий поток: берёт задачи из очереди; завершает работу,
     * когда пул закрыт и очередь пуста.
     */
    private final class Worker extends Thread {
        Worker(String name) { super(name); }

        @Override
        public void run() {
            try {
                while (true) {
                    Runnable task;
                    synchronized (queue) {
                        while (queue.isEmpty() && !shutdown) {
                            try {
                                queue.wait();
                            } catch (InterruptedException ie) {
                                Thread.currentThread().interrupt();
                                return;
                            }
                        }
                        if (queue.isEmpty() && shutdown) {
                            return;
                        }
                        task = queue.removeFirst();
                    }
                    try {
                        task.run();
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            } finally {
                terminated.countDown();
            }
        }
    }
}
