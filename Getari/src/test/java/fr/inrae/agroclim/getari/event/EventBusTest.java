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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import javafx.beans.NamedArg;
import javafx.event.Event;
import javafx.event.EventType;
import lombok.Getter;

/**
 * Test the event bus.
 *
 * Last changed : $Date$
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 * @author $Author$
 * @version $Revision$
 */
public class EventBusTest {

    static class TestEvent extends Event {

        private static final long serialVersionUID = 4448922865924801976L;

        /**
         * Test type.
         */
        public static final EventType<TestEvent> TYPE = new EventType<>(Event.ANY, "TEST_EVENT");

        /**
         * Associated data.
         */
        @Getter
        private final Object data;

        /**
         * Constructor.
         *
         * @param eventType event type
         * @param val      data
         */
        TestEvent(final @NamedArg("eventType") EventType<? extends Event> eventType, final Object val) {
            super(eventType);
            this.data = val;
        }

    }

    /**
     * Event bus to test bus.
     */
    private EventBus bus;

    /**
     * Nb of calls.
     */
    private int calls;

    /**
     * Current thread ID.
     */
    private long threadID;

    @Before
    public void localSetUp() {
        bus = new EventBus();
        calls = 0;
        threadID = Thread.currentThread().getId();
    }

    @Test
    public void testFireEvent() {
        final EventType<TestEvent> eventType = TestEvent.TYPE;
        final Object data = new Object();
        final Subscriber<TestEvent> subscriber = bus.addEventHandler(TestEvent.TYPE, event -> {
            calls++;
            assertEquals("Handled event on a different thread", threadID,
                    Thread.currentThread().getId());
            assertTrue("Received wrong event", data == event.getData()
                    && eventType == event.getEventType());
        });

        final TestEvent e = new TestEvent(eventType, data);
        bus.fireEvent(e);
        assertEquals(1, calls);
        bus.fireEvent(e);
        assertEquals(2, calls);

        subscriber.unsubscribe();
        bus.fireEvent(e);
        assertEquals(2, calls);
    }
}
