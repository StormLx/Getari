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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;

import org.xml.sax.SAXException;

import fr.inrae.agroclim.getari.model.Execution;
import fr.inrae.agroclim.getari.model.MultiExecution;
import fr.inrae.agroclim.getari.resources.Messages;
import fr.inrae.agroclim.indicators.exception.TechnicalException;
import fr.inrae.agroclim.indicators.xml.MarshallerBuilder;
import fr.inrae.agroclim.indicators.xml.UnmarshallerBuilder;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

/**
 * Helper methods for multiple executions.
 *
 * Last changed : $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
@Log4j2
public abstract class MultiExecutionHelper {
    /**
     * Public ID of doctype for an evaluation.
     */
    private static final String DTD_PUBLIC_ID_MULTIEXECUTION = "-//INRAE AgroClim.//DTD MultiExecution 1.1//EN";

    /**
     * DOCTYPE header for an evaluation.
     */
    private static final String DOCTYPE_MULTIEXECUTION = "<!DOCTYPE multiExecution PUBLIC\n"
            + "    \"" + DTD_PUBLIC_ID_MULTIEXECUTION + "\"\n"
            + "    \"https://w3.avignon.inrae.fr/getari/dtd/1.1/multiexecution.dtd\">";

    /**
     * Classes to de/serialize MultiExecution in XmlUtil.
     */
    private static final Class<?>[] MULTIEXECUTION_CLASSES = {MultiExecution.class, Execution.class};

    /**
     * URL for the XML schema.
     */
    private static final String XSD_MULTIEXECUTION = "https://w3.avignon.inrae.fr/getari/xsd/1.1/multiexecution.xsd";
    /**
     * Load MultiExecution from file path.
     *
     * @param file XML file
     * @return deserialized object
     * @throws TechnicalException error while deserialization
     */
    public static MultiExecution deserialize(final File file) throws TechnicalException {
        try (InputStream inputStream = new FileInputStream(file);) {
            final UnmarshallerBuilder builder = new UnmarshallerBuilder();
            builder.setDtds(getDtds());
            builder.setClassesToBeBound(MULTIEXECUTION_CLASSES);
            final Unmarshaller um = builder.build();
            final Source source = builder.buildSource(inputStream);
            return (MultiExecution) um.unmarshal(source);
        } catch (final FileNotFoundException ex) {
            throw new TechnicalException("File not found " + file.getAbsolutePath(), ex);
        } catch (final IOException ex) {
            throw new TechnicalException("Unable to load file ", ex);
        } catch (JAXBException | ParserConfigurationException | SAXException ex) {
            throw new TechnicalException("Unable to load", ex);
        }
    }

    /**
     * Get value of an execution.
     *
     * @param type file type
     * @param execution execution to handle
     * @return value for file type
     */
    public static String get(final FileType type, final Execution execution) {
        switch (type) {
        case CLIMATE:
            return execution.getClimate();
        case PHENOLOGY:
            return execution.getPhenology();
        case RESULTS:
            return execution.getOutput();
        default:
            throw new IllegalArgumentException("Is not a property of Execution: " + type);
        }
    }

    /**
     * @return  Public ID of doctype → Resource for the doctype file.
     */
    private static Map<String, InputStream> getDtds() {
        final Map<String, InputStream> map = new HashMap<>();
        map.put(DTD_PUBLIC_ID_MULTIEXECUTION,
                MultiExecutionHelper.class.getResourceAsStream("/fr/inrae/agroclim/getari/multiexecution.dtd"));
        return map;
    }

    /**
     * Check if path is valid for an execution.
     *
     * @param type file type for the path to check
     * @param e execution to check
     * @return error found
     */
    private static Optional<String> getErrorForPath(final FileType type, final Execution e) {
        final String path = get(type, e);
        if (StringUtils.isBlank(path)) {
            return Optional.of(Messages.getString("error.empty.path." + type.name().toLowerCase()));
        }
        final File file = new File(path);
        if (!file.exists()) {
            return Optional.of(Messages.getString("error.non-existent.file", path));
        }
        if (!file.canRead()) {
            return Optional.of(Messages.getString("error.non-readable.file", path));
        }

        return Optional.empty();
    }

    /**
     * @param e execution to check
     * @return error for each file type
     */
    public static Optional<Map<FileType, String>> getErrors(final Execution e) {
        final Map<FileType, String> errors = new EnumMap<>(FileType.class);
        Arrays.asList(FileType.CLIMATE, FileType.PHENOLOGY).forEach(type
                -> getErrorForPath(type, e)
                .ifPresent(error -> errors.put(type, error))
                );
        if (StringUtils.isBlank(e.getOutput())) {
            final FileType type = FileType.RESULTS;
            errors.put(type, Messages.getString("error.empty.path." + type.name().toLowerCase()));
        }
        if (errors.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(errors);
    }

    /**
     * Check if a file can be deserialized as MultiExecution.
     *
     * @param file file to check
     * @return true if the file is an XML of MultiExecution.
     */
    public static boolean isMultiExecution(final File file) {
        try {
            deserialize(file);
            return true;
        } catch (final TechnicalException ex) {
            LOGGER.trace(ex);
        }
        return false;
    }

    /**
     * Check if execution is valid.
     *
     * @param e execution to check
     * @return validity
     */
    public static boolean isValid(final Execution e) {
        return isValidPath(e.getClimate())
                && isValidPath(e.getPhenology())
                && StringUtils.isNotBlank(e.getOutput());
    }

    /**
     * Check if MultiExecution and its executions are valid.
     *
     * @param me multiexecution to check
     * @return validity
     */
    public static boolean isValid(final MultiExecution me) {
        if (!isValidPath(me.getEvaluation())) {
            return false;
        }
        if (me.getExecutions() == null) {
            return false;
        }
        return me.getExecutions().stream().allMatch(MultiExecutionHelper::isValid);
    }

    /**
     * Check if a file path is not empty and exists.
     *
     * @param path path to check
     * @return validity
     */
    public static boolean isValidPath(final String path) {
        if (StringUtils.isBlank(path)) {
            return false;
        }
        final File file = new File(path);
        return file.exists();
    }

    /**
     * Relative paths for climate, output and phenology files.
     *
     * @param basePath the path of the XML file
     * @param multiexecution multi-execution to handle
     * @return new multiexecution with relativized paths
     */
    public static MultiExecution relativize(@NonNull final String basePath,
            @NonNull final MultiExecution multiexecution) {
        final MultiExecution relativized = new MultiExecution();
        relativized.setTimestamp(multiexecution.getTimestamp());
        relativized.setVersion(multiexecution.getVersion());
        relativized.setEvaluation(relativizePath(basePath, multiexecution.getEvaluation()));
        multiexecution.getExecutions().forEach(execution -> {
            final Execution relativizedExecution = new Execution();
            relativizedExecution.setClimate(relativizePath(basePath, execution.getClimate()));
            relativizedExecution.setOutput(relativizePath(basePath, execution.getOutput()));
            relativizedExecution.setPhenology(relativizePath(basePath, execution.getPhenology()));
            relativized.getExecutions().add(relativizedExecution);
        });
        return relativized;
    }

    /**
     * Constructs a relative path between a base path and another path.
     *
     * <p> Relativization is the inverse of {@link #resolvePath(java.lang.String, java.lang.String) resolution}.
     * This method attempts to construct a {@link Path#isAbsolute relative} path that when
     * {@link #resolvePath(java.lang.String, java.lang.String) resolved} against this path, yields a path that locates
     * the same file as the given path.</p>
     *
     * @param basePath
     * @param filePath
     * @return relative path
     */
    public static String relativizePath(final String basePath, final String filePath) {
        if (filePath == null) {
            return null;
        }
        final Path parentPath = Paths.get(basePath).getParent();
        if (parentPath == null) {
            return filePath;
        }
        final Path path = parentPath.relativize(Paths.get(filePath));
        return path.toString();
    }

    /**
     * Resolve paths for climate, output and phenology files.
     *
     * @param basePath the path of the XML file
     * @param multiexecution multi-execution to handle
     * @return new multiexecution with resolved paths
     */
    public static MultiExecution resolve(@NonNull final String basePath, @NonNull final MultiExecution multiexecution) {
        final MultiExecution resolved = new MultiExecution();
        resolved.setTimestamp(multiexecution.getTimestamp());
        resolved.setVersion(multiexecution.getVersion());
        resolved.setEvaluation(resolvePath(basePath, multiexecution.getEvaluation()));
        if (multiexecution.getExecutions() != null) {
            multiexecution.getExecutions().forEach(execution -> {
                final Execution resolvedExecution = new Execution();
                resolvedExecution.setClimate(resolvePath(basePath, execution.getClimate()));
                resolvedExecution.setOutput(resolvePath(basePath, execution.getOutput()));
                resolvedExecution.setPhenology(resolvePath(basePath, execution.getPhenology()));
                resolved.getExecutions().add(resolvedExecution);
            });
        }
        return resolved;
    }

    /**
     * Resolve the given path against this path.
     *
     * <p> Resolution is the inverse of {@link #relativizePath(java.lang.String, java.lang.String) relativization}.
     * If the {@code relativePath} parameter is an {@link Path#isAbsolute() absolute}  path then this method trivially
     * returns {@code relativePath}.</p>
     *
     * @param evaluationPath
     * @param relativePath
     * @return absolute path
     */
    public static String resolvePath(final String evaluationPath, final String relativePath) {
        if (relativePath == null) {
            return null;
        }
        if (Paths.get(relativePath).isAbsolute()) {
            return relativePath;
        }
        final Path path = Paths.get(evaluationPath).getParent().resolve(relativePath);
        return path.toString();
    }

    /**
     * Serialize to file.
     *
     * @param o object to serialize
     * @param file file to write into
     * @throws TechnicalException exception from JAXBException
     */
    public static void serialize(final MultiExecution o, final File file) throws TechnicalException {
        final Class<?>[] clazz = MULTIEXECUTION_CLASSES;
        try (FileOutputStream stream = new FileOutputStream(file);
                OutputStreamWriter writer = new OutputStreamWriter(stream, StandardCharsets.UTF_8);
                ) {
            final MarshallerBuilder builder = new MarshallerBuilder();
            builder.setDocType(DOCTYPE_MULTIEXECUTION);
            builder.setClassesToBeBound(clazz);
            builder.setNoNamespaceSchemaLocation(XSD_MULTIEXECUTION);
            final Marshaller marshaller = builder.build();
            marshaller.marshal(o, writer);
        } catch (final IOException ex) {
            LOGGER.catching(ex);
            throw new TechnicalException("Unable to create FileOutputStream : " + o, ex);
        } catch (final JAXBException ex) {
            LOGGER.catching(ex);
            throw new TechnicalException("Unable to serialize : " + o, ex);
        }
    }

    /**
     * Set value of an execution.
     *
     * @param type file type
     * @param execution execution to change
     * @param value value to set
     */
    public static void set(final FileType type, final Execution execution, final String value) {
        switch (type) {
        case CLIMATE:
            execution.setClimate(value);
            return;
        case PHENOLOGY:
            execution.setPhenology(value);
            return;
        case RESULTS:
            execution.setOutput(value);
            return;
        default:
            throw new IllegalArgumentException("Is not a property of Execution: " + type);
        }
    }

    /**
     * According to already given output, suggest new output.
     *
     * @param executions existing execution
     * @return suggested unique file name
     */
    public static String suggestedOutput(final List<Execution> executions) {
        final String format = "output%d.csv";
        final Pattern pattern = Pattern.compile("output(\\d+)\\.csv");
        int nb;
        if (executions == null || executions.isEmpty()) {
            return String.format(format, 1);
        }
        final Set<String> outputs = executions.stream().map(Execution::getOutput).collect(Collectors.toSet());
        final Optional<Integer> outputNb = executions.stream().map(e -> {
            if (StringUtils.isBlank(e.getOutput())) {
                return null;
            }
            final Matcher m = pattern.matcher(e.getOutput());
            if (m.find()) {
                final String chars = m.group(1);
                return Integer.valueOf(chars);
            }
            return null;
        }).filter(Objects::nonNull).max((i, j) -> i.compareTo(j));
        if (outputNb.isEmpty()) {
            nb = executions.size() + 1;
        } else {
            nb = outputNb.get() + 1;
        }
        String suggestion = String.format(format, nb);
        while (outputs.contains(suggestion)) {
            nb++;
            suggestion = String.format(format, nb);
        }
        return suggestion;
    }

    /**
     * No constructor for helper class.
     */
    private MultiExecutionHelper() {
        //
    }
}
