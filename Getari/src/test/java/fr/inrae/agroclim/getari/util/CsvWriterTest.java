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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.junit.Test;

import fr.inrae.agroclim.indicators.exception.FunctionalException;
import fr.inrae.agroclim.indicators.exception.TechnicalException;
import fr.inrae.agroclim.indicators.model.Evaluation;
import fr.inrae.agroclim.indicators.model.EvaluationSettings;
import fr.inrae.agroclim.indicators.model.result.EvaluationResult;
import fr.inrae.agroclim.indicators.xml.XMLUtil;

/**
 * JUnit test of {@link CsvWriter}.
 *
 * Last changed : $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
public class CsvWriterTest {

    /**
     * Whole test.
     * @throws java.io.IOException while creating tmp file
     * @throws fr.inrae.agroclim.indicators.exception.TechnicalException while
     * loading .gri
     * @throws fr.inrae.agroclim.indicators.exception.FunctionalException while
     * computing
     */
    @Test(expected = Test.None.class)
    public final void write() throws IOException, TechnicalException, FunctionalException {
        final CsvWriter writer = new CsvWriter();
        final Path path = Files.createTempFile("getari-test-csvwriter", ".csv");
        path.toFile().delete();
        assertFalse(path.toFile().exists());

        final File xmlFile = new File("src/test/resources/adura.gri");
        EvaluationSettings settings;
        settings = (EvaluationSettings) XMLUtil.load(xmlFile,
                EvaluationSettings.CLASSES_FOR_JAXB);
        settings.setFilePath(xmlFile.getAbsolutePath());
        settings.initializeKnowledge();
        final Evaluation evaluation = new Evaluation(settings.getEvaluation());
        evaluation.setSettings(settings);
        evaluation.initializeResources();
        evaluation.compute();
        final Map<Integer, EvaluationResult> results = evaluation.getResults();

        writer.write(results, path.toFile().getAbsolutePath());
        assertTrue(path.toFile().exists());
        path.toFile().delete();
    }

}
