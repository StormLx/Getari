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
package fr.inrae.agroclim.getari.component;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

import fr.inrae.agroclim.getari.Getari;
import fr.inrae.agroclim.getari.event.EventBus;
import fr.inrae.agroclim.getari.event.RecentUpdateEvent;
import fr.inrae.agroclim.getari.util.FileType;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

/**
 * User preferences handling for Getari.
 *
 * Uses java.util.prefs.Preferences
 *
 * Last change $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
@Log4j2
public final class GetariPreferences {

    /**
     * Key for lastly opened directory.
     */
    private static final String KEY_LAST_DIR = "lastly_opened_directory_";

    /**
     * Key for the chosen locale.
     */
    private static final String KEY_LOCALE_COUNTRY = "locale_country";

    /**
     * Key for the chosen language.
     */
    private static final String KEY_LOCALE_LANGUAGE = "locale_language";

    /**
     * The event bus.
     */
    @Setter
    private EventBus eventBus;

    /**
     * The user preferences.
     */
    private final Preferences prefs;

    /**
     * Constructor.
     */
    public GetariPreferences() {
        this(Getari.class.getName());
    }

    /**
     * Constructor.
     *
     * @param nodeName alternate node name, for tests
     */
    public GetariPreferences(final String nodeName) {
        prefs = Preferences.userRoot().node(nodeName);
    }

    /**
     * Clear all user preferences.
     */
    public void clear() {
        getKeys().forEach(prefs::remove);
    }

    /**
     * @param type file type
     * @return paths of recently opened file, only the existing files
     */
    public List<String> getExistingRecentlyOpened(final FileType type) {
        return getRecentlyOpened(type).stream()
                .filter(path -> new File(path).exists())
                .collect(Collectors.toList());
    }

    /**
     * @return keys for user preferences
     */
    public List<String> getKeys() {
        try {
            return Arrays.asList(prefs.keys());
        } catch (final BackingStoreException ex) {
            LOGGER.error("Error while getting Preferences keys.", ex);
            return new ArrayList<>();
        }
    }

    /**
     * @param type type of directory
     * @return lastly opened directory of type
     */
    public String getLastDirectory(final String type) {
        return prefs.get(KEY_LAST_DIR + type, null);
    }

    /**
     * @return lastly chosen locale
     */
    public Locale getLocale() {
        final String lang = prefs.get(KEY_LOCALE_LANGUAGE, null);
        final String country = prefs.get(KEY_LOCALE_COUNTRY, null);
        if (lang != null && country != null) {
            return new Locale(lang, country);
        }
        return null;
    }

    /**
     * @param type file type
     * @return paths of recently opened evaluations, limited list
     */
    public List<String> getRecentlyOpened(final FileType type) {
        final String key = getRecentlyOpenedKey(type);
        final String val = prefs.get(key, null);
        if (val == null) {
            return new ArrayList<>();
        }
        return Arrays.asList(val.split(File.pathSeparator));
    }

    /**
     * @param type file type
     * @return preference key for the file type
     */
    private String getRecentlyOpenedKey(final FileType type) {
        switch (type) {
        case EVALUATION:
            return "recently_opened_evaluations";
        case MULTIEXECUTION:
            return "recently_opened_multiexecutions";
        default:
            throw new IllegalArgumentException("FileType not handled: " + type);
        }
    }

    /**
     * @param type file type
     * @param path path of recently opened evaluation
     */
    public void putRecentlyOpened(final FileType type, final String path) {
        final Queue<String> queue = new LinkedList<>(getRecentlyOpened(type));
        queue.remove(path);
        queue.add(path);
        final int limit = 10;
        while (queue.size() > limit) {
            queue.remove();
        }
        final String val = String.join(File.pathSeparator, queue);
        final String key = getRecentlyOpenedKey(type);
        prefs.put(key, val);
        if (eventBus != null) {
            eventBus.fireEvent(new RecentUpdateEvent());
        }
    }

    /**
     * @param type file type
     * @param path path of recently opened evaluation
     */
    public void removeRecentlyOpened(final FileType type, final String path) {
        final List<String> queue = new ArrayList<>(getRecentlyOpened(type));
        queue.remove(path);
        final String val = String.join(File.pathSeparator, queue);
        final String key = getRecentlyOpenedKey(type);
        prefs.put(key, val);
        if (eventBus != null) {
            eventBus.fireEvent(new RecentUpdateEvent());
        }
    }

    /**
     * @param type type of directory
     * @param absolutePath lastly opend directory of type
     */
    public void setLastDirectory(final FileType type, final String absolutePath) {
        LOGGER.info("set last directory: {}", absolutePath);
        prefs.put(KEY_LAST_DIR + type.name(), absolutePath);
    }

    /**
     * @param locale chosen locale
     */
    public void setLocale(final Locale locale) {
        prefs.put(KEY_LOCALE_COUNTRY, locale.getCountry());
        prefs.put(KEY_LOCALE_LANGUAGE, locale.getLanguage());
    }
}
