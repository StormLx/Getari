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
package fr.inrae.agroclim.getari.controller;

import static fr.inrae.agroclim.getari.util.GetariConstants.INDICATOR;

import fr.inrae.agroclim.getari.view.GraphView;
import fr.inrae.agroclim.indicators.model.indicator.Detailable;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/**
 * Contrôleur d'affichage du détail d'un indicateur dans le panneau de détail.
 *
 * L'évènement est déclenché par le clic de la souris sur les objets Rectangle
 * ou Text des objects GraphNode ou GraphRoot.
 *
 * @see GraphView
 *
 * Last change $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
public final class IndicatorDetailsHandler implements EventHandler<MouseEvent> {

    /**
     * Graph view.
     */
    private final GraphView view;

    /**
     * Constructor.
     *
     * @param v graph view
     */
    public IndicatorDetailsHandler(final GraphView v) {
        view = v;
    }

    @Override
    public void handle(final MouseEvent event) {
        final Node node = (Node) event.getSource();

        final Node target = (Node) event.getTarget();
        if (target instanceof Rectangle || target instanceof Text) {
            /*
             * Affichage du panneau de détail si on clique sur le rectangle ou
             * sur le label du noeud et non sur une image de type "close" ou
             * "onIndicatorAdd"
             */
            final Detailable detailableElement = (Detailable) node.getProperties()
                    .get(INDICATOR);
            view.buildDetailedPanel(detailableElement);
        }
    }
}
