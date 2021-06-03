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

import static fr.inrae.agroclim.getari.util.GetariConstants.INIT_VALUE;

import fr.inrae.agroclim.getari.util.StringUtils;
import fr.inrae.agroclim.indicators.model.Evaluation;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;

/**
 * A text field to handle Evaluation properties.
 *
 * Last change $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
public class EvaluationTextField extends TextField {

    /**
     * CSS class for an invalid field.
     */
    private static final String INVALID_STYLECLASS = "invalid-field";

    /**
     * CSS class for a valid field.
     */
    private static final String VALID_STYLECLASS = "valid-field";

    /**
     * class of the property to handle (Double, Integer, String).
     */
    private Class<?> clazz;

    /**
     * Simple constructor for FXML.
     */
    public EvaluationTextField() {
        super("");
        this.clazz = String.class;
        setAlignment(Pos.BASELINE_LEFT);
        final int width = 150;
        final int height = 20;
        setPrefWidth(width);
        setPrefHeight(height);
    }

    /**
     * Creates a TextField with initial text content.
     *
     * @param fieldClass class of the property to handle (Double, Integer,
     * String)
     * @param evaluation evalution to handle
     */
    public EvaluationTextField(final Class<?> fieldClass,
            final Evaluation evaluation) {
        this();
        if (fieldClass != Double.class
                && fieldClass != Integer.class
                && fieldClass != String.class) {
            throw new IllegalArgumentException(fieldClass.getName()
                    + " not handled!");
        }
        this.clazz = fieldClass;
        textProperty().addListener(
                (final ObservableValue<? extends String> observable,
                        final String oldValue, final String newValue) -> {
                            if (!oldValue.equals(INIT_VALUE)
                                    && !newValue.equals(oldValue)
                                    && evaluation != null) {
                                evaluation.setTranscient(true);
                            }
                        });
    }

    /**
     * Constructor.
     *
     * @param text initial value
     */
    public EvaluationTextField(final String text) {
        this();
        setText(text);
    }

    /**
     * @return handled field type
     */
    public final String getFieldType() {
        return clazz.getName();
    }

    /**
     * @return Style of TextField is not invalid
     */
    public final boolean isValid() {
        return getStyleClass().contains(VALID_STYLECLASS);
    }

    @Override
    public void replaceSelection(final String text) {
        if (StringUtils.validate(clazz, text)) {
            super.replaceSelection(text);
        }
    }

    @Override
    public void replaceText(final int start, final int end, final String text) {
        // If the replaced text would end up being invalid, then simply
        // ignore this call!
        if (StringUtils.validate(clazz, text)) {
            super.replaceText(start, end, text);
        }
    }

    /**
     * @param className handled field type (java.lang.String...)
     * @throws ClassNotFoundException if unknown type
     */
    public final void setFieldType(final String className)
            throws ClassNotFoundException {
        final Class<?> fieldClass = Class.forName(className);
        if (fieldClass != Double.class
                && fieldClass != Integer.class
                && fieldClass != String.class) {
            throw new IllegalArgumentException(fieldClass.getName()
                    + " not handled!");
        }
        this.clazz = fieldClass;
    }

    /**
     * Set the style of field.
     */
    public final void setInvalid() {
        getStyleClass().add(INVALID_STYLECLASS);
        getStyleClass().remove(VALID_STYLECLASS);
    }

    /**
     * Set the style of field.
     */
    public final void setValid() {
        getStyleClass().add(VALID_STYLECLASS);
        getStyleClass().remove(INVALID_STYLECLASS);
    }

    /**
     * Set the style of field.
     *
     * @param valid validation
     */
    public final void setValid(final boolean valid) {
        if (valid) {
            setValid();
        } else {
            setInvalid();
        }
    }

}
