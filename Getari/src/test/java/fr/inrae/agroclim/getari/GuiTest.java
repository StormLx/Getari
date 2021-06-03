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
package fr.inrae.agroclim.getari;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Locale;
import java.util.Set;
import java.util.concurrent.Callable;

import org.testfx.framework.junit.ApplicationTest;

import fr.inrae.agroclim.getari.resources.Messages;
import javafx.scene.Node;
import javafx.scene.control.ComboBoxBase;

/**
 * Test Getari UI with TestFX.
 *
 * Last changed : $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
abstract class GuiTest extends ApplicationTest {

    /**
     * Set system properties to get TestFX works.
     */
    protected static void setUpTestFx() {
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.text", "t2k");
        System.setProperty("java.awt.headless", "true");
        Messages.setLocale(Locale.FRENCH);
    }

    /**
     * Assert that a widget with id exists.
     *
     * @param id id to look up
     */
    protected void assertExists(final String id) {
        assertNotNull(id + " must exist!", lookup(id).query());
    }

    /**
     * Assert that a widget with id does not exist.
     *
     * @param id id to look up
     */
    protected void assertNotExists(final String id) {
        boolean exists = true;

        try {
            lookup(id).query();
        } catch (final org.testfx.service.query.EmptyNodeQueryException e) {
            exists = false;
        }

        assertFalse(id + " must not exist!", exists);
    }

    /**
     * Assert that a widget with id does not exist or is not visible .
     *
     * @param id id to look up
     */
    protected void assertNotVisible(final String id) {
        boolean exists;
        final Set<Node> nodes = lookup(id).queryAll();
        if (nodes.isEmpty()) {
            exists = false;
        } else {
            final Node layout = nodes.iterator().next();
            if (layout == null) {
                exists = false;
            } else {
                exists = layout.isVisible();
            }
        }

        assertFalse(id + " must not be visible!", exists);
    }

    /**
     * @param query selector
     * @return true if element is enabled, false if disabled or not exist
     */
    protected Callable<Boolean> hasPromptText(final String query) {
        return () -> {
            final Node node = lookup(query).query();
            if (node == null) {
                System.err.println(query + " not found");
                return false;
            }
            @SuppressWarnings("unchecked")
            final String promptText = ((ComboBoxBase<String>) node).getPromptText();
            return promptText != null && !promptText.isEmpty();
        };
    }

    /**
     * @param query selector
     * @return true if element is enabled, false if disabled or not exist
     */
    protected Callable<Boolean> isEnabled(final String query) {
        return () -> {
            final Node node = lookup(query).query();
            if (node == null) {
                return false;
            }
            return !node.isDisabled();
        };
    }

    /**
     * @param query selector
     * @return true if element is not visible or not exist
     */
    protected Callable<Boolean> isNotVisible(final String query) {
        return () -> {
            final Set<Node> nodes = lookup(query).queryAll();
            if (nodes.isEmpty()) {
                return true;
            }
            final Node layout = nodes.iterator().next();
            if (layout == null) {
                return true;
            }
            return !layout.isVisible();
        };
    }

    /**
     * @param query selector
     * @return true if element exists and is visible
     */
    protected Callable<Boolean> isVisible(final String query) {
        return () -> {
            final Set<Node> nodes = lookup(query).queryAll();
            if (nodes.isEmpty()) {
                return false;
            }
            final Node layout = nodes.iterator().next();
            if (layout == null) {
                return false;
            }
            return layout.isVisible();
        };
    }
}
