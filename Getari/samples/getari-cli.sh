#!/bin/bash
# Example of a Bash script to run the same evaluation file on different datasets.
# $Id$
GETARI_JAR=../target/Getari-1.1.1.jar
GETARI="java -jar $GETARI_JAR"
$GETARI --evaluation evaluation_daily_sample.gri --climate climate_daily_sample.txt --phenology pheno_sample.csv --results out_sample.csv
$GETARI --evaluation evaluation_daily_sample.gri --climate climate_daily_sample_1997_2018.txt --phenology pheno_sample_1997_2018.csv --results out_sample_1997_2018.csv
$GETARI --multiexecution multiexecution.xml
