+++
# $Id$
title = "How to use Getari?"
author = "David Delannoy, Julie Caubel, Iñaki Garcia de Cortazar Atauri"
description = "Usage of GETARI: data, installation, handling"
keywords = ["FAQ","How do I","questions","what if"]
date = 2021-04-08
toc = true
+++

**Generic Evaluation Tool of AgRoclimatic Indicators**

## What do you need to run GETARI?

In order to use GETARI, you need two informations:

- weather data from one point,
- phenological data or calendar dates to calculate indicators.

### Weather data

Getari uses daily or hourly weather data according to the time scale of indicators to compute.
Files should have a defined structure that you can create in different software as Excel or LibreOffice.
Data can be in a CSV or txt format, separator should be semicolon, comma or tabulation.
Data have to be structured by columns: year, month, day of month and after weather variables.

If the indicators to be calculated are at hourly time scale, a column for the hour is required.
The hour is either an integer between 0 and 23 inclusive, or 1 and 24 inclusive.

![Diagramme](/getari/images/usage-fig1.png)

Getari includes a tool allowing you to put your data in the correct format.
You can use this tool to identify which are the different variables you will use to compute indicators.

### Phases data

In this file you will include information of the different phases that you want to use to calculate indicators.
You must create one file by site.
However in this file you can identify all the years you need.
First column identify harvest year.

![Diagramme](/getari/images/usage-fig2.png)

## How to start with GETARI?

After you have [downloaded GETARI](/getari/en/download/), you can install directly in your computer.

Simply click on the desktop icon or search GETARI in the Windows menu.

If you choose the JAR archive, in order to run GETARI you need to verify that you **have at least Java version 11**.
To run GETARI, you have to execute **Getari-1.1.XX.jar** either double-clicking on the file or in command line:

    /path/to/java-11/bin/java -jar Getari-1.1.XX.jar

You will find this window

![Diagramme](/getari/images/usage-fig4.png)

You can open an existing evaluation (`*.gri` files) or to start a new evaluation.
If you want to create a new evaluation, you will open this window

![Diagramme](/getari/images/usage-fig5.png)

You can name your evaluation as you want.
After you have to choose different files: a) phenological file (with calendar data) and b) weather data file.

**a) Phenological file format setting**

The phenological file will ask you to identify year column (information described in the “Columns to drag”).
All the other columns are after the “year column” and must be in increasing order.

![Diagramme](/getari/images/usage-fig6.png)

**b) Climatic file format setting**

The climatic file needs to identify several columns.
The header of different columns are available at the “Columns to drag” box.
If headers of your original file do not have the same names that climatic variables described in GETARI, program will ask you to identify them (`?` red values).
When you have identified all the columns you need in the file you can “Create” it.

![Diagramme](/getari/images/usage-fig7.png)

For an evaluation at hourly time scale, the column "hour" must also be defined, with the value for midnight:

* either 0:00 for midnight at day D,
* or 24:00 for midnight at day D+1.

![Diagramme](/getari/images/usage-fig7b.fr.png)

Once you have chosen your climatic and phase files, you can create your evaluation.
A new window appears allowing you to create a new evaluation tree.

![Diagramme](/getari/images/usage-fig8.png)

When you create a tree, firstly you have to choose the phase that you want to evaluate, after the processes (growth, mortality and management), the climatic effect and finally the indicator you want to calculate.

![Diagramme](/getari/images/usage-fig9.png)

For each indicator you can edit normalization function and when necessary you can also write different aggregation functions (mean, min, max… values).
To calculate aggregation functions you must use the Id value which is identified in each box.

![Diagramme](/getari/images/usage-fig10.png)

If you want to save the evaluation tree, you can use the “Save” button.

![Diagramme](/getari/images/usage-fig11.png)

If you have chosen one indicator which is missing in the climatic file, an error will appear in the “Problem tab”.
If you have forgotten to define the aggregation function, an error will also appear.

![Diagramme](/getari/images/usage-fig12.png)

Once, you have finalized to prepare your evaluation tree (define normalization functions, thresholds, aggregation functions) and you do not have any error (only warnings), you can run the evaluation.

A new tab will open with results for each indicator, climatic effect, ecophysiological processes and phase.
Raw data represents absolute values of the indicator.
You can save our results as a file with `.out` extension or a CSV file.
You can also copy your results (“Copy to clipboard button“) to be used in other programs as Excel or LibreOffice.

![Diagramme](/getari/images/usage-fig14.png)

NB: files with extenstion `.gri` are associated to GETARI, so you can directly double-click on the file to launch GETARI.
You can also drag and drop the file into the menu in GETARI to open the file.

### Command line

You can launch GETARI using the command line interface.
This is an example to show the help on Windows:

`C:\Users\nom_d_utilisateur\AppData\Local\Getari\Getari.exe --help`

or with the JAR file:

`java -jar Getari-1.1.XX.jar --help`

You can run an evaluation using the command line interface with this arguments:

`--evaluation evaluation_sample.gri --climate climat_sample_1997_2018.txt --phenology pheno_sample_1997_2018.csv --results out_sample_1997_2018.csv`

You can run a multiple execution with this arguments:

`--multiexecution samples/multiexecution.xml`

Samples for Windows and Bash are given in the sample archive available in the [download page](../download/).

## How to get support?

Support for GETARI is available in a variety of different forms:

* using the form in the GETARI application (prefered method),
* using the [Redmine forge](https://w3.avignon.inrae.fr/forge/projects/getarj/issues/new),
* using the [contact form](/getari/en/contact/) on this web site.

## References

- Garcia De Cortazar Atauri, Inaki; Maury, Olivier, 2019, "GETARI : Generic Evaluation Tool of AgRoclimatic Indicators", DOI [10.15454/IZUFAP](https://doi.org/10.15454/IZUFAP), Portail Data INRAE, V1
- Caubel, J., Garcia de Cortazar-Atauri, I., Launay, M., De Noblet-Ducoudré, N., Huard, F., Bertuzzi, P., Graux, A-I. (2015). Broadening the scope for ecoclimatic indicators to assess crop climate suitability according to ecophysiological, technical and quality criteria. DOI [10.1016/j.agrformet.2015.02.005](http://doi.org/10.1016/j.agrformet.2015.02.005).

----

Olivier Maury, Iñaki Garcia de Cortazar Atauri, David Delannoy, Julie Caubel
08/04/2021

----
