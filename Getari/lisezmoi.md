# GETARI

# Développement

Une version de Java postérieure à 14 est nécessaire.
Les développements ont été faits sous Ubuntu 18.04 avec OpenJDK 14.0.1.


## Contenu du dossier `bin/`

Ce dossier `bin/` contient différents scripts pour faciliter le développement :

* `check_images.sh` pour lister les images inutilisées,
* `check_properties.sh` pour vérification de l’utilisation des chaînes de caractères des fichiers `.properties` et afficher les clefs inutilisées,
* `jpackage.bat` pour la construction de l'installeur à partir du fichier `.jar`, à exécuter ainsi `bin\jpackage.bat %JAVA_HOME% 1.0.14`,
* `jpackage.sh` pour la construction des paquets `.deb` et `.rpm` à partir du fichier `.jar`, à exécuter ainsi `bin\jpackage.sh %JAVA_HOME% 1.0.14`,
* `package.bat` pour construire l'installeur à partir des sources, paramétrer les chemins avant exécution,
* `sloccount.bat` est un fichier de commandes BAT vide fourni pour pouvoir développer sous Windows sans lancer de dénombrement de lignes,
* `sloccount.sh` pour dénombrer le nombre de lignes et fournir à Jenkins un fichier Sloccount afin de générer le graphique d'évolution du nombre de lignes,
* `tokei2sloccount.py` pour transformer les sorties de Tokei dans le format Sloccount,
* `web2site.sh` recopie les pages du site web nécessaire à la génération du document PDF à l'aide de `mvn pdf:pdf -DincludeReports=false`.

# Publication

## Conseils pour les notes de version

Tenir compte du public visé lors de la rédaction en réfléchissant à leurs besoins.

Les notes de version publiques doivent contenir au moins :

- le numéro de version, le numéro de construction
- tous les bogues publiques corrigés
- toutes les fonctionnalités publiques ajoutées

Les notes de version AQ doivent contenir au moins :

- le numéro de version, le numéro de construction
- tous les bogues corrigés, avec leur numéro
- toutes les fonctionnalités ajoutées, avec les liens vers les documents de conception

## Empaquetage sous Windows

Ne fonctionne pas avec les versions qui finissent par `-SNAPSHOT` à cause des règles de nommage des versions sous Windows.

1. Récupérer les sources.
2. Installer le JDK 14, la version [OpenJDK 14 d'AdoptOpenJDK](https://adoptopenjdk.net/?variant=openjdk14&jvmVariant=hotspot) a été testée sous Windows.
3. Installer Maven, la version 3.6.0 a été testée sous Windows.
4. Installer [WiX](https://github.com/wixtoolset/wix3/releases).
5. Ajouter dans le PATH le chemin du dossier de ses exécutables (Panneau de configuration\Tous les Panneaux de configuration\Comptes d’utilisateurs).
6. Lancer `bin\package.bat` en ligne de commande.
7. Le paquet JAR exécutable est dans `target\Getari-1.0.14.jar`.
8. L'installeur EXE est dans `package\Getari-1.0.14.exe`.

----

$Id$