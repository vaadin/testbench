/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.unit;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.LoggerFactory;

import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.signals.SignalEnvironment;

/**
 * Test-only {@link SignalEnvironment} that records submitted tasks instead of
 * executing them asynchronously. This allows unit tests to deterministically
 * drive and observe Signal processing by explicitly flushing the task queue.
 *
 * <p>
 * How it works:
 * <ul>
 * <li>{@link #getEffectDispatcher()} returns an executor that enqueues tasks
 * into an internal queue. {@link #getResultNotifier()} returns {@code null} so
 * that result notifications fall through to the next environment or run
 * immediately.</li>
 * <li>Tests call {@link #runPendingTasks(long, TimeUnit)} to dequeue and run
 * all pending tasks on the calling thread.</li>
 * <li>If the current thread holds a {@link VaadinSession} lock, the lock is
 * temporarily released so that background threads have a chance of locking the
 * session.</li>
 * </ul>
 *
 * <p>
 * Usage:
 *
 * <pre>
 * {@code
 * TestSignalEnvironment env = TestSignalEnvironment.register();
 * try {
 *     // trigger signals here
 *     env.waitForTasksCompletion(100, TimeUnit.MILLISECONDS);
 * } finally {
 *     env.unregister();
 * }
 * }
 * </pre>
 *
 * <p>
 * For internal use only. May be renamed or removed in a future release.
 */
class TestSignalEnvironment extends SignalEnvironment {

    private final LinkedBlockingQueue<Runnable> tasks = new LinkedBlockingQueue<>();
    private Runnable cleanup;

    private TestSignalEnvironment() {
    }

    /**
     * Registers this test environment as active {@link SignalEnvironment} and
     * returns the created instance.
     *
     * <p>
     * Use together with {@link #unregister()} in a try/finally block to ensure
     * proper cleanup.
     *
     * @return a new registered {@link TestSignalEnvironment} instance
     */
    static TestSignalEnvironment register() {
        TestSignalEnvironment environment = new TestSignalEnvironment();
        environment.cleanup = SignalEnvironment.registerFirst(environment);
        return environment;
    }

    /**
     * Unregisters this test environment if it was registered.
     *
     * <p>
     * This method is idempotent and safe to call multiple times. If a cleanup
     * action was provided by
     * {@link SignalEnvironment#register(SignalEnvironment)}, it will be
     * invoked.
     */
    void unregister() {
        if (cleanup != null) {
            cleanup.run();
            cleanup = null;
        }
    }

    @Override
    protected boolean isActive() {
        // Test environment is always active to catch all signals task submitted
        // when a Vaadin session or service is not available, for example when
        // effects are triggered by background threads.
        return true;
    }

    @Override
    protected Executor getResultNotifier() {
        // Return null so result notifications fall through to the next
        // environment (e.g. VaadinServiceEnvironment) or to the immediate
        // executor. This keeps result processing synchronous on the calling
        // thread, which is important for deterministic test behavior when
        // signal operations are triggered on the test thread.
        return null;
    }

    @Override
    protected Executor getEffectDispatcher() {
        return tasks::offer;
    }

    /**
     * Executes pending tasks from the queue, continuously polling for new tasks
     * until the timeout expires with no new task arriving.
     *
     * <p>
     * If a {@link VaadinSession} lock is held by the current thread, it is
     * temporarily released while polling for tasks, allowing background threads
     * to acquire the lock and enqueue tasks. The lock is reacquired before
     * running each task and released again before the next poll.
     *
     * <p>
     * If the current thread is interrupted while waiting for tasks, the method
     * restores the interrupt status and fails with an {@link AssertionError}.
     *
     * @param maxWaitTime
     *            the maximum time to wait for the next task to arrive in the
     *            given time unit. If &lt;= 0, returns immediately if no tasks
     *            are available.
     * @param unit
     *            the time unit of the timeout value
     * @return {@code true} if any pending Signals tasks were processed.
     */
    boolean runPendingTasks(long maxWaitTime, TimeUnit unit) {
        long deadlineMillis = System.currentTimeMillis()
                + unit.toMillis(maxWaitTime);
        VaadinSession session = VaadinSession.getCurrent();
        boolean hadLock = false;
        if (session != null && session.hasLock()) {
            hadLock = true;
            session.unlock();
        }
        try {
            boolean processedAny = false;
            while (true) {
                long remainingMillis = deadlineMillis
                        - System.currentTimeMillis();
                Runnable task;
                try {
                    task = remainingMillis > 0
                            ? tasks.poll(remainingMillis,
                                    TimeUnit.MILLISECONDS)
                            : tasks.poll();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new AssertionError(
                            "Thread interrupted while waiting for pending Signals tasks");
                }
                if (task == null) {
                    if (!processedAny) {
                        LoggerFactory.getLogger(TestSignalEnvironment.class)
                                .debug("No pending Signals tasks found after waiting for {} {}",
                                        maxWaitTime, unit);
                    }
                    break;
                }
                // Re-acquire the session lock before running the task so
                // that DOM operations (which assert the lock is held) work
                // correctly when the effect runs directly on the test
                // thread instead of going through ui.access().
                if (hadLock) {
                    session.lock();
                }
                try {
                    task.run();
                } finally {
                    if (hadLock) {
                        session.unlock();
                    }
                }
                processedAny = true;
            }
            return processedAny;
        } finally {
            if (hadLock) {
                session.lock();
            }
        }
    }

}
