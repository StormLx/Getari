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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import fr.inrae.agroclim.getari.model.Execution;
import fr.inrae.agroclim.getari.model.MultiExecution;
import fr.inrae.agroclim.indicators.exception.TechnicalException;

/**
 * JUnit test of {@link MultiExecutionHelper}.
 *
 * Last changed : $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
public class MultiExecutionHelperTest {
    /**
     * Path for climate file.
     */
    private final Path climatePath = Paths.get("src", "test", "resources", "climate-only-wind.csv");
    /**
     * Path for evaluation file.
     */
    private final Path evaluationPath = Paths.get("src", "test", "resources", "adura.gri");
    /**
     * Path for output file.
     */
    private final Path outputPath = Paths.get("output.csv");
    /**
     * Path for phenology file.
     */
    private final Path phenologyPath = Paths.get("src", "test", "resources", "adura-phenotrqil.csv");

    @Test
    public void deserialized() throws TechnicalException {
        final File file = Paths.get("samples", "multiexecution.xml").toFile();
        assertTrue(file.exists());
        final MultiExecution deserialized = MultiExecutionHelper.deserialize(file);
        assertNotNull(deserialized);
        assertNotNull(file.getParent());
        final MultiExecution actual = MultiExecutionHelper.resolve(file.getAbsolutePath(), deserialized);
        assertNotNull(actual);
        assertTrue(MultiExecutionHelper.isValid(actual));
    }
    @Test
    public void isMultiExecutionFalse() {
        final File file = Paths.get("samples", "evaluation_hourly_sample.gri").toFile();
        assertFalse(MultiExecutionHelper.isMultiExecution(file));
    }
    @Test
    public void isMultiExecutionTrue() {
        final File file = Paths.get("samples", "multiexecution.xml").toFile();
        assertTrue(MultiExecutionHelper.isMultiExecution(file));
    }
    @Test
    public void relativizePathOfSameDir() {
        final String actual = MultiExecutionHelper.relativizePath(evaluationPath.toString(), climatePath.toString());
        final String expected = "climate-only-wind.csv";
        assertEquals(expected, actual);
    }

    @Test
    public void relativizePathOfSubDir() {
        final Path basePath = Paths.get("a", "b", "c", "adura.gri");
        final Path filePath = Paths.get("a", "b", "c", "d", "climate-only-wind.csv");
        final String actual = MultiExecutionHelper.relativizePath(basePath.toString(), filePath.toString());
        final String expected = "d" + File.separator + "climate-only-wind.csv";
        assertEquals(expected, actual);
    }

    @Test
    public void relativizePathSelf() throws IOException {
        final String filePath = evaluationPath.toFile().getAbsolutePath();
        final String actual = MultiExecutionHelper.relativizePath(filePath, filePath);
        final String expected = evaluationPath.toFile().getName();
        assertEquals(expected, actual);
    }

    @Test
    public void resolvePathOfSameDir() {
        final String filePath = "climate-only-wind.csv";
        final String expected = Paths.get("src", "test", "resources", filePath).toString();
        final String actual = MultiExecutionHelper.resolvePath(evaluationPath.toString(), filePath);
        assertEquals(expected, actual);
    }

    @Test
    public void resolvePathOfSubDir() {
        final Path basePath = Paths.get("a", "b", "c", "adura.gri");
        final String filePath = "d" + File.separator + "climate-only-wind.csv";
        final String expected = Paths.get("a", "b", "c", filePath).toString();
        final String actual = MultiExecutionHelper.resolvePath(basePath.toString(), filePath);
        assertEquals(expected, actual);
    }

    @Test
    public void serializeDeserialize() throws IOException, TechnicalException {
        final MultiExecution me = new MultiExecution();
        me.setEvaluation(evaluationPath.toFile().getAbsolutePath());
        final Execution ex1 = new Execution();
        ex1.setClimate(climatePath.toFile().getAbsolutePath());
        ex1.setOutput(outputPath.toFile().getAbsolutePath());
        ex1.setPhenology(phenologyPath.toFile().getAbsolutePath());
        me.getExecutions().add(ex1);
        final File file = Files.createTempFile("multievaluation", ".xml").toFile();
        final MultiExecution relativized = MultiExecutionHelper.relativize(file.getAbsolutePath(), me);
        MultiExecutionHelper.serialize(relativized, file);
        assertTrue("File must exist after serialization", file.exists());

        try {
            final MultiExecution deserialized = MultiExecutionHelper.deserialize(file);
            final MultiExecution actual = MultiExecutionHelper.resolve(file.getAbsolutePath(), deserialized);
            assertNotNull(actual);
            assertEquals(me.getExecutions().size(), actual.getExecutions().size());
            final Execution actual1 = me.getExecutions().get(0);
            assertEquals(ex1, actual1);
        } catch (final TechnicalException e) {
            assertFalse(e.getMessage(), true);
        }
    }

    @Test
    public void suggestedOutput() {
        final List<Execution> executions = Arrays.asList("output.csv", "output5.csv", "resultat.csv")
                .stream()
                .map(output -> {
                    final Execution e = new Execution();
                    e.setOutput(output);
                    return e;
                }).collect(Collectors.toList());
        final String actual = MultiExecutionHelper.suggestedOutput(executions);
        final String expected = "output6.csv";
        assertEquals(expected, actual);
    }

    @Test
    public void suggestedOutputBis() {
        final List<Execution> executions = Arrays.asList("output.csv", "output2.csv")
                .stream()
                .map(output -> {
                    final Execution e = new Execution();
                    e.setOutput(output);
                    return e;
                }).collect(Collectors.toList());
        final String actual = MultiExecutionHelper.suggestedOutput(executions);
        final String expected = "output3.csv";
        assertEquals(expected, actual);
    }
}
