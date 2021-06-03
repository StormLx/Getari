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
import static fr.inrae.agroclim.getari.util.GetariConstants.INDICATOR;

import java.util.Locale;

import fr.inrae.agroclim.getari.controller.AddIndicatorHandler;
import fr.inrae.agroclim.getari.controller.IndicatorDetailsHandler;
import fr.inrae.agroclim.getari.util.ComponentUtil;
import fr.inrae.agroclim.getari.util.DrawingTool;
import fr.inrae.agroclim.indicators.model.indicator.Indicator;
import fr.inrae.agroclim.indicators.model.indicator.listener.IndicatorEvent;
import fr.inrae.agroclim.indicators.model.indicator.listener.IndicatorListener;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import lombok.extern.log4j.Log4j2;

/**
 * Noeud graphique représentant un indicateur.
 *
 * Last change $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
@Log4j2
public class GraphNode extends GraphElement implements IndicatorListener {

    /**
     * The container of the node representation.
     */
    private StackPane stackPane;
    /**
     * The arrow between this element and the parent.
     */
    private Path arrow;
    /**
     * Name and id of indicator.
     */
    private Text nodeText;
    /**
     * Rendering container of element.
     */
    private VBox childBox;
    /**
     * Rectangle of the node.
     */
    private Group group;
    /**
     * Tooltip with indicator id and description.
     */
    private final Tooltip tooltip = new Tooltip();

    /**
     * Constructor.
     *
     * @param indicator indicator to display
     * @param detailsHandler Handle clicking indicator to show details.
     * @param addHandler Handles adding indicator.
     */
    public GraphNode(final Indicator indicator,
            final IndicatorDetailsHandler detailsHandler,
            final AddIndicatorHandler addHandler) {
        super(indicator, detailsHandler, addHandler);
        final String labelText = getIndicator().getName() + ", Id : " + getId();
        LOGGER.trace("New GraphNode for " + labelText);
    }

    @Override
    public final VBox getBox() {
        return childBox;
    }

    /**
     * @param p pane with children
     * @param parent graph element with children
     * @return maximum index of graph element children in pane children
     */
    private int getIndexOfLastChild(final Pane p, final GraphElement parent) {
        int max = 0;
        for (final GraphElement n : parent.getChildren()) {
            max = Math.max(p.getChildren().indexOf(n.getStackPane()), max);
        }
        return max;
    }

    /**
     * @param p pane with children
     * @param element element to get index
     * @return index of graph element in the pane children
     */
    private int getPositionInSiblings(final Pane p,
            final GraphElement element) {
        int numberOfElements = 0;
        GraphElement brother;
        if (p.getChildren().isEmpty()) {
            return numberOfElements;
        }
        for (final Node node : p.getChildren()) {
            StackPane pane;
            if (node instanceof StackPane) {
                pane = (StackPane) node;
                brother = (GraphElement) pane.getProperties().get(
                        GRAPH_NODE);
                /*
                 * Si le frère a des enfants et si le frère est plus jeune
                 * que l'élément courant
                 */
                if (!brother.getChildren().isEmpty()
                        && p.getChildren().indexOf(element.getStackPane())
                        > p.getChildren().indexOf(pane)) {
                    numberOfElements += brother.getChildren().size();
                }
            } else {
                pane = (StackPane) ((Pane) node).getChildren().get(1);
                brother = (GraphElement) pane.getProperties().get(
                        GRAPH_NODE);
                /*
                 * Si le frère a des enfants et si le frère est plus jeune
                 * que l'élément courant
                 */
                if (!brother.getChildren().isEmpty()
                        && p.getChildren().indexOf(element.getRoot()) > p
                        .getChildren().indexOf(node)) {
                    numberOfElements += brother.getChildren().size();
                }
            }
        }
        return numberOfElements;
    }

    @Override
    public final VBox getRoot() {
        return null;
    }

    @Override
    public final StackPane getStackPane() {
        return stackPane;
    }

    /**
     * @return text for tooltip with indicator id and description
     */
    private String getTooltipText() {
        String text = getIndicator().getName()
                + "\nId : " + getIndicator().getId();
        final String description = getIndicator().getPrettyDescription(
                Locale.getDefault().getLanguage());
        if (description != null) {
            text += "\n" + description;
        }
        return text;
    }

    /**
     * @param pane pane in which the node is rendered
     * @param parent graph element
     * @return true if graph element is already added
     */
    private boolean isAlreadyAdded(final Pane pane, final GraphElement parent) {
        return ((Pane) pane.getParent().getParent().getParent()).getChildren()
                .contains(parent.getAggregate());
    }

    @Override
    public final void lightOff() {
        setSelected(false);
        if (getIndicator().isComputable()) {
            nodeText.getStyleClass().remove(LIGHTON_CLASS);
            nodeText.getStyleClass().add(LIGHTOFF_CLASS);
            getStackPane().getStyleClass().add("indicator");
            getStackPane().getStyleClass().remove("indicator-selected");
            final GraphElement parent = getGraph().getGraphElement(getParent());
            if (parent != null) {
                DrawingTool.restoreColor(getRectangle(), arrow,
                        parent.getAggregate(), getColor());
            }
        }
    }

    @Override
    public final void lightOn() {
        setSelected(true);
        if (getIndicator().isComputable()) {
            final GraphElement parent = getGraph().getGraphElement(getParent());
            DrawingTool.inverseColor(getRectangle(), arrow,
                    parent.getAggregate(), getColor());
            nodeText.getStyleClass().remove("indicator-text");
            nodeText.getStyleClass().add(LIGHTON_CLASS);
            getStackPane().getStyleClass().remove("indicator");
            getStackPane().getStyleClass().add("indicator-selected");
        }
    }

    @Override
    public final void onIndicatorEvent(final IndicatorEvent e) {
        if (e.getAssociatedType() == IndicatorEvent.Type.UPDATED_VALUE) {
            final Indicator i = e.getSource();
            setColor(Color.web(i.getColor()));
            final GraphElement parent = getGraph().getGraphElement(getParent());
            DrawingTool.updateColor(group, arrow, parent.getAggregate(),
                    getColor());
            return;
        }
        if (e.getAssociatedType() == IndicatorEvent.Type.CHANGE) {
            tooltip.setText(getTooltipText());
        }
    }

    @Override
    public final void remove() {
        getChildren().forEach(GraphElement::remove);
        if (stackPane.getParent() == null) {
            LOGGER.fatal("Strange, "
                    + "stackPane of GraphNode is null!");
        } else {
            ((Pane) stackPane.getParent()).getChildren().remove(stackPane);
        }
        if ((Pane) arrow.getParent() == null) {
            LOGGER.fatal("Strange, "
                    + "arrow of GraphNode is null!");
        } else {
            ((Pane) arrow.getParent()).getChildren().remove(arrow);
        }
        if (getAggregate().getParent() != null) {
            // Si l'aggrégat est dessiné, alors on le supprime de la liste des
            // fils de son père
            ((Pane) getAggregate().getParent()).getChildren().remove(
                    getAggregate());
        }
        // Suppression de l'aggrégat du parent si un seul fils
        final GraphElement parent = getGraph().getGraphElement(getParent());
        if (parent.getChildren().size() == 1
                && parent.getAggregate().getParent() != null) {
            // Si l'aggrégat est dessiné
            ((Pane) parent.getAggregate().getParent()).getChildren()
            .remove(parent.getAggregate());
        }
    }

    @Override
    public final void render(final VBox pane) {
        LOGGER.trace("start");
        childBox = pane;

        getIndicator().setColor(ComponentUtil.toRGBCode(getColor()));

        // Définition de l'aggrégat
        final StackPane aggregate = DrawingTool.newAggregate(getColor(),
                isAggregated());
        setAggregate(aggregate);


        group = DrawingTool.newNodeShape(getColor(), this,
                getIndicator().isComputable());
        final Rectangle rec = (Rectangle) group.getChildren().get(0);

        final int charLimit = 40;
        String text = getIndicator().getName() + ",\n Id : " + getId();
        if (text.length() > charLimit) {
            text = text.substring(0, charLimit).concat("...");
        }
        nodeText = new Text(text);
        nodeText.getStyleClass().add("indicator-text");

        nodeText.setWrappingWidth(rec.getWidth() - 10);

        // Besoin pour centrer les multi-ligne en cas setWrapText(true)
        nodeText.setTextAlignment(TextAlignment.CENTER);

        Tooltip.install(nodeText, tooltip);
        tooltip.setText(getTooltipText());
        tooltip.setWrapText(true);
        tooltip.maxWidthProperty().bind(rec.widthProperty().multiply(2));

        setRectangle(rec);
        stackPane = new StackPane();

        stackPane.getProperties().put(INDICATOR, getIndicator());
        stackPane.getProperties().put(GRAPH_NODE, this);

        getRectangle().getProperties().put(INDICATOR, getIndicator());
        getRectangle().getProperties().put(GRAPH_NODE, this);

        nodeText.getProperties().put(INDICATOR, getIndicator());
        nodeText.getProperties().put(GRAPH_NODE, this);

        stackPane.getChildren().add(group);
        stackPane.getChildren().add(nodeText);
        nodeText.setTranslateX(5);
        nodeText.setTranslateY(5);

        final DropShadow dropShadow = new DropShadow();
        dropShadow.setOffsetY(5.0);
        dropShadow.setOffsetX(5.0);
        dropShadow.setColor(Color.GRAY);
        stackPane.setEffect(dropShadow);

        ComponentUtil.setCursorHoverStyleProperty(stackPane);

        stackPane.addEventHandler(MouseEvent.MOUSE_CLICKED,
                getDetailsHandler());

        setLightOnMouseClicked(stackPane);

        final GraphElement parent = getGraph().getGraphElement(getParent());

        if (parent != null) {
            if (parent.getChildren().size() > 1) {
                // get the max sibling position
                final int position = getIndexOfLastChild(pane, parent);
                pane.getChildren().add(position + 1, stackPane);
            } else {
                pane.getChildren().add(
                        getPositionInSiblings(parent.getBox(), parent),
                        stackPane);
            }
            arrow = DrawingTool.drawArrowBetween((Pane) pane.getParent()
                    .getParent(), parent, this, getColor());

            if (parent.getChildren().size() > 1
                    && !isAlreadyAdded(pane, parent)) {
                DrawingTool.drawAggregate((Pane) pane.getParent().getParent(),
                        parent);
            }
        } else {
            // case of phenologic phases
            pane.getChildren().add(stackPane);
        }
    }
}
