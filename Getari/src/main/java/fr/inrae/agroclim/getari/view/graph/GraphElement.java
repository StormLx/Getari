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
package fr.inrae.agroclim.getari.view.graph;

import static fr.inrae.agroclim.getari.util.GetariConstants.GRAPH_NODE;

import java.util.ArrayList;
import java.util.List;

import fr.inrae.agroclim.getari.controller.AddIndicatorHandler;
import fr.inrae.agroclim.getari.controller.IndicatorDetailsHandler;
import fr.inrae.agroclim.getari.view.visitor.GraphVisitor;
import fr.inrae.agroclim.indicators.model.EvaluationType;
import fr.inrae.agroclim.indicators.model.indicator.CompositeIndicator;
import fr.inrae.agroclim.indicators.model.indicator.Indicator;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Abstract class for representation of phase or indicator in the graph.
 *
 * Last change $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
@ToString
public abstract class GraphElement {
    /**
     * Default class for node.
     */
    protected static final String LIGHTOFF_CLASS = "indicator-text";
    /**
     * Class for selected node.
     */
    protected static final String LIGHTON_CLASS = "indicator-text-selected";
    /**
     * Light on graph node in stack pane when node is clicked.
     *
     * @param stackPane stack pane containing nodes
     */
    protected static void setLightOnMouseClicked(final StackPane stackPane) {
        stackPane.addEventHandler(MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>() {
            @Override
            public void handle(final MouseEvent event) {
                final Node n = (Node) event.getSource();
                final GraphElement graphNode = (GraphElement) n
                        .getProperties().get(GRAPH_NODE);

                // Left clic
                if (event.getButton().toString().equals("PRIMARY")) {

                    if (graphNode.getGraph().getSelectedNode() == null) {
                        graphNode.lightOn();
                    } else if (graphNode.getGraph().getSelectedNode()
                            .equals(graphNode)) {
                        handleLight(graphNode);
                    } else {
                        graphNode.getGraph().getSelectedNode()
                        .lightOff();
                        graphNode.lightOn();
                    }
                    graphNode.getGraph().setSelectedNode(graphNode);
                }
            }

            private void handleLight(final GraphElement n) {
                if (n.isSelected()) {
                    n.lightOff();
                } else {
                    n.lightOn();
                }
            }
        });
    }

    /**
     * Visitor of the element.
     */
    @Getter
    @Setter
    private GraphVisitor graph;

    /**
     * Displayed indicator.
     */
    @Getter
    private final Indicator indicator;

    /**
     * Rectangle of the element.
     */
    @Getter
    @Setter
    private Rectangle rectangle;

    /**
     * X position.
     */
    @Getter
    @Setter
    private int x;

    /**
     * Y position.
     */
    @Getter
    @Setter
    private int y;

    /**
     * Aggregation representation.
     */
    @Getter
    @Setter
    private StackPane aggregate;

    /**
     * If evaluation has aggregation.
     */
    @Getter(AccessLevel.PROTECTED)
    private final boolean aggregated;

    /**
     * Handle clicking indicator to show details.
     */
    @Getter
    private final IndicatorDetailsHandler detailsHandler;

    /**
     * Handle clicking on Add.
     */
    @Getter
    private final AddIndicatorHandler viewController;

    /**
     * Node color.
     */
    @Getter
    @Setter
    private Color color;

    /**
     * If element is selected.
     */
    @Getter
    @Setter
    private boolean selected = false;

    /**
     * GraphElement children.
     */
    @Getter
    private final List<GraphElement> children = new ArrayList<>();

    /**
     * Constructor.
     *
     * @param ind indicator to display
     * @param indicatorDetailsHandler Handle clicking indicator to show details.
     * @param addIndicatorHandler Handle adding indicator.
     */
    protected GraphElement(final Indicator ind,
            final IndicatorDetailsHandler indicatorDetailsHandler,
            final AddIndicatorHandler addIndicatorHandler) {
        this.indicator = ind;
        this.detailsHandler = indicatorDetailsHandler;
        this.viewController = addIndicatorHandler;
        final Indicator parent = ind.getParent();
        if (parent instanceof CompositeIndicator) {
            final CompositeIndicator composite = (CompositeIndicator) parent;
            this.aggregated = composite.getType()
                    != EvaluationType.WITHOUT_AGGREGATION;
        } else {
            this.aggregated = false;
        }

    }

    /**
     * Add graph element to the element children.
     *
     * @param c element to add
     */
    public final void add(final GraphElement c) {
        children.add(c);
    }

    /**
     * @return rendering container of element.
     */
    public abstract VBox getBox();

    /**
     * @return ID of indicator
     */
    public final String getId() {
        return indicator.getId();
    }

    /**
     * @return Parent indicator (composite or phase or evaluation).
     */
    public final Indicator getParent() {
        return indicator.getParent();
    }

    /**
     * @return container of element.
     */
    public abstract VBox getRoot();

    /**
     * @return container of the node representation.
     */
    public abstract StackPane getStackPane();

    /**
     * Remove highlight the element on focus.
     */
    public abstract void lightOff();

    /**
     * Highlight the element on focus.
     */
    public abstract void lightOn();

    /**
     * Remove element from graph.
     */
    public abstract void remove();

    /**
     * Render element in pane.
     *
     * @param pane pane to render into
     */
    public abstract void render(VBox pane);

}
