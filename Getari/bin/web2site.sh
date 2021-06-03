#!/bin/bash
# $Id$

# Number of lines in header to ignore in Hugo files
HEADER=10

for IMG in web/static/images/*[0-9].png web/static/images/*[0-9]b.png; do
    BASENAME=$(basename $IMG)
    # English
    (cd src/site/resources/images/; ln -snf ../../../../$IMG $BASENAME);
    # French
    (cd src/site/fr/resources/images/; ln -snf ../../../../../$IMG $BASENAME)
done
# French
for IMG in web/static/images/*[0-9].fr.png web/static/images/*[0-9]b.fr.png; do
    BASENAME=$(basename $IMG)
    echo $BASENAME;
    (cd src/site/fr/resources/images/; ln -snf ../../../../../$IMG $BASENAME)
done

# English
tail -n +$HEADER web/content/about.en.md | sed -e "s:/getari/::g" > src/site/markdown/index.md
tail -n +$HEADER web/content/usage.en.md | sed -e "s:/getari/::g" > src/site/markdown/usage.md

# French
tail -n +$HEADER web/content/about.fr.md | sed -e "s:/getari/::g" > src/site/fr/markdown/index.md
tail -n +$HEADER web/content/usage.fr.md | sed -e "s:/getari/::g" > src/site/fr/markdown/usage.md
