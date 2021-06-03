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
package fr.inrae.agroclim.getari.view.visitor;

import static fr.inrae.agroclim.getari.util.ComponentUtil.initGridPane;

import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import lombok.AccessLevel;
import lombok.Getter;

/**
 * Base class for detailed view visitors.
 *
 * @author jcufi
 */
public abstract class DetailedViewVisitor {

    /**
     * GridPane to display details.
     */
    @Getter(AccessLevel.PROTECTED)
    private final GridPane gridPane;

    /**
     * Counter for GridPane building.
     */
    @Getter(AccessLevel.PROTECTED)
    private final Cpt cpt;

    /**
     * Constructor.
     */
    protected DetailedViewVisitor() {
        this(new GridPane(), new Cpt());
        initGridPane(gridPane);
    }

    /**
     * Constructor.
     *
     * @param g GridPane to display details.
     * @param c Counter for GridPane building.
     */
    protected DetailedViewVisitor(final GridPane g, final Cpt c) {
        gridPane = g;
        cpt = c;
    }

    /**
     * Add a separator in the grid pane.
     */
    protected final void addSeparator() {
        final Separator separator = new Separator(Orientation.HORIZONTAL);
        separator.setValignment(VPos.TOP);
        GridPane.setConstraints(separator, 0, getCpt().nextRow());
        GridPane.setColumnSpan(separator, 2);
        gridPane.getChildren().add(separator);
        getCpt().nextRow();
    }

}
