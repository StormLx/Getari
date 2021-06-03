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
package fr.inrae.agroclim.getari.component;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Before;
import org.junit.Test;

/**
 * Test progress bar for console line interface.
 *
 * Last change $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
public class ConsoleProgressBarTest {

    /**
     * Output stream for the console.
     */
    private ByteArrayOutputStream baos;
    /**
     * Instance to test.
     */
    private ConsoleProgressBar progressBar;

    @Before
    public void setUp() {
        baos  = new ByteArrayOutputStream();
        final PrintStream stream = new PrintStream(baos);
        progressBar = new ConsoleProgressBar(stream);
    }

    @Test
    public void update100() {
        progressBar.update(400, 400);
        final String expected = "\r100% ################################################# /" + System.lineSeparator();
        assertEquals(expected, baos.toString());
    }

    @Test
    public void update50() {
        progressBar.update(50, 100);
        final String expected = "\r 50% ######################## \\";
        assertEquals(expected, baos.toString());
    }

    @Test
    public void update99() {
        progressBar.update(99, 100);
        final String expected = "\r 99% ################################################ |";
        assertEquals(expected, baos.toString());
    }
}
