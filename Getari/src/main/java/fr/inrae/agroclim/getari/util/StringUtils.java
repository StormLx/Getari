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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;

/**
 * String manipulation utilities.
 *
 * Last changed : $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
public final class StringUtils {

    /**
     * Convert Markdown source to HTML.
     *
     * @param markdown source
     * @return HTML
     */
    public static String convertMarkdown(final String markdown) {
        final Parser parser = Parser.builder().build();
        final HtmlRenderer renderer = HtmlRenderer.builder().build();
        final StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE HTML>\n");
        sb.append("<html><head><meta charset=\"utf-8\"/></head><body>\n");
        sb.append(renderer.render(parser.parse(markdown)));
        sb.append("\n</body></html>\n");
        return sb.toString();

    }

    /**
     * Get lowercase extension from file name or path.
     *
     * @param path file name, not file path
     * @return lowercase extension
     */
    public static String extension(final String path) {
        String extension = "";
        final int i = path.lastIndexOf('.');
        if (i > 0) {
            extension = path.toLowerCase().substring(i + 1);
        }
        return extension;
    }

    /**
     * Return list of object representation.
     *
     * @param objects objects where toString() will be called
     * @return object representations.
     */
    public static List<String> extracting(final Set<? extends Object> objects) {
        final List<String> values = new ArrayList<>();
        objects.forEach(object ->
        values.add(object.toString())
                );
        return values;
    }

    /**
     * Get file content of embedded file.
     *
     * @param clazz root class to get resource file
     * @param resource file path
     * @return file content
     * @throws java.io.IOException if resource was not found or encoding is not
     * UTF-8 or while reading
     */
    public static String getFileContent(final Class<?> clazz,
            final String resource) throws IOException {
        final InputStream stream = clazz.getResourceAsStream(resource);
        if (stream == null) {
            throw new IOException("Resource not found: " + resource);
        }
        final Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
        final BufferedReader br = new BufferedReader(reader);
        final StringBuilder sb = new StringBuilder();
        for (int c = br.read(); c != -1; c = br.read()) {
            sb.append((char) c);
        }
        return sb.toString();
    }

    /**
     * Check if the string does not contain text.
     *
     * @param string value to check
     * @return null, empty, only spaces
     */
    public static boolean isBlank(final String string) {
        return string == null || string.isEmpty() || string.matches("\\s*");
    }

    /**
     * Check if the string contains text.
     *
     * @param string value to check
     * @return not null, not empty, not only spaces
     */
    public static boolean isNotBlank(final String string) {
        return string != null && !string.isEmpty() && !string.matches("\\s*");
    }

    /**
     * check if the string contains only digits.
     *
     * @param string value to check
     * @return the string is a number
     */
    public static boolean isNumeric(final String string) {
        if (isBlank(string)) {
            return false;
        }
        for (final char c : string.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Join object representations inserting delimiter between them.
     *
     * @param delimiter delimiter between strings
     * @param values values to join
     * @return the concatenated representations
     */
    public static String join(final Object[] values, final String delimiter) {
        final StringBuilder result = new StringBuilder();

        for (final Object s : values) {
            if (result.length() != 0) {
                result.append(delimiter);
            }
            if (s == null) {
                result.append("null");
            } else {
                result.append(s.toString());
            }
        }

        return result.toString();
    }

    /**
     * Returns input repeated multiplier times.
     *
     * @param input The string to be repeated.
     * @param multiplier Number of time the input string should be repeated.
     * multiplier has to be greater than or equal to 0. If the multiplier is set
     * to 0, the function will return an empty string.
     * @return Returns the repeated string.
     */
    public static String repeat(final String input, final int multiplier) {
        if (multiplier < 1) {
            return "";
        }
        return new String(new char[multiplier]).replace("\0", input);
    }

    /**
     * Make a string's first character uppercase.
     *
     * @param str The input string.
     * @return string with the first character of str capitalized, if that
     * character is alphabetic.
     */
    public static String ucFirst(final String str) {
        return str.substring(0, 1).toUpperCase()
                + str.substring(1).toLowerCase();
    }

    /**
     * Check if the text match the property class.
     *
     * @param clazz class of the property to handle (Double, Integer, String).
     * @param text text to validate
     * @return validation
     */
    public static boolean validate(final Class<?> clazz, final String text) {
        if (clazz == Double.class) {
            return text.matches("-?[0-9]*\\.?[0-9]*");
        } else if (clazz == Integer.class) {
            return text.matches("[0-9]*");
        } else if (clazz == String.class) {
            return true;
        }
        throw new IllegalArgumentException(clazz.getName()
                + " not handled!");
    }

    /**
     * No constructor in utility class.
     */
    private StringUtils() {

    }
}
