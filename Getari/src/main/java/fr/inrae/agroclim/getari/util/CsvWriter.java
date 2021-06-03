/*
 * Copyright (C) 2020 INRAE Agroclim
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import fr.inrae.agroclim.getari.resources.Messages;
import fr.inrae.agroclim.getari.resources.Version;
import fr.inrae.agroclim.indicators.model.Evaluation;
import fr.inrae.agroclim.indicators.model.EvaluationSettings;
import fr.inrae.agroclim.indicators.model.indicator.IndicatorCategory;
import fr.inrae.agroclim.indicators.model.result.EvaluationResult;
import fr.inrae.agroclim.indicators.model.result.IndicatorResult;
import fr.inrae.agroclim.indicators.model.result.PhaseResult;
import fr.inrae.agroclim.indicators.resources.I18n;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

/**
 * CSV writer for evaluation results.
 *
 * Last change $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
@Log4j2
public class CsvWriter {
    /**
     * Formater for phase dates.
     */
    private final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    /**
     * Localization messages.
     */
    @Setter
    private I18n msg;
    /**
     * Buffer.
     */
    @Setter
    private Writer writer = null;
    /**
     * Path of CSV file.
     */
    private String filePath;
    /**
     * CSV representation of phase columns.
     */
    private String phaseCsvPart = ",,,";

    /**
     * Run after looping on evaluation results.
     */
    private void afterLoopOnResults() {
        if (writer != null) {
            try {
                // Close the writer regardless of what happens...
                writer.close();
            } catch (final IOException e) {
                LOGGER.error(e);
            }
        }
    }

    /**
     * Run before looping on evaluation results.
     */
    private void beforeLoopOnResults() {
        LOGGER.debug("Writing results to {}", filePath);
        final File file = new File(filePath);
        if (file.getParentFile() != null && !file.getParentFile().exists()
                && !file.getParentFile().mkdirs()) {
            throw new RuntimeException("Cannot create directory "
                    + file.getParentFile().getAbsolutePath());
        }
        try {
            if (writer == null) {
                writer = new BufferedWriter(new FileWriter(file));
            }
            writer.write(Messages.getString("csv.headers") + "\n");
        } catch (final IOException e) {
            LOGGER.error(e);
        }
    }

    /**
     * @param year year of indicator
     * @param effect climatic effect details with normalized value
     */
    private void handleEffect(final short year, final IndicatorResult effect) {
        try {
            writer.write(String.format(Locale.ROOT, "%d%s%s,effect,%.3f%n",
                    year, phaseCsvPart, effect.getIndicatorId(),
                    effect.getNormalizedValue()));
        } catch (final IOException e) {
            LOGGER.error(e);
        }
    }

    /**
     * Loop on indicators.
     *
     * @param year year of indicators
     * @param indicator indicator to handle
     * @return number of indicator results
     */
    private int handleIndicator(final short year,
            final IndicatorResult indicator) {
        try {
            writer.write(String.format(Locale.ROOT, "%d%s%s,%s,%.3f,%.3f%n",
                    year, phaseCsvPart, indicator.getIndicatorId(),
                    indicator.getIndicatorCategory(),
                    indicator.getNormalizedValue(),
                    indicator.getRawValue()));
        } catch (final IOException e) {
            LOGGER.error(e);
        }
        int nbOfResults = 1;
        if (!indicator.getIndicatorResults().isEmpty()) {
            nbOfResults += indicator.getIndicatorResults()
                    .stream()
                    .map(res -> handleIndicator(year, res))
                    .reduce(nbOfResults, Integer::sum);
        }
        return nbOfResults;
    }

    /**
     * Loop on indicators.
     *
     * @param year year of indicators
     * @param indicators indicators to handle
     * @return number of indicator results
     */
    private int handleIndicators(final short year,
            final Collection<IndicatorResult> indicators) {
        int nbOfResults = 0;
        for (final IndicatorResult ind : indicators) {
            if (ind == null) {
                LOGGER.trace("Strange, indicator value is null!");
                continue;
            }
            // No computation on phase,
            // phase is not really an indicator ...
            final IndicatorCategory cat = ind.getIndicatorCategory();
            if (!IndicatorCategory.PHENO_PHASES.equals(cat)) {
                nbOfResults += handleIndicator(year, ind);
            }
            //-
            if (!ind.getIndicatorResults().isEmpty()) {
                nbOfResults
                += handleIndicators(year, ind.getIndicatorResults());
            }
        }
        return nbOfResults;
    }

    /**
     * @param year year of indicator
     * @param result phase details with normalized value
     */
    private void handlePhase(final short year, final PhaseResult result) {
        try {
            phaseCsvPart = "," + result.getPhaseId() + ","
                    + df.format(result.getAnnualPhase().getStart()) + ","
                    + df.format(result.getAnnualPhase().getEnd()) + ",";
            writer.write(String.format(Locale.ROOT, "%d%sphase,phase,%.3f%n",
                    year, phaseCsvPart, result.getNormalizedValue()));
        } catch (final IOException e) {
            LOGGER.error(e);
        }
    }

    /**
     * @param year year of indicator
     * @param process process details with normalized value
     */
    private void handleProcess(final short year,
            final IndicatorResult process) {
        try {
            writer.write(String.format(Locale.ROOT, "%d%s%s,process,%.3f%n",
                    year, phaseCsvPart, process.getIndicatorId(),
                    process.getNormalizedValue()));
        } catch (final IOException e) {
            LOGGER.error(e);
        }
    }

    /**
     * @param year year of indicator
     * @param value normalized value of evaluation for the year
     */
    private void handleYear(final short year, final Double value) {
        try {
            writer.write(String.format(Locale.ROOT,
                    "%d,,,,evaluation,evaluation,%.3f", year, value));
            writer.write("\n");
        } catch (final IOException e) {
            LOGGER.error(e);
        }
    }

    /**
     * Write evaluation results into a CSV file.
     *
     * @param results evaluation results
     * @param absolutePath path of CSV file
     */
    public final void write(final Map<Integer, EvaluationResult> results,
            final String absolutePath) {
        LOGGER.traceEntry(absolutePath);
        this.filePath = absolutePath;
        beforeLoopOnResults();
        final long startTime = System.currentTimeMillis();
        int nbOfResults = 0;
        for (final Map.Entry<Integer, EvaluationResult> entry
                : results.entrySet()) {
            final short year = entry.getKey().shortValue();
            final EvaluationResult evaluationResult = entry.getValue();
            if (evaluationResult == null) {
                LOGGER.warn("Strange, no result for {}!", year);
                continue;
            }
            handleYear(year, evaluationResult.getNormalizedValue());
            for (final PhaseResult phaseResult
                    : evaluationResult.getPhaseResults()) {
                if (phaseResult == null) {
                    LOGGER.warn("Strange, null phaseResult in {}!", year);
                    continue;
                }
                handlePhase(year, phaseResult);
                for (final IndicatorResult processResult
                        : phaseResult.getIndicatorResults()) {
                    handleProcess(year, processResult);
                    for (final IndicatorResult effectResult
                            : processResult.getIndicatorResults()) {
                        handleEffect(year, effectResult);
                        nbOfResults += handleIndicators(year,
                                effectResult.getIndicatorResults());
                    }
                }
            }
        }
        final long endTime = System.currentTimeMillis();
        final double msToSeconds = 1000.;
        final double duration = (endTime - startTime) / msToSeconds;
        LOGGER.trace(String.format(
                "Handling %d results %fs",
                nbOfResults, duration));
        afterLoopOnResults();
        LOGGER.traceExit("Handling results done");
    }

    /**
     * Write metadata of evaluation into a file.
     *
     * @param evaluation evaluation
     */
    public final void writeMetadata(final Evaluation evaluation) {
        final String absoluteFilePath = this.filePath
                .replace(".csv", "-metadata.txt");
        LOGGER.debug("Writing metadata to {}", absoluteFilePath);
        final EvaluationSettings settings = evaluation.getSettings();
        final File file = new File(absoluteFilePath);
        if (file.getParentFile() != null && !file.getParentFile().exists()
                && !file.getParentFile().mkdirs()) {
            throw new RuntimeException("Cannot create directory "
                    + file.getParentFile().getAbsolutePath());
        }
        try (Writer w = new BufferedWriter(new FileWriter(file))) {
            w.write(msg.get("data.files") + "\n");
            w.write(msg.get("data.climate") + "\t"
                    + settings.getClimateLoader().getFile().getPath() + "\n");
            w.write(msg.get("data.phenology") + "\t"
                    + settings.getPhenologyLoader().getFile().getPath() + "\n");
            w.write(msg.get("evaluation") + "\t"
                    + settings.getFilePath() + "\n");
            w.write(msg.get("data.results") + "\t"
                    + filePath + "\n");
            w.write(msg.get("run") + "\n");
            w.write("GETARI\t" + Version.getString("version") + " "
                    + Version.getString("build.number") + " "
                    + Version.getString("build.date") + "\n");
            w.write(msg.get("run.datetime") + "\t"
                    + LocalDateTime.now()
                    .format(DateTimeFormatter.ISO_DATE_TIME) + "\n");
        } catch (final IOException e) {
            LOGGER.error(e);
        }
        LOGGER.traceExit();
    }

}
