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

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

/**
 * Pane with title.
 *
 * Last change $Date$
 *
 * @author ddelannoy
 * @author $Author$
 * @version $Revision$
 */
public final class BorderedTitledPane extends StackPane {

    /**
     * Label displaying the title.
     */
    private final Label title;

    /**
     * Simple constructor for FXML.
     */
    public BorderedTitledPane() {
        super();
        title = new Label();
        title.getStyleClass().add("bordered-titled-title");
        title.setWrapText(true);
        StackPane.setAlignment(title, Pos.TOP_LEFT);
        getStyleClass().add("bordered-titled-border");
        getChildren().add(title);
    }

    /**
     * Constructor.
     *
     * @param content node for the content
     * @param contentClass class name for the node style
     */
    public BorderedTitledPane(final Node content, final String contentClass) {
        this();
        content.getStyleClass().add(contentClass);
        getChildren().add(content);
    }

    /**
     * Constructor with default node style.
     *
     * @param content node for the content
     */
    public BorderedTitledPane(final Pane content) {
        this(content, "bordered-titled-content");
    }

    /**
     * @return title text
     */
    public String getTitle() {
        return title.getText();
    }

    /**
     * @param text title text
     */
    public void setTitle(final String text) {
        if (text == null) {
            this.title.setVisible(false);
        } else {
            this.title.setText(" " + text + " ");
            this.title.setVisible(true);
        }
    }
}
