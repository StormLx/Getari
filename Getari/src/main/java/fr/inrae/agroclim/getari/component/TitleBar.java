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

import fr.inrae.agroclim.getari.util.ComponentUtil;
import fr.inrae.agroclim.getari.util.PlatformUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * Title bar for the application.
 *
 * Last change $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
public final class TitleBar extends ToolBar {

    /**
     * X offset.
     */
    private double mouseDragOffsetX = 0;

    /**
     * Y offset.
     */
    private double mouseDragOffsetY = 0;

    /**
     * Box for title or application icon.
     */
    private final HBox leftBox;

    /**
     * Box for window buttons.
     */
    private final HBox rightBox;

    /**
     * Create title bar for main window, where title is replaced by Logo.
     *
     * @param stage main stage
     */
    public TitleBar(final Stage stage) {
        this(stage, null, null);

        // Logo Getari
        final Image getariImage = ComponentUtil.loadImage("logo_getari_small.png");
        final ImageView getariView = new ImageView(getariImage);
        final double left = 5;
        HBox.setMargin(getariView, new Insets(0, 0, 0, left));
        leftBox.getChildren().add(getariView);

        // Logo agroclim
        rightBox.getChildren().add(0, createLink("logo_agroclim_small.png",
                "https://www6.paca.inrae.fr/agroclim/"));

        // Logo INRAE
        rightBox.getChildren().add(1, createLink("logo_inrae_small.png",
                "http://www.inrae.fr/"));

        // Logo cnrs
        rightBox.getChildren().add(2, createLink("logo_cnrs_small.png",
                "http://www.cnrs.fr/"));
    }

    /**
     * Create title bar for secondary windows.
     *
     * @param stage stage to decorate.
     * @param scene null for main stage scene
     * @param title title of window
     */
    public TitleBar(final Stage stage, final Scene scene, final String title) {
        super();
        Scene theScene;
        if (scene == null) {
            theScene = stage.getScene();
        } else {
            theScene = scene;
        }
        final HBox toolBarPane = new HBox();
        final int margin = -30;
        final int spacing = 5;
        toolBarPane.prefWidthProperty().bind(
                theScene.widthProperty().add(margin));
        leftBox = new HBox();
        rightBox = new HBox();
        leftBox.setSpacing(spacing);
        rightBox.setSpacing(spacing);
        leftBox.prefWidthProperty().bind(
                toolBarPane.prefWidthProperty().divide(2));
        rightBox.prefWidthProperty().bind(
                toolBarPane.prefWidthProperty().divide(2));
        leftBox.setAlignment(Pos.CENTER_LEFT);
        rightBox.setAlignment(Pos.CENTER_RIGHT);

        if (title != null) {
            leftBox.getChildren().add(new Label(title));
        }

        getStyleClass().add("mainToolBar");
        getItems().add(leftBox);

        // add close min max
        WindowButtons windowButtons;
        if (scene == null) {
            // main window can be resized
            windowButtons = new WindowButtons(stage, true, true, true);
            // add window header double clicking
            setOnMouseClicked((final MouseEvent event) -> {
                if (event.getClickCount() == 2) {
                    windowButtons.toogleMaximized();
                }
            });
        } else {
            windowButtons = new WindowButtons(stage, false, false, true);
        }
        rightBox.getChildren().add(windowButtons);
        getItems().add(rightBox);

        // add window dragging
        setOnMousePressed((final MouseEvent event) -> {
            mouseDragOffsetX = event.getSceneX();
            mouseDragOffsetY = event.getSceneY();
        });
        setOnMouseDragged((final MouseEvent event) -> {
            stage.setX(event.getScreenX() - mouseDragOffsetX);
            stage.setY(event.getScreenY() - mouseDragOffsetY);
        });
    }

    /**
     * @param menuBar menu bar to add to left part
     */
    public void addMenuBar(final Node menuBar) {
        leftBox.getChildren().add(menuBar);
    }

    /**
     * @param path image path
     * @param url url to set to link
     * @return link with image and action to open URL in browser
     */
    private Hyperlink createLink(final String path, final String url) {
        final Hyperlink link = new Hyperlink();
        final Image image = ComponentUtil.loadImage(path);
        final ImageView view = new ImageView(image);
        link.setGraphic(view);
        link.setOnAction(e -> PlatformUtil.openBrowser(url));
        final double right = 5;
        HBox.setMargin(view, new Insets(0, right, 0, 0));
        return link;
    }
}
