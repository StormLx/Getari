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

import static fr.inrae.agroclim.getari.util.GetariConstants.ADD_RADIUS;
import static fr.inrae.agroclim.getari.util.GetariConstants.GRAPH_NODE;
import static fr.inrae.agroclim.getari.util.GetariConstants.REC_HEIGHT;
import static fr.inrae.agroclim.getari.util.GetariConstants.REC_WIDTH;

import java.lang.reflect.Field;

import fr.inrae.agroclim.getari.controller.MouseAddMenuController;
import fr.inrae.agroclim.getari.controller.MouseDeleteController;
import fr.inrae.agroclim.getari.view.graph.GraphElement;
import fr.inrae.agroclim.getari.view.graph.GraphRoot;
import fr.inrae.agroclim.indicators.model.indicator.IndicatorCategory;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.HLineTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import lombok.extern.log4j.Log4j2;

/**
 * Utilitaire de génération des noeuds graphiques de l'application pour
 * GraphNode et GraphRoot.
 *
 * Last change $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
@Log4j2
public final class DrawingTool {

    /**
     * Color for nodes.
     */
    private static Color color = Color.TRANSPARENT;

    /**
     * Increment for color node.
     */
    private static int colorIncrement = 14;

    /**
     * @param overlay the pane that will contain the element
     * @param parentElement element
     */
    public static void drawAggregate(final Pane overlay,
            final GraphElement parentElement) {

        final Rectangle origin = parentElement.getRectangle();

        if (parentElement instanceof GraphRoot) {
            parentElement
            .getAggregate()
            .layoutXProperty()
            .bind(origin
                    .getParent()
                    .layoutXProperty()
                    .add(GetariConstants.PHASE_WIDTH * 3)
                    .add(origin.layoutXProperty())
                    .add(origin.getParent().getParent()
                            .layoutXProperty()));
            parentElement
            .getAggregate()
            .layoutYProperty()
            .bind(origin
                    .getParent()
                    .getParent()
                    .getParent()
                    .getParent()
                    .layoutYProperty()
                    .add(GetariConstants.PHASE_HEIGHT / 2
                            + ADD_RADIUS + 24)
                    .add(origin.getParent().getParent().getParent()
                            .layoutYProperty()));
        } else {
            parentElement
            .getAggregate()
            .layoutXProperty()
            .bind(origin
                    .getParent()
                    .getParent()
                    .layoutXProperty()
                    .add(REC_WIDTH + 20 + 31)
                    .add(origin.layoutXProperty())
                    .add(origin.getParent().getParent().getParent()
                            .layoutXProperty()));
            parentElement
            .getAggregate()
            .layoutYProperty()
            .bind(origin
                    .getParent()
                    .getParent()
                    .getParent()
                    .layoutYProperty()
                    .add(REC_HEIGHT / 2 + ADD_RADIUS)
                    .add(origin.getParent().getParent()
                            .layoutYProperty()));
        }
        parentElement.getAggregate().toFront();
        if (!overlay.getChildren().contains(parentElement.getAggregate())) {
            overlay.getChildren().add(parentElement.getAggregate());
        }
    }

    /**
     * Draw on a pane an arrow between a graph element and its parent.
     *
     * @param overlay pane which will contain the arrow
     * @param parentElement the origin of the arrow
     * @param childElement the target of the arrow
     * @param arrowColor the color for the arrow
     * @return the arrow
     */
    public static Path drawArrowBetween(final Pane overlay,
            final GraphElement parentElement, final GraphElement childElement,
            final Color arrowColor) {
        final Path path = new Path();

        final MoveTo moveTo = new MoveTo();
        final Rectangle origin = parentElement.getRectangle();
        if (origin == null) {
            throw new RuntimeException("Parent "
                    + "GraphElement.getRectangle() must not be null!");
        }
        final Rectangle destination = childElement.getRectangle();
        HLineTo hLineTo;
        final Parent originParent = origin.getParent();
        if (parentElement instanceof GraphRoot) {
            // 6 = débordement du label pour le premier stade
            moveTo.xProperty().bind(
                    originParent
                    .layoutXProperty()
                    .add(35 + ADD_RADIUS + 6)
                    .add(origin.layoutXProperty())
                    .add(origin.getParent().getParent()
                            .layoutXProperty()));

            // 24 = taille du label pour le premier stade
            moveTo.yProperty().bind(
                    originParent
                    .getParent()
                    .getParent()
                    .getParent()
                    .layoutYProperty()
                    .add(GetariConstants.PHASE_HEIGHT / 2
                            + ADD_RADIUS + 24)
                    .add(origin.getParent().getParent().getParent()
                            .layoutYProperty()));

            /* Ligne horizontale */
            hLineTo = new HLineTo();
            hLineTo.xProperty().bind(
                    originParent
                    .layoutXProperty()
                    .add(GetariConstants.PHASE_WIDTH * 3)
                    .add(origin.layoutXProperty())
                    .add(origin.getParent().getParent()
                            .layoutXProperty()));
        } else {
            moveTo.xProperty().bind(
                    originParent
                    .getParent()
                    .layoutXProperty()
                    .add(REC_WIDTH + ADD_RADIUS + 1)
                    .add(origin.layoutXProperty())
                    .add(origin.getParent().getParent().getParent()
                            .layoutXProperty()));
            moveTo.yProperty().bind(
                    originParent
                    .getParent()
                    .getParent()
                    .layoutYProperty()
                    .add(REC_HEIGHT / 2 + ADD_RADIUS)
                    .add(origin.getParent().getParent()
                            .layoutYProperty()));
            /* Ligne horizontale */
            hLineTo = new HLineTo();

            // 51 = position de l'angle droit
            hLineTo.xProperty().bind(
                    originParent
                    .getParent()
                    .layoutXProperty()
                    .add(REC_WIDTH + 51)
                    .add(origin.layoutXProperty())
                    .add(origin.getParent().getParent().getParent()
                            .layoutXProperty()));
        }

        /* Lignes droites */
        final LineTo lineTo = new LineTo();
        final Parent destinationParent = destination.getParent();
        lineTo.yProperty().bind(
                destinationParent
                .getParent()
                .getParent()
                .layoutYProperty()
                .add(REC_HEIGHT / 2 + ADD_RADIUS)
                .add(destination.getParent().getParent()
                        .layoutYProperty()));
        lineTo.xProperty().bind(
                destinationParent
                .getParent()
                .layoutXProperty()
                .add(ADD_RADIUS)
                .add(destination.layoutXProperty())
                .add(destination.getParent().getParent().getParent()
                        .layoutXProperty()));

        path.getElements().add(moveTo);
        path.getElements().add(hLineTo);
        path.getElements().add(lineTo);

        path.setId("arrow");
        path.setStroke(arrowColor);
        path.setStrokeWidth(2);
        final DropShadow dropShadow = new DropShadow();
        dropShadow.setOffsetY(5.0);
        dropShadow.setOffsetX(5.0);
        dropShadow.setColor(Color.GRAY);
        path.setEffect(dropShadow);

        // on l'ajoute en dessous des elements dessines, 0 correspond au z-order
        overlay.getChildren().add(path);
        parentElement.getAggregate().toFront();
        return path;
    }

    /**
     * Used by GraphRoot to inverse color gradient. Used in conjunction with
     * restoreColor().
     *
     * @param group rectangle of GraphRoot
     * @param baseColor base color
     */
    public static void inverseColor(final Group group, final Color baseColor) {
        final Stop[] stops = new Stop[]{new Stop(0, baseColor),
                new Stop(1, Color.WHITE)};
        final LinearGradient lg1 = new LinearGradient(0, 0, 0, 1, true,
                CycleMethod.NO_CYCLE, stops);
        ((Shape) group.getChildren().get(0)).setFill(lg1);
        ((Shape) group.getChildren().get(0)).setStroke(Color.GREY.darker());
    }

    /**
     * Used by GraphNode to inverse color gradient. Used in conjunction with
     * restoreColor().
     *
     * @param rect rectangle of GraphNode
     * @param arrow arrow of GraphNode
     * @param aggregate aggregate of GraphNode
     * @param baseColor base color
     */
    public static void inverseColor(final Rectangle rect, final Path arrow,
            final StackPane aggregate, final Color baseColor) {
        final Stop[] stops = new Stop[]{new Stop(0, baseColor),
                new Stop(1, Color.WHITE)};
        final LinearGradient lg1 = new LinearGradient(0, 0, 0, 1, true,
                CycleMethod.NO_CYCLE, stops);
        rect.setFill(lg1);
        rect.setStrokeWidth(2);
        rect.setStroke(Color.GREY.darker());
        arrow.setStroke(Color.GREY.darker());
        arrow.toFront();
        ((Shape) aggregate.getChildren().get(0)).setStroke(Color.GREY.darker());
        aggregate.toFront();
    }

    /**
     * Create a new rectangle for the aggregation.
     *
     * @param baseColor base color
     * @param aggregation use aggregation symbol
     * @return Aggregation representation
     */
    public static StackPane newAggregate(final Color baseColor,
            final boolean aggregation) {
        int radius;
        Image image;
        if (aggregation) {
            radius = GetariConstants.AGGREGATION_RADIUS;
            image = ComponentUtil.loadImage("sum-icon.png");
        } else {
            radius = GetariConstants.NO_AGGREGATION_RADIUS;
            image = null;
        }
        final Circle circle = new Circle(radius, baseColor);
        final Stop[] stops = new Stop[]{new Stop(0, Color.WHITE),
                new Stop(1, baseColor)};
        final LinearGradient lg1 = new LinearGradient(0, 0, 0, 1, true,
                CycleMethod.NO_CYCLE, stops);
        circle.setFill(lg1);
        circle.setStrokeWidth(2);
        circle.setStroke(baseColor);
        final StackPane imageContainer = new StackPane();
        final ImageView imageView = new ImageView();
        imageView.setImage(image);
        imageContainer.getChildren().addAll(circle, imageView);
        final DropShadow dropShadow = new DropShadow();
        dropShadow.setOffsetY(5.0);
        dropShadow.setOffsetX(5.0);
        dropShadow.setColor(Color.GRAY);
        imageContainer.setEffect(dropShadow);
        return imageContainer;
    }

    /**
     * Create a new rectangle for the node.
     *
     * @param baseColor base color
     * @param node indicator node to represent
     * @param isComputable computable property to stroke the rectangle
     * @return indicator representation
     */
    public static Group newNodeShape(final Color baseColor,
            final GraphElement node,
            final boolean isComputable) {
        final Group myGroup = new Group();
        final Rectangle rect = new Rectangle(REC_WIDTH, REC_HEIGHT);
        Stop[] stops;
        stops = new Stop[]{new Stop(0, Color.WHITE), new Stop(1, baseColor)};
        final LinearGradient lg1 = new LinearGradient(0, 0, 0, 1, true,
                CycleMethod.NO_CYCLE, stops);
        rect.setFill(lg1);
        rect.setStrokeWidth(2);
        rect.setStroke(baseColor);
        myGroup.getChildren().add(rect);

        if (!isComputable) {
            final Path firstPath = new Path();
            firstPath.setStroke(Color.GRAY);
            firstPath.setStrokeWidth(2);
            LineTo lineTo = new LineTo();
            lineTo.setX(rect.getX() + rect.getWidth());
            lineTo.setY(rect.getY() + rect.getHeight());
            MoveTo moveTo = new MoveTo();
            moveTo.setX(rect.getX());
            moveTo.setY(rect.getY());
            firstPath.getElements().add(moveTo);
            firstPath.getElements().add(lineTo);
            myGroup.getChildren().add(firstPath);
            final Path secondPath = new Path();
            secondPath.setStroke(Color.GRAY);
            secondPath.setStrokeWidth(2);
            lineTo = new LineTo();
            lineTo.setX(rect.getX());
            lineTo.setY(rect.getY() + rect.getHeight());
            moveTo = new MoveTo();
            moveTo.setX(rect.getX() + rect.getWidth());
            moveTo.setY(rect.getY());
            secondPath.getElements().add(moveTo);
            secondPath.getElements().add(lineTo);
            myGroup.getChildren().add(secondPath);
        }

        final StackPane addImageContainer = new StackPane();
        addImageContainer.setTranslateX(-ADD_RADIUS);
        addImageContainer.setTranslateY(-ADD_RADIUS);
        if (!IndicatorCategory.INDICATORS.equals(
                node.getIndicator().getIndicatorCategory())) {
            final Image addImage = ComponentUtil.loadImage("plus-icon.png");
            final ImageView addImageView = new ImageView();
            addImageView.setImage(addImage);
            addImageContainer.getChildren().add(addImageView);
            addImageContainer.addEventHandler(MouseEvent.MOUSE_CLICKED,
                    new MouseAddMenuController());
            addImageContainer.getProperties().put(GRAPH_NODE, node);
        } else if (node.getIndicator().getCategory() == null) {
            LOGGER.warn("Strange, category of indicator {} is null!",
                    node.getIndicator().getName());
        } else {
            final Circle addCircle = new Circle(ADD_RADIUS, baseColor);
            addCircle.setFill(Color.TRANSPARENT);
            addImageContainer.getChildren().addAll(addCircle);
        }
        myGroup.getChildren().add(addImageContainer);

        final StackPane closeImageContainer = new StackPane();
        final Image closeImage = ComponentUtil.loadImage("cross-icon.png");
        final ImageView closeImageView = new ImageView();
        closeImageView.setImage(closeImage);
        closeImageContainer.getChildren().add(closeImageView);
        closeImageContainer.setTranslateX(REC_WIDTH - 18);
        closeImageContainer.setTranslateY(2);
        closeImageContainer.addEventHandler(MouseEvent.MOUSE_CLICKED,
                new MouseDeleteController());
        closeImageContainer.getProperties().put(GRAPH_NODE, node);
        myGroup.getChildren().add(closeImageContainer);
        return myGroup;
    }

    /**
     * Create a new rectangle for the phase.
     *
     * @param baseColor base color
     * @param node phase node to represent
     * @return phase representation
     */
    public static Group newRootShape(final Color baseColor,
            final GraphElement node) {
        final Group myGroup = new Group();
        final Rectangle rect = new Rectangle(GetariConstants.PHASE_WIDTH,
                GetariConstants.PHASE_HEIGHT);
        final Stop[] stops = new Stop[]{new Stop(0, Color.WHITE),
                new Stop(1, baseColor)};
        final LinearGradient lg1 = new LinearGradient(0, 0, 0, 1, true,
                CycleMethod.NO_CYCLE, stops);
        rect.setFill(lg1);
        rect.setStrokeWidth(2);
        rect.setStroke(baseColor);

        final StackPane addImageContainer = new StackPane();
        final Image addImage = ComponentUtil.loadImage("plus-icon.png");
        final ImageView addImageView = new ImageView();
        addImageView.setImage(addImage);
        addImageContainer.getChildren().add(addImageView);
        addImageContainer.setTranslateX(-ADD_RADIUS);
        addImageContainer.setTranslateY(-ADD_RADIUS);
        addImageContainer.addEventHandler(MouseEvent.MOUSE_CLICKED,
                new MouseAddMenuController());
        addImageContainer.getProperties().put(GRAPH_NODE, node);

        final StackPane closeImageContainer = new StackPane();
        final Image closeImage = ComponentUtil.loadImage("cross-icon.png");
        final ImageView closeImageView = new ImageView();
        closeImageView.setImage(closeImage);
        closeImageContainer.getChildren().add(closeImageView);
        closeImageContainer.setTranslateX(22);
        closeImageContainer.setTranslateY(2);
        closeImageContainer.addEventHandler(MouseEvent.MOUSE_CLICKED,
                new MouseDeleteController());
        closeImageContainer.getProperties().put(GRAPH_NODE, node);
        final DropShadow dropShadow = new DropShadow();
        dropShadow.setOffsetY(5.0);
        dropShadow.setOffsetX(5.0);
        dropShadow.setColor(Color.GRAY);

        myGroup.setEffect(dropShadow);
        myGroup.getChildren().add(rect);
        myGroup.getChildren().add(addImageContainer);
        myGroup.getChildren().add(closeImageContainer);

        return myGroup;
    }

    /**
     * @return next color for node
     * @throws Exception this exception from reflection should not occur
     */
    public static Color nextColor() throws Exception {
        final Field[] fields = Color.class.getFields();
        int i = 0;
        for (final Field field : fields) {
            if (field.getType() == Color.class && colorIncrement == i) {
                final Color newColor = (Color) field.get(null);
                color = newColor;
                colorIncrement++;
                break;
            }
            i++;
        }
        return color;
    }

    /**
     * Used by GraphRoot to restore color gradient. Used in conjunction with
     * inverseColor().
     *
     * @param group rectangle of GraphRoot
     * @param baseColor base color
     */
    public static void restoreColor(final Group group, final Color baseColor) {
        final Stop[] stops = new Stop[]{new Stop(0, Color.WHITE),
                new Stop(1, baseColor)};
        final LinearGradient lg1 = new LinearGradient(0, 0, 0, 1, true,
                CycleMethod.NO_CYCLE, stops);
        ((Shape) group.getChildren().get(0)).setFill(lg1);
        ((Shape) group.getChildren().get(0)).setStroke(baseColor);
        // This should always occur:
        if (group.getChildren().size() == 3) {
            // patch to allow clicking on the cross icon
            group.getChildren().get(2).toFront();
        }
    }

    /**
     * Used by GraphNode to restore color gradient. Used in conjunction with
     * inverseColor().
     *
     * @param rect rectangle of GraphNode
     * @param arrow arrow of GraphNode
     * @param aggregate aggregate of GraphNode
     * @param baseColor base color
     */
    public static void restoreColor(final Rectangle rect, final Path arrow,
            final StackPane aggregate, final Color baseColor) {
        final Stop[] stops = new Stop[]{new Stop(0, Color.WHITE),
                new Stop(1, baseColor)};
        final LinearGradient lg1 = new LinearGradient(0, 0, 0, 1, true,
                CycleMethod.NO_CYCLE, stops);
        rect.setFill(lg1);
        rect.setStrokeWidth(2);
        rect.setStroke(baseColor);
        arrow.setStroke(baseColor);
        arrow.toFront();
        ((Shape) aggregate.getChildren().get(0)).setStroke(baseColor);
        aggregate.toFront();
    }

    /**
     * Update color of phase representation.
     *
     * @param group phase representation
     * @param baseColor new color to set
     */
    public static void updateColor(final Group group, final Color baseColor) {
        final Rectangle nodeRect = (Rectangle) group.getChildren().get(0);
        final Stop[] stops = new Stop[]{new Stop(0, Color.WHITE),
                new Stop(1, baseColor)};
        final LinearGradient lg1 = new LinearGradient(0, 0, 0, 1, true,
                CycleMethod.NO_CYCLE, stops);
        nodeRect.setFill(lg1);
        nodeRect.setStrokeWidth(2);
        nodeRect.setStroke(baseColor);
    }

    /**
     * Update color of indicator representation.
     *
     * @param group indicator representation
     * @param arrow associated arrow from indicator to parent
     * @param aggregate aggregation representation
     * @param baseColor new color to set
     */
    public static void updateColor(final Group group, final Path arrow,
            final StackPane aggregate, final Color baseColor) {
        updateColor(group, baseColor);
        final Stop[] stops = new Stop[]{new Stop(0, Color.WHITE),
                new Stop(1, baseColor)};

        arrow.setStroke(baseColor);
        final LinearGradient lg2 = new LinearGradient(0, 0, 0, 1, true,
                CycleMethod.NO_CYCLE, stops);
        ((Shape) aggregate.getChildren().get(0)).setFill(lg2);
        ((Shape) aggregate.getChildren().get(0)).setStroke(baseColor);
    }

    /**
     * Not constructor for utility class.
     */
    private DrawingTool() {
    }
}
