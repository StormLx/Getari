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

import static fr.inrae.agroclim.getari.util.GetariConstants.INDICATOR;
import static fr.inrae.agroclim.getari.util.GetariConstants.PARENT_INDICATOR;

import fr.inrae.agroclim.getari.component.GetariApp;
import fr.inrae.agroclim.getari.resources.Messages;
import fr.inrae.agroclim.getari.util.AlertUtils;
import fr.inrae.agroclim.getari.view.GraphView;
import fr.inrae.agroclim.indicators.model.indicator.CompositeIndicator;
import fr.inrae.agroclim.indicators.model.indicator.Indicator;
import fr.inrae.agroclim.indicators.model.indicator.IndicatorCategory;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import lombok.extern.log4j.Log4j2;

/**
 * Gestionnaire de l'évènement d'ajout d'indicateurs au sein du graphe.
 *
 * L'évènement est déclenché par le choix d'un item dans un menu contextuel ou
 * dans la fenêtre d'ajout d'une phase
 *
 * Last change $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
@Log4j2
public final class AddIndicatorHandler implements EventHandler<ActionEvent> {

    /**
     * Graph view.
     */
    private final GraphView view;

    /**
     * Constructor.
     *
     * @param v graph view.
     */
    public AddIndicatorHandler(final GraphView v) {
        view = v;
    }

    @Override
    public void handle(final ActionEvent event) {
        LOGGER.trace("start");
        CompositeIndicator parentIndicator;
        Indicator selectedIndicator;
        String[] tab;
        String category;
        MenuItem item;
        String id;

        item = (MenuItem) event.getSource();
        id = item.getId();
        LOGGER.trace("id={}", id);
        tab = id.split("-");
        if (tab == null) {
            category = null;
        } else {
            category = tab[1];
        }
        LOGGER.trace("category={}", category);

        if (category == null) {
            return;
        }

        // Pourquoi mettre des JavaBeanProperties; dans notre cas mettre les
        // indicateurs directement devrait suffire non?
        //
        parentIndicator = (CompositeIndicator) item.getProperties().get(
                PARENT_INDICATOR);
        selectedIndicator = (Indicator) item.getProperties().get(INDICATOR);
        if (selectedIndicator == null) {
            AlertUtils.showError(
                    Messages.getString(
                            "graph.indicator.notselected.category", category)
                    );
            return;
        }
        if (parentIndicator == null) {
            AlertUtils.showError(
                    Messages.getString(
                            "graph.parentindicator.notselected.category",
                            category)
                    );
            return;
        }
        GetariApp.logAppend(getClass(), null, "add indicator "
                + selectedIndicator.getId() + " into "
                + parentIndicator.getId());
        /*
         * Va récupérer l'évaluation courante dans l'objet singleton GetariApp
         * (à renommer) et lui ajoute la nouvelle relation entre l'indicateur
         * parent et l'indicateur fils
         */
        final String cat = category;
        final CompositeIndicator parentI = parentIndicator;
        final Indicator selectedI = selectedIndicator;

        try {
            GetariApp.get().getCurrentEval().add(cat, parentI, selectedI);
        } catch (final CloneNotSupportedException ex) {
            LOGGER.fatal("This should never occurs as indicator "
                    + "must implement clone().", ex);
            throw new RuntimeException(ex);
        }
        GetariApp.get().getCurrentEval().setTranscient(true);
        GetariApp.get().getCurrentEval().validate();

        if (cat.equals(IndicatorCategory.PHENO_PHASES.getTag())) {
            view.gotoBottom();
        }

        if (item.getParentPopup() != null) {
            item.getParentPopup().hide();
        }
    }
}
