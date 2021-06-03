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
package fr.inrae.agroclim.getari.component;

import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Simple draggable area for the bottom right of a window to support resizing.
 *
 * Last change $Date$
 *
 * @author ddelannoy
 * @author $Author$
 * @version $Revision$
 */
public class WindowResizeButton extends Region {

    /**
     * Offsets.
     */
    private double dragOffsetX;
    /**
     * Offsets.
     */
    private double dragOffsetY;

    /**
     * Constructor.
     *
     * @param stage stage to handle
     * @param stageMinimumWidth minimum width for the stage
     * @param stageMinimumHeight maximum height for the stage
     */
    public WindowResizeButton(final Stage stage,
            final double stageMinimumWidth, final double stageMinimumHeight) {
        setId("window-resize-button");
        final int size = 11;
        setPrefSize(size, size);
        setOnMousePressed((final MouseEvent e) -> {
            dragOffsetX = stage.getX() + stage.getWidth() - e.getScreenX();
            dragOffsetY = stage.getY() + stage.getHeight() - e.getScreenY();
            e.consume();
        });
        setOnMouseDragged((final MouseEvent e) -> {
            final ObservableList<Screen> screens = Screen.getScreensForRectangle(
                    stage.getX(), stage.getY(), 1, 1);
            final Screen screen;
            if (!screens.isEmpty()) {
                screen = Screen.getScreensForRectangle(stage.getX(), stage.getY(), 1, 1).get(0);
            } else {
                screen = Screen.getScreensForRectangle(0, 0, 1, 1).get(0);
            }
            final Rectangle2D visualBounds = screen.getVisualBounds();
            final double maxX = Math.min(visualBounds.getMaxX(), e.getScreenX()
                    + dragOffsetX);
            final double maxY = Math.min(visualBounds.getMaxY(), e.getScreenY()
                    - dragOffsetY);
            stage.setWidth(Math.max(stageMinimumWidth, maxX - stage.getX()));
            stage.setHeight(Math.max(stageMinimumHeight, maxY - stage.getY()));
            e.consume();
        });
    }
}
