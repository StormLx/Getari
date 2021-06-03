#!/bin/bash
# $Id$
for FI in $(ls src/main/resources/fr/inrae/agroclim/getari/); do
	echo $(grep -cr "$FI" src/main/java src/main/resources | grep -v 0$ | wc -l) " : $FI";
done | grep "^0" | sort -rn
