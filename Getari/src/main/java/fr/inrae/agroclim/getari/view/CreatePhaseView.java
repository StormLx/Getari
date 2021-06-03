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

import java.util.Objects;

import fr.inrae.agroclim.getari.controller.CreatePhaseController;

/**
 * Partial view to modal for definition of new phase.
 *
 * Last change $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
public class CreatePhaseView extends FxmlView {

    /**
     * Controller for the view.
     *
     * @return controller for the view.
     */
    public final CreatePhaseController getController() {
        Objects.requireNonNull(getLoader(), "loader must not be null!");
        return (CreatePhaseController) getLoader().getController();
    }
}
