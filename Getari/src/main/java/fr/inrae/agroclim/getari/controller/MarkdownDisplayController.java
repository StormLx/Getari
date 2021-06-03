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
package fr.inrae.agroclim.getari.controller;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import fr.inrae.agroclim.getari.resources.Messages;
import fr.inrae.agroclim.getari.util.StringUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;

/**
 * Display embedded release notes.
 *
 * Last change $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
@Log4j2
public final class MarkdownDisplayController extends AbstractController implements Initializable {

    /**
     * Close button.
     */
    @FXML
    private Button close;

    /**
     * Using a webview to display formatted markdown.
     */
    @FXML
    private WebView webView;

    @Override
    protected Stage getStage() {
        return (Stage) close.getScene().getWindow();
    }

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        final String file = url.getFile();
        final String baseName = file.substring(file.lastIndexOf('/') + 1,
                file.lastIndexOf('.'));
        String fileContent;
        final String fr = new Locale("fr").getLanguage();
        String resource = "/" + baseName + "_fr.md";
        if (!Messages.getLocale().getLanguage().equals(fr)
                || getClass().getResource(resource) == null) {
            resource = "/" + baseName + ".md";
        }
        try {
            fileContent = StringUtils.getFileContent(getClass(), resource);
        } catch (final IOException ex) {
            LOGGER.error("Error while loading " + resource, ex);
            fileContent = ex.getLocalizedMessage();
        }
        final String content = StringUtils.convertMarkdown(fileContent);
        final WebEngine webEngine = webView.getEngine();
        webEngine.loadContent(content, "text/html");
    }

    /**
     * @param event onAction of button
     */
    @FXML
    private void onCloseAction(final ActionEvent event) {
        logAppend("close");
        close();
    }

}
