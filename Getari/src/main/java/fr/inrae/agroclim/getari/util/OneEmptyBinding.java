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

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 * Used by SelectDataController.
 */
public final class OneEmptyBinding extends BooleanBinding {

    /**
     * The list related to this binding.
     */
    private final ObservableList<BooleanProperty> boundList;

    /**
     * Listener for the Change event on the list.
     */
    private final ListChangeListener<BooleanProperty> listChangeListener = change -> refreshBinding();

    /**
     * Properties of the list.
     */
    private BooleanProperty[] observedProperties = {};

    /**
     * Constructor.
     *
     * @param booleanList The list related to this binding.
     */
    public OneEmptyBinding(final ObservableList<BooleanProperty> booleanList) {
        booleanList.addListener(listChangeListener);
        boundList = booleanList;
        refreshBinding();
    }

    @Override
    protected boolean computeValue() {
        for (final BooleanProperty bp : observedProperties) {
            if (bp.get()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void dispose() {
        boundList.removeListener(listChangeListener);
        super.dispose();
    }

    /**
     * Refresh observedProperties, Properties of the list.
     */
    private void refreshBinding() {
        super.unbind(observedProperties);
        observedProperties = boundList.toArray(new BooleanProperty[boundList
                                                                   .size()]);
        super.bind(observedProperties);
        this.invalidate();
    }
}
