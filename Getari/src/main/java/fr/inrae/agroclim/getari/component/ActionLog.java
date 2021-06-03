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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

/**
 * Logs into a file all user interaction on GUI.
 *
 * Last changed : $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
@Log4j2
public class ActionLog {

    /**
     * Formatter for the prefixed date.
     */
    private static final DateTimeFormatter FORMATTER;

    static {
        FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    }

    /**
     * The output stream for the file.
     */
    private final FileOutputStream fos;

    /**
     * Path of the log file.
     */
    @Getter
    private final Path path;

    /**
     * Constructor.
     *
     * @param filePath path of the log file
     * @throws java.io.FileNotFoundException when directory of log file does not
     * exist
     */
    public ActionLog(final Path filePath) throws FileNotFoundException {
        path = filePath;
        fos = new FileOutputStream(path.toFile());
    }

    /**
     * Append a string to the file.
     *
     * @param text string to append
     */
    public final void append(final String... text) {
        try {
            fos.write(LocalDateTime.now().format(FORMATTER).getBytes());
            for (final String t : text) {
                fos.write("\t".getBytes());
                if (t != null) {
                    fos.write(t.getBytes());
                }
            }
            fos.write("\n".getBytes());
        } catch (final IOException ex) {
            LOGGER.catching(ex);
        }
    }
}
