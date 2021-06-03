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

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;

/**
 * Subscriber after adding an event handler to the event bus.
 *
 * Last change $Date$
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 * @author $Author$
 * @version $Revision$
 * @param <T> event
 */
public class Subscriber<T extends Event> {

    /**
     * Bus.
     */
    private final EventBus eventBus;
    /**
     * Event type.
     */
    private final EventType<T> eventType;
    /**
     * Event handler.
     */
    private final EventHandler<T> eventHandler;

    /**
     * Constructor.
     *
     * @param bus bus
     * @param type event type
     * @param handler handler
     */
    Subscriber(final EventBus bus, final EventType<T> type,
            final EventHandler<T> handler) {
        this.eventBus = bus;
        this.eventType = type;
        this.eventHandler = handler;
    }

    /**
     * Stop listening for events.
     */
    public final void unsubscribe() {
        eventBus.removeEventHandler(eventType, eventHandler);
    }
}
