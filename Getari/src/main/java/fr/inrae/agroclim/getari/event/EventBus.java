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
import javafx.scene.Group;
import lombok.extern.log4j.Log4j2;

/**
 * A simple event bus using JavaFX event API.
 *
 * Last change $Date$
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 * @author $Author$
 * @version $Revision$
 */
@Log4j2
public final class EventBus {

    /**
     * Some JavaFX component that has event handling.
     */
    private final Group eventHandlers = new Group();

    /**
     * Register event handler for event type.
     *
     * @param eventType type
     * @param eventHandler handler
     * @param <T> event
     * @return subscribers
     */
    public <T extends Event> Subscriber<T> addEventHandler(
            final EventType<T> eventType,
            final EventHandler<T> eventHandler) {
        eventHandlers.addEventHandler(eventType, eventHandler);
        return new Subscriber<>(this, eventType, eventHandler);
    }
    /**
     * Post (fire) given event. All listening parties will be notified. Events
     * will be handled on the same thread that fired the event, i.e.
     * synchronous.
     *
     * <p>
     * Note: according to JavaFX doc this must be called on JavaFX Application
     * Thread. In reality this doesn't seem to be true.
     * </p>
     *
     * @param event the event
     */
    public void fireEvent(final Event event) {
        LOGGER.traceEntry(event.getEventType().getName());
        eventHandlers.fireEvent(event);
    }

    /**
     * Remove event handler for event type.
     *
     * @param eventType type
     * @param eventHandler handler
     * @param <T> event
     */
    public <T extends Event> void removeEventHandler(
            final EventType<T> eventType,
            final EventHandler<? super T> eventHandler) {
        eventHandlers.removeEventHandler(eventType, eventHandler);
    }
}
