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
package fr.inrae.agroclim.getari.util;

import fr.inrae.agroclim.getari.resources.Messages;
import fr.inrae.agroclim.indicators.model.indicator.Indicator;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;

/**
 * Cell used to build ComboBox with phases.
 *
 * Last change $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
public final class NameablePhaseCell extends ListCell<Indicator> {

    /**
     * Provide a custom cell factory to the combobox for complete customization
     * of the rendering of items in the ComboBox.
     *
     * @param combo combobox to customize
     */
    public static void setCellFactory(final ComboBox<Indicator> combo) {
        combo.setCellFactory(p -> new NameablePhaseCell());
        combo.setButtonCell(new NameablePhaseCell());
    }

    @Override
    protected void updateItem(final Indicator item, final boolean empty) {
        super.updateItem(item, empty);
        if (item != null) {
            setText(Messages.getString("action.phase", item.getId()));
        } else {
            setText("");
        }
    }
}
