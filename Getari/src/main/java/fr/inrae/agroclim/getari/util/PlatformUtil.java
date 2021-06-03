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

import java.io.File;
import java.io.IOException;

import fr.inrae.agroclim.getari.resources.Messages;
import javafx.application.HostServices;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

/**
 * Operating system helpers.
 *
 * Last changed : $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
@Log4j2
public final class PlatformUtil {

    /**
     * Operating system name.
     */
    private static final String OS = System.getProperty("os.name").toLowerCase();

    /**
     * Host services provider, which lets the application obtain its code and
     * document bases, show a Web page in a browser, and communicate with the
     * enclosing Web page using JavaScript when running in a browser.
     */
    @Setter
    private static HostServices hostServices = null;

    /**
     * Get current working directory path, with final separator.
     *
     * @return path
     */
    public static String getCurrentDirectory() {
        return System.getProperty("user.dir") + File.separator;
    }

    /**
     * @return command to open file/URI
     */
    public static String getFileBrowserCommand() {
        if (isWin()) {
            return "explorer";
        } else if (isMac()) {
            return "open";
        } else if (isUnix()) {
            return "xdg-open";
        }
        return null;
    }

    /**
     * Get user's home directory path, with final separator.
     *
     * @return path
     */
    public static String getHomeDirectory() {
        final String val = System.getProperty("user.home");
        if (val == null) {
            if (isWin()) {
                return "C:\\";
            } else if (isUnix()) {
                return "/";
            } else if (isMac()) {
                return "/";
            } else {
                throw new RuntimeException(OS + " not handled!");
            }
        }
        return val + File.separator;
    }

    /**
     * @return Version of JavaFX.
     */
    public static String getJavaFxVersion() {
        try {
            return (String) System.getProperties()
                    .get("javafx.runtime.version");
        } catch (final java.security.AccessControlException e) {
            return "";
        }
    }

    /**
     * @return OS is Mac
     */
    public static boolean isMac() {
        return OS.contains("mac");
    }

    /**
     * @return OS is a *nix
     */
    public static boolean isUnix() {
        return OS.contains("nix") || OS.contains("nux") || OS.contains("aix");
    }

    /**
     * @return OS is Windows
     */
    public static boolean isWin() {
        return OS.contains("win");
    }

    /**
     * Launch browser on the URL.
     *
     * @param url
     *            URL to show in browser
     */
    public static void openBrowser(final String url) {
        if (hostServices == null) {
            final String cmd = getFileBrowserCommand() + " " + url;
            try {
                Runtime.getRuntime().exec(cmd);
            } catch (final IOException e) {
                LOGGER.error(e.getLocalizedMessage());
                AlertUtils.showException(
                        Messages.getString("warning.open.url.failure", url),
                        e
                        );
            }
        } else {
            hostServices.showDocument(url);
        }
    }

    /**
     * No constructor for helper class.
     */
    private PlatformUtil() {
    }
}
