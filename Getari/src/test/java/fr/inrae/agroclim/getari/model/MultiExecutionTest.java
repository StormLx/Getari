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
package fr.inrae.agroclim.getari.model;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.assertj.core.util.Files;
import org.junit.Test;

import fr.inrae.agroclim.getari.util.MultiExecutionHelper;
import fr.inrae.agroclim.indicators.exception.TechnicalException;

/**
 * Test of multi-execution.
 *
 * Last changed : $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
public class MultiExecutionTest {

    @Test
    public void serialize() throws TechnicalException {
        final Execution execution = new Execution();
        execution.setClimate("climate");
        execution.setOutput("output");
        execution.setPhenology("phenology");
        final MultiExecution me = new MultiExecution();
        me.getExecutions().add(execution);
        me.getExecutions().add(execution);
        final File file = Files.newTemporaryFile();
        MultiExecutionHelper.serialize(me, file);
        assertTrue(file.length() > 0);
    }
}
