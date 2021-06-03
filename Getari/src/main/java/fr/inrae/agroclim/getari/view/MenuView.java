/**
 * This file is part of GETARI.
 *
 * GETARI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GETARI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GETARI. If not, see <https://www.gnu.org/licenses/>.
 */
package fr.inrae.agroclim.getari.view;

import fr.inrae.agroclim.getari.controller.MenuController;
import lombok.extern.log4j.Log4j2;

/**
 * Partial view to display toolbar in GraphView.
 *
 * Last change $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
@Log4j2
public class MenuView extends FxmlView {

    /**
     * Controller for the view.
     *
     * @return controller for the view.
     */
    public final MenuController getController() {
        if (getLoader() == null) {
            LOGGER.fatal("Hey! loader is null!");
        }
        return (MenuController) getLoader().getController();
    }
}
