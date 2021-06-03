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

import static fr.inrae.agroclim.getari.util.GetariConstants.GRAPH_NODE;

import fr.inrae.agroclim.getari.component.GetariApp;
import fr.inrae.agroclim.getari.resources.Messages;
import fr.inrae.agroclim.getari.util.AlertUtils;
import fr.inrae.agroclim.getari.view.graph.GraphElement;
import fr.inrae.agroclim.indicators.model.indicator.CompositeIndicator;
import fr.inrae.agroclim.indicators.model.indicator.Indicator;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import lombok.extern.log4j.Log4j2;

/**
 * Contrôleur de l'évènement de suppression d'un indicateur au sein du graphe.
 *
 * Last change $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
@Log4j2
public final class MouseDeleteController implements EventHandler<MouseEvent> {

    @Override
    public void handle(final MouseEvent event) {
        final Node node = (Node) event.getSource();
        final GraphElement graphElement = (GraphElement) node.getProperties().get(
                GRAPH_NODE);
        try {
            final Indicator indicator = graphElement.getIndicator();
            CompositeIndicator composite;
            composite = (CompositeIndicator) indicator.getParent();
            GetariApp.logAppend(getClass(), null,
                    String.format("remove indicator %s from %s",
                            indicator.getId(), composite.getId()));
            composite.remove(indicator);
            GetariApp.get().getCurrentEval().remove(indicator);
        } catch (final Exception e) {
            AlertUtils.showError(Messages.getString(
                    "evaluation.error.indicator.deletion"));
            LOGGER.catching(e);
        }
    }
}
