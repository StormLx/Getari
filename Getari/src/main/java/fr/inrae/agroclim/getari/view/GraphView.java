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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import fr.inrae.agroclim.getari.component.GetariApp;
import fr.inrae.agroclim.getari.component.GetariPreferences;
import fr.inrae.agroclim.getari.controller.AddIndicatorHandler;
import fr.inrae.agroclim.getari.controller.IndicatorDetailsHandler;
import fr.inrae.agroclim.getari.controller.NodeRightClickHandler;
import fr.inrae.agroclim.getari.controller.ToolbarController;
import fr.inrae.agroclim.getari.event.EvaluationSaveEvent;
import fr.inrae.agroclim.getari.memento.History;
import fr.inrae.agroclim.getari.memento.Origin;
import fr.inrae.agroclim.getari.resources.Messages;
import fr.inrae.agroclim.getari.util.FileType;
import fr.inrae.agroclim.getari.util.GetariFileChooser;
import fr.inrae.agroclim.getari.util.NameablePhaseCell;
import fr.inrae.agroclim.getari.view.visitor.DetailedIndicatorViewVisitor;
import fr.inrae.agroclim.getari.view.visitor.GraphVisitor;
import fr.inrae.agroclim.indicators.model.Evaluation;
import fr.inrae.agroclim.indicators.model.data.DataLoadingListener;
import fr.inrae.agroclim.indicators.model.function.listener.AggregationFunctionListener;
import fr.inrae.agroclim.indicators.model.indicator.CompositeIndicator;
import fr.inrae.agroclim.indicators.model.indicator.Detailable;
import fr.inrae.agroclim.indicators.model.indicator.Indicator;
import fr.inrae.agroclim.indicators.model.indicator.IndicatorCategory;
import fr.inrae.agroclim.indicators.model.indicator.listener.IndicatorEvent;
import fr.inrae.agroclim.indicators.model.indicator.listener.IndicatorListener;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

/**
 * Vue de représentation du graphe d'indicateurs.
 *
 * Last change $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
@Log4j2
public class GraphView implements IndicatorListener, AggregationFunctionListener, EventHandler<EvaluationSaveEvent> {
    /**
     * CSS style for header.
     */
    private static final String CSS_GRAPH_HEADER = "graph-header";
    /**
     * Handle adding indicator.
     */
    private final AddIndicatorHandler addIndicatorHandler;
    /**
     * Handle clicking indicator to show details.
     */
    private final IndicatorDetailsHandler detailsHandler;
    /**
     * Errors exist in evaluation.
     */
    private final BooleanBinding existErrors;
    /**
     * Tab for details of evaluation in right panel.
     */
    private Tab generalTab;
    /**
     * Main for the graph.
     */
    private final GridPane graphPane = new GridPane();
    /**
     * Visitor for the graph editor of an evaluation.
     */
    private GraphVisitor graphVisitor;
    /**
     * Tab for details of indicator in right panel.
     */
    private Tab indicatorTab;
    /**
     * Partial view to display Evaluation logs.
     */
    private final LogView logView = new LogView();
    /**
     * Root node of logView.
     */
    private final Parent logViewParent;
    /**
     * GETARI main view.
     */
    private final MainView mainView;
    /**
     * Handle right clicking on background or node.
     */
    private final NodeRightClickHandler menuHandler;
    /**
     * Combo listing the phases.
     */
    private ComboBox<Indicator> phaseCombo;
    /**
     * Indicates that an evaluation is running.
     */
    private final ProgressIndicator progressIndicator = new ProgressIndicator();
    /**
     * Container for save button and progress indicator.
     */
    private final HBox rightButtonPane = new HBox();
    /**
     * Right pane containing the detailed views.
     */
    @Getter
    private final TabPane rightPane = new TabPane();
    /**
     * Button to save results.
     */
    private Button saveButton;
    /**
     * Scroll for the graph pane.
     */
    private ScrollPane scrollPane;
    /**
     * Selection model of tabulations in right panel.
     */
    private SingleSelectionModel<Tab> selectionModel;
    /**
     * Primary stage.
     */
    @Getter
    private final Stage stage;
	/**
	 * Current state.
	 */
	private Origin origin = new Origin();
	/**
	 * History of actions.
	 */
	@Getter
	private History history = new History();

    /**
     * Constructor.
     *
     * @param primaryStage primary stage
     * @param view main view of GETARI
     * @throws IOException if logView fails to build
     */
    public GraphView(final Stage primaryStage, final MainView view)
            throws IOException {
        this.addIndicatorHandler = new AddIndicatorHandler(this);
        this.detailsHandler = new IndicatorDetailsHandler(this);
        this.menuHandler = new NodeRightClickHandler(this);
        this.stage = primaryStage;
        this.mainView = view;
        logViewParent = logView.build();
        existErrors = this.logView.getController().existErrors();
    }

    /**
     * @return root node of view
     * @throws java.io.IOException should never occurs when building view
     */
    public final Parent build() throws IOException {
        LOGGER.trace("start");

        final VBox root = new VBox();
        root.setId("graphView");
        root.getStyleClass().add("mainPane");
        root.prefWidthProperty().bind(stage.getScene().widthProperty());
        root.prefHeightProperty().bind(stage.getScene().heightProperty());

        root.getChildren().add(buildToolBar());

        final VBox vPane = new VBox();
        vPane.prefHeightProperty().bind(
                stage.getScene().heightProperty().add(-350));

        // OK 11h44
        graphPane.getStyleClass().add("graphPane");
        graphPane.setId("graph");
        graphPane.setGridLinesVisible(false);
        final ColumnConstraints myCol1Constraint = new ColumnConstraints(150);
        final ColumnConstraints myCol2Constraint = new ColumnConstraints(65);
        myCol1Constraint.setHalignment(HPos.CENTER);
        graphPane.getColumnConstraints().add(myCol1Constraint);
        graphPane.getColumnConstraints().add(myCol2Constraint);
        graphPane.getColumnConstraints().add(myCol1Constraint);
        graphPane.getColumnConstraints().add(myCol2Constraint);
        graphPane.getColumnConstraints().add(myCol1Constraint);
        graphPane.getColumnConstraints().add(myCol2Constraint);
        graphPane.getColumnConstraints().add(myCol1Constraint);
        graphPane.getColumnConstraints().add(myCol2Constraint);
        graphPane.setAlignment(Pos.CENTER);
        graphPane.addEventHandler(MouseEvent.MOUSE_CLICKED, menuHandler);

        graphVisitor = new GraphVisitor(graphPane, detailsHandler,
                addIndicatorHandler);

        scrollPane = new ScrollPane();
        scrollPane.getStyleClass().add("scrollPane");
        scrollPane.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        scrollPane.prefWidthProperty().bind(
                stage.widthProperty().divide(3).multiply(2.17));
        scrollPane.prefHeightProperty().bind(vPane.heightProperty());
        /*
         * Bug in javafx : for background color, use -fx-background instead of
         * -fx-background-color
         */
        scrollPane.setStyle("-fx-background:white;");
        scrollPane.setContent(graphPane);
        vPane.getChildren().addAll(buildHeader(), scrollPane);

        final SplitPane hPane = new SplitPane();
        hPane.setOrientation(Orientation.HORIZONTAL);
        hPane.setDividerPositions(0.75);
        hPane.getItems().addAll(vPane, rightPane);

        final SplitPane global = new SplitPane();
        global.setOrientation(Orientation.VERTICAL);
        global.setDividerPositions(0.8);
        global.getItems().addAll(hPane, logViewParent);
        root.getChildren().add(global);

        final HBox bottom = new HBox();
        bottom.setStyle("-fx-background-color: #EEEEEE;");
        bottom.getChildren().add(new Label(""));
        root.getChildren().add(bottom);
        buildDetailPanel();

        return root;
    }

    /**
     * Build panel for indicator details.
     *
     * @param detailableElement indicator to detail
     */
    public final void buildDetailedPanel(final Detailable detailableElement) {
        Objects.requireNonNull(detailableElement, "detailableElement must not be null!");
        final DetailedIndicatorViewVisitor visitor = new DetailedIndicatorViewVisitor(this);
        visitor.build(detailableElement);
        setDetailedPanel(visitor.getNode());
        selectionModel.select(indicatorTab);
    }

    /**
     * Build panel for details : evaluation and indicator tabs.
     */
    private void buildDetailPanel() {
        selectionModel = rightPane.getSelectionModel();
        generalTab = new Tab();
        rightPane.getTabs().add(generalTab);
        generalTab.setText(Messages.getString("detail.tab.evaluation.title"));
        generalTab.setClosable(false);
        final Label emptyLabel = new Label(Messages.getString("detail.tab.empty"));
        emptyLabel.setWrapText(true);

        rightPane.getStyleClass().add("tab-pane");
        indicatorTab = new Tab();
        rightPane.getTabs().add(indicatorTab);
        indicatorTab.setText(Messages.getString("detail.tab.indicator.title"));
        indicatorTab.setClosable(false);
        final VBox myBox = new VBox();
        myBox.getChildren().add(emptyLabel);
        myBox.setPadding(new Insets(20));
        indicatorTab.setContent(myBox);

        selectionModel.select(generalTab);
        buildEvaluationPanel(GetariApp.get().getCurrentEval());
    }

    /**
     * Build panel for evaluation details.
     *
     * @param detailableElement evaluation to detail
     */
    private void buildEvaluationPanel(final Detailable detailableElement) {
        final DetailedIndicatorViewVisitor visitor = new DetailedIndicatorViewVisitor(this);
        visitor.build(detailableElement);
        setEvaluationPanel(visitor.getNode());
        selectionModel.select(generalTab);
    }

    /**
     * Draw column headers.
     *
     * @return Top pane for the column headers.
     */
    private GridPane buildHeader() {
        final GridPane headerPane = new GridPane();
        headerPane.setPrefHeight(80);
        final int colspan = 2;
        final int prefWidth = 220;
        final int prefHeight = 40;

        final Label firstColumnHeader = new Label(Messages.getString("graph.header.phases"));
        firstColumnHeader.setPrefWidth(prefWidth);
        firstColumnHeader.setPrefHeight(prefHeight);
        firstColumnHeader.setAlignment(Pos.BASELINE_LEFT);
        firstColumnHeader.getStyleClass().add(CSS_GRAPH_HEADER);

        final Label secondColumnHeader = new Label(Messages.getString("graph.header.practices"));
        final Tooltip tooltip = new Tooltip(Messages.getString("graph.header.practices.tooltip"));
        secondColumnHeader.setTooltip(tooltip);
        secondColumnHeader.setPrefWidth(prefWidth);
        secondColumnHeader.setPrefHeight(prefHeight);
        secondColumnHeader.setAlignment(Pos.BASELINE_LEFT);
        secondColumnHeader.getStyleClass().add(CSS_GRAPH_HEADER);

        final Label thirdColumnHeader = new Label(Messages.getString("graph.header.effects"));
        thirdColumnHeader.setPrefWidth(prefWidth);
        thirdColumnHeader.setPrefHeight(prefHeight);
        thirdColumnHeader.setAlignment(Pos.BASELINE_LEFT);
        thirdColumnHeader.getStyleClass().add(CSS_GRAPH_HEADER);

        final Label fourthColumnHeader = new Label(
                Messages.getString("graph.header.indicators"));
        fourthColumnHeader.setAlignment(Pos.BASELINE_LEFT);
        fourthColumnHeader.setPrefWidth(prefWidth);
        fourthColumnHeader.setPrefHeight(prefHeight);
        fourthColumnHeader.getStyleClass().add(CSS_GRAPH_HEADER);

        int colIndex = 0;
        headerPane.add(firstColumnHeader, colIndex, 0);
        GridPane.setColumnSpan(firstColumnHeader, colspan);

        colIndex += colspan;
        headerPane.add(secondColumnHeader, colIndex, 0);
        GridPane.setColumnSpan(secondColumnHeader, colspan);

        colIndex += colspan;
        headerPane.add(thirdColumnHeader, colIndex, 0);
        GridPane.setColumnSpan(thirdColumnHeader, colspan);

        colIndex += colspan;
        headerPane.add(fourthColumnHeader, colIndex, 0);
        GridPane.setColumnSpan(fourthColumnHeader, colspan);

        return headerPane;
    }

    /**
     * @return created toolbar
     * @throws IOException should never occur when building view.
     */
    private Parent buildToolBar() throws IOException {
        final ToolbarView toolbarView = new ToolbarView();
        final Parent pane = toolbarView.build();
        final ToolbarController controller = toolbarView.getController();
        controller.setHistory(getHistory());
        controller.setClearCmd(() -> onClearAction(null));
        controller.setCloseCmd(mainView::closeCurrentTab);
        controller.setNewCmd(this::showNewPhaseDialog);
        controller.setEvaluateCmd(() -> {
            LOGGER.trace("EvaluateCmd()");
            GetariApp.get().runCurrentEvaluation();
        });
        phaseCombo = controller.getPhaseCombo();
        NameablePhaseCell.setCellFactory(phaseCombo);
        phaseCombo
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
                (final ObservableValue<? extends Indicator> observable,
                        final Indicator oldValue, final Indicator newValue) -> {
                            final VBox cell = graphVisitor.getCell(newValue);
                            ensureVisible(scrollPane, cell);
                        });
        saveButton = controller.getSaveBtn();
        configureSaveButton(saveButton, stage);
        controller.getEvaluateBtnDisableProperty().bind(existErrors);
        return pane;
    }

    /**
     * Empty indicator tab.
     */
    private void clearDetailedPanel() {
        indicatorTab.setContent(null);
    }

    /**
     * Empty evaluation tab.
     */
    private void clearGeneralPanel() {
        generalTab.setContent(null);
    }

    /**
     * Configure button to act as a Save/Save As button.
     *
     * @param save button to configure
     * @param primaryStage stage for the GetariFileChooser dialog
     */
    private void configureSaveButton(final Button save,
            final Stage primaryStage) {
        if (GetariApp.get().getCurrentEval().isNew()) {
            save.setText(Messages.getString("action.saveas"));
            save.setOnAction((final ActionEvent t) -> {
                logAppend("Save as");
                final GetariFileChooser chooser = new GetariFileChooser(FileType.EVALUATION);
                final File file = chooser.showSaveDialog(primaryStage);
                if (file != null) {
                    final String absolutePath = file.getParentFile().getAbsolutePath();
                    final GetariPreferences prefs = GetariApp.get().getPreferences();
                    prefs.setLastDirectory(FileType.EVALUATION, absolutePath);
                    GetariApp.setLastDirectory(absolutePath);
                    if (saveEvaluation(file.getAbsolutePath())) {
                        save.setDisable(true);
                        save.setText(Messages.getString("action.save"));
                    }
                }
            });
        } else {
            save.setText(Messages.getString("action.save"));
            save.setOnAction((final ActionEvent t) -> {
                logAppend("Save");
                saveEvaluation(null);
            });
        }
    }

    /**
     * Scroll to the graph node.
     *
     * @param pane scroll pane
     * @param node graph node
     */
    private void ensureVisible(final ScrollPane pane, final Node node) {
        final double width = pane.getContent().getBoundsInLocal().getWidth();
        final double height = pane.getContent().getBoundsInLocal().getHeight();

        final double x = node.getBoundsInParent().getMaxX();
        final double y = node.getBoundsInParent().getMaxY();

        // scrolling values range from 0 to 1
        pane.setVvalue(y / height);
        pane.setHvalue(x / width);

        // just for usability
        node.requestFocus();
    }

    /**
     * @return Logger to handle Evaluation logs
     */
    public final DataLoadingListener getDataLoadingListener() {
        return logView.getController();
    }

    /**
     * @return Logger to handle Evaluation logs
     */
    public final IndicatorListener getIndicatorListener() {
        return logView.getController();
    }

    /**
     * Set vertical scroll position of the ScrollPane to 1.
     */
    public final void gotoBottom() {
        scrollPane.setVvalue(1);
    }

    @Override
    public final void handle(final EvaluationSaveEvent e) {
        saveButton.setDisable(true);
        configureSaveButton(saveButton, stage);
    }

    /**
     * Load the evaluation into the current evaluation of Getari to notify
     * observers.
     *
     * @throws CloneNotSupportedException should never occurs as indicator must
     * implement clone()
     */
    public final void loadGraph() throws CloneNotSupportedException {
        LOGGER.info("start");

        final Evaluation evaluation = GetariApp.get().getCurrentEval();
        // setCurrent GetariApp.get().setCurrentEval(evaluation);
        origin.setEvaluation(evaluation);
        history.addMemento(origin.save());
        LOGGER.trace("added a memento");
        LOGGER.trace(history.getSize());
        final List<Indicator> phases = new ArrayList<>(
                evaluation.getIndicators());
        evaluation.clearIndicators();

        for (final Indicator phase : phases) {
            LOGGER.trace("Evaluation phase : {} {}", phase.getId(),
                    phase.getName());
            /* Création des phases phénologiques */
            Indicator currentPhase = phase.clone();
            ((CompositeIndicator) currentPhase).clearIndicators();
            ((CompositeIndicator) currentPhase)
            .add(((CompositeIndicator) phase).getIndicators()
                    .iterator().next());

            currentPhase = evaluation.add(
                    IndicatorCategory.PHENO_PHASES, evaluation,
                    currentPhase);

            final List<Indicator> practices = ((CompositeIndicator) phase)
                    .getIndicators();
            for (final Indicator practice : practices) {
                Indicator currentPractice = practice.clone();
                ((CompositeIndicator) currentPractice).clearIndicators();

                /* Création des pratiques culturales */
                currentPractice = evaluation.add(
                        IndicatorCategory.CULTURAL_PRATICES,
                        (CompositeIndicator) currentPhase, currentPractice);

                final List<Indicator> effects = ((CompositeIndicator) practice)
                        .getIndicators();

                for (final Indicator effect : effects) {
                    Indicator currentEffect = effect.clone();
                    ((CompositeIndicator) currentEffect).clearIndicators();

                    /* Création des effets climatiques */
                    currentEffect = evaluation
                            .add(IndicatorCategory.CLIMATIC_EFFECTS,
                                    (CompositeIndicator) currentPractice,
                                    currentEffect);

                    final List<Indicator> indicators = ((CompositeIndicator) effect)
                            .getIndicators();

                    for (final Indicator indicator : indicators) {
                        /* Création des indicateurs climatiques */
                        evaluation.add(IndicatorCategory.INDICATORS,
                                (CompositeIndicator) currentEffect, indicator);
                    }
                }
            }
        }

        evaluation.validate();

        phaseCombo.setPromptText(Messages.getString("action.display.phase"));
    }

    /**
     * Append a string to the file.
     *
     * @param text string to append
     */
    private void logAppend(final String... text) {
        GetariApp.logAppend(getClass(), getStage(), text);
    }
    /**
     * @param event onAction of button not used
     */
    private void onClearAction(final ActionEvent event) {
        graphPane.getChildren().clear();
        scrollPane.setContent(null);
        scrollPane.setContent(graphPane);
        graphVisitor.clearGraph();
        phaseCombo.getItems().clear();
        GetariApp.get().getCurrentEval().clearIndicators();
        GetariApp.get().getCurrentEval().setTranscient(true);
        
    }

    @Override
    public final void onFunctionAdded(final CompositeIndicator c) {
        if (graphVisitor.getGraphElement(c).isSelected()) {
            clearDetailedPanel();
            buildDetailedPanel(c);
        }
    }

    @Override
    public final void onIndicatorEvent(final IndicatorEvent event) {
        final Indicator indicator = event.getSource();
        switch (event.getAssociatedType()) {
        case ADD:
            // draw graphic
            LOGGER.trace("onIndicatorEvent ADD {}", indicator.getId());
            graphVisitor.addGraphElement(indicator);
            if (indicator instanceof CompositeIndicator
                    && ((CompositeIndicator) indicator).isPhase()) {
                phaseCombo.getItems().add(indicator);
            }
            indicator.addIndicatorListener(getIndicatorListener());

            buildEvaluationPanel(GetariApp.get().getCurrentEval());
            break;
        case AGGREGATION_MISSING:
            // Do nothing.
            break;
        case CHANGE:
            LOGGER.trace("onIndicatorEvent CHANGE");
            if (!(indicator instanceof Evaluation)) {
                return;
            }
            final Evaluation e = (Evaluation) indicator;
            saveButton.setDisable(!e.isTranscient());
            break;
        case CLIMATIC_MISSING:
            // Do nothing.
            break;
        case COMPUTE_FAILURE:
            LOGGER.trace("onIndicatorEvent COMPUTE_FAILURE");
            rightButtonPane.getChildren().remove(progressIndicator);
            stage.getScene().setCursor(Cursor.DEFAULT);
            break;
        case COMPUTE_START:
            LOGGER.trace("onIndicatorEvent COMPUTE_START");
            stage.getScene().setCursor(Cursor.WAIT);
            // if double click.
            if (rightButtonPane.getChildren().contains(progressIndicator)) {
                LOGGER.warn("progressIndicator already added!");
            } else {
                rightButtonPane.getChildren().add(0, progressIndicator);
            }
            break;
        case COMPUTE_SUCCESS:
            LOGGER.trace("onIndicatorEvent COMPUTE_SUCCESS");
            rightButtonPane.getChildren().remove(progressIndicator);
            stage.getScene().setCursor(Cursor.DEFAULT);
            break;
        case NOT_COMPUTABLE:
            // Do nothing.
            break;
        case PHASE_MISSING:
            // Do nothing.
            break;
        case REMOVE:
            LOGGER.trace("onIndicatorEvent REMOVE {}", indicator.getId());
            graphVisitor.removeGraphElement(indicator);
            if (indicator instanceof CompositeIndicator
                    && ((CompositeIndicator) indicator).isPhase()) {
                phaseCombo.getItems().remove(indicator);
            }
            clearDetailedPanel();
            buildEvaluationPanel(GetariApp.get().getCurrentEval());
            break;
        case UPDATED_VALUE:
            // Do nothing.
            break;
        default:
            LOGGER.info("IndicatorEvent.Type {} not handled!",
                    event.getAssociatedType());
        }
    }

    /**
     * Save the current evaluation at path.
     *
     * @param path path for the saved evaluation
     * @return success
     */
    private boolean saveEvaluation(final String path) {
        return GetariApp.get().saveEvaluation(path);
    }

    /**
     * Set content of indicator tab.
     *
     * @param n content with details
     */
    private void setDetailedPanel(final Node n) {
        clearDetailedPanel();
        indicatorTab.setContent(n);
    }

    /**
     * Set content of evaluation tab.
     *
     * @param n content with details
     */
    private void setEvaluationPanel(final Node n) {
        clearGeneralPanel();
        generalTab.setContent(n);
    }

    /**
     * Show a dialog to create a new phase.
     */
    private void showNewPhaseDialog() {
        try {
            new CreatePhaseView().build(stage);
        } catch (final IOException e) {
            LOGGER.fatal("Error while loading license view.", e);
        }
    }

}
