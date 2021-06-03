# Notes de version

## Version 1.1.2 - 31/05/2021

__Corrections :__

* Suppression de l'initialisation des données sol quand elles sont fournies
* Changement des valeurs des fonctions de normalisation dans l'interface
* Ordonner les années dans le fichier résultat CSV
* Détermination des chemins relatifs sous Windows dans le fichier .gri
* Calcul des indicateurs photothermalquotient, hrainpart, shrainpart

__Évolutions :__

* Intégration de la version 1.1.2 de la bibliothèque d’indicateurs
* Ajouter les indicateurs horaires THI

## Version 1.1.1 - 20/04/2021

__Évolutions :__

* Exécution multiple d'une évaluation
* Adaptation de la largeur des colonnes dans la fenêtre d'import des données
* Intégration de la version 1.1.1 de la bibliothèque d’indicateurs
* Ajout de la normalisation affine par morceaux

## Version 1.1.0 - 16/03/2021

__Corrections :__

* Encodage du fichier .gri sous Windows

__Évolutions :__

* Intégration de la version 1.1.0 de la bibliothèque d’indicateurs
* 32 indicateurs au pas de temps horaire
* Définition de XSD et DTD pour la validation des fichiers XML

## Version 1.0.15 - 07/01/2021

__Corrections :__

* Séparateur décimal des résultats
* Exemple d'exécution en ligne de commande

__Évolutions :__

* Avertissement sur les données climatiques manquantes
* Avertissement sur les noms de stade comportant un tiret
* Intégration de la version 1.0.28 de la bibliothèque d’indicateurs

## Version 1.0.14 - 26/06/2020

__Corrections :__

* Suppression d'une virgule en trop dans un CSV

__Évolutions :__

* Suppression des bornes des abscisses du graphique de normalisation
* Passage à Java 11 et OpenJDK14
* Utilisation de WiX Toolset pour la création de l’installeur
* Exemple pour lancement sous Linux

## Version 1.0.13 - 05/05/2020

__Corrections :__

* Rechargement du graphique lors du changement de fonction de normalisation

__Évolutions :__

* Mise en avant du message d'erreur lors de la soumission du formulaire de demande de support sans sujet
* Bornes des abscisses de la représentation de la fonction de normalisation : -9000 à +9000
* Intégration de la version 1.0.24 de la bibliothèque d’indicateurs

## Version 1.0.12 - 03/04/2020

__Corrections :__

* Gestion d’erreurs lors de la recherche des mises à jour
* Affichage de `PotentialSowingDaysFrequency`

__Évolutions :__

* Traduction complète du site Internet
* Changement de bibliothèque de lecture des CSV
* Largeur de l’infobulle sur les indicateurs limitée
* Intégration de la version 1.0.23 de la bibliothèque d’indicateurs
  * Correction des calculs avec `NumberOfWaves`
  * Ajout de l’indicateur sur la moniliose de l’abricotier

## Version 1.0.11 - 05/03/2020

__Corrections :__

* Chemin du fichier si l’importation est annulée

__Évolutions :__

* Ajout de la liste des indicateurs sur le [site Internet](https://w3.avignon.inrae.fr/getari/indicators/)
* Ajout des arguments `--evaluation <path> --climate <path> --phenology <path> --results <path>` pour lancement en ligne de commande
* Prise en charge de l’affichage `DiffOfSum` et `NoCriteria`
* Intégration de la version 1.0.22 de la bibliothèque d’indicateurs

## Version 1.0.10 - 22/01/2020

__Corrections :__

* Affichage de NaN, rencontré notamment dans le cas de divisions par 0 pour les normalisations
* Affichage du message d’erreur d'agrégation manquante après changement de valeur de seuil

__Évolutions :__

* Passage de INRA à INRAE
* Chemin pour l’enregistrement d’évaluation au précédent dossier choisi
* Intégration de la version 1.0.21 de la bibliothèque d’indicateurs
  * Affichage de la formule de normalisation

## Version 1.0.9 - 18/12/2019

__Corrections :__

* Correction d’icônes
* Prise en charge de l’affichage de `CompositeCriteria`, utilisé dans les indicateurs _Jours vernalisants_ et _% de jours vernalisants_
* Intégration de la version 1.0.20 de la bibliothèque d’indicateurs
  * Calcul de l’indicateur « Nombre de jours en déficit de pluie. » `defraidays`
  * Précisions dans les descriptions

__Évolutions :__

* Duplication d’une phase
* Ascenseur horizontal dans la visualisation des résultats
* Valeurs négatives pour les paramètres de la normalisation et les abcisses du graphique
* Intégration de la version 1.0.20 de la bibliothèque d’indicateurs
  * Indicateurs sur l’humidité relative

## Version 1.0.8 - 04/11/2019

__Corrections :__

* Redimensionnement des propriétés de l’indicateur
* Icône de l’application sous macOS X
* Intégration de la version 1.0.18 de la bibliothèque d’indicateurs
  * Gestion des chemins relatifs pour les fichiers en dehors de l’arborescence de l’évaluation

__Évolutions :__

* Arrondi des valeurs du CSV
* Avertissement à de la fermeture d’une évaluation non enregistrée
* Logos en plus haute définition pour macOS X

## Version 1.0.7 - 01/10/2019

__Corrections :__

* Affichage des propriétés d’une évaluation à sa création
* Ajout de l’extension au fichier de résultat si pas spécifiée

__Évolutions :__

* Ajout d’un fichier de métadonnées à côté du fichier CSV
* Arrondi à 3 chiffres après la virgule des résultats dans le fichier CSV
* Modification des abscisses de la représentation de la fonction de normalisation
* Lien vers la documentation des fonctions d’agrégation Math
* Insertion de la fonction d’agrégation à l’emplacement du curseur
* Confirmation de la fermeture lorsque l’évaluation n’est pas enregistrée
* Affichage des phases en colonnes dans les résultats

## Version 1.0.6 - 19/09/2019

__Corrections :__

* Affichage des résultats lorsqu’une valeur normalisée est > 1

## Version 1.0.5 - 04/09/2019

__Corrections :__

* Mise en lumière après changement de couleur d’un nœud

__Évolutions :__

* Avertissement de la présence de valeurs normalisées > 1
* Ajout de métadonnées en entête du fichier .out
* Prise en charge les fichiers CSV en lecture et en glisser-déposer
* Ajout du CSV comme format d’enregistrement des calculs d’indicateurs
* Intégration de la version 1.0.16 de la bibliothèque d’indicateurs

## Version 1.0.4 - 13/06/2019

__Corrections :__

* Correction de suppression d’indicateurs
* Intégration de la version 1.0.15 de la bibliothèque d’indicateurs
    * Correction de suppression d’indicateurs

__Évolutions :__

* Séparation entre les erreurs et les avertissements
* Redimensionnement du panneau de détails et des problèmes
* Taille minimale de la fenêtre principale à 1000x750px
* Homogénéisation des emplacements de boutons dans toute l’application
* Test de cohérence entre les données phénologiques et climatiques
* Nouveau type d’évaluation : évaluation sans agrégation ni normalisation
* Enregistrement des chemins relatifs des fichiers climatiques et phénologiques dans le fichier .gri
* Intégration de la version 1.0.15 de la bibliothèque d’indicateurs
    * Test de cohérence entre les données phénologiques et climatiques
    * Nouveau type d’évaluation : évaluation sans agrégation ni normalisation
    * Enregistrement des chemins relatifs des fichiers climatiques et phénologiques dans le fichier .gri

## Version 1.0.3 - 22/05/2019

__Corrections :__

* Gestion de agrégations manquantes lors de l’ajout ou de la suppression d’indicateurs
* Bordure noire à toutes les fenêtres
* Suppression de l’indicateur ou de la phase par le bouton Supprimer depuis le panneau de détails
* Avertissement des données climatiques manquantes à la création d’une évaluation
* Changement de fichier (bouton Mettre à jour)
* Variables utilisables pour l’agrégation sans l’identifiant de stade de la phase
* Titre de l’onglet modifié au changement du nom de l’évaluation
* Affichage des erreurs à l’ajout d’une phase
* Intégration de la version 1.0.14 de la bibliothèque d’indicateurs
    * Agrégation nulle possible si une seule phase

__Évolutions :__

* Mise à jour du logo CNRS
* Fermeture automatique de la fenêtre de demande de support après envoi
* Affichage de la licence GPL dans la fenêtre licence d’utilisation
* Affichage dans le menu des dernières évaluations utilisées d’une nouvelle évaluation sauvegardée
* Canevas pour les demandes de support
* Possibilité de joindre l’évaluation dans la demande de support
* Changement du titre du bouton « Enregistrer » en « Enregistrer sous » suivant l’état de l’évaluation
* Soumission des traces des actions de l’utilisateur dans l’interface lors de la demande de support
* Intégration de la version 1.0.14 de la bibliothèque d’indicateurs
    * Lecture des fichiers contenant des séparateurs finaux

## Version 1.0.2 - 10/01/2019

__Évolutions :__

* Notification d’une nouvelle version au lancement de l’application
* Documentation JavaDoc complète
* Intégration de la version 1.0.12 de la bibliothèque d’indicateurs
    * Affichage d’un message si une valeur manque dans le fichier de données climatiques

## Version 1.0.1 - 03/12/2018

__Corrections :__

* Homogénéisation des menus et libellés
* Libération de la mémoire à la fermeture des onglets
* Fonctions _Enregistrer_ / _Enregistrer sous_
* Annulation de l’importation des données
* Liste des indicateurs dépendante des variables disponibles
* Lecture des fichiers de données climatiques

__Évolutions :__

* Plus d’informations dans la fenêtre _Aide > À propos_
* Ajout d’un formulaire de demande de support lié à la forge
* Traduction en français
* Ajout du le logo CNRS
* Affichage de la licence d’utilisation
* Affichage des notes de version
* Menu des évaluations ouvertes précédemment
* Ouverture de la boîte de dialogue de sélection de fichier sur le dernier dossier utilisé
* Description des indicateurs dans une infobulle du menu contextuel
* Prise en charge de l’affichage de `AverageOfDiff`, `Quotient` et `DayOfYear`
* Infobulle sur l’onglet affichant le chemin du fichier `.gri`
* Indicateurs proposés suivant les variables disponibles
* Limitation des stades proposés suivant les phases déjà créées
* Infobulle affichant la description des indicateurs
* Ouverture avec une évaluation en passant l’argument en ligne de commande
* Changement du composant d’affichage des erreurs et avertissements
* Prise en charge du glisser-déposer pour ouvrir les fichier `.gri`
* Activation des éléments du menu selon le contexte
* Licence logicielle
* Création d’un installeur pour Windows à l’aide de [Inno Setup 5.6.1](http://www.jrsoftware.org/isinfo.php)

__Évolutions techniques :__

* Utilisation de Maven
* Passage à Java 8
* Intégration de la version 1.0.11 de la bibliothèque d’indicateurs
    * Nouveaux indicateurs
    * Description des indicateurs
    * Fonction de normalisation linéaire
    * Utilisation de Maven
    * Réusinage et tests
    * Passage à Java 8
    * Utilisation de Log4j2
    * Utilisation de Lombok
* Utilisation du patron MVC avec la vue en [FXML](https://docs.oracle.com/javase/8/javafx/fxml-tutorial/)
* Utilisation de Log4j2
* Utilisation de Lombok
* Utilisation de TestFX

## 05/05/2014

Getari version bêta (2ème phase de test - bêta privée)

__Corrections :__ Néant

__Fonctionnalités ajoutées :__

* Gestion des connaissances : `knowledge.xml`
* Ajout de l’attribut [strict] dans une classe [criteria] pour indiquer si une variable est strictement inférieur ou supérieur au seuil
* Ajout d’un indicateur de type [numberOfWaves]
* Ajout de l’attribut [threshold] dans un indicateur de type [sum] avec cumul de la différence entre la valeur et le seuil

## 10/03/2014

Getari version bêta (2ème phase de test - bêta privée)

__Description technique :__

* Développé en JAVA avec le framework JavaFX 2.2

__Fonctionnalités présentes :__

* Nécessite Java Runtime Environnement (JRE) 7
* Compatible Ubuntu 12+, Windows 7+
* Création d’une évaluation à partir d’un fichier climatique fourni, d’un fichier phénologique fourni et d’une base de connaissance imposée
* Edition graphique d’une évaluation existante
* Sauvegarde d’une évaluation
* Calcul d’une évaluation
* Génération graphique des résultats de calcul
* Ouverture d’un résultat de calcul existant

----

$Date$.
