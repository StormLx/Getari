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

import fr.inrae.agroclim.getari.controller.StartController;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;

/**
 * Startup splash screen.
 *
 * Last change $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
public final class StartView extends FxmlView {

    /**
     * Controller for the view.
     *
     * @return controller for the view.
     */
    public StartController getController() {
        return (StartController) getLoader().getController();
    }

    @Override
    public void initStage() {
        // Escape close window
        getStage().getScene().addEventFilter(KeyEvent.KEY_PRESSED,
                (final KeyEvent ke) -> {
                    if (ke.getCode() == KeyCode.ESCAPE) {
                        // stops passing the event to next node
                        ke.consume();
                        getStage().close();
                    }
                });
        // Si APPLICATION_MODAL,
        // la fenêtre principale est décalée de quelques pixels.
        getStage().initModality(Modality.NONE);
    }

}
