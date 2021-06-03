:: Example of Batch script to run the same evaluation file on different datasets.
@echo off
setlocal EnableDelayedExpansion
:: /!\ Change this absolute path to Getari.exe according to your installation /!\
set "GETARI=C:\Users\omaury\AppData\Local\Getari\Getari.exe"

CALL :Getari evaluation_daily_sample.gri climate_daily_sample.txt pheno_sample.csv out_sample.csv
CALL :Getari evaluation_daily_sample.gri climate_daily_sample_1997_2018.txt pheno_sample_1997_2018.csv out_sample_1997_2018.csv

ECHO Using the multiple execution file
"%GETARI%" --multiexecution multiexecution.xml

ECHO All done
GOTO :eof

:Getari
"%GETARI%" --evaluation %~1 --climate %~2 --phenology %~3 --results %~4
EXIT /B 0
endlocal

