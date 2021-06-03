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
import java.util.ResourceBundle;

import fr.inrae.agroclim.getari.resources.Version;
import fr.inrae.agroclim.getari.util.PlatformUtil;
import fr.inrae.agroclim.getari.view.FxmlView;
import fr.inrae.agroclim.indicators.resources.I18n;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;

/**
 * Display About informations.
 *
 * Last change $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
@Log4j2
public final class AboutController extends AbstractController implements Initializable {

    /**
     * Format memory size in MB.
     *
     * @param msg  message resources
     * @param key  key for memory type
     * @param size memory size (Bytes)
     * @return formated memory size
     */
    private static String prettySize(final I18n msg, final String key,
            final long size) {
        final double mb = 1024. * 1024.;
        return msg.format(key, String.format("%.2f %s%n", size / mb, msg.get("mb")));
    }

    /**
     * Close button.
     */
    @FXML
    private Button close;

    /**
     * DOI of the software.
     */
    @FXML
    private Label doi;

    /**
     * System information.
     */
    @FXML
    private TextArea sysinfo;

    /**
     * Title.
     */
    @FXML
    private Label version;

    @Override
    protected Stage getStage() {
        return (Stage) close.getScene().getWindow();
    }

    @Override
    @SuppressWarnings("checkstyle:linelength")
    public void initialize(final URL url, final ResourceBundle rb) {
        final I18n msg = new I18n(rb);

        final String getariRevision = Version.getString("build.number");
        final String getariVersion = Version.getString("version");
        final String getariDate = Version.getString("build.date");

        final String indicatorsRevision = fr.inrae.agroclim.indicators.resources.Version.getString("build.number");
        final String indicatorsVersion = fr.inrae.agroclim.indicators.resources.Version.getString("version");
        final String indicatorsDate = fr.inrae.agroclim.indicators.resources.Version.getString("build.date");

        version.setText("Getari-" + getariVersion);
        final StringBuilder sb = new StringBuilder();
        sb.append(msg.format("product.version", "Getari-" + getariVersion,
                getariRevision, getariDate)).append("\n");
        sb.append(msg.format("builtwith", Version.getString("maven.version")))
        .append("\n").append("\n");
        sb.append(msg.format("indicators.version", indicatorsVersion,
                indicatorsRevision, indicatorsDate)).append("\n");
        sb.append(msg.format("java.version", System.getProperty("java.vm.name"),
                System.getProperty("java.vm.version"),
                System.getProperty("java.version"))).append("\n");
        sb.append(msg.format("javafx.version", PlatformUtil.getJavaFxVersion()))
        .append("\n");
        sb.append(msg.format("operatingsystem", System.getProperty("os.name"),
                System.getProperty("os.version"),
                System.getProperty("os.arch")))
        .append("\n");
        final Runtime runtime = Runtime.getRuntime();
        sb.append(prettySize(msg, "jvm.maxmemory", runtime.maxMemory()));
        sb.append(prettySize(msg, "jvm.usedmemory",
                runtime.totalMemory() - runtime.freeMemory()));
        sb.append(prettySize(msg, "jvm.totalmemory", runtime.totalMemory()));
        sb.append(prettySize(msg, "jvm.freememory", runtime.freeMemory()));
        sysinfo.setText(sb.toString());
    }

    /**
     * @param event onAction of button
     */
    @FXML
    private void onCloseAction(final ActionEvent event) {
        logAppend("close");
        close();
    }

    /**
     * @param event onMouse of button
     */
    @FXML
    private void onDoiClick(final MouseEvent event) {
        logAppend("doi");
        PlatformUtil.openBrowser(doi.getText());
    }

    /**
     * @param event onAction of button
     */
    @FXML
    private void onLicenseAction(final ActionEvent event) {
        logAppend("license");
        try {
            new FxmlView("license").build(getStage());
        } catch (final IOException e) {
            LOGGER.fatal("Error while loading license view.", e);
        }
    }

    /**
     * @param event onAction of button
     */
    @FXML
    private void onWebsiteAction(final ActionEvent event) {
        logAppend("website");
        PlatformUtil.openBrowser("https://w3.avignon.inrae.fr/getari/");
    }
}
