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

/**
 * Ensemble des constantes de l'application.
 *
 * Last change $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
public final class GetariConstants {
    /**
     * Radius for the circle ADD in the GraphElement shape.
     */
    public static final int ADD_RADIUS = 15;

    /**
     * Radius for the aggregation.
     */
    public static final int AGGREGATION_RADIUS = 20;

    /**
     * Style for cursor.
     */
    public static final String DEFAULT_CURSOR = "-fx-cursor:default";

    /**
     * Key for properties of Indicator representation.
     */
    public static final String GRAPH_NODE = "node";

    /**
     * Style for cursor.
     */
    public static final String HAND_CURSOR = "-fx-cursor:hand";

    /**
     * Key for MenuItem properties.
     */
    public static final String INDICATOR = "indicator";

    /**
     * Initial value for TextField.
     */
    public static final String INIT_VALUE = "null";

    /**
     * Prefix for context menu (Menu Add).
     */
    public static final String MENU = "menu-";

    /**
     * Radius for evaluation without aggregation.
     */
    public static final int NO_AGGREGATION_RADIUS = 10;

    /**
     * Key for MenuItem properties.
     */
    public static final String PARENT_INDICATOR = "parent_indicator";

    /**
     * Height for the rectangle representing the phase.
     */
    public static final int PHASE_HEIGHT = 200;

    /**
     * Width for the rectangle representing the phase.
     */
    public static final int PHASE_WIDTH = 40;

    /**
     * Key for Tab.getProperties().
     */
    public static final Integer PROPERTIES_EVALUATION = 1;

    /**
     * Key for Tab.getProperties().
     */
    public static final Integer PROPERTIES_RESULTVIEW = 2;
    /**
     * Height for the rectangle representing the indicator.
     */
    public static final int REC_HEIGHT = 80;

    /**
     * Max size for the VBox representing the indicator.
     */
    public static final int INDICATOR_BOX_MAX_SIZE = REC_HEIGHT + 20;

    /**
     * Width for the rectangle representing the indicator.
     */
    public static final double REC_WIDTH = 120;

    /**
     * No constructor for helper class.
     */
    private GetariConstants() {
    }
}
