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

import java.util.Optional;

import fr.inrae.agroclim.getari.resources.Messages;
import javafx.event.ActionEvent;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Horizontal box with 3 small buttons for window close, minimize and maximize.
 *
 * Last change $Date$
 *
 * @author ddelannoy
 * @author $Author$
 * @version $Revision$
 */
public final class WindowButtons extends HBox {

    /**
     * The amount of horizontal space between each child in the hbox.
     */
    private static final int SPACING = 4;

    /**
     * Stage to handle by buttons.
     */
    private final Stage stage;

    /**
     * Stage size before maximization.
     */
    private Rectangle2D backupWindowBounds = null;

    /**
     * Maximization state.
     */
    private boolean maximized = false;

    /**
     * @param handledStage stage to handle
     * @param hasMin with minimize button
     * @param hasMax with maximize button
     * @param hasClose with close button
     */
    public WindowButtons(final Stage handledStage, final boolean hasMin,
            final boolean hasMax, final boolean hasClose) {
        super(SPACING);
        this.stage = handledStage;

        // create buttons
        if (hasMin) {
            final Button minBtn = new Button();
            minBtn.setId("window-min");
            minBtn.setOnAction((final ActionEvent actionEvent) -> {
                logAppend("minBtn action");
                stage.setIconified(true);
            });
            getChildren().add(minBtn);
        }

        if (hasMax) {
            final Button maxBtn = new Button();
            maxBtn.setId("window-max");
            maxBtn.setOnAction((final ActionEvent actionEvent) -> {
                logAppend("maxBtn action");
                toogleMaximized();
            });
            getChildren().add(maxBtn);
        }

        if (hasClose) {
            final Button closeBtn = new Button();
            closeBtn.setId("window-close");
            closeBtn.setOnAction((final ActionEvent event) -> {
                logAppend("closeBtn action");
                // primary stage does not have owner
                if (stage.getOwner() == null) {
                    final Alert alert = new Alert(AlertType.CONFIRMATION);
                    alert.setTitle(Messages.getString("exit.text"));
                    alert.setHeaderText(Messages.getString("confirm.exit"));
                    final Optional<ButtonType> result = alert.showAndWait();
                    if (!result.isPresent()
                            || result.get() == ButtonType.CANCEL) {
                        event.consume();
                    } else {
                        GetariApp.close();
                    }
                } else {
                    stage.close();
                }
            });
            // TODO : find why the tooltip does not display the text...
            final Tooltip tooltip = new Tooltip(Messages.getString("action.close"));
            closeBtn.setTooltip(tooltip);
            getChildren().add(closeBtn);
        }
    }

    /**
     * Append a string to the file.
     *
     * @param text string to append
     */
    private void logAppend(final String text) {
        GetariApp.logAppend(getClass(), stage, text);
    }

    /**
     * Toggle maximization/initial size.
     */
    public void toogleMaximized() {
        final Screen screen = Screen.getScreensForRectangle(stage.getX(),
                stage.getY(), 1, 1).get(0);
        if (maximized) {
            maximized = false;
            if (backupWindowBounds != null) {
                stage.setX(backupWindowBounds.getMinX());
                stage.setY(backupWindowBounds.getMinY());
                stage.setWidth(backupWindowBounds.getWidth());
                stage.setHeight(backupWindowBounds.getHeight());
            }
        } else {
            maximized = true;
            backupWindowBounds = new Rectangle2D(stage.getX(), stage.getY(),
                    stage.getWidth(), stage.getHeight());
            stage.setX(screen.getVisualBounds().getMinX());
            stage.setY(screen.getVisualBounds().getMinY());
            stage.setWidth(screen.getVisualBounds().getWidth());
            stage.setHeight(screen.getVisualBounds().getHeight());
        }
        logAppend("=> maximized=" + maximized);
    }
}
