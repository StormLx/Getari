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

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import fr.inrae.agroclim.indicators.resources.I18n;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Messages for the UI from resource bundle.
 *
 * Last change $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
public final class Messages {

    /**
     * The dynamic locale.
     */
    private static final ObjectProperty<Locale> LOCALE;

    /**
     * Singleton.
     */
    private static Messages instance;

    /**
     * Bundle name for common messages.
     */
    public static final String BUNDLE_NAME;

    static {
        BUNDLE_NAME = "fr.inrae.agroclim.getari.resources.messages";
        LOCALE = new SimpleObjectProperty<>(Locale.getDefault());
    }

    /**
     * @return singleton
     */
    private static Messages getInstance() {
        if (instance == null) {
            ResourceBundle resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME, getLocale(),
                    Messages.class.getModule());
            // Fallback to xxxx.properties and not xxxx_current_locale.properties.
            if (!resourceBundle.getLocale().equals(getLocale())) {
                resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME, Locale.ROOT, Messages.class.getModule());
            }
            instance = new Messages(new I18n(resourceBundle));
        }
        return instance;
    }

    /**
     * @return current locale.
     */
    public static Locale getLocale() {
        return LOCALE.get();
    }

    /**
     * Return message with inlined arguments.
     *
     * @param plural value for plural form
     * @param key property key in general messages
     * @param messageArguments arguments for the message.
     * @return message with arguments or exclamation message
     */
    public static String getString(final int plural, final String key,
            final Object... messageArguments) {
        return getInstance().res.format(plural, key, messageArguments);
    }

    /**
     * @param key property key in general messages
     * @return message
     */
    public static String getString(final String key) {
        return getInstance().get(key);
    }

    /**
     * @param key property key in general messages
     * @param parameters parameters for the message
     * @return message with parameters
     */
    public static String getString(final String key,
            final Object... parameters) {
        return getInstance().get(key, parameters);
    }

    /**
     * Get the supported Locales.
     *
     * @return List of Locale objects.
     */
    public static List<Locale> getSupportedLocales() {
        return Arrays.asList(Locale.ENGLISH, Locale.FRENCH);
    }

    /**
     * @return locale property
     */
    public static ObjectProperty<Locale> localeProperty() {
        return LOCALE;
    }

    /**
     * Change locale to update translations using bindings.
     *
     * Tooltip tooltip = new Tooltip();
     *
     * tooltip.textProperty().bind(Bindings.createStringBinding( () ->
     * Utils.i18n(key), Utils.localeProperty()));
     *
     * @param locale locale of UI
     */
    public static void setLocale(final Locale locale) {
        if (!getSupportedLocales().contains(locale)) {
            throw new IllegalArgumentException("Unsupported locale: " + locale);
        }
        instance = null;
        localeProperty().set(locale);
        Locale.setDefault(locale);
    }

    /**
     * The resource bundle for the messages.
     */
    private final I18n res;

    /**
     * Constructor.
     *
     * @param rb The resource bundle for the messages.
     */
    public Messages(final I18n rb) {
        res = rb;
    }

    /**
     * @param key property key in bundle
     * @return message
     */
    public String get(final String key) {
        return res.get(key);
    }

    /**
     * @param key property key in bundle
     * @param parameters parameters for the message
     * @return message with parameters
     */
    public String get(final String key, final Object... parameters) {
        return res.format(key, parameters);
    }
}
