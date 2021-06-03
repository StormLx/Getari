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

import fr.inrae.agroclim.getari.controller.AddIndicatorHandler;
import fr.inrae.agroclim.getari.controller.IndicatorDetailsHandler;
import fr.inrae.agroclim.getari.resources.Messages;
import fr.inrae.agroclim.getari.util.AlertUtils;
import fr.inrae.agroclim.getari.util.ComponentUtil;
import fr.inrae.agroclim.getari.util.DrawingTool;
import fr.inrae.agroclim.getari.util.GetariConstants;
import fr.inrae.agroclim.indicators.model.indicator.CompositeIndicator;
import fr.inrae.agroclim.indicators.model.indicator.Indicator;
import fr.inrae.agroclim.indicators.model.indicator.listener.IndicatorEvent;
import fr.inrae.agroclim.indicators.model.indicator.listener.IndicatorListener;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

/**
 * Noeud graphique représentant une phase phénologique.
 *
 * Last change $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
@Log4j2
public class GraphRoot extends GraphElement implements IndicatorListener {

    /**
     * The container of the node representation.
     */
    private StackPane stackPane;
    /**
     * Label for the phase.
     */
    private Label firstStageLabel;
    /**
     * Text for the phase.
     */
    private final String labelText;
    /**
     * Pane containing all phases.
     */
    private VBox childBox;
    /**
     * The container of label and node representation.
     */
    private VBox root;
    /**
     * Label for the phase.
     */
    private Label secondStageLabel;
    /**
     * Rectangle in the stackPane.
     */
    private Group shape;

    /**
     * Constructor.
     *
     * @param indicator phase
     * @param detailsHandler Handle clicking indicator to show details.
     * @param addHandler Handle adding indicator.
     */
    public GraphRoot(@NonNull final Indicator indicator,
            final IndicatorDetailsHandler detailsHandler,
            final AddIndicatorHandler addHandler) {
        super(indicator, detailsHandler, addHandler);
        if (!(indicator instanceof CompositeIndicator)) {
            throw new IllegalArgumentException(
                    "Indicator for GraphRoot must be a CompositeIndicator: "
                            + indicator.getId()
                    );
        }
        labelText = getIndicator().getName() + ", Id : " + getId();
        LOGGER.trace("New GraphRoot for " + labelText);
        indicator.addIndicatorListener(this);
    }

    /**
     * Pick a new color for the phase.
     *
     * @return new color for the phase
     */
    private Color createColor() {
        Color newColor = null;
        try {
            newColor = DrawingTool.nextColor();
        } catch (final Exception e) {
            AlertUtils.showException(e.getMessage(), e);
            LOGGER.error(e);
        }
        return newColor;
    }

    @Override
    public final VBox getBox() {
        return childBox;
    }

    @Override
    public final VBox getRoot() {
        return root;
    }

    @Override
    public final StackPane getStackPane() {
        return stackPane;
    }

    @Override
    public final void lightOff() {
        setSelected(false);
        firstStageLabel.getStyleClass().remove(LIGHTON_CLASS);
        firstStageLabel.getStyleClass().add(LIGHTOFF_CLASS);
        secondStageLabel.getStyleClass().remove(LIGHTON_CLASS);
        secondStageLabel.getStyleClass().add(LIGHTOFF_CLASS);
        DrawingTool.restoreColor(shape, getColor());
    }

    @Override
    public final void lightOn() {
        setSelected(true);
        DrawingTool.inverseColor(shape, getColor());
        firstStageLabel.getStyleClass().remove(LIGHTOFF_CLASS);
        firstStageLabel.getStyleClass().add(LIGHTON_CLASS);
        secondStageLabel.getStyleClass().remove(LIGHTOFF_CLASS);
        secondStageLabel.getStyleClass().add(LIGHTON_CLASS);
    }

    /**
     * Define color of graph element.
     */
    public final void makeColor() {
        if (getIndicator().getColor() == null) {
            setColor(createColor());

        } else {
            setColor(Color.web(getIndicator().getColor()));
        }
    }

    @Override
    public final void onIndicatorEvent(final IndicatorEvent e) {
        if (e.getAssociatedType() == IndicatorEvent.Type.UPDATED_VALUE) {
            final Indicator i = e.getSource();
            if (i.getColor() == null) {
                i.setColor("#5F9EA0");
            }
            setColor(Color.web(i.getColor()));
            DrawingTool.updateColor(shape, getColor());
        }
    }

    @Override
    public final void remove() {
        getChildren().forEach(GraphElement::remove);
        final Pane pane = (Pane) stackPane.getParent();
        if (pane != null) {
            // in case of Phase
            pane.getChildren().remove(stackPane);
        }
        if (getAggregate().getParent() != null) {
            // Si l'aggrégat est dessiné, alors on le supprime de la liste des
            // fils de son père
            ((Pane) getAggregate().getParent()).getChildren().remove(
                    getAggregate());
        }
    }

    @Override
    public final void render(final VBox pane) {
        LOGGER.trace("start");
        root = new VBox(10);

        childBox = pane;
        final Tooltip idtoolTip = new Tooltip(labelText);
        shape = DrawingTool.newRootShape(getColor(), this);

        getIndicator().setColor(ComponentUtil.toRGBCode(getColor()));

        // Définition de l'aggrégat
        final StackPane aggregate = DrawingTool.newAggregate(getColor(),
                isAggregated());
        setAggregate(aggregate);

        final String secondStageText = Messages.getString("graph.stage",
                getIndicator().getName());
        final CompositeIndicator compositeIndicator;
        compositeIndicator = (CompositeIndicator) getIndicator();
        if (compositeIndicator.getIndicators() == null) {
            throw new RuntimeException(
                    "Indicator list of composite indicator is null!");
        }
        if (compositeIndicator.getIndicators().isEmpty()) {
            final String msg = String.format(
                    "Indicator list of composite indicator \"%s\" is empty!",
                    compositeIndicator.getName());
            LOGGER.warn(msg);
            return;
        }
        final Indicator indicator = compositeIndicator.getIndicators().iterator()
                .next();
        final String firstStageText = Messages.getString("graph.stage",
                indicator.getName());

        final String phaseId = "Id : " + getIndicator().getId();

        firstStageLabel = new Label(firstStageText);
        secondStageLabel = new Label(secondStageText);
        final Label phaseIdLabel = new Label(phaseId);
        firstStageLabel.getStyleClass().add(LIGHTOFF_CLASS);
        secondStageLabel.getStyleClass().add(LIGHTOFF_CLASS);
        phaseIdLabel.getStyleClass().add(LIGHTOFF_CLASS);
        firstStageLabel.setMinWidth(GetariConstants.REC_WIDTH);
        secondStageLabel.setMinWidth(GetariConstants.REC_WIDTH);
        phaseIdLabel.setMinWidth(GetariConstants.REC_WIDTH);
        firstStageLabel.setWrapText(false);
        secondStageLabel.setWrapText(false);
        phaseIdLabel.setWrapText(false);

        // Besoin pour centrer les multi-ligne en cas setWrapText(true)
        firstStageLabel.setTextAlignment(TextAlignment.CENTER);
        firstStageLabel.setAlignment(Pos.CENTER);
        secondStageLabel.setTextAlignment(TextAlignment.CENTER);
        secondStageLabel.setAlignment(Pos.CENTER);
        phaseIdLabel.setTextAlignment(TextAlignment.CENTER);
        phaseIdLabel.setAlignment(Pos.CENTER);

        Tooltip.install(firstStageLabel, idtoolTip);
        setRectangle((Rectangle) shape.getChildren().get(0));

        stackPane = new StackPane();
        stackPane.getProperties().put(INDICATOR, getIndicator());
        stackPane.getProperties().put(GRAPH_NODE, this);
        getRectangle().getProperties().put(INDICATOR, getIndicator());
        getRectangle().getProperties().put(GRAPH_NODE, this);
        firstStageLabel.getProperties().put(INDICATOR, getIndicator());
        firstStageLabel.getProperties().put(GRAPH_NODE, this);
        stackPane.getChildren().add(shape);
        stackPane.getChildren().add(phaseIdLabel);

        root.getChildren().add(firstStageLabel);
        root.getChildren().add(stackPane);
        root.getChildren().add(secondStageLabel);
        root.getProperties().put(INDICATOR, getIndicator());
        root.getProperties().put(GRAPH_NODE, this);

        ComponentUtil.setCursorHoverStyleProperty(stackPane);

        stackPane.addEventHandler(MouseEvent.MOUSE_CLICKED,
                getDetailsHandler());

        setLightOnMouseClicked(stackPane);

        // case of phenologic phases
        pane.getChildren().add(root);
    }
}
