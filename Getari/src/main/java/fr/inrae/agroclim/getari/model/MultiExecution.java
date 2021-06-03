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
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Description of multiple executions of an evaluation.
 *
 * Last changed : $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
@XmlRootElement(name = "multiexecution")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"evaluation", "executions"})
@EqualsAndHashCode(
        callSuper = false,
        of = {"evaluation", "executions"})
@ToString
public class MultiExecution {
    /**
     * Combinations to execute.
     */
    @Getter
    @Setter
    @XmlElement(name = "execution")
    private List<Execution> executions = new ArrayList<>();
    /**
     * Path of evaluation.
     */
    @Getter
    @Setter
    private String evaluation;
    /**
     * Version of GETARI.
     */
    @Getter
    @Setter
    @XmlAttribute(required = false)
    private String version = "";

    /**
     * Date of last modification.
     */
    @Getter
    @Setter
    @XmlAttribute(name = "timestamp", required = false)
    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    private LocalDateTime timestamp;
}
