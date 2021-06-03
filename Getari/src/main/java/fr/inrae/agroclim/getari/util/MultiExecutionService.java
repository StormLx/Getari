/*
 * Copyright (C) 2021 INRAE AgroClim
 *
 * This file is part of Getari.
 *
 * Getari is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Getari is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Getari. If not, see <http://www.gnu.org/licenses/>.
 */
package fr.inrae.agroclim.getari.util;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import fr.inrae.agroclim.getari.GetariCli;
import fr.inrae.agroclim.getari.model.Execution;
import fr.inrae.agroclim.getari.model.MultiExecution;
import fr.inrae.agroclim.indicators.exception.FunctionalException;
import fr.inrae.agroclim.indicators.exception.TechnicalException;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

/**
 * MultiExecution Runner.
 *
 * Last change $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
@Log4j2
public class MultiExecutionService {

    /**
     * Consumer of execution number when an execution fails.
     */
    @Setter
    private BiConsumer<Integer, Exception> onFailed;

    /**
     * Consumer of execution number when an execution succeeds.
     */
    @Setter
    private Consumer<Integer> onSucceeded;

    /**
     * Total number of run executions.
     */
    @Getter
    private final IntegerProperty progressProperty = new SimpleIntegerProperty();

    /**
     * To set number on task.
     */
    private final AtomicInteger count = new AtomicInteger(0);

    /**
     * Total number of handled executions.
     */
    @Getter
    private final IntegerProperty sizeProperty = new SimpleIntegerProperty();

    /**
     * Thread executor.
     */
    private final ExecutorService executor = Executors.newCachedThreadPool();

    /**
     * Blocks until all Executions have completed or the current thread is interrupted.
     *
     * @throws InterruptedException if interrupted while waiting
     */
    public void awaitTermination() throws InterruptedException {
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    }

    /**
     * Create a task for an evaluation and the related data files.
     *
     * @param evaluation evaluation path
     * @param execution execution with data files
     * @return task to submit
     */
    private Callable<Void> createTask(final String evaluation, final Execution execution) {
        final int nb = count.addAndGet(1);
        return () -> {
            final GetariCli cli = new GetariCli();
            cli.setEvaluationPath(evaluation);
            cli.setExecution(execution);
            try {
                cli.call();
                if (onSucceeded != null) {
                    onSucceeded.accept(nb);
                }
                progressProperty.set(1 + progressProperty.get());
                return null;
            } catch (final FunctionalException | TechnicalException | IOException | RuntimeException e) {
                if (onFailed != null) {
                    onFailed.accept(nb, e);
                }
                progressProperty.set(1 + progressProperty.get());
                LOGGER.catching(e);
                throw e;
            }
        };
    }

    /**
     * Attempts to stop all actively executing tasks, halts the processing of waiting tasks.
     */
    public void shutdown() {
        executor.shutdown();
    }

    /**
     * Attempts to stop all actively executing tasks, halts the processing of waiting tasks.
     */
    public void shutdownNow() {
        executor.shutdownNow();
    }

    /**
     * Submit all Executions to executor.
     *
     * @param me MultiExecution with Executions
     */
    public void submit(final MultiExecution me) {
        count.set(0);
        progressProperty.set(0);
        sizeProperty.set(me.getExecutions().size());
        me.getExecutions().stream()
        .map(e -> Paths.get(e.getOutput()).getParent()).distinct()
        .forEach(dir -> {
            if (dir != null && !dir.toFile().exists()) {
                LOGGER.info("New directory is created: {}", dir);
                dir.toFile().mkdirs();
            }
        });
        me.getExecutions().stream()
        .map(execution -> createTask(me.getEvaluation(), execution))
                .forEachOrdered(executor::submit);
    }
}
