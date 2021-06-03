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
package fr.inrae.agroclim.getari.event;

import java.util.Map;

import fr.inrae.agroclim.indicators.exception.ErrorMessage;
import fr.inrae.agroclim.indicators.model.data.ResourceManager;
import javafx.event.Event;
import javafx.event.EventType;
import lombok.Getter;
import lombok.Setter;

/**
 * Event when the phenological data and climatic data are not consistent.
 *
 * Last change $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
public class ConsistencyEvent extends Event {

    /**
     * UID for serializable.
     */
    private static final long serialVersionUID = 4448922865924801902L;

    /**
     * Event type.
     */
    public static final EventType<ConsistencyEvent> TYPE = new EventType<>(Event.ANY, "CONSISTENCY_EVENT");

    /**
     * Errors about consitency.
     */
    @Getter
    @Setter
    private Map<ResourceManager.ErrorI18nKey, ErrorMessage> errors;

    /**
     * Constructor.
     */
    public ConsistencyEvent() {
        super(TYPE);
    }
}
