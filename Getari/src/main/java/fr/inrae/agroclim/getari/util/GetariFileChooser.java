/*
 * Copyright (C) 2021 INRAE AgroClim
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
package fr.inrae.agroclim.getari.util;

import java.io.File;

import fr.inrae.agroclim.getari.component.GetariApp;
import fr.inrae.agroclim.getari.component.GetariPreferences;
import fr.inrae.agroclim.getari.resources.Messages;
import javafx.beans.property.ObjectProperty;
import javafx.stage.FileChooser;
import javafx.stage.Window;

/**
 * File chooser for file types handled in GETARI.
 *
 * Last change $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
public final class GetariFileChooser {

    /**
     * Default file chooser.
     */
    private final FileChooser chooser;

    /**
     * Handled file type.
     */
    private final FileType type;

    /**
     * Constructor.
     *
     * @param fileType handled file type
     */
    public GetariFileChooser(final FileType fileType) {
        type = fileType;
        chooser = new FileChooser();
        final File initialDir = GetariApp.getLastDirectory(type);
        chooser.setInitialDirectory(initialDir);
        type.getFilters().forEach((description, extension)
                -> chooser.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter(
                                Messages.getString(description), extension))
                );
        chooser.setTitle(Messages.getString(type.getTitle()));
    }

    /**
     * This property is used to pre-select the extension filter for the next displayed dialog and to read the
     * user-selected extension filter from the dismissed dialog.
     * <p>
     * When the file dialog is shown, the selectedExtensionFilter will be checked. If the value of
     * selectedExtensionFilter is null or is not contained in the list of extension filters, then the first extension
     * filter in the list of extension filters will be selected instead. Otherwise, the specified
     * selectedExtensionFilter will be activated.
     * <p>
     * After the dialog is dismissed the value of this property is updated to match the user-selected extension filter
     * from the dialog.
     *
     * @return property with pre-selected or selected extension filter.
     */
    public ObjectProperty<FileChooser.ExtensionFilter> selectedExtensionFilterProperty() {
        return chooser.selectedExtensionFilterProperty();
    }

    /**
     * The initial file name for the displayed dialog.
     * <p>
     * This property is used mostly in the displayed file save dialogs as the
     * initial file name for the file being saved. If set for a file open
     * dialog it will have any impact on the displayed dialog only if the
     * corresponding platform provides support for such property in its
     * file open dialogs.
     * </p>
     *
     * @param value initial file name
     */
    public void setInitialFileName(final String value) {
        chooser.setInitialFileName(value);
    }

    /**
     * Memory directory for the file type.
     *
     * @param file file to memory
     */
    private void setLastDirectory(final File file) {
        if (file != null) {
            final String absolutePath = file.getParentFile().getAbsolutePath();
            final GetariPreferences prefs = GetariApp.get().getPreferences();
            prefs.setLastDirectory(type, absolutePath);
            GetariApp.setLastDirectory(absolutePath);
        }
    }

    /**
     * Shows a new file open dialog.
     *
     * The method doesn't return until the displayed open dialog is dismissed. The return value specifies the file
     * chosen by the user or {@code null} if no selection has been made. If the owner window for the file dialog is set,
     * input to all windows in the dialog's owner chain is blocked while the file dialog is being shown.
     *
     * @param ownerWindow the owner window of the displayed file dialog
     * @return the selected file or {@code null} if no file has been selected
     */
    public File showOpenDialog(final Window ownerWindow) {
        final File file = chooser.showOpenDialog(ownerWindow);
        setLastDirectory(file);
        return file;
    }

    /**
     * Shows a new file save dialog.
     *
     * The method doesn't return until the displayed file save dialog is dismissed. The return value specifies the file
     * chosen by the user or {@code null} if no selection has been made. If the owner window for the file dialog is set,
     * input to all windows in the dialog's owner chain is blocked while the file dialog is being shown.
     *
     * @param ownerWindow the owner window of the displayed file dialog
     * @return the selected file or {@code null} if no file has been selected
     */
    public File showSaveDialog(final Window ownerWindow) {
        File file = chooser.showSaveDialog(ownerWindow);
        if (file == null) {
            return null;
        }
        final String ext = chooser.selectedExtensionFilterProperty()
                .get().getExtensions().get(0).substring(2);
        if (!ext.endsWith("*") && StringUtils.extension(file.getName()).isEmpty()) {
            file = new File(file.getAbsolutePath() + "." + ext);
        }
        setLastDirectory(file);
        return file;
    }

}
