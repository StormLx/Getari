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
package fr.inrae.agroclim.getari.model;

import java.time.LocalDateTime;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * XML adapter for LocalDateTime.
 *
 * Handles string such as 2007-12-03T10:15:30. The string must represent a valid date-time and is parsed using
 * DateTimeFormatter.ISO_LOCAL_DATE_TIME.
 *
 * Last changed : $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
public final class LocalDateTimeAdapter extends XmlAdapter<String, LocalDateTime> {

    @Override
    public String marshal(final LocalDateTime date) throws Exception {
        if (date == null) {
            return null;
        }
        return date.toString();
    }

    @Override
    public LocalDateTime unmarshal(final String source) throws Exception {
        if (source == null) {
            return null;
        }
        return LocalDateTime.parse(source);
    }
}
