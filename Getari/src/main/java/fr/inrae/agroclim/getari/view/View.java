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

import javafx.scene.Parent;
import javafx.stage.Stage;

/**
 * Ancêtre des vues.
 *
 * Last change $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
interface View {
    /**
     * Build the root node of the view.
     *
     * @param stage main stage
     * @return root node of view
     */
    Parent build(Stage stage);
}
