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

import fr.inrae.agroclim.getari.component.GetariApp;
import fr.inrae.agroclim.getari.util.ComponentUtil;
import fr.inrae.agroclim.getari.util.DrawingTool;
import fr.inrae.agroclim.getari.util.GetariConstants;
import fr.inrae.agroclim.getari.util.MenuUtils;
import fr.inrae.agroclim.getari.view.graph.GraphElement;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.MouseEvent;

/**
 * Contrôleur de l'évènement d'affichage d'un menu contextuel pour l'ajout
 * d'indicateurs au sein du graphe.
 *
 * L'évènement est déclenché par un clic gauche sur une image d'ajout.
 *
 * @see DrawingTool
 *
 * Last change $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
public final class MouseAddMenuController implements EventHandler<MouseEvent> {

    /**
     * Contextual menu.
     */
    private static final ContextMenu MENU = new ContextMenu();

    @Override
    public void handle(final MouseEvent event) {
        MENU.hide();
        MENU.getItems().clear();

        ComponentUtil.setCursorShowingStyleProperty(MENU);

        final Node node = (Node) event.getSource();

        final GraphElement graphNode = (GraphElement) node.getProperties().get(
                GetariConstants.GRAPH_NODE);
        GetariApp.logAppend(getClass(), null, graphNode.getId());

        MenuUtils.buildMenu(MENU, graphNode);
        MENU.show(node, event.getScreenX(), event.getScreenY());
    }
}
