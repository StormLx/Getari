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

import static fr.inrae.agroclim.getari.util.GetariConstants.INDICATOR_BOX_MAX_SIZE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.inrae.agroclim.getari.controller.AddIndicatorHandler;
import fr.inrae.agroclim.getari.controller.IndicatorDetailsHandler;
import fr.inrae.agroclim.getari.view.graph.GraphElement;
import fr.inrae.agroclim.getari.view.graph.GraphNode;
import fr.inrae.agroclim.getari.view.graph.GraphRoot;
import fr.inrae.agroclim.indicators.model.Evaluation;
import fr.inrae.agroclim.indicators.model.indicator.Indicator;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

/**
 * Visitor for the graph editor of an evaluation.
 *
 * Adding and suppressions of nodes (GraphNode et GraphRoot).
 *
 * Last change $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
@Log4j2
public class GraphVisitor {
    /**
     * Selected node.
     */
    @Getter
    @Setter
    private GraphElement selectedNode;
    /**
     * Graph elements on root.
     */
    private final List<GraphElement> rootElements = new ArrayList<>();
    /**
     * XML path of indicator => graph element.
     */
    private final Map<String, GraphElement> graphElementsByIndicator;
    /**
     * XML path of indicator => Box of graph element.
     */
    private final Map<String, VBox> cellByIndicator;
    /**
     * Pane containing the representation of indicators and arrows and
     * aggregations.
     */
    private final GridPane gridPane;
    /**
     * Handler to build detailed pannel.
     */
    private final IndicatorDetailsHandler controller;
    /**
     * Handler to add an indicator.
     */
    private final AddIndicatorHandler viewController;
    /**
     * "X-Y" => Box of graph element.
     */
    private final Map<String, VBox> boxByPosition = new HashMap<>();

    /**
     * Constructor.
     *
     * @param g Pane containing the represenation of indicators and arrows and
     * aggregations.
     * @param h Handler to build detailed pannel.
     * @param a Handler to add an indicator.
     */
    public GraphVisitor(final GridPane g,
            final IndicatorDetailsHandler h,
            final AddIndicatorHandler a) {
        this.graphElementsByIndicator = new HashMap<>();
        this.cellByIndicator = new HashMap<>();
        this.gridPane = g;
        this.controller = h;
        this.selectedNode = null;
        this.viewController = a;
    }

    /**
     * @param indicator indicator to add in the graph
     */
    public final void addGraphElement(final Indicator indicator) {
        if (indicator == null) {
            LOGGER.trace("Strange, indicator is null!");
            return;
        }
        if (indicator instanceof Evaluation) {
            return;
        }
        GraphElement parentElement;
        GraphElement newElement;
        VBox childBox;
        parentElement = getGraphElement(indicator.getParent());

        if (parentElement == null) {
            newElement = new GraphRoot(indicator, controller, viewController);
            newElement.setX(0);
            newElement.setY(rootElements.size() + 1);
            ((GraphRoot) newElement).makeColor();
            rootElements.add(newElement);
        } else {
            newElement = new GraphNode(indicator, controller, viewController);
            indicator.addIndicatorListener((GraphNode) newElement);
            parentElement.add(newElement);
            newElement.setX(parentElement.getX() + 2);
            newElement.setY(parentElement.getY());
            newElement.setColor(parentElement.getColor());
        }

        // add the new element
        final String key = newElement.getX() + "-" + newElement.getY();

        if (boxByPosition.containsKey(key)) {
            // retrieve the child box from the parent
            childBox = boxByPosition.get(key);
        } else {
            // build a new child child box and use it for rendering
            childBox = newVBox();
            boxByPosition.put(key, childBox);
            gridPane.add(childBox, newElement.getX(), newElement.getY());
        }

        newElement.setGraph(this);
        graphElementsByIndicator.put(indicator.getPath(), newElement);
        cellByIndicator.put(indicator.getPath(), childBox);
        newElement.render(childBox);
    }

    /**
     * Remove graph elements from graph.
     */
    public final void clearGraph() {
        LOGGER.traceEntry();
        boxByPosition.clear();
        cellByIndicator.clear();
        graphElementsByIndicator.clear();
        gridPane.getChildren().clear();
    }

    /**
     * @param indicator indicator to search box.
     * @return Box of graph element for the indicator.
     */
    public final VBox getCell(final Indicator indicator) {
        return cellByIndicator.get(indicator.getPath());
    }

    /**
     * @param indicator indicator to search graph element.
     * @return Graph element for the indicator.
     */
    public final GraphElement getGraphElement(final Indicator indicator) {
        return graphElementsByIndicator.get(indicator.getPath());
    }

    /**
     * @return box used to represent an indicator
     */
    private VBox newVBox() {
        final int spacing = 40;
        final int padding = 70;
        final VBox childBox = new VBox(spacing);
        childBox.setAlignment(Pos.CENTER);
        childBox.setMaxWidth(INDICATOR_BOX_MAX_SIZE);
        childBox.setMinHeight(INDICATOR_BOX_MAX_SIZE);
        childBox.getStyleClass().add("graph-column-border");
        childBox.setPadding(new Insets(padding));
        return childBox;
    }

    /**
     * @param indicator indicator to remove
     */
    public final void removeGraphElement(final Indicator indicator) {
        final GraphElement element = getGraphElement(indicator);
        final GraphElement parent = getGraphElement(indicator.getParent());

        final String key = element.getX() + "-" + element.getY();

        if (parent == null) {
            /* Suppression de la phase phénologique */
            gridPane.getChildren().remove(boxByPosition.get(key));
            boxByPosition.remove(key);
        }

        if (parent != null) {
            parent.getChildren().remove(element);
        }
        element.remove();
    }
}
