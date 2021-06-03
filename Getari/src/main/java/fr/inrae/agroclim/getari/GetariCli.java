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
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;

import fr.inrae.agroclim.getari.model.Execution;
import fr.inrae.agroclim.getari.resources.Messages;
import fr.inrae.agroclim.getari.util.CsvWriter;
import fr.inrae.agroclim.getari.util.MultiExecutionHelper;
import fr.inrae.agroclim.getari.util.StringUtils;
import fr.inrae.agroclim.indicators.exception.FunctionalException;
import fr.inrae.agroclim.indicators.exception.TechnicalException;
import fr.inrae.agroclim.indicators.model.Evaluation;
import fr.inrae.agroclim.indicators.model.EvaluationSettings;
import fr.inrae.agroclim.indicators.resources.I18n;
import fr.inrae.agroclim.indicators.xml.XMLUtil;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

/**
 * Command line interface for GETARI.
 *
 * Last change $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
@Log4j2
public final class GetariCli implements Callable<Void> {

    /**
     * Check and return File.
     *
     * @param path file path
     * @return File
     * @throws IOException if the file is not readable.
     */
    private static File getFile(final String path) throws IOException {
        final File file = new File(path);
        if (!file.canRead()) {
            throw new IOException(Messages.getString("error.non-readable.file", path));
        }
        return file;
    }

    /**
     * File path for evaluation.
     */
    @Setter
    private String evaluationPath;

    /**
     * Execution details.
     */
    @Setter
    private Execution execution;

    @Override
    public Void call() throws IOException, TechnicalException, FunctionalException {
        // load
        LOGGER.trace("Loading evaluation \"{}\" with climate \"{}\" "
                + "and phenology \"{}\"", evaluationPath, execution.getClimate(),
                execution.getPhenology());
        final Evaluation evaluation = getEvaluation();
        if (evaluation == null) {
            throw new TechnicalException("evaluation should never be null");
        }
        evaluation.initializeResources();
        // compute
        LOGGER.trace("Computing {}", evaluation.getName());
        if (Thread.currentThread().isInterrupted()) {
            throw new TechnicalException("Interruption");
        }
        evaluation.compute();
        // write
        LOGGER.info("Writing to CSV and TXT {}", execution.getOutput());
        final CsvWriter writer = new CsvWriter();
        final I18n msg = new I18n("fr.inrae.agroclim.getari.view.results",
                Locale.ENGLISH);
        writer.setMsg(msg);
        writer.write(evaluation.getResults(), execution.getOutput());
        writer.writeMetadata(evaluation);
        LOGGER.info("Done");
        return null;
    }

    /**
     * Load Evaluation from file.
     *
     * @return Evaluation
     * @throws IOException if the file is not readable.
     * @throws TechnicalException if XML cannot be loaded
     */
    private Evaluation getEvaluation() throws IOException, TechnicalException {
        final File climateFile = getFile(execution.getClimate());
        final File evaluationFile = getFile(evaluationPath);
        final File phenologyFile = getFile(execution.getPhenology());
        EvaluationSettings settings;
        settings = (EvaluationSettings) XMLUtil.load(evaluationFile,
                EvaluationSettings.CLASSES_FOR_JAXB);
        settings.initializeKnowledge();
        settings.setFilePath(evaluationPath);
        settings.getClimateLoader().getFile().setFile(climateFile);
        settings.getPhenologyLoader().getFile().setFile(phenologyFile);
        final Map<String, String> errors = settings.getConfigurationErrors();
        if (errors != null && !errors.isEmpty()) {
            LOGGER.error(errors.toString());
            return null;
        }
        final Evaluation eval = new Evaluation(settings.getEvaluation());
        eval.setSettings(settings);
        eval.initializeParent();
        return eval;
    }

    /**
     * @return all paths are set
     */
    public boolean isOk() {
        return MultiExecutionHelper.isValid(execution)
                && StringUtils.isNotBlank(evaluationPath);
    }

    /**
     * @return at least one file path is set
     */
    public boolean isSet() {
        return execution.getClimate() != null || evaluationPath != null
                || execution.getPhenology() != null || execution.getOutput() != null;
    }
}
