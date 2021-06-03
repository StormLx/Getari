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

package fr.inrae.agroclim.getari;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import fr.inrae.agroclim.indicators.exception.TechnicalException;
import fr.inrae.agroclim.indicators.model.Knowledge;
import fr.inrae.agroclim.indicators.model.criteria.SimpleCriteria;
import fr.inrae.agroclim.indicators.model.indicator.Indicator;
import fr.inrae.agroclim.indicators.model.indicator.NumberOfDays;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.adapter.JavaBeanDoublePropertyBuilder;

/**
 * Test using JavaBeanDoublePropertyBuilder to get DoubleProperty.
 *
 * Last change $Date$
 *
 * @author Olivier Maury
 * @author $Author$
 * @version $Revision$
 */
public class DoublePropertyTest {

    /**
     * Test JavaBeanDoublePropertyBuilder on cdaystmin.
     *
     * @throws TechnicalException on knowledge.load()
     * @throws NoSuchMethodException on JavaBeanDoublePropertyBuilder.build()
     */
    @Test(expected = Test.None.class)
    public final void cdaystmin() throws TechnicalException,
    NoSuchMethodException {
        final Knowledge k = Knowledge.load();

        final String indicatorId1 = "cdaystmin";
        final Indicator ind1 = k.getIndicator(indicatorId1);
        assertNotNull("cdaystmin must exist", ind1);
        assertTrue(ind1 instanceof NumberOfDays);

        final NumberOfDays cdaystmin = (NumberOfDays) ind1;
        assertTrue(cdaystmin.getCriteria() instanceof SimpleCriteria);
        final SimpleCriteria criteria = (SimpleCriteria) cdaystmin.getCriteria();

        final Double expected1 = criteria.getThreshold();

        final DoubleProperty property = JavaBeanDoublePropertyBuilder.create()
                .bean(cdaystmin.getCriteria())
                .name("threshold")
                .build();

        assertEquals(expected1, property.getValue());

        criteria.setThreshold(expected1 + 1);

        final Double expected2 = criteria.getThreshold();
        assertNotEquals(expected1, expected2);
        assertEquals(expected2, property.getValue());
    }
}
