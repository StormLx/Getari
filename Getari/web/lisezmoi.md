Site web de GETARI
===============

## Comment est-il construit ?

Le site web est statique, hormis le script d'envoi de courriels (`php/contact.php`).
Les pages sont générées par [Hugo](https://gohugo.io/) à l'aide du thème [Hugo Universal](https://themes.gohugo.io/hugo-universal-theme/) modifié.

Pour information :

- l'arborescence originale a été construite avec `hugo new site`,
- le thème a été récupéré avec `(cd themes; git clone https://github.com/devcows/hugo-universal-theme hugo-theme-universal`,
- la configuration a été copiée et adaptée de `themes/hugo-theme-universal/exampleSite/config.toml`.

Pour construire le site, lancer `hugo`. Les pages du site sont générées dans `public/`.

## Comment déployer ?

Le site web est accessible à https://w3.avignon.inrae.fr/getari/.

Le site web est déployé sur `agroclimVM45` (147.100.20.95) à l'aide du script `bin/deploy.sh`.

----

$Id$
