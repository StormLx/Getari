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

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;

import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineManagerFactory;
import com.taskadapter.redmineapi.bean.Attachment;
import com.taskadapter.redmineapi.bean.Issue;

import fr.inrae.agroclim.getari.component.GetariApp;
import fr.inrae.agroclim.getari.resources.Version;
import fr.inrae.agroclim.getari.util.PlatformUtil;
import fr.inrae.agroclim.getari.util.StringUtils;
import fr.inrae.agroclim.indicators.exception.TechnicalException;
import fr.inrae.agroclim.indicators.model.Evaluation;
import fr.inrae.agroclim.indicators.model.EvaluationSettings;
import fr.inrae.agroclim.indicators.xml.XMLUtil;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.extern.log4j.Log4j2;

/**
 * Support request form handling.
 *
 * Last change $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
@Log4j2
public final class SupportController implements Initializable {

    /**
     * Delay after sending issue before automatically closing the window.
     */
    private static final Duration THREE_SECONDS = Duration.seconds(3);
    /**
     * Format memory size in MB.
     *
     * @param key key for memory type
     * @param size memory size (Bytes)
     * @return formated memory size
     */
    private static String prettySize(final String key, final long size) {
        final double mb = 1024d * 1024d;
        return key + String.format(": %.2fMB", size / mb) + "\n";
    }

    /**
     * @param value email address
     * @return email is valid
     */
    private static boolean validateEmailAddress(final String value) {
        final String regex;
        regex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return value.matches(regex);
    }

    /**
     * Request body.
     */
    @FXML
    private TextArea body;

    /**
     * Send button.
     */
    @FXML
    private Button button;

    /**
     * Send request with evaluation and data files.
     */
    @FXML
    private CheckBox evaluation;

    /**
     * Displayed message.
     */
    @FXML
    private Label label;

    /**
     * Send request with screenshot.
     */
    @FXML
    private CheckBox screenshot;

    /**
     * User email address.
     */
    @FXML
    private TextField email;

    /**
     * Request subject.
     */
    @FXML
    private TextField subject;

    /**
     * User name.
     */
    @FXML
    private TextField username;

    /**
     * User's preferences for GETARI.
     */
    private Preferences preferences;

    /**
     * View resources.
     */
    private ResourceBundle resources;

    /**
     * Add a file into a zip.
     *
     * @param zos Zip stream
     * @param file file to add
     * @throws IOException error while adding the zip entry or reading the file
     */
    private void addZipEntry(final ZipOutputStream zos,
            final File file) throws IOException {
        addZipEntry(zos, file, file.getName());
    }

    /**
     * Add a file into a zip with a new name.
     *
     * @param zos Zip stream
     * @param file file to add
     * @param fileName new name for the added file
     * @throws IOException error while adding the zip entry or reading the file
     */
    private void addZipEntry(final ZipOutputStream zos,
            final File file, final String fileName) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            zos.putNextEntry(new ZipEntry(fileName));
            int length;
            final int bufferSize = 1024;
            final byte[] buffer = new byte[bufferSize];
            while ((length = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, length);
            }
            zos.closeEntry();
        }
    }

    /**
     * Close the stage.
     */
    private void close() {
        ((Stage) body.getScene().getWindow()).close();
    }

    /**
     * @return success
     * @throws IOException when uploading attachment
     * @throws RedmineException when uploading attachment and creating issue
     * @see http://www.redmine.org/projects/redmine/wiki/Rest_api_with_java
     */
    private Boolean createIssue() throws RedmineException, IOException {
        final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        // Build issue description
        final StringBuilder sb = new StringBuilder(body.getText());
        sb.append("\n----\n");
        sb.append(username.getText());
        sb.append("\n");
        sb.append(email.getText());
        sb.append("\nGETARI : ");
        sb.append(Version.getString("version"));
        sb.append(" ; ");
        sb.append(Version.getString("build.number"));
        sb.append(" ; ");
        sb.append(Version.getString("build.date"));
        sb.append("\nJavaFX : ");
        sb.append(PlatformUtil.getJavaFxVersion());
        final List<String> keys = Arrays.asList("java.version", "java.vm.version",
                "java.vendor", "java.vm.vendor", "os.name", "os.arch",
                "os.version", "user.name");
        keys.forEach(key -> {
            sb.append("\n");
            sb.append(key);
            sb.append(" : ");
            sb.append(System.getProperty(key));
        });
        sb.append("\nDefault locale : ");
        sb.append(Locale.getDefault());
        sb.append("\nPlatform encoding : ");
        sb.append(System.getProperty("file.encoding", "<unknown encoding>"));
        final Runtime runtime = Runtime.getRuntime();
        sb.append("\n");
        sb.append(prettySize("JVM heap size", runtime.maxMemory()));
        sb.append(prettySize("Used memory",
                runtime.totalMemory() - runtime.freeMemory()));
        sb.append(prettySize("Total memory", runtime.totalMemory()));
        sb.append(prettySize("Free memory", runtime.freeMemory()));
        sb.append("Screen: ")
        .append(screen.getWidth())
        .append("x")
        .append(screen.getHeight())
        .append(" ")
        .append(Toolkit.getDefaultToolkit().getScreenResolution())
        .append(" dpi");
        sb.append("\n----\nDemande créée depuis GETARI");
        //

        final String uri = "https://w3.avignon.inrae.fr/forge/";
        final String apiAccessKey = "9ecb1fcb46acaef3f9b54207b6e45ce57dcc0911";
        // https://w3.avignon.inrae.fr/forge/projects.xml?key=
        // Public GETARI:
        final int projectId = 32;

        final RedmineManager mgr = RedmineManagerFactory.createWithApiKey(uri,
                apiAccessKey);
        // Create issue
        final Issue issue = new Issue(mgr.getTransport(), projectId)
                .setSubject(subject.getText())
                .setDescription(sb.toString());
        if (screenshot.isSelected()) {
            final String contentType = "image/png";
            final File file = takeScreenshot();
            if (file != null) {
                Attachment attachment;
                attachment = mgr.getAttachmentManager().uploadAttachment(
                        contentType, file);
                issue.addAttachment(attachment);
                LOGGER.info("{} is attached",
                        file.getAbsolutePath());
            }
        }
        if (evaluation.isSelected()) {
            final File file = makeEvaluationArchive();
            if (file != null) {
                Attachment attachment;
                attachment = mgr.getAttachmentManager().uploadAttachment(
                        "application/zip", file);
                attachment.setFileName("evaluation.zip");
                issue.addAttachment(attachment);
                LOGGER.info("{} is attached as evaluation.zip",
                        file.getAbsolutePath());
            }
        }
        final File actionLog = makeActionsLogArchive();
        if (actionLog != null) {
            Attachment attachment;
            attachment = mgr.getAttachmentManager().uploadAttachment(
                    "application/zip", actionLog);
            attachment.setFileName("action-log.zip");
            issue.addAttachment(attachment);
            LOGGER.info("{} is attached as action-log.zip", actionLog.getAbsolutePath());
        }
        issue.create();

        return true;
    }

    /**
     * Shortcut.
     *
     * @param key property of view resources.
     * @return translated string.
     */
    private String i18n(final String key) {
        return resources.getString(key);
    }

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        resources = rb;
        preferences = Preferences.userNodeForPackage(this.getClass());

        String usernameText;
        usernameText = preferences.get("username", null);
        if (StringUtils.isBlank(usernameText)) {
            usernameText = System.getProperty("user.name");
        }
        username.setText(usernameText);

        String emailText;
        emailText = preferences.get("email", null);
        if (StringUtils.isBlank(emailText)) {
            emailText = System.getenv("EMAIL");
        }
        email.setText(emailText);
    }

    /**
     * @return file with actions.log, zipped.
     */
    private File makeActionsLogArchive() {
        if (GetariApp.get().getLog() == null) {
            return null;
        }
        final Path path = GetariApp.get().getLog().getPath();
        if (path == null) {
            return null;
        }
        File zipFile;
        try {
            zipFile = Files.createTempFile("actions-log",
                    ".zip").toFile();
            final FileOutputStream fos = new FileOutputStream(zipFile);
            // evaluation.gri
            try (ZipOutputStream zos = new ZipOutputStream(fos)) {
                addZipEntry(zos, path.toFile(), "actions.log");
            }
            return zipFile;
        } catch (final IOException ex) {
            LOGGER.catching(ex);
        }
        return null;
    }

    /**
     * @return file with evaluation, climatic and phenological data
     */
    private File makeEvaluationArchive() {
        final Evaluation e = GetariApp.get().getCurrentEval();
        if (e == null) {
            return null;
        }
        final EvaluationSettings settings = e.getSettings();
        if (settings == null) {
            return null;
        }

        try {
            // .gri
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            settings.setTimestamp(LocalDateTime.now());
            settings.setVersion(fr.inrae.agroclim.indicators.resources.Version.getVersionAndBuildDate());
            XMLUtil.serialize(settings, baos, EvaluationSettings.CLASSES_FOR_JAXB);
            final Path griPath = Files.createTempFile("evaluation", ".gri");
            try (OutputStream os = new FileOutputStream(griPath.toFile())) {
                baos.writeTo(os);
            }
            // .zip
            final File zipFile = Files.createTempFile("getari-evaluation-", ".zip").toFile();
            final FileOutputStream fos = new FileOutputStream(zipFile);
            // evaluation.gri
            try (ZipOutputStream zos = new ZipOutputStream(fos)) {
                // evaluation.gri
                addZipEntry(zos, griPath.toFile(), "evaluation.gri");
                // climate.csv
                if (settings.getClimateLoader() != null
                        && settings.getClimateLoader().getFile() != null) {
                    final File f = settings.getClimateLoader().getFile().getFile();
                    addZipEntry(zos, f);
                }
                // pheno.csv
                if (settings.getPhenologyLoader() != null
                        && settings.getPhenologyLoader().getFile() != null) {
                    final File f = settings.getPhenologyLoader().getFile().getFile();
                    addZipEntry(zos, f);
                }
            }
            Files.delete(griPath);
            return zipFile;
        } catch (final IOException ioe) {
            LOGGER.error("Error creating zip file", ioe);
        } catch (final TechnicalException ex) {
            LOGGER.error("Error while creating gri file", ex);
        }
        return null;
    }

    /**
     * @param event onAction of button
     */
    @FXML
    private void onButtonAction(final ActionEvent event) {
        if (StringUtils.isBlank(username.getText())
                || StringUtils.isBlank(email.getText())
                || StringUtils.isBlank(subject.getText())
                || StringUtils.isBlank(body.getText())) {
            label.setText(i18n("error.notempty"));
            label.getStyleClass().add("error");
            return;
        }
        if (!validateEmailAddress(email.getText())) {
            label.setText(i18n("error.invalidemail"));
            label.getStyleClass().add("error");
            return;
        }
        label.getStyleClass().remove("error");
        preferences.put("username", username.getText());
        preferences.put("email", email.getText());
        boolean success = false;
        try {
            success = createIssue();
        } catch (final RedmineException e) {
            if (e.getCause() instanceof java.net.UnknownHostException) {
                label.setText(i18n("failure.UnknownHostException"));
                return;
            }
            LOGGER.error(e);
        } catch (final IOException e) {
            LOGGER.error(e);
        }
        if (success) {
            label.setText(i18n("success"));
            username.setDisable(true);
            email.setDisable(true);
            subject.setDisable(true);
            body.setDisable(true);
            evaluation.setDisable(true);
            screenshot.setDisable(true);
            button.setDisable(true);
            final Timeline timeline = new Timeline(
                    new KeyFrame(THREE_SECONDS, ae -> close())
                    );
            timeline.play();

        } else {
            label.setText(i18n("failure"));
        }
    }

    /**
     * @return screenshot file of GETARI main stage
     */
    private File takeScreenshot() {
        final Scene scene = GetariApp.getMainStage().getScene();
        final WritableImage image = new WritableImage((int) scene.getWidth(),
                (int) scene.getHeight());
        scene.snapshot(image);

        File file;
        try {
            file = File.createTempFile("screenshot", ".png");
        } catch (final IOException e) {
            LOGGER.error(e);
            return null;
        }

        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            LOGGER.info(file.getAbsolutePath());
        } catch (final IOException e) {
            LOGGER.error(e);
            return null;
        }
        return file;
    }
}
