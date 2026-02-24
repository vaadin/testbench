/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
 * <li>{@link #getResultNotifier()} and {@link #getEffectDispatcher()} return
 * executors that enqueue tasks into an internal queue.</li>
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
        environment.cleanup = SignalEnvironment.register(environment);
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
        return createTaskEnqueueExecutor();
    }

    @Override
    protected Executor getEffectDispatcher() {
        return createTaskEnqueueExecutor();
    }

    private Executor createTaskEnqueueExecutor() {
        return tasks::offer;
    }

    /**
     * Executes all pending tasks from the queue, waiting for the first task to
     * arrive if necessary. Once the first task is processed, all remaining
     * tasks are processed immediately without additional waiting.
     *
     * <p>
     * If a {@link VaadinSession} lock is held by the current thread, it is
     * temporarily released during the wait for the first task, allowing
     * background threads to acquire the lock and enqueue tasks. The lock is
     * automatically reacquired before this method returns.
     *
     * <p>
     * If the current thread is interrupted while waiting for tasks, the method
     * restores the interrupt status and fails with an {@link AssertionError}.
     *
     * @param maxWaitTime
     *            the maximum time to wait for the first task to arrive in the
     *            given time unit. If &lt;= 0, returns immediately if no tasks
     *            are available.
     * @param unit
     *            the time unit of the timeout value
     * @return {@code true} if any pending Signals tasks were processed.
     */
    boolean runPendingTasks(long maxWaitTime, TimeUnit unit) {
        long waitMillis = unit.toMillis(maxWaitTime);
        VaadinSession session = VaadinSession.getCurrent();
        boolean hadLock = false;
        if (session != null && session.hasLock()) {
            hadLock = true;
            session.unlock();
        }
        try {
            // Wait for the first task with the specified timeout
            Runnable task;
            try {
                task = waitMillis > 0
                        ? tasks.poll(waitMillis, TimeUnit.MILLISECONDS)
                        : tasks.poll();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new AssertionError(
                        "Thread interrupted while waiting for pending Signals tasks");
            }
            if (task == null) {
                LoggerFactory.getLogger(TestSignalEnvironment.class).debug(
                        "No pending Signals tasks found after waiting for {} {}",
                        maxWaitTime, unit);
                return false;
            }
            while (task != null) {
                task.run();
                // Process remaining tasks immediately, do not wait for
                // additional tasks to be enqueued
                task = tasks.poll();
            }
        } finally {
            if (hadLock) {
                session.lock();
            }
        }
        return true;
    }

}
