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
package fr.inrae.agroclim.getari.view.visitor;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author jcufi
 */
public final class Cpt {

    /**
     * Row number (from 0).
     */
    @Getter
    @Setter
    private int row = 0;

    /**
     * Column number (from 0).
     */
    @Getter
    @Setter
    private int col = 0;

    /**
     * Increment column number.
     *
     * @return incremented column number
     */
    public int nextCol() {
        col += 1;
        return col;
    }

    /**
     * @param nb
     *            number to add to col number
     * @return current col number
     */
    public int nextCol(final int nb) {
        col += nb;
        return col;
    }

    /**
     * Increment row number.
     *
     * @return incremented row number
     */
    public int nextRow() {
        row += 1;
        return row;
    }
}
