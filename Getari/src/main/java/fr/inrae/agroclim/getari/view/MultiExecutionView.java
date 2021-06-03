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
package fr.inrae.agroclim.getari.view;

import fr.inrae.agroclim.getari.controller.MultiExecutionController;
import lombok.extern.log4j.Log4j2;

/**
 * Partial view to edit MultiEvaluation.
 *
 * Last change $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
@Log4j2
public class MultiExecutionView extends FxmlView {

    /**
     * Controller for the view.
     *
     * @return controller for the view.
     */
    public final MultiExecutionController getController() {
        if (getLoader() == null) {
            LOGGER.fatal("Hey! loader is null!");
        }
        return (MultiExecutionController) getLoader().getController();
    }

}
