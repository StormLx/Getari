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

import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;

import fr.inrae.agroclim.getari.component.GetariApp;
import fr.inrae.agroclim.getari.resources.Messages;
import fr.inrae.agroclim.getari.view.graph.GraphElement;
import fr.inrae.agroclim.indicators.model.indicator.CompositeIndicator;
import fr.inrae.agroclim.indicators.model.indicator.Indicator;
import fr.inrae.agroclim.indicators.model.indicator.IndicatorCategory;
import javafx.event.ActionEvent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import lombok.extern.log4j.Log4j2;

/**
 * Helper class to handle contextual menus.
 *
 * Last change $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
@Log4j2
public final class MenuUtils {

    /**
     * Add an indicator to the menu for the current locale.
     *
     * @param menu menu to populate
     * @param indicator indicator to add
     * @param langCode locale for the tooltip
     * @param graphNode graph node
     * @param currentIndicator current indicator to search category
     */
    private static void addIndicator(final ContextMenu menu,
            final Indicator indicator, final String langCode,
            final GraphElement graphNode, final Indicator currentIndicator) {
        final CustomMenuItem item = new CustomMenuItem(
                new Label(
                        Messages.getString("graph.menu.add",
                                indicator.getName(langCode))));
        item.setHideOnClick(false);
        final StringJoiner sj = new StringJoiner("\n");
        final String description = indicator.getDescription(langCode);
        if (description != null) {
            sj.add(description);
        }

        item.addEventHandler(ActionEvent.ACTION,
                graphNode.getViewController());
        item.setId(GetariConstants.MENU
                + GetariApp.getNextCategory(currentIndicator.getCategory(),
                        currentIndicator.getIndicatorCategory().equals(
                                IndicatorCategory.CULTURAL_PRATICES)));
        item.getProperties().put(GetariConstants.INDICATOR, indicator);
        item.getProperties().put(GetariConstants.PARENT_INDICATOR,
                graphNode.getIndicator());
        menu.getItems().add(item);

        if (IndicatorCategory.INDICATORS.equals(
                indicator.getIndicatorCategory())) {
            final List<String> missingVariables = GetariApp.get().getCurrentEval()
                    .getResourceManager().getClimaticResource()
                    .getMissingVariables();
            final StringJoiner missing = new StringJoiner(", ");
            indicator.getVariables().stream()
            .filter(var -> missingVariables.contains(var.getName()))
            .forEach(var -> {
                item.setDisable(true);
                missing.add(var.name());
            });
            if (missing.length() > 0) {
                sj.add(Messages.getString("indicator.missing.variables",
                        missing.toString()));
            }
        }
        if (sj.length() > 0) {
            final Tooltip t = new Tooltip(sj.toString());
            Tooltip.install(item.getContent(), t);
        }
    }

    /**
     * Add child indicators to the menu.
     *
     * @param menu menu to build
     * @param graphNode clicked graph node
     */
    public static void buildMenu(final ContextMenu menu,
            final GraphElement graphNode) {
        final Indicator indicator = graphNode.getIndicator();
        LOGGER.trace("id: {}", indicator.getId());
        LOGGER.trace("IndicatorCategory: {}", indicator.getIndicatorCategory());
        LOGGER.trace("category: {}", indicator.getCategory());
        if (indicator.getCategory() != null
                && indicator instanceof CompositeIndicator) {
            LOGGER.trace("tag: {}", ((CompositeIndicator) indicator).getTag());
            try {
                final List<? extends Indicator> nextIndicators = GetariApp
                        .get()
                        .getCurrentEval()
                        .getSettings()
                        .getKnowledge()
                        .getNextIndicators((CompositeIndicator) indicator);
                LOGGER.trace("nb of indicators: {}", nextIndicators.size());

                final String langCode = Locale.getDefault().getLanguage();
                nextIndicators.forEach(itkIndicator ->
                MenuUtils.addIndicator(menu, itkIndicator, langCode,
                        graphNode, indicator)
                        );
            } catch (final Exception e) {
                AlertUtils.showException(Messages.getString("error.title"), e);
                LOGGER.warn(e);
            }
        }
    }

    /**
     * No controller for helper class.
     */
    private MenuUtils() {
    }
}
