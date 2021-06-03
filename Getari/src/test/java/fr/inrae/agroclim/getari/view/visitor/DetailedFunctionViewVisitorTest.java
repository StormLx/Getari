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
package fr.inrae.agroclim.getari.view.visitor;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import lombok.RequiredArgsConstructor;

/**
 * Test DetailedFunctionViewVisitor.
 *
 * Last changed : $Date: 2018-11-22 11:03:34 +0100 (jeu., 22 nov. 2018) $
 *
 * @author $Author: omaury $
 * @version $Revision: 360 $
 */
@RunWith(Parameterized.class)
@RequiredArgsConstructor
public class DetailedFunctionViewVisitorTest {

    /**
     * @return JEXL function name and expected path on API.
     */
    @Parameterized.Parameters
    public static List<String[]> data() {
        final List<String[]> data = new ArrayList<>();
        data.add(new String[]{"math:abs(?)", "#abs-double-"});
        data.add(new String[]{"math:max(?,?)", "#max-double-double-"});
        return data;
    }

    /**
     * Representation in combobox of JEXL function.
     */
    private final String funcName;
    /**
     * Expected path for API URL.
     */
    private final String path;

    @Test
    public void jexlFunctionToApiDocUrl() {
        final String found = DetailedFunctionViewVisitor
                .jexlFunctionToApiDocUrl(funcName);
        assertEquals(DetailedFunctionViewVisitor.MATH_JAVADOC_BASE_PATH + path, found);
    }
}
