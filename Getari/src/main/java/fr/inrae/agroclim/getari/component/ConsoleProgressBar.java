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

import java.io.PrintStream;

/**
 * Progress bar for console line interface.
 *
 * Last change $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
public class ConsoleProgressBar {

    /**
     * Format of progress bar.
     */
    private static final String FORMAT = "\r%3d%% %s %c";
    /**
     * Number of characters in the progress bar.
     */
    private static final int SIZE = 60;
    /**
     * Characters to create the spinning wait symbol.
     */
    private static final char[] WORKCHARS = {'|', '/', '-', '\\'};
    /**
     * The stream to use to display the progress bar.
     */
    private final PrintStream outStream;
    /**
     * Previous position, to avoid backward.
     */
    private int previous = -1;
    /**
     * Builder for the progress bar text.
     */
    private StringBuilder progress;

    /**
     * Constructor.
     *
     * @param stream The stream to use to display the progress bar.
     */
    public ConsoleProgressBar(final PrintStream stream) {
        outStream = stream;
        init();
    }

    /**
     * Initialize progress bar properties.
     */
    private void init() {
        this.progress = new StringBuilder(SIZE);
    }

    /**
     * Update and draw progress bar.
     *
     * @param current an int representing the work done so far
     * @param total an int representing the total work
     */
    public void update(final int current, final int total) {
        if (previous >= current) {
            return;
        }
        previous = current;
        final int percent = current * 100 / total;
        int extrachars = percent / 2 - this.progress.length();
        while (--extrachars > 0) {
            progress.append('#');
        }
        outStream.printf(FORMAT, percent, progress, WORKCHARS[(current + 1) % WORKCHARS.length]);

        if (current == total) {
            outStream.flush();
            outStream.print(System.lineSeparator());
            init();
        }
    }
}
