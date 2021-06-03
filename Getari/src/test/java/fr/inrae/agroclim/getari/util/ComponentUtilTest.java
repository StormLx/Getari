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

import org.junit.Test;

import fr.inrae.agroclim.getari.exception.GetariException;

/**
 * JUnit test of {@link ComponentUtil}.
 *
 * Last changed : $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
public class ComponentUtilTest {

    @Test
    public void isAValidCsvFile() throws GetariException {
        final File csv = new File("src/test/resources/adura-phenonorm.csv");
        assertTrue(csv.exists());
        assertTrue(ComponentUtil.isAValidCsvFile(csv));
        final File gri = new File("src/test/resources/adura.gri");
        assertTrue(gri.exists());
        assertFalse(ComponentUtil.isAValidCsvFile(gri));
    }
}
