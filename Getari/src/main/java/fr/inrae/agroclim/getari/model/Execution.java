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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * One execution of an evaluation.
 *
 * Last changed : $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
@EqualsAndHashCode(of = { "climate", "output", "phenology" })
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
public class Execution {

    /**
     * Relative path of climate file.
     */
    @Setter
    @Getter
    @XmlAttribute
    private String climate;

    /**
     * Relative path of output file.
     */
    @Setter
    @Getter
    @XmlAttribute
    private String output;

    /**
     * Relative path of phenology file.
     */
    @Setter
    @Getter
    @XmlAttribute
    private String phenology;

}
