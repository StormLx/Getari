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

module Getari {
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.dataformat.csv;
    requires commons.jexl3;
    requires commons.logging;
    requires flexmark;
    requires flexmark.util.ast;
    requires flexmark.util.builder;
    requires flexmark.util.data;
    requires flexmark.util.sequence;
    requires fr.inrae.agroclim.indicators;
    requires java.desktop;
    requires java.prefs;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.swing;
    requires javafx.web;
    requires lombok;
    requires org.apache.logging.log4j;
    requires org.apache.logging.log4j.core;
    requires redmine.java.api;
    requires semver4j;
    requires java.xml.bind;

    opens fr.inrae.agroclim.getari to javafx.graphics;
    opens fr.inrae.agroclim.getari.component to javafx.fxml;
    opens fr.inrae.agroclim.getari.controller to javafx.fxml, javafx.graphics;
    opens fr.inrae.agroclim.getari.view to javafx.fxml;
    opens fr.inrae.agroclim.getari.resources to fr.inrae.agroclim.indicators, fr.inrae.agroclim.indicators.resources;
}
