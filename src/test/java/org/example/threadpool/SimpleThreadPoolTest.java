package org.example.threadpool;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class SimpleThreadPoolTest {

    @Test
    @DisplayName("Базово выполняет все задачи")
    @Timeout(5)
    void executesAllTasks() {
        SimpleThreadPool pool = new SimpleThreadPool(3);
        AtomicInteger counter = new AtomicInteger();

        for (int i = 0; i < 100; i++) {
            pool.execute(counter::incrementAndGet);
        }

        pool.shutdown();
        pool.awaitTermination();
        assertEquals(100, counter.get());
        assertTrue(pool.isShutdown());
        assertTrue(pool.isTerminated());
        assertEquals(0, pool.getQueueSize());
    }

    @Test
    @DisplayName("Задачи, поставленные до shutdown(), выполняются до конца")
    @Timeout(5)
    void tasksQueuedBeforeShutdownAreExecuted() {
        SimpleThreadPool pool = new SimpleThreadPool(2);
        AtomicInteger counter = new AtomicInteger();

        for (int i = 0; i < 20; i++) {
            pool.execute(() -> {
                try { Thread.sleep(10); } catch (InterruptedException ignored) {}
                counter.incrementAndGet();
            });
        }
        pool.shutdown();
        pool.awaitTermination();

        assertEquals(20, counter.get());
        assertTrue(pool.isShutdown());
        assertTrue(pool.isTerminated());
    }

    @Test
    @DisplayName("После shutdown() новые задачи отклоняются")
    @Timeout(2)
    void rejectAfterShutdown() {
        SimpleThreadPool pool = new SimpleThreadPool(1);
        pool.shutdown();
        assertThrows(IllegalStateException.class, () -> pool.execute(() -> {}));
        pool.awaitTermination();
    }

    @Test
    @DisplayName("Исключение в задаче не убивает worker — следующий таск выполняется")
    @Timeout(5)
    void workerSurvivesTaskException() {
        SimpleThreadPool pool = new SimpleThreadPool(1);
        AtomicInteger ok = new AtomicInteger();

        pool.execute(() -> { throw new RuntimeException("boom"); });
        pool.execute(ok::incrementAndGet);

        pool.shutdown();
        pool.awaitTermination();
        assertEquals(1, ok.get());
    }

    @Test
    @DisplayName("Параллелизм: две долгие задачи на двух потоках быстрее последовательного исполнения")
    @Timeout(5)
    void runsInParallelWhenCapacityAllows() {
        SimpleThreadPool pool = new SimpleThreadPool(2);
        CountDownLatch started = new CountDownLatch(2);

        Runnable longTask = () -> {
            started.countDown();
            try { started.await(1, TimeUnit.SECONDS); } catch (InterruptedException ignored) {}
            try { Thread.sleep(300); } catch (InterruptedException ignored) {}
        };

        long t0 = System.nanoTime();
        pool.execute(longTask);
        pool.execute(longTask);
        pool.shutdown();
        pool.awaitTermination();
        long elapsedMs = (System.nanoTime() - t0) / 1_000_000;

        assertTrue(elapsedMs < 550, "Ожидали ~300–450 мс, получили " + elapsedMs + " мс");
    }


    @Test
    @DisplayName("Стресс: 10_000 коротких задач на 8 потоков завершаются корректно")
    @Timeout(15)
    void stressManyShortTasks() {
        int workers = 8;
        int tasks = 10_000;

        SimpleThreadPool pool = new SimpleThreadPool(workers);
        CountDownLatch done = new CountDownLatch(tasks);

        for (int i = 0; i < tasks; i++) {
            pool.execute(done::countDown);
        }

        pool.shutdown();
        pool.awaitTermination();

        assertEquals(0, done.getCount(), "Все задачи должны были выполниться");
        assertTrue(pool.isTerminated());
        assertEquals(0, pool.getQueueSize());
    }

    @Test
    @DisplayName("Стресс: смешанные задачи (долгие и короткие) не зависают")
    @Timeout(20)
    void stressMixedTasks() {
        int workers = 4;
        int longTasks = 100;
        int shortTasks = 3000;

        SimpleThreadPool pool = new SimpleThreadPool(workers);
        CountDownLatch done = new CountDownLatch(longTasks + shortTasks);

        // долгие
        for (int i = 0; i < longTasks; i++) {
            pool.execute(() -> {
                try { Thread.sleep(5); } catch (InterruptedException ignored) {}
                done.countDown();
            });
        }
        for (int i = 0; i < shortTasks; i++) {
            pool.execute(done::countDown);
        }

        pool.shutdown();
        pool.awaitTermination();

        assertEquals(0, done.getCount());
        assertTrue(pool.isShutdown());
        assertTrue(pool.isTerminated());
    }


    @Test
    void invalidCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new SimpleThreadPool(0));
        assertThrows(IllegalArgumentException.class, () -> new SimpleThreadPool(-1));
    }

    @Test
    void nullTaskRejected() {
        SimpleThreadPool pool = new SimpleThreadPool(1);
        assertThrows(NullPointerException.class, () -> pool.execute(null));
        pool.shutdown();
        pool.awaitTermination();
    }
}
