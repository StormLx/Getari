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
package fr.inrae.agroclim.getari.component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Properties;

import com.vdurmont.semver4j.Semver;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

/**
 * Check new version using remote file (from GETARI web site).
 *
 * Last changed : $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
@Log4j2
public class UpdateChecker {

    /**
     * Release details from remote properties file.
     */
    @EqualsAndHashCode(of = {"version", "date"})
    public static class Release {

        /**
         * Release version.
         */
        @Getter
        private final String version;

        /**
         * Release build date.
         */
        @Getter
        private final LocalDateTime date;

        /**
         * Constructor.
         *
         * @param versionString release version
         * @param dateString release build date
         */
        public Release(final String versionString, final String dateString) {
            DateTimeFormatter formatter;
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            date = LocalDateTime.parse(dateString, formatter);
            version = versionString;
        }
    }

    /**
     * URL used to check if new version exist.
     */
    @Setter
    private String url;

    /**
     * @param currentVersion current version to check if new version exist.
     * @return release details if a new version is available or
     * Optional.empty().
     */
    public final Optional<Release> check(@NonNull final String currentVersion) {
        try (InputStream is = new URL(url).openStream();) {
            final Properties prop = new Properties();
            prop.load(is);
            final String key = "version";
            final String version = prop.getProperty(key);
            if (version == null) {
                LOGGER.info("Strange, no value for key {} in {}", key, url);
                return Optional.empty();
            }
            final String dateKey = "build.date";
            final String date = prop.getProperty(dateKey);
            if (date == null) {
                LOGGER.info("Strange, no value for key {} in {}", dateKey, url);
                return Optional.empty();
            }
            if (currentVersion.equals(version)) {
                LOGGER.trace("Current version {} is the last version",
                        currentVersion);
                return Optional.empty();
            }
            final Semver newVersion = new Semver(version);
            final Semver current = new Semver(currentVersion);
            if (newVersion.isGreaterThan(current)) {
                LOGGER.trace("A new version exists: {}", version);
                return Optional.of(new Release(version, date));
            }
            LOGGER.trace("Last version is {}, currentVersion is {}", version,
                    currentVersion);
        } catch (final java.io.FileNotFoundException ex) {
            LOGGER.info("Checking updates, the file {} was not found!", url);
            return Optional.empty();
        } catch (final java.net.ConnectException ex) {
            LOGGER.info("Checking updates, can't get {}!", url);
            return Optional.empty();
        } catch (final IOException ex) {
            LOGGER.catching(ex);
            return Optional.empty();
        }
        LOGGER.trace("Nothing to say.");
        return Optional.empty();
    }

}
