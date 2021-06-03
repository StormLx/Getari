/*
 * Copyright (C) 2020 INRAE AgroClim
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
package fr.inrae.agroclim.getari;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.NodeQueryUtils.hasText;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Simple test to check TestFX configuration.
 *
 * From https://github.com/TestFX/TestFX/wiki/Getting-Started
 */
public final class TestFXTest extends ApplicationTest {

    public static final class ClickApplication extends Application {
        // application for acceptance tests.

        // scene object for unit tests
        public static class ClickPane extends StackPane {

            public ClickPane() {
                super();
                final Button button = new Button("click me!");
                button.setOnAction(actionEvent -> button.setText("clicked!"));
                getChildren().add(button);
            }
        }

        @Override
        public void start(final Stage stage) {
            final Parent sceneRoot = new ClickPane();
            final Scene scene = new Scene(sceneRoot, 100, 100);
            stage.setScene(scene);
            stage.show();
        }
    }

    @Test
    public void shouldClickOnButton() {
        // when:
        clickOn(".button");

        // then:
        verifyThat(".button", hasText("clicked!"));
    }

    @Test
    public void shouldContainButton() {
        // expect:
        verifyThat(".button", hasText("click me!"));
    }

    @Override
    public void start(final Stage stage) {
        final Parent sceneRoot = new ClickApplication.ClickPane();
        final Scene scene = new Scene(sceneRoot, 100, 100);
        stage.setScene(scene);
        stage.show();
    }

}
