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

import java.io.IOException;
import java.util.List;

import fr.inrae.agroclim.getari.component.GetariApp;
import fr.inrae.agroclim.getari.resources.Messages;
import fr.inrae.agroclim.getari.util.ComponentUtil;
import fr.inrae.agroclim.getari.util.GetariConstants;
import fr.inrae.agroclim.getari.util.MenuUtils;
import fr.inrae.agroclim.getari.view.CreatePhaseView;
import fr.inrae.agroclim.getari.view.GraphView;
import fr.inrae.agroclim.getari.view.graph.GraphElement;
import fr.inrae.agroclim.indicators.model.data.ResourceManager;
import fr.inrae.agroclim.indicators.model.data.phenology.PhenologicalResource;
import fr.inrae.agroclim.indicators.model.indicator.CompositeIndicator;
import fr.inrae.agroclim.indicators.model.indicator.Indicator;
import fr.inrae.agroclim.indicators.model.indicator.IndicatorCategory;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import lombok.extern.log4j.Log4j2;

/**
 * Contrôleur d'affichage d'un menu contextuel par clic droit sur un noeud ou
 * sur l'arrière-plan du graphe.
 *
 * Last change $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
@Log4j2
public final class NodeRightClickHandler implements EventHandler<MouseEvent> {

    /**
     * Contextual menu.
     */
    private ContextMenu contextMenu = new ContextMenu();

    /**
     * Graph view.
     */
    private final GraphView view;

    /**
     * Constructor.
     *
     * @param v Graph view
     */
    public NodeRightClickHandler(final GraphView v) {
        view = v;
    }

    @Override
    public void handle(final MouseEvent event) {
        contextMenu.hide();
        contextMenu = new ContextMenu();
        if (!event.getButton().toString().equals("SECONDARY")) {
            return;
        }

        ComponentUtil.setCursorShowingStyleProperty(contextMenu);

        final Node source = (Node) event.getSource();
        final Node target = (Node) event.getTarget();

        if (target instanceof Rectangle || target instanceof Text) {
            final GraphElement graphNode = (GraphElement) target.getProperties()
                    .get(GetariConstants.GRAPH_NODE);
            final Indicator indicator = graphNode.getIndicator();
            final MenuItem deleteItem = new MenuItem(
                    Messages.getString("action.delete"));
            deleteItem.setOnAction(e -> onDelete(indicator));

            contextMenu.getItems().add(deleteItem);
            if (indicator.getIndicatorCategory()
                    == IndicatorCategory.PHENO_PHASES) {
                final MenuItem duplicateItem = new MenuItem(
                        Messages.getString("action.duplicate"));
                duplicateItem.setOnAction(evt
                        -> onDuplicate((CompositeIndicator) indicator));
                contextMenu.getItems().add(duplicateItem);
            }

            MenuUtils.buildMenu(contextMenu, graphNode);
            contextMenu.show(target, event.getScreenX(), event.getScreenY());
        } else {
            final ResourceManager mgr = GetariApp.get().getCurrentEval()
                    .getResourceManager();
            final PhenologicalResource phenoResource = mgr
                    .getPhenologicalResource();

            final List<String> stages = phenoResource.getStages();
            for (final String stage : stages) {
                if (stages.indexOf(stage) == stages.size() - 1) {
                    break;
                }

                final Menu menu = new Menu(
                        Messages.getString("graph.menu.add.phase.from",
                                stage));

                final List<Indicator> myPhases = phenoResource
                        .getPhasesByStage(stage);

                for (final Indicator phase : myPhases) {
                    final MenuItem item = new MenuItem(
                            Messages.getString("graph.menu.add.phase.to",
                                    phase.getName()));

                    item.addEventHandler(ActionEvent.ACTION,
                            new AddIndicatorHandler(view));
                    item.setId(GetariConstants.MENU
                            + IndicatorCategory.PHENO_PHASES.getTag());
                    item.getProperties().put(INDICATOR, phase);
                    item.getProperties().put(
                            GetariConstants.PARENT_INDICATOR,
                            GetariApp.get().getCurrentEval());
                    menu.getItems().add(item);
                }
                contextMenu.getItems().add(menu);
            }
            contextMenu
            .show(source, event.getScreenX(), event.getScreenY());
        }
    }

    /**
     * On click on "Delete" item.
     *
     * @param indicator indicator to delete
     */
    private void onDelete(final Indicator indicator) {
        if (indicator == null) {
            return;
        }
        CompositeIndicator composite;
        composite = (CompositeIndicator) indicator.getParent();
        GetariApp.logAppend(getClass(), null,
                String.format("remove indicator %s from %s",
                        indicator.getId(), composite.getId()));
        composite.remove(indicator);
        GetariApp.get().getCurrentEval().remove(indicator);
    }

    /**
     * On click on "Duplicate" item, show a dialog to create a new phase.
     *
     * @param phase original phase to duplicate
     */
    private void onDuplicate(final CompositeIndicator phase) {
        LOGGER.trace("duplicate : {}", phase.getId());
        try {
            final CreatePhaseView v = new CreatePhaseView();
            v.build(view.getStage());
            v.getController().setOnAction(new DuplicatePhaseHandler(phase));
        } catch (final IOException e) {
            LOGGER.fatal("Error while loading license view.", e);
        }
    }
}
