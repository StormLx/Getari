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
package fr.inrae.agroclim.getari.resources;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Various details about application version.
 *
 * Last change $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
public final class Version {

    /**
     * Resource bundle with info.
     */
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
            .getBundle("fr.inrae.agroclim.getari.resources.version");

    /**
     * @param key property key in bundle
     * @return message value or exclamation message
     */
    public static String getString(final String key) {
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (final MissingResourceException e) {
            return '!' + key + '!';
        }
    }

    /**
     * No constructor, use static methods.
     */
    private Version() {
    }
}
