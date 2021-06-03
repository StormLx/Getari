#!/usr/bin/env python
# -*- coding: UTF-8 -*-
# $Id$
#
# Author : Olivier Maury
# Creation Date : 2018-08-09 17:18:29 +0200
# Last Revision : $Date$
# Revision : $Rev$
u"""
[1mNOM[m
        %prog - Tokei2Sloccount

[1mDESCRIPTION[m
        Formate la sortie de tokei dans le format de Sloccount

[1mEXEMPLE[m
        tokei -f -o json bin src | %prog

[1mAUTEUR[m
        Olivier Maury

[1mVERSION[m
        $Date$
"""

__revision__ = "$Rev$"
__author__ = "Olivier Maury"
import json
import sys

results = json.loads(sys.stdin.read())
for lang in results:
    for result in results[lang]['stats']:
        path = result['name']
        directory = path.split('/')[-2]
        print("%s\t%s\t%s\t%s\t" % (result['lines'], lang.lower(), directory, path))
