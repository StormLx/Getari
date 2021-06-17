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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import fr.inrae.agroclim.getari.component.EvaluationTextField;
import fr.inrae.agroclim.getari.component.GetariApp;
import fr.inrae.agroclim.getari.controller.MainViewController;
import fr.inrae.agroclim.getari.controller.SelectDataController;
import fr.inrae.agroclim.getari.event.ConsistencyEvent;
import fr.inrae.agroclim.getari.exception.GetariException;
import fr.inrae.agroclim.getari.resources.Messages;
import fr.inrae.agroclim.getari.view.SelectDataView;
import fr.inrae.agroclim.indicators.model.Evaluation;
import fr.inrae.agroclim.indicators.model.EvaluationSettings;
import fr.inrae.agroclim.indicators.model.data.climate.ClimateFileLoader;
import fr.inrae.agroclim.indicators.model.data.phenology.PhenologyFileLoader;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.adapter.JavaBeanDoublePropertyBuilder;
import javafx.beans.property.adapter.JavaBeanIntegerPropertyBuilder;
import javafx.beans.property.adapter.JavaBeanProperty;
import javafx.beans.property.adapter.JavaBeanStringProperty;
import javafx.beans.property.adapter.JavaBeanStringPropertyBuilder;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.image.Image;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

/**
 * Classe utilitaire de génération de composants graphiques uniformisés.
 *
 * Last change $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
@Log4j2
public final class ComponentUtil {

    /**
     * Prefered width of combo boxes, spinners and text boxes, to align them.
     */
    public static final int PREF_WIDTH = 150;

    /**
     * Sync value when user uses keyboard.
     *
     * @param <T>    parser class
     * @param sp     spinner to hack
     * @param parser String parser
     */
    public static <T> void addKeyReleasedEventHandling(final Spinner<T> sp, final Function<String, T> parser) {
        sp.getEditor().setOnKeyReleased(e -> {
            switch (e.getCode()) {
            case CONTROL:
            case FINAL:
            case ENTER:
            case HOME:
            case LEFT:
            case RIGHT:
            case SHIFT:
            case TAB:
                return;
            default:
                // do
            }
            final String text = sp.getEditor().getText();
            final int pos = sp.getEditor().getCaretPosition();
            if (text == null || text.isEmpty()) {
                sp.getValueFactory().setValue(null);
                return;
            }
            try {
                final T value = parser.apply(text.replace(",", "."));
                sp.getValueFactory().setValue(value);
                sp.getEditor().positionCaret(pos);
            } catch (final NumberFormatException ex) {
                LOGGER.trace("text={}", text);
            }
        });
    }

    /**
     * Bind object property to input control.
     *
     * @param inputControl binded input control
     * @param c            object
     * @param fieldName    object property name
     */
    @SuppressWarnings("unchecked")
    public static void bindSpinnerToDouble(@NonNull final Spinner<Double> inputControl, final Object c,
            final String fieldName) {
        Objects.requireNonNull(inputControl.getValueFactory());
        Objects.requireNonNull(inputControl.getValueFactory().valueProperty());
        JavaBeanProperty<? extends Number> beanProperty;
        try {
            beanProperty = JavaBeanDoublePropertyBuilder.create().name(fieldName).bean(c).build();
            Bindings.bindBidirectional(inputControl.getValueFactory().valueProperty(),
                    (JavaBeanProperty<Double>) beanProperty);
        } catch (final NoSuchMethodException ex) {
            LOGGER.error(ex);
        }
    }

    /**
     * Bind object property to input control.
     *
     * @param inputControl binded input control
     * @param c            object
     * @param fieldName    object property name
     */
    @SuppressWarnings("unchecked")
    public static void bindSpinnerToInteger(@NonNull final Spinner<Integer> inputControl, final Object c,
            final String fieldName) {
        Objects.requireNonNull(inputControl.getValueFactory());
        Objects.requireNonNull(inputControl.getValueFactory().valueProperty());
        JavaBeanProperty<? extends Number> beanProperty;
        try {
            beanProperty = JavaBeanIntegerPropertyBuilder.create().name(fieldName).bean(c).build();
            Bindings.bindBidirectional(inputControl.getValueFactory().valueProperty(),
                    (JavaBeanProperty<Integer>) beanProperty);
        } catch (final NoSuchMethodException ex) {
            LOGGER.error(ex);
        }
    }

    /**
     * Bind object property to input control.
     *
     * @param inputControl binded input control
     * @param c            object
     * @param fieldName    object property name
     */
    @SuppressWarnings("unchecked")
    public static void bindTextToDouble(final TextInputControl inputControl, final Object c, final String fieldName) {
        JavaBeanProperty<? extends Number> beanProperty;
        StringConverter<? extends Number> converter;
        try {
            beanProperty = JavaBeanDoublePropertyBuilder.create().name(fieldName).bean(c).build();
            converter = new DoubleStringConverter();
            Bindings.bindBidirectional(inputControl.textProperty(), (JavaBeanProperty<Double>) beanProperty,
                    (StringConverter<Double>) converter);
        } catch (final NoSuchMethodException ex) {
            LOGGER.error(ex);
        }
    }

    /**
     * Bind object property to input control.
     *
     * @param inputControl binded input control
     * @param c            object
     * @param fieldName    object property name
     */
    @SuppressWarnings("unchecked")
    public static void bindTextToInteger(final TextInputControl inputControl, final Object c, final String fieldName) {
        JavaBeanProperty<? extends Number> beanProperty;
        StringConverter<? extends Number> converter;
        try {
            beanProperty = JavaBeanIntegerPropertyBuilder.create().name(fieldName).bean(c).build();
            converter = new IntegerStringConverter();
            Bindings.bindBidirectional(inputControl.textProperty(), (JavaBeanProperty<Integer>) beanProperty,
                    (StringConverter<Integer>) converter);
        } catch (final NoSuchMethodException ex) {
            LOGGER.error(ex.getMessage());
        }
    }

    /**
     * Bind object property to input control.
     *
     * @param inputControl binded input control
     * @param c            object
     * @param fieldName    object property name
     */
    public static void bindTextToString(final TextInputControl inputControl, @NonNull final Object c,
            final String fieldName) {
        try {
            final JavaBeanStringProperty beanProperty = JavaBeanStringPropertyBuilder.create().name(fieldName).bean(c)
                    .build();
            Bindings.bindBidirectional(inputControl.textProperty(), beanProperty);
        } catch (final NoSuchMethodException ex) {
            LOGGER.error(ex.getMessage());
        }
    }

    /**
     * Launch GetariFileChooser to choose file for the type.
     *
     * @param stage attached stage for dialog (usually: primary stage)
     * @param type  GetariConstants.CLIMATIC or else for pheno
     * @return chosen file
     */
    public static Optional<File> chooseDataFile(final FileType type, final Stage stage) {
        final GetariFileChooser chooser = new GetariFileChooser(type);
        final File file = chooser.showOpenDialog(stage);
        if (file != null && file.exists() && (type == FileType.CLIMATE || type == FileType.PHENOLOGY)) {

            boolean isValid = false;
            try {
                isValid = ComponentUtil.isAValidCsvFile(file);
            } catch (final GetariException e) {
                LOGGER.error(e);
            }

            if (!isValid) {
                AlertUtils.showError(Messages.getString("filechoser.not.valid.text", file.toString()));
            } else {
                return Optional.of(file);
            }
        }
        return Optional.empty();
    }

    /**
     * Set choose action on button and related text field.
     *
     * @param browse     button
     * @param fileName   text field
     * @param stage      attached stage for dialog (usually: primary stage)
     * @param type       GetariConstants.CLIMATIC or else for pheno
     * @param evaluation to get loaders
     */
    public static void chooseDataFileButton(final Button browse, final EvaluationTextField fileName, final Stage stage,
            final FileType type, final Evaluation evaluation) {
        final EvaluationSettings settings = evaluation.getSettings();
        fileName.setPrefWidth(400);
        fileName.setEditable(false);
        browse.setOnAction((final ActionEvent t) -> {
            final Optional<File> choice = chooseDataFile(type, stage);
            if (choice.isPresent()) {
                final File file = choice.get();
                try {
                    final SelectDataView view = new SelectDataView();
                    view.build(stage);
                    final SelectDataController controller = view.getController();
                    controller.setFile(file);
                    controller.setType(type);
                    controller.setOkCallback((values, midnight) -> {
                        final String separator = values.get(0);
                        values.remove(0);
                        String[] headers = new String[values.size()];
                        headers = values.toArray(headers);
                        if (type.equals(FileType.CLIMATE)) {
                            ClimateFileLoader loader;
                            loader = settings.getClimateLoader().getFile();
                            loader.setMidnight(midnight);
                            loader.setFile(file);
                            loader.setSeparator(separator);
                            loader.setHeaders(headers);
                        } else {
                            PhenologyFileLoader loader;
                            loader = settings.getPhenologyLoader().getFile();
                            loader.setFile(file);
                            loader.setSeparator(separator);
                            loader.setHeaders(headers);
                        }
                        fileName.setText(file.getAbsolutePath());
                        fileName.setValid();
                        try {
                            evaluation.initializeResources();
                        } catch (final Exception e) {
                            AlertUtils.showException("pb", e);
                        }
                        evaluation.getResourceManager().setCropDevelopmentYears(1);
                        final ConsistencyEvent event = new ConsistencyEvent();
                        evaluation.getResourceManager().setTimeScale(evaluation.getTimescale());
                        event.setErrors(evaluation.getResourceManager().getConsitencyErrors());
                        GetariApp.get().getEventBus().fireEvent(event);
                        evaluation.validate();
                    });
                    controller.setTimeScale(evaluation.getTimescale());
                    controller.initializeView();
                } catch (final IOException ex) {
                    LOGGER.fatal("this should never occur!", ex);
                }
            } else {
                fileName.setInvalid();
            }
        });
    }

    /**
     * Fire close handler on tab.
     *
     * @param tab tab to close
     */
    public static void closeTab(final Tab tab) {
        final EventHandler<Event> handler = tab.getOnClosed();
        if (null != handler) {
            handler.handle(null);
        } else {
            tab.getTabPane().getTabs().remove(tab);
        }
    }

    /**
     * Get file from dragging action.
     *
     * @param db dragboard
     * @return dragged file, if extension is supported
     */
    private static File getFile(final Dragboard db) {
        File file = null;
        if (db.hasFiles()) {
            if (db.getFiles().isEmpty()) {
                LOGGER.warn("This should never occur!");
            } else if (db.getFiles().size() > 1) {
                LOGGER.warn("Opening several files is not handled!");
            } else {
                file = db.getFiles().get(0);
                final String extension = StringUtils.extension(file.getName());
                if (!"csv".equals(extension) && !"gri".equals(extension) && !"out".equals(extension)) {
                    LOGGER.warn(Messages.getString("warning.extension.not.handled"), extension);
                    file = null;
                }
            }
        }
        return file;
    }

    /**
     * Called by view visitors for common layout.
     *
     * @param gridPane grid pane
     */
    public static void initGridPane(final GridPane gridPane) {
        gridPane.setGridLinesVisible(false);
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        final ColumnConstraints col1 = new ColumnConstraints();
        col1.setMinWidth(125);
        gridPane.getColumnConstraints().add(col1);
    }

    /**
     * Parse first lines of file to validate file.
     *
     * @param fileToParse file to parse
     * @return true: file is a CSV
     * @throws GetariException if error while reading the file
     */
    public static boolean isAValidCsvFile(final File fileToParse) throws GetariException {

        // Lire les 3 premières lignes pour valider le fichier : même nombre de
        // colonnes obligatoire
        // ne pas utiliser supercsv mais ça :
        // http://howtodoinjava.com/2013/05/27/parse-csv-files-in-java/
        String separator = null;
        Boolean isValidNomberOfColumns = true;
        int numberOfColumns = 0;
        final int nbOfLines = 3;
        final String comma = ",";
        final String semicolon = ";";
        final String tab = "\t";

        try (
                // Create the file reader
                BufferedReader fileReader = new BufferedReader(new FileReader(fileToParse));) {
            String line = fileReader.readLine();

            String[] tokens = line.split(tab);

            if (tokens.length <= 1) {
                tokens = line.split(semicolon);
                if (tokens.length <= 1) {
                    tokens = line.split(comma);
                    if (tokens.length > 1) {
                        separator = comma;
                        numberOfColumns = tokens.length;
                    }
                } else {
                    separator = semicolon;
                    numberOfColumns = tokens.length;
                }
            } else {
                separator = tab;
                numberOfColumns = tokens.length;
            }
            int i = 1;
            if (separator != null) {
                // Read the file line by line
                line = fileReader.readLine();
                while (line != null && i <= nbOfLines) {
                    // Get all tokens available in line
                    tokens = line.split(separator);

                    if (tokens.length != numberOfColumns) {
                        isValidNomberOfColumns = false;
                    }
                    line = fileReader.readLine();
                    i++;
                }
            }
        } catch (final IOException | NullPointerException e) {
            throw new GetariException(e.getMessage());
        }
        return separator != null && isValidNomberOfColumns;

    }

    /**
     * @param f file name in resources in images/
     * @return loaded image
     */
    public static Image loadImage(final String f) {
        final String resource = "/fr/inrae/agroclim/getari/images/" + f;
        final URL url = ComponentUtil.class.getResource(resource);
        if (url != null) {
            return new Image(url.toExternalForm());
        }
        LOGGER.fatal("resource not found: " + resource);
        return null;
    }

    /**
     * Create a text field to handle an Double property.
     *
     * @return text field
     */
    public static TextField newDoubleField() {
        return new EvaluationTextField(Double.class, GetariApp.get().getCurrentEval());
    }

    /**
     * Create a spinner to handle an Double property.
     *
     * @param negativeAllowed if negative values are allowed
     * @return spinner field
     */
    public static Spinner<Double> newDoubleSpinner(final boolean negativeAllowed) {
        final double min;
        if (negativeAllowed) {
            min = -100;
        } else {
            min = 0;
        }
        final double max = 100;
        final double initialValue = 0;
        final double amountToStepBy = 1;
        return newDoubleSpinner(min, max, initialValue, amountToStepBy);
    }

    /**
     * Create a spinner instance to handle an Double property.
     *
     * @param min            The minimum allowed double value for the Spinner.
     * @param max            The maximum allowed double value for the Spinner.
     * @param initialValue   The value of the Spinner when first instantiated, must
     *                       be within the bounds of the min and max arguments, or
     *                       else the min value will be used.
     * @param amountToStepBy The amount to increment or decrement by, per step.
     * @return the instancied and parametrized spinner
     */
    public static Spinner<Double> newDoubleSpinner(final double min, final double max, final double initialValue,
            final double amountToStepBy) {
        final Spinner<Double> sp = new Spinner<>();
        // User a value factory to use a DoubleStringConverter to allow precision with
        // more than 2 digits
        final SpinnerValueFactory<Double> valueFactory;
        valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(min, max, initialValue, amountToStepBy);
        valueFactory.setConverter(new DoubleStringConverter());
        sp.setValueFactory(valueFactory);
        sp.setEditable(true);
        final int height = 20;
        sp.setPrefWidth(PREF_WIDTH);
        sp.setPrefHeight(height);
        sp.valueProperty().addListener(
                (final ObservableValue<? extends Double> observable, final Double oldValue, final Double newValue) -> {
                    if ((oldValue == null && newValue != null || newValue != null && !newValue.equals(oldValue))
                            && GetariApp.get().getCurrentEval() != null) {
                        GetariApp.get().getCurrentEval().setTranscient(true);
                    }
                });
        addKeyReleasedEventHandling(sp, Double::parseDouble);
        return sp;
    }

    /**
     * Create a spinner instance to handle an Integer property.
     *
     * @param min            The minimum allowed double value for the Spinner.
     * @param max            The maximum allowed double value for the Spinner.
     * @param initialValue   The value of the Spinner when first instantiated, must
     *                       be within the bounds of the min and max arguments, or
     *                       else the min value will be used.
     * @param amountToStepBy The amount to increment or decrement by, per step.
     * @return the instancied and parametrized spinner
     */
    public static Spinner<Integer> newIntegerSpinner(final int min, final int max, final int initialValue,
            final int amountToStepBy) {
        final Spinner<Integer> sp = new Spinner<>(min, max, initialValue, amountToStepBy);
        sp.setEditable(true);
        final int height = 20;
        sp.setPrefWidth(PREF_WIDTH);
        sp.setPrefHeight(height);
        sp.valueProperty().addListener((final ObservableValue<? extends Integer> observable, final Integer oldValue,
                final Integer newValue) -> {
                    if (!oldValue.equals(initialValue) && !newValue.equals(oldValue)
                            && GetariApp.get().getCurrentEval() != null) {
                        GetariApp.get().getCurrentEval().setTranscient(true);
                    }
                });
        addKeyReleasedEventHandling(sp, Integer::parseInt);
        return sp;
    }

    /**
     * @param key I18n key for label text
     * @return title label with text
     */
    public static Label newLabel(final String key) {
        return new Label(Messages.getString(key));
    }

    /**
     * Create a text field to handle Evaluation properties.
     *
     * @return text field
     */
    public static EvaluationTextField newTextField() {
        return new EvaluationTextField(String.class, GetariApp.get().getCurrentEval());
    }

    /**
     * Bind style property on hover to change cursor.
     *
     * @param node node to bind
     */
    public static void setCursorHoverStyleProperty(final Node node) {
        node.styleProperty()
        .bind(Bindings.when(node.hoverProperty()).then(new SimpleStringProperty(GetariConstants.HAND_CURSOR))
                .otherwise(new SimpleStringProperty(GetariConstants.DEFAULT_CURSOR)));
    }

    /**
     * Bind style property on show to change cursor.
     *
     * @param contextMenu menu to bind
     */
    public static void setCursorShowingStyleProperty(final ContextMenu contextMenu) {
        contextMenu.styleProperty()
        .bind(Bindings.when(contextMenu.showingProperty())
                .then(new SimpleStringProperty(GetariConstants.HAND_CURSOR))
                .otherwise(new SimpleStringProperty(GetariConstants.DEFAULT_CURSOR)));
    }

    /**
     * Set event handlers to open dragged files on StartView.
     *
     * @param controller controller to open evaluation file
     * @param sc         scene
     * @param closeStage stage of scene must be close after opening?
     */
    public static void setUpDragDropHandling(final MainViewController controller, final Scene sc,
            final boolean closeStage) {
        sc.setOnDragDropped(e -> {
            final Dragboard db = e.getDragboard();
            final File file = getFile(db);
            if (file != null) {
                e.setDropCompleted(true);
                final String extension = StringUtils.extension(file.getName());
                switch (extension) {
                case "csv":
                    controller.openCsvFile(file);
                    break;
                case "gri":
                    controller.openEvaluationFile(file, () -> {
                        if (closeStage) {
                            // get a handle to the stage
                            final Stage stage = (Stage) sc.getWindow();
                            // do what you have to do
                            stage.close();
                        }
                    });
                    break;
                case "out":
                    controller.openResultFile(file);
                    break;
                case "xml":
                    controller.openMEFile(file);
                    break;
                default:
                    throw new UnsupportedOperationException("Extension not handled: " + extension);
                }
            } else {
                e.setDropCompleted(false);
            }
        });
        sc.setOnDragOver(e -> {
            final Dragboard db = e.getDragboard();
            final File file = getFile(db);
            if (file != null) {
                e.acceptTransferModes(TransferMode.COPY);
            }
        });

    }

    /**
     * Serialize color as hexadecimal code.
     *
     * @param color color
     * @return hexadecimal code (as in HTML)
     */
    public static String toRGBCode(final Color color) {
        return String.format("#%02X%02X%02X", (int) (color.getRed() * 255), (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    /**
     * No constructor for helper class.
     */
    private ComponentUtil() {
    }
}
