/*
 * Copyright (C) 2020 INRAE Agroclim
 *
 * This file is part of GETARI.
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
 * along with GETARI. If not, see <http://www.gnu.org/licenses/>.
 */
package fr.inrae.agroclim.getari;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;

import fr.inrae.agroclim.getari.component.ConsoleProgressBar;
import fr.inrae.agroclim.getari.model.Execution;
import fr.inrae.agroclim.getari.model.MultiExecution;
import fr.inrae.agroclim.getari.resources.Messages;
import fr.inrae.agroclim.getari.util.MultiExecutionHelper;
import fr.inrae.agroclim.getari.util.MultiExecutionService;
import fr.inrae.agroclim.indicators.exception.FunctionalException;
import fr.inrae.agroclim.indicators.exception.TechnicalException;
import lombok.extern.log4j.Log4j2;

/**
 * Main class for GETARI.
 *
 * Last change $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
@Log4j2
public final class Getari {

    /**
     * Print help in CLI.
     *
     * @param status exit status.
     */
    private static void help(final int status) {
        LOGGER.info(Messages.getString("console.help"));
        System.exit(status);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(final String[] args) {
        LOGGER.trace("start {}", String.join(",", args));
        if (Locale.FRANCE.equals(Locale.getDefault())) {
            Locale.setDefault(Locale.FRENCH);
        }
        final GetariCli cli = new GetariCli();
        final Execution execution = new Execution();
        cli.setExecution(execution);
        String multiEvaluation = null;
        // default log level for CLI.
        String logLevel = Level.ERROR.name();
        for (int i = 0; i < args.length; i++) {
            final String arg = args[i];
            if (arg == null || !arg.startsWith("--")) {
                continue;
            }
            if ("--help".equals(arg) || "-h".equals(arg)) {
                help(0);
                return;
            }
            String value = "";
            if (args.length > i + 1) {
                value = args[i + 1];
            }
            switch (arg) {
            case "--climate":
                execution.setClimate(value);
                continue;
            case "--evaluation":
                cli.setEvaluationPath(value);
                continue;
            case "--multiexecution":
                multiEvaluation = value;
                continue;
            case "--phenology":
                execution.setPhenology(value);
                continue;
            case "--results":
                execution.setOutput(value);
                continue;
            case "--splash":
                GetariFx.setSplash(Boolean.valueOf(value));
                continue;
            case "--verbose":
                logLevel = value;
                continue;
            default:
                LOGGER.error("Unkown command line argument: " + arg);
                help(1);
            }
        }
        if (multiEvaluation != null) {
            setLogLevel(logLevel);
            runMultiexecution(multiEvaluation);
            return;
        }
        if (cli.isSet()) {
            setLogLevel(logLevel);
            if (cli.isOk()) {
                try {
                    cli.call();
                } catch (IOException | TechnicalException | FunctionalException ex) {
                    LOGGER.fatal("Error while running evaluation", ex);
                }
            } else {
                help(1);
            }
            return;
        }
        GetariFx.start(args);
    }

    /**
     * Run MultiEvaluation from file path.
     *
     * @param path file path of XML file
     */
    private static void runMultiexecution(final String path) {
        final long startTime = new Date().getTime();
        final File file = new File(path);
        if (!file.exists()) {
            LOGGER.fatal(Messages.getString("error.non-existent.file", path));
            System.exit(1);
            return;
        }
        if (!file.canRead()) {
            LOGGER.fatal(Messages.getString("error.non-readable.file", path));
            System.exit(1);
            return;
        }
        MultiExecution me;
        try {
            final MultiExecution deserialized = MultiExecutionHelper.deserialize(file);
            me = MultiExecutionHelper.resolve(file.getAbsolutePath(), deserialized);
        } catch (final TechnicalException ex) {
            LOGGER.error(ex);
            System.exit(1);
            return;
        }
        final int total = me.getExecutions().size();
        LOGGER.info(Messages.getString(total, "execution.count", total));
        final ConsoleProgressBar progressBar = new ConsoleProgressBar(System.out);
        progressBar.update(0, total);
        final MultiExecutionService service = new MultiExecutionService();
        service.getProgressProperty().addListener((obs, oldVal, val) ->  progressBar.update(val.intValue(), total));
        service.setOnFailed((n, e) -> LOGGER.error(Messages.getString("execution.failed", n) + e.getMessage()));
        service.submit(me);
        service.shutdown();
        try {
            service.awaitTermination();
            final long duration = new Date().getTime() - startTime;
            final double msInSecond = 1_000;
            progressBar.update(total, total);
            LOGGER.info(Messages.getString("execution.duration", duration / msInSecond));
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Define log level, print error if level is unknown.
     *
     * @param value name of level
     */
    private static void setLogLevel(final String value) {
        final Level level = Level.getLevel(value.toUpperCase());
        if (level == null) {
            LOGGER.fatal(Messages.getString("error.console.log.level"));
            return;
        }
        Configurator.setLevel("fr.inrae.agroclim", level);
        Configurator.setLevel("fr.inrae.agroclim.getari.Getari", Level.INFO);
    }

    /**
     * No constructor for the main class.
     */
    private Getari() {
    }
}
