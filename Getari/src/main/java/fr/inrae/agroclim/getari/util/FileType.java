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

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Getter;

/**
 * Handled file types in GETARI.
 *
 * Last change $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
public enum FileType {
    /**
     * Climatic data file.
     */
    CLIMATE("evaluation.climatic.action", new String[][]{//
        {"filechooser.all.files", "*.*"}, //
        {"filechooser.csv.files", "*.csv"}, //
        {"filechooser.txt.files", "*.txt"}}),
    /**
     * Evaluation file.
     */
    EVALUATION("action.choose.evaluation", new String[][]{//
        {"filechooser.gri.files", "*.gri"},
        {"filechooser.xml.files", "*.xml"}}),
    /**
     * Multi-execution file.
     */
    MULTIEXECUTION("action.choose.multiexecution", new String[][]{//
        {"filechooser.xml.files", "*.xml"}}),
    /**
     * Phenological data file.
     */
    PHENOLOGY("evaluation.phenological.action", new String[][]{//
        {"filechooser.all.files", "*.*"}, //
        {"filechooser.csv.files", "*.csv"}, //
        {"filechooser.txt.files", "*.txt"}}),
    /**
     * Evaluation results file.
     */
    RESULTS("action.result.file", new String[][]{//
        {"filechooser.csv.files", "*.csv"}, //
        {"filechooser.out.files", "*.out"}});

    /**
     * Extension filters.
     */
    private final String[][] filters;

    /**
     * Dialog title.
     */
    @Getter
    private final String title;

    /**
     * Constructor.
     *
     * @param dialogTitle dialog title
     * @param extensionFilters extension filters
     */
    FileType(final String dialogTitle, final String[][] extensionFilters) {
        this.filters = extensionFilters;
        this.title = dialogTitle;
    }

    /**
     * @return extension filters
     */
    public final Map<String, String> getFilters() {
        final Map<String, String> result = new LinkedHashMap<>();
        for (final String[] filter : filters) {
            result.put(filter[0], filter[1]);
        }
        return result;
    }

}
