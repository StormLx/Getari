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

import java.io.PrintWriter;
import java.io.StringWriter;

import fr.inrae.agroclim.getari.Getari;
import fr.inrae.agroclim.indicators.resources.Messages;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextArea;

/**
 * Helper to use Alert.
 *
 * Last changed : $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
public final class AlertUtils {

    /**
     * Apply commoon CSS to the alert.
     *
     * @param alert alert to style.
     */
    public static void setStyle(final Alert alert) {
        final DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(Getari.class.getResource("getari.css")
                .toExternalForm());
    }

    /**
     * Show an Alert with an error text.
     *
     * @param text
     *            error text
     */
    public static void showError(final String text) {
        final Alert alert = new Alert(AlertType.WARNING);
        setStyle(alert);
        alert.setTitle(Messages.get("error.title"));
        alert.setHeaderText(text);
        alert.showAndWait();
    }

    /**
     * Show an Alert with stack trace of exception.
     *
     * @param text
     *            header text
     * @param e
     *            exception to show
     */
    public static void showException(final String text, final Exception e) {
        final Alert alert = new Alert(AlertType.WARNING);
        setStyle(alert);
        alert.setTitle(Messages.get("error.title"));
        alert.setHeaderText(text);
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        final TextArea area = new TextArea(sw.toString());
        alert.getDialogPane().setExpandableContent(area);
        alert.showAndWait();
    }

    /**
     * Show an alert with informations.
     *
     * @param title alert title
     * @param msg header text
     */
    public static void showInfo(final String title, final String msg) {
        final Alert alert = new Alert(AlertType.INFORMATION);
        setStyle(alert);
        alert.setTitle(title);
        alert.setHeaderText(msg);
        alert.showAndWait();
    }

    /**
     * No constructor for helper class.
     */
    private AlertUtils() {
    }
}
