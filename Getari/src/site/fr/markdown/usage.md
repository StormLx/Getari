
**Generic Evaluation Tool of AgRoclimatic Indicators**

## De quoi avez-vous besoin pour faire fonctionner GETARI ?

Pour utiliser GETARI, vous avez besoin de deux types de données :

- données météorologiques d'un lieu donné,
- données phénologiques ou dates de calendrier pour le calcul des indicateurs.

### Données météorologiques

Getari utilise des données météorologiques quotidiennes ou horaires selon le pas de temps des indicateurs à calculer.
Les fichiers doivent avoir une structure définie que vous pouvez créer dans différents logiciels comme Excel ou LibreOffice.
Les données peuvent être au format CSV ou TXT, le séparateur peut être le point-virgule, la virgule ou la tabulation.
Les données doivent être structurées par colonnes : année, mois, jour du mois et après les variables météorologiques.

Si les indicateurs à calculer sont au pas de temps horaire, une colonne pour l'heure est nécessaire.
L'heure est un entier entre 0 et 23 inclus ou 1 et 24 inclus.

![Diagramme](images/usage-fig1.png)

Getari intègre un outil vous permettant de mettre vos données dans le bon format.
Vous pouvez utiliser cet outil pour déterminer quelles sont les différentes variables que vous utiliserez pour calculer les indicateurs.

### Données des phases

Dans ce fichier, vous trouverez les dates (jour de l'année) des différents stades que vous souhaitez utiliser pour calculer les indicateurs.
Vous devez créer un fichier par site.
Toutefois, dans ce fichier, vous pouvez identifier toutes les années dont vous avez besoin.
La première colonne identifie l'année de récolte.

![Diagramme](images/usage-fig2.png)

## Comment commencer à utiliser GETARI ?

Après avoir [téléchargé GETARI](download/), vous pouvez l'installer directement sur votre ordinateur.

Il suffit de cliquer sur l'icône du bureau ou de rechercher GETARI dans le menu Windows.

Si vous choisissez l'archive JAR pour lancer GETARI, vous devez vérifier que vous **avez au moins la version 11** de Java.
Pour exécuter GETARI, vous devez exécuter **Getari-1.1.XX.jar** soit en double-cliquant sur le fichier soit en ligne de commande :

    /path/to/java-11/bin/java -jar Getari-1.1.XX.jar

Vous allez voir apparaitre cette fenêtre

![Diagramme](images/usage-fig4.fr.png)

Vous pouvez ouvrir une évaluation existante (fichiers `*.gri`) ou commencer une nouvelle évaluation.
Si vous voulez créer une nouvelle évaluation, vous pouvez ouvrir la fenêtre suivante

![Diagramme](images/usage-fig5.fr.png)

Vous pouvez nommer votre évaluation comme vous le souhaitez.
Ensuite, vous devez choisir différents fichiers : a) le fichier phénologique (avec les données du calendrier) et b) le fichier des données météorologiques.

**a) Définition du format du fichier phénologique**

Le fichier phénologique vous demandera d'identifier la colonne de l'année (information décrite dans la rubrique "Colonnes à glisser").
Toutes les autres colonnes se trouvent après la "colonne année" et doivent être dans l'ordre croissant.

![Diagramme](images/usage-fig6.fr.png)

**b) Définition du format du fichier climatique**

Le fichier climatique doit identifier plusieurs colonnes.
Les en-têtes des différentes colonnes sont disponibles dans la boîte "Colonnes à glisser".
Si les en-têtes de votre fichier original n'ont pas les mêmes noms que les variables climatiques décrites dans GETARI, le programme vous demandera de les identifier (`?` valeurs rouges).
Lorsque vous avez identifié toutes les colonnes dont vous avez besoin dans le fichier, vous pouvez le "Créer".

![Diagramme](images/usage-fig7.fr.png)

Dans le cas d'une évaluation au pas de temps horaire, la colonne "hour" est aussi à définir, ainsi que la valeur pour minuit :

* soit 0h00 pour minuit au jour J,
* soit 24h00 pour minuit au jour J+1.

![Diagramme](images/usage-fig7b.fr.png)

Une fois que vous avez choisi vos fichiers climatiques et de phases, vous pouvez créer votre évaluation.
Une nouvelle fenêtre s'ouvre et vous permet de créer un nouvel arbre d'évaluation.

![Diagramme](images/usage-fig8.fr.png)

Lorsque vous créez un arbre, vous devez d'abord choisir la phase que vous voulez évaluer, puis les processus (croissance, mortalité et gestion), l'effet climatique et enfin l'indicateur que vous voulez calculer.

![Diagramme](images/usage-fig9.fr.png)

Pour chaque indicateur, vous pouvez modifier la fonction de normalisation et, si nécessaire, vous pouvez également écrire différentes fonctions d'agrégation (valeurs moyennes, min, max...).
Pour calculer les fonctions d'agrégation, vous devez utiliser la valeur Id qui est identifiée dans chaque case.

![Diagramme](images/usage-fig10.fr.png)

Si vous souhaitez enregistrer l'arbre d'évaluation que vous venez de créer, vous pouvez utiliser le bouton "Enregistrer".

![Diagramme](images/usage-fig11.png)

Si vous avez choisi un indicateur qui manque dans le fichier climatique, une erreur apparaîtra dans l'onglet "Erreurs".
Si vous avez oublié de définir la fonction d'agrégation, une erreur apparaîtra également.

![Diagramme](images/usage-fig12.fr.png)

Une fois que vous avez finalisé la préparation de votre arbre d'évaluation (définition des fonctions de normalisation, des seuils, des fonctions d'agrégation) et que vous n'avez aucune erreur (seulement des avertissements), vous pouvez lancer l'évaluation.

Un nouvel onglet s'ouvrira avec les résultats pour chaque indicateur, effet climatique, processus et phase écophysiologiques.
Les données brutes représentent les valeurs absolues de l'indicateur.
Vous pouvez enregistrer vos résultats dans un fichier portant l'extension `.out` ou un fichier CSV.
Vous pouvez également copier vos résultats ("bouton Copier dans le presse-papiers") pour les utiliser dans d'autres programmes comme Excel ou LibreOffice.

![Diagramme](images/usage-fig14.fr.png)

À noter que les fichier avec l'extention `.gri` sont associés à GETARI et que vous pouvez directement double-cliquer dessus pour lancer GETARI.
Vous pouvez aussi glisser-déposer le fichier dans la barre de menu de GETARI pour ouvrir le fichier.

### Ligne de commande

Vous pouvez lancer GETARI en ligne de commande.
Voici un exemple pour afficher l'aide sous Windows :

`C:\Users\nom_d_utilisateur\AppData\Local\Getari\Getari.exe --help`

ou avec le fichier JAR :

`java -jar Getari-1.1.XX.jar --help`

Vous pouvez effectuer une évaluation en utilisant l'interface en ligne de commande avec ces arguments :

`--evaluation evaluation_sample.gri --climate climat_sample_1997_2018.txt --phenology pheno_sample_1997_2018.csv --results out_sample_1997_2018.csv`

Vous pouvez lancer une exécution multiple avec ces arguments :

`--multiexecution samples/multiexecution.xml`

Des exemples pour Windows et Bash sont donnés dans l'archive des exemples disponible dans la [page de téléchargement](../download/).

## Comment obtenir de l'aide ?

L'aide pour GETARI est disponible sous différentes formes :

* en utilisant le formulaire dans l'application GETARI (méthode préférée),
* en utilisant la [forge de Redmine](https://w3.avignon.inrae.fr/forge/projects/getarj/issues/new),
* en utilisant le [formulaire de contact](contact/) sur ce site web.

## References

- Garcia De Cortazar Atauri, Inaki; Maury, Olivier, 2019, "GETARI : Generic Evaluation Tool of AgRoclimatic Indicators", DOI [10.15454/IZUFAP](https://doi.org/10.15454/IZUFAP), Portail Data INRAE, V1
- Caubel, J., Garcia de Cortazar-Atauri, I., Launay, M., De Noblet-Ducoudré, N., Huard, F., Bertuzzi, P., Graux, A-I. (2015). Broadening the scope for ecoclimatic indicators to assess crop climate suitability according to ecophysiological, technical and quality criteria. DOI [10.1016/j.agrformet.2015.02.005](http://doi.org/10.1016/j.agrformet.2015.02.005).

----

Olivier Maury et Iñaki Garcia de Cortazar Atauri
05/02/2021

----
