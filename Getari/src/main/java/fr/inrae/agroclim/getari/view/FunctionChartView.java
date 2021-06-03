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
package fr.inrae.agroclim.getari.view;

import fr.inrae.agroclim.getari.controller.FunctionChartController;

/**
 * Partial view to display chart of normalization function in
 * DetailedFunctionViewVisitor.
 *
 * Last change $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
public class FunctionChartView extends FxmlView {

    /**
     * Controller for the view.
     *
     * @return controller for the view.
     */
    public final FunctionChartController getController() {
        return (FunctionChartController) getLoader().getController();
    }
}
