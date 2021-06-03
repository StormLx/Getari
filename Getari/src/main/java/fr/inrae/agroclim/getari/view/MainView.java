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
import java.util.Objects;
import java.util.Optional;

import fr.inrae.agroclim.getari.component.GetariApp;
import fr.inrae.agroclim.getari.component.TitleBar;
import fr.inrae.agroclim.getari.component.WindowButtons;
import fr.inrae.agroclim.getari.component.WindowResizeButton;
import fr.inrae.agroclim.getari.controller.MainViewController;
import fr.inrae.agroclim.getari.controller.MenuController;
import fr.inrae.agroclim.getari.event.RecentUpdateEvent;
import fr.inrae.agroclim.getari.resources.Messages;
import fr.inrae.agroclim.getari.util.ComponentUtil;
import fr.inrae.agroclim.getari.util.GetariConstants;
import fr.inrae.agroclim.indicators.model.Evaluation;
import fr.inrae.agroclim.indicators.model.indicator.listener.IndicatorEvent;
import fr.inrae.agroclim.indicators.model.indicator.listener.IndicatorListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

/**
 * View for the main window.
 *
 * Last change $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
@Log4j2
public class MainView implements IndicatorListener, View {

    /**
     * Initial height of the window.
     */
    private static final int INITIAL_HEIGHT = 1_000;
    /**
     * Initial width of the window.
     */
    private static final int INITIAL_WIDTH = 1_200;
    /**
     * Minimal height of the window.
     */
    private static final int MIN_HEIGHT = 720;
    /**
     * Minimal width of the window.
     */
    private static final int MIN_WIDTH = 1_000;
    /**
     * Screen bounds.
     */
    private final Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
    /**
     * Controller.
     */
    @Getter
    private final MainViewController controller;
    /**
     * Stage of StartView.
     */
    @Getter
    @Setter
    private Stage dialog;
    /**
     * Application menu.
     */
    @Getter
    private MenuController menuController;
    /**
     * Counter for evaluation tabs.
     */
    private int nbOfEvaluationTab = 0;
    /**
     * Counter for results tabs.
     */
    private int nbOfResultsTab = 0;
    /**
     * Parent node of MainView.
     */
    private BorderPane root;
    /**
     * Scene of root.
     */
    private Scene scene;
    /**
     * Selection of tab.
     */
    private SingleSelectionModel<Tab> selectionModel;
    /**
     * Primary stage.
     */
    @Getter
    private Stage stage = null;
    /**
     * Tab pane for graphes and results.
     */
    private final TabPane tabPane = new TabPane();
    /**
     * Bottom right of a window to support resizing.
     */
    private WindowResizeButton windowResizeButton;

    /**
     * Constructor.
     */
    public MainView() {
        controller = new MainViewController(this);
        tabPane.getStyleClass().add("general-tab");
    }

    /**
     * @param e evaluation to show
     * @param content GraphView node
     */
    public final void addEvaluationTab(final Evaluation e, final Node content) {
        final Tab tab = new Tab();
        tabPane.getTabs().add(tab);
        tab.setText(e.getName());
        tab.setGraphic(new ImageView(ComponentUtil.loadImage("evaluation-icon.png")));
        tab.setContent(content);
        tab.setOnClosed((final Event evt) -> {
            tabPane.getTabs().remove(tab);
            // Garbage collector is needed now or memory used by tab is not free
            Runtime.getRuntime().gc();
            nbOfEvaluationTab--;
            if (nbOfEvaluationTab < 1) {
                menuController.updateEvaluationMenu(null);
            }
        });
        // evaluation file path
        final Tooltip tooltip = new Tooltip();
        tab.setTooltip(tooltip);
        if (!e.isNew()) {
            tooltip.setText(e.getSettings().getFilePath());
        }
        //-
        tab.getProperties().put(GetariConstants.PROPERTIES_EVALUATION, e);
        selectionModel = tabPane.getSelectionModel();
        selectionModel.select(tab);
        nbOfEvaluationTab++;
    }

    /**
     * Add the view into a new tab.
     *
     * @param view view to add
     * @return created tab
     * @throws java.io.IOException while building view from FXML
     */
    public Tab addMETab(final MultiExecutionView view) throws IOException {
        final String tabText = Messages.getString("multiexecution.new");
        final String tooltipText = null;
        final Node content = view.build();
        final Tab tab = addTab("meTab", content, tabText, tooltipText, "multi-icon.png");
        tab.setOnClosed(evt -> tabPane.getTabs().remove(tab));
        return tab;
    }

    /**
     * @param e related evaluation
     * @param view view of results
     * @param file to display file name
     * @throws java.io.IOException while building results.fxml
     */
    public final void addResultsTab(final Evaluation e,
            final ResultsView view, final File... file) throws IOException {
        String tabText;
        String tooltipText = null;
        if (file.length != 0) {
            tabText = file[0].getName();
            tooltipText = file[0].getAbsolutePath();
        } else {
            tabText = Messages.getString("result.title", e.getName());
        }
        final Node content = view.build();
        final Tab tab = addTab("resultTab", content, tabText, tooltipText,
                "attached-icon.png");
        tab.setOnClosed(evt -> {
            tabPane.getTabs().remove(tab);
            // Garbage collector is needed now or memory used by tab is not free
            Runtime.getRuntime().gc();
            nbOfResultsTab--;
            menuController.setSaveResultsItemDisabled(nbOfResultsTab < 1);
        });
        tab.getProperties().put(GetariConstants.PROPERTIES_RESULTVIEW, view);
        menuController.setSaveResultsItemDisabled(false);
        nbOfResultsTab++;
    }

    /**
     * @param tabId ID of tab
     * @param content content of the tab
     * @param title title of the tab
     * @param tooltipText tooltip on the tab
     * @param icon icon for the tab
     * @return created tab
     */
    public final Tab addTab(final String tabId, final Node content,
            final String title, final String tooltipText, final String icon) {
        LOGGER.traceEntry(tabId);
        final Tab tab = new Tab();
        tabPane.getTabs().add(tab);
        if (tooltipText != null) {
            final Tooltip tooltip = new Tooltip();
            tab.setTooltip(tooltip);
            tooltip.setText(tooltipText);
        }
        tab.setContent(content);
        tab.setGraphic(new ImageView(ComponentUtil.loadImage(icon)));
        tab.setId(tabId);
        tab.getStyleClass().add(tabId);
        tab.setText(title);
        tab.setOnClosed(evt -> {
            tabPane.getTabs().remove(tab);
            // Garbage collector is needed now or memory used by tab is not free
            Runtime.getRuntime().gc();
        });
        selectionModel = tabPane.getSelectionModel();
        selectionModel.select(tab);
        return tab;
    }

    @Override
    public final Parent build(final Stage s) {
        this.stage = s;
        boolean isToMaximize = false;
        GetariApp.setMainView(this);

        final Double screenWidth = bounds.getWidth();
        final Double screenHeight = bounds.getHeight();
        Double width;
        Double height;

        if (screenWidth <= INITIAL_WIDTH || screenHeight <= INITIAL_HEIGHT) {
            width = screenWidth;
            height = screenHeight;
            isToMaximize = true;
        } else {
            width = Double.valueOf(INITIAL_WIDTH);
            height = Double.valueOf(INITIAL_HEIGHT);
        }
        // create window resize button
        windowResizeButton = new WindowResizeButton(stage, MIN_WIDTH,
                MIN_HEIGHT);
        // create root
        stage.initStyle(StageStyle.UNDECORATED);
        root = new BorderPane() {
            @Override
            protected void layoutChildren() {
                super.layoutChildren();
                windowResizeButton.autosize();
                windowResizeButton.setLayoutX(getWidth()
                        - windowResizeButton.getLayoutBounds().getWidth());
                windowResizeButton.setLayoutY(getHeight()
                        - windowResizeButton.getLayoutBounds().getHeight());
            }
        };

        setUpScene(width, height);
        setUpStage();

        tabPane.getSelectionModel().selectedItemProperty().addListener(
                (final ObservableValue<? extends Tab> ov, final Tab t, final Tab t1) ->
                refreshMenu(t1)
                );

        final TitleBar titleBar = new TitleBar(stage);
        final MenuView menuView = new MenuView();
        try {
            titleBar.addMenuBar(menuView.build());
        } catch (final IOException ex) {
            LOGGER.catching(ex);
        }
        menuController = menuView.getController();
        menuController.setMainView(this);
        GetariApp.get().getEventBus().fireEvent(new RecentUpdateEvent());

        root.setId("root");
        root.getStyleClass().add("application");
        root.setTop(titleBar);
        root.setCenter(tabPane);
        windowResizeButton.setManaged(false);
        root.getChildren().add(windowResizeButton);
        ComponentUtil.setUpDragDropHandling(controller, scene, false);

        stage.show();

        if (isToMaximize) {
            final WindowButtons windowButtons = new WindowButtons(stage, true, true,
                    true);
            windowButtons.toogleMaximized();
        }
        return root;
    }

    /**
     * Close tab, if open.
     */
    public final void closeCurrentTab() {
        if (getCurrentTab() == null) {
            LOGGER.warn("No current tab!");
            return;
        }
        if (GetariApp.get().getCurrentEval() != null
                && GetariApp.get().getCurrentEval().isTranscient()) {
            final Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(Messages.getString("close.text"));
            alert.setHeaderText(
                    Messages.getString("confirm.close.transcient.evaluation"));
            final Optional<ButtonType> result = alert.showAndWait();
            if (!result.isPresent()
                    || result.get() == ButtonType.CANCEL) {
                return;
            }
        }
        ComponentUtil.closeTab(getCurrentTab());
    }

    /**
     * @return selected tab
     */
    public final Tab getCurrentTab() {
        return selectionModel.getSelectedItem();
    }

    @Override
    public final void onIndicatorEvent(final IndicatorEvent event) {
        if (!(event.getSource() instanceof Evaluation)) {
            return;
        }
        final Evaluation e = (Evaluation) event.getSource();
        Objects.requireNonNull(e);
        switch (event.getAssociatedType()) {
        case ADD:
            // Do nothing.
            break;
        case AGGREGATION_MISSING:
            // Do nothing.
            break;
        case CHANGE:
            LOGGER.trace("onIndicatorEvent CHANGE");
            if (selectionModel.getSelectedItem() == null) {
                break;
            }
            final Tab selectedTab = selectionModel.getSelectedItem();
            if (e.isTranscient()) {
                selectedTab.setText("* " + e.getName());
            } else {
                selectedTab.setText(e.getName());
            }
            if (selectedTab.getTooltip() == null) {
                final Tooltip tltp = new Tooltip();
                selectedTab.setTooltip(tltp);
            }
            if (!e.isNew()) {
                selectedTab.getTooltip().setText(e.getSettings()
                        .getFilePath());
            }
            break;
        case CLIMATIC_MISSING:
            // Do nothing.
            break;
        case COMPUTE_FAILURE:
            // Do nothing.
            break;
        case COMPUTE_START:
            // Do nothing.
            break;
        case COMPUTE_SUCCESS:
            LOGGER.trace("onIndicatorEvent COMPUTE_SUCCESS");
            try {
                final ResultsView view = new ResultsView();
                this.addResultsTab(e, view);
                view.getController().loadResults();
            } catch (final IOException ex) {
                LOGGER.catching(ex);
            }
            break;

        case NOT_COMPUTABLE:
            // Do nothing.
            break;
        case PHASE_MISSING:
            // Do nothing.
            break;
        case REMOVE:
            // Do nothing.
            break;
        default:
            LOGGER.warn("IndicatorEvent.Type {} not handled!",
                    event.getAssociatedType());
        }
    }

    /**
     * @param tab current tab open in Getari.
     */
    private void refreshMenu(final Tab tab) {
        if (tab == null) {
            GetariApp.get().setCurrentEval(null);
            GetariApp.get().setCurrentResultView(null);
        } else if (tab.getProperties().containsKey(
                GetariConstants.PROPERTIES_EVALUATION)) {
            menuController.updateEvaluationMenu((Evaluation) tab.getProperties()
                    .get(GetariConstants.PROPERTIES_EVALUATION));
            GetariApp.get().setCurrentEval((Evaluation) tab.getProperties()
                    .get(GetariConstants.PROPERTIES_EVALUATION));
        } else if (tab.getProperties().containsKey(
                GetariConstants.PROPERTIES_RESULTVIEW)) {
            GetariApp.get().setCurrentResultView(
                    (ResultsView) tab.getProperties()
                    .get(GetariConstants.PROPERTIES_RESULTVIEW));
        }
    }

    /**
     * @param width the width of the scene
     * @param height the height of the scene
     */
    private void setUpScene(final double width, final double height) {
        scene = new Scene(root, width, height);
        scene.getStylesheets().add(GetariApp.getGlobalCss());
    }

    /**
     * Set properties on main stage.
     */
    private void setUpStage() {
        stage.setTitle(Messages.getString("application.name"));
        stage.setScene(scene);

        stage.setResizable(true);
        stage.setMinHeight(MIN_HEIGHT);
        stage.setMinWidth(MIN_WIDTH);

        /* Fenêtre centrée */
        stage.setX((bounds.getWidth() - scene.getWidth()) / 2);
        stage.setY((bounds.getHeight() - scene.getHeight()) / 2);
    }
}
