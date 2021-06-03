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

import java.util.Locale;

import fr.inrae.agroclim.indicators.model.Nameable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Datafile separator for SelectDataView and CSV detection.
 *
 * Last change $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
@RequiredArgsConstructor
@ToString
public class Separator implements Nameable {

    /**
     * Label.
     */
    @Getter
    private final String name;

    /**
     * Character.
     */
    @Getter
    private final String value;

    @Override
    public final String getName(final Locale locale) {
        return name;
    }
}
