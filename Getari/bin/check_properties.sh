#!/bin/bash
capitalize_first() {
	string0="$@"
	firstchar=${string0:0:1}
	string1=${string0:1}
	FirstChar=`echo "$firstchar" | tr a-z A-Z`
	echo "$FirstChar$string1"
}

RES_DIR=src/main/resources/fr/inrae/agroclim/getari
JAVA_DIR=src/main/java/fr/inrae/agroclim/getari
for LANG in "" "_fr"; do
    echo "- LANG=$LANG"
    PROP=$RES_DIR/resources/messages${LANG}.properties
    echo "  - $PROP"
    for TOK in $(grep '=' $PROP | awk 'BEGIN{FS="="}{print $1}' | awk 'BEGIN{FS="["}{print $1}'); do
            echo $(grep -cr "$TOK" src/main/java src/main/resources | grep -v 0$ | wc -l) " : $TOK";
    done | grep "^0" | sort -rn

    PROPS=$(ls src/main/resources/fr/inrae/agroclim/getari/view/*_fr.properties)
    for PROP in $PROPS; do
            VIEW=$(basename $PROP _fr.properties)
            echo "  - $VIEW"
            FXML=$RES_DIR/view/${VIEW}.fxml
            PROP=$RES_DIR/view/${VIEW}${LANG}.properties
            CONTROLLER=$JAVA_DIR/controller/$(capitalize_first $VIEW)Controller.java
            for TOK in $(grep '=' $PROP | awk 'BEGIN{FS="="}{print $1}'| awk 'BEGIN{FS="["}{print $1}'); do
                    echo $(grep -c "$TOK" $FXML $CONTROLLER| grep -v 0$ | wc -l) " : $TOK";
            done | grep "^0" | grep -v ": ${VIEW}.view.title$" | sort -rn
    done

done
