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
package fr.inrae.agroclim.getari.view;

import java.io.IOException;
import java.util.ResourceBundle;

import fr.inrae.agroclim.getari.component.GetariApp;
import fr.inrae.agroclim.getari.component.TitleBar;
import fr.inrae.agroclim.getari.resources.Messages;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

/**
 * Ancestor of view based on FXML.
 *
 * Simply extends this class, eg. as TheviewView and create theview.fxml.
 * Resources can be put in getari/resources/messages.properties or in
 * theview.properties.
 *
 * Last change $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
@Log4j2
public class FxmlView {

    /**
     * Loader for the view.
     */
    @Getter(AccessLevel.PROTECTED)
    private FXMLLoader loader;

    /**
     * View name.
     */
    private String name;

    /**
     * Stage for the dialog displaying the view.
     */
    @Getter
    private Stage stage;

    /**
     * Constructor.
     */
    public FxmlView() {
    }

    /**
     * Constructor.
     *
     * @param viewName view name
     */
    public FxmlView(final String viewName) {
        name = viewName;
    }

    /**
     * Build the node for viewName.fxml.
     *
     * viewName.properties (if exists) or getari/resources/messages.properties
     * are used as resources for fxml translations.
     *
     * @return root node of viewName.fxml
     * @throws IOException FXMLLoader exception
     */
    public final Parent build() throws IOException {
        final String baseName = getName();

        ResourceBundle resources;
        final String bundleName = getClass().getPackage()
                .getName() + "." + baseName;
        try {
            resources = ResourceBundle.getBundle(bundleName);
            LOGGER.trace("Bundle {} loaded for {}", bundleName, baseName);
        } catch (final java.util.MissingResourceException e) {
            LOGGER.trace("No bundle {} for {}", bundleName, baseName);
            resources = ResourceBundle.getBundle(Messages.BUNDLE_NAME);
        }

        final String key = baseName + ".view.title";
        String title;
        try {
            title = resources.getString(key);
        } catch (final java.util.MissingResourceException e) {
            LOGGER.trace("No bundle {} for {}", resources.getBaseBundleName(), key);
            title = "!" + key;
        }
        loader = new FXMLLoader(getClass().getResource(baseName + ".fxml"));
        loader.setResources(resources);
        final Parent root = (Parent) loader.load();
        root.setUserData(title);
        return root;
    }

    /**
     * Build and show the dialog for viewName.fxml.
     *
     * viewName.properties (if exists) or getari/resources/messages.properties
     * are used as resources for fxml translations.
     *
     * resources must contain the key viewName.view.title.
     *
     * @param primaryStage
     *            GETARI main stage
     * @throws IOException
     *             FXMLLoader exception
     */
    public final void build(@NonNull final Stage primaryStage)
            throws IOException {
        final Parent root = build();
        final String title = (String) root.getUserData();

        final Scene scene = new Scene(root);
        scene.getStylesheets().add(GetariApp.getGlobalCss());

        stage = new Stage(StageStyle.UNDECORATED);
        stage.initOwner(primaryStage);
        stage.setTitle(title);
        stage.setScene(scene);
        initStage();

        if (root instanceof BorderPane) {
            ((BorderPane) root)
            .setTop(new TitleBar(stage, scene, title));
        }

        stage.show();
    }

    /**
     * @return view name
     */
    public final String getName() {
        if (name == null) {
            final String className = getClass().getSimpleName();
            return className.substring(0, 1).toLowerCase()
                    + className.substring(1, className.length()
                            - ".xml".length());
        } else {
            return name;
        }
    }

    /**
     * Do some initialization before showing the stage.
     */
    protected void initStage() {
        //
    }
}
