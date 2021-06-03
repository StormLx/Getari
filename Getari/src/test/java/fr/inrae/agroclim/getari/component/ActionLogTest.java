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
package fr.inrae.agroclim.getari.component;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * JUnit test of {@link ActionLog}.
 *
 * Last changed : $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
public class ActionLogTest {

    /**
     * Path of log file.
     */
    private Path path;

    @Test
    public void appendString() throws IOException {
        final String text = "Tio estas provo";
        final ActionLog log = new ActionLog(path);
        log.append(text);
        assertEquals(log.getPath(), path);
        assertTrue(log.getPath().toFile().exists());
        List<String> lines = Files.readAllLines(path);
        assertEquals(1, lines.size());
        assertTrue(lines.get(0).contains(text));
        log.append(text, text);
        lines = Files.readAllLines(path);
        assertEquals(2, lines.size());
        assertTrue(lines.get(1).contains(text + "\t" + text));
    }

    @Test
    public void constructor() throws FileNotFoundException {
        path.toFile().delete();
        assertFalse(path.toFile().exists());
        final ActionLog log = new ActionLog(path);
        assertEquals(log.getPath(), path);
        assertTrue(path.toFile().exists());
    }

    @Before
    public void setUp() throws IOException {
        path = Files.createTempFile("action-log", ".log");
    }

    @After
    public void tearDown() {
        path.toFile().delete();
    }
}
