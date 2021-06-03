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

import fr.inrae.agroclim.indicators.model.Nameable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;

/**
 * Cell used to build ComboBox with Nameable objects.
 *
 * Last change $Date$
 *
 * @author $Author$
 * @version $Revision$
 * @param <T> class of nameable items
 */
public final class NameableListCell<T extends Nameable> extends ListCell<T> {

    /**
     *
     * @param <T> class of items
     * @param combo combobox to custumize
     */
    public static <T extends Nameable> void setCellFactory(
            final ComboBox<T> combo) {
        combo.setCellFactory(p -> new NameableListCell<>());
        combo.setButtonCell(new NameableListCell<>());
    }

    @Override
    protected void updateItem(final T item, final boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setText("");
        } else {
            setText(item.getName());
        }
    }
}
