#!/bin/bash

TOKEITGZ=tokei-v7.0.3-x86_64-unknown-linux-gnu.tar.gz
if [ ! -f bin/tokei ]; then
	mkdir -p ~/tmp
	if [ ! -f ~/tmp/$TOKEITGZ ]; then
		echo ~/tmp/$TOKEITGZ
		wget "https://github.com/Aaronepower/tokei/releases/download/v7.0.3/$TOKEITGZ" -O ~/tmp/$TOKEITGZ
	fi
	tar zxf ~/tmp/$TOKEITGZ -C bin
fi
if [ -f bin/tokei ]; then
	bin/tokei -f -o json src | bin/tokei2sloccount.py > target/sloccount.sc
	exit
fi

SLOCCOUNT=$(which sloccount);
if [ "$SLOCCOUNT" != "" ]; then
	DATADIR=$(dirname $0)/.slocdata;
	if [ ! -f $DATADIR ]; then
		mkdir -p $DATADIR;
	fi
	mkdir -p target ;
	/usr/bin/sloccount --datadir $DATADIR --duplicates --wide --details src > target/sloccount.sc;
else 
	echo "sloccount not found!";
fi
