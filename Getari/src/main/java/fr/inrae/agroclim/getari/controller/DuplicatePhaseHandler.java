/*
 * Copyright (C) 2020 INRAE Agroclim
 *
 * This file is part of Getari.
 *
 * Getari is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Getari is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Getari. If not, see <http://www.gnu.org/licenses/>.
 */
package fr.inrae.agroclim.getari.controller;

import java.util.function.Consumer;

import fr.inrae.agroclim.getari.component.GetariApp;
import fr.inrae.agroclim.indicators.model.Evaluation;
import fr.inrae.agroclim.indicators.model.indicator.CompositeIndicator;
import fr.inrae.agroclim.indicators.model.indicator.Indicator;
import fr.inrae.agroclim.indicators.model.indicator.IndicatorCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * Event handler to duplicate a phase.
 *
 * Last change $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
@Log4j2
@RequiredArgsConstructor
public final class DuplicatePhaseHandler implements Consumer<CompositeIndicator> {

    /**
     * The original phase to duplicate.
     */
    private final CompositeIndicator original;

    /**
     * @param phase the phase to copy indicators into.
     */
    @Override
    public void accept(final CompositeIndicator phase) {
        LOGGER.trace("duplicate : {}", phase.getId());
        final Evaluation e = GetariApp.get().getCurrentEval();
        try {
            // processes
            for (final Indicator i1 : original.getIndicators()) {
                final CompositeIndicator process = (CompositeIndicator) i1;
                if (process.getIndicatorCategory()
                        == IndicatorCategory.PHENO_PHASES) {
                    continue;
                }
                final CompositeIndicator newProcess = (CompositeIndicator) e.add(
                        process.getIndicatorCategory(), phase, process);
                // effects
                for (final Indicator i2 : process.getIndicators()) {
                    final CompositeIndicator effect = (CompositeIndicator) i2;
                    final CompositeIndicator newEffect = (CompositeIndicator) e.add(
                            effect.getIndicatorCategory(), newProcess, effect);
                    // indicators
                    for (final Indicator ind : effect.getIndicators()) {
                        e.add(ind.getIndicatorCategory(), newEffect, ind);
                    }
                }
            }
        } catch (final CloneNotSupportedException ex) {
            LOGGER.catching(ex);
        }
        e.setTranscient(true);
        e.validate();
    }

}
