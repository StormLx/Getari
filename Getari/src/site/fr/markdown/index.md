**Generic Evaluation Tool of AgRoclimatic Indicators - Outil d'évaluation générique d'indicateurs agroclimatiques**

GETARI est un outil d'évaluation générique permettant de calculer et d'étudier la faisailité climatique des cultures dans un lieu donné.
Les cultures sont très dépendantes des effets climatiques qui se produisent au cours de leur cycle et ceux-ci définissent leur distribution spatiale.
Les indicateurs agroclimatiques (c'est-à-dire les degrés-jours de chaleur, les jours de gel et la quantité de précipitations sur des périodes spécifiques) sont utilisés classiquement pour fournir des informations synthétiques sur les effets du climat sur le fonctionnement des cultures. Des études récentes ont utilisé des indicateurs agroclimatiques calculés sur des périodes phénologiques (ci-après dénommés indicateurs écoclimatiques) (Holzkämper et al., 2013, 2011 ; Caubel et al., 2015). Holzkämper et al. (2013, 2011), ont développé une méthode d'évaluation où plusieurs indicateurs sont calculés sur le cycle de culture, normalisés et agrégés pour obtenir un indice global de faisabilité climatique.
Récemment, Caubel et al. (2015) ont proposé plusieurs cas d'évaluation à partir de la méthode proposé par Holzkämper et al. (2013), en améliorant sa généricité et en la rendant suffisamment flexible pour répondre aux questions agronomiques diverses comme la faisaibilité climatique de chaque culture selon des critères écophysiologiques, les jours disponibles pour effectuer les pratiques culturales ou la qualité de la production.

**Figure 1.- Informations plus précises sur les effets du climat sur des processus écophysiologiques se produisant au cours de phases spécifiques de développement des cultures**

![Diagramme](images/what-fig1.fr.png)

# Description de la méthode d'évaluation

La méthode d'évaluation générique est décrite dans Caubel et al., 2015. Cette méthode est basée sur la méthode d'agrégation d'indicateurs écoclimatiques normalisés développés par Holzkämper et al. (2013).
Les indicateurs écoclimatiques sont des indicateurs agroclimatiques qui sont calculés à l'échelle du cycle d'une culture. Ces indicateurs peuvent fournir des informations sur la réponse des cultures au climat par le biais de seuils écophysiologiques ou agronomiques. La méthode développée permet de concevoir des arbres d'évaluation (figure 2).

**Figure 2.- Description de la méthode**

![Diagramme](images/what-fig2.fr.png)

Pour cela, nous devons identifier les informations nécessaires à différents niveaux pour constituer l'évaluation de la faisaibilité climatique :

1. nous définirons les ***périodes phénologiques*** affectées par les effets négatifs du climat ;
2. nous identifierons les ***processus physiologiques*** (croissance, mortalité, qualité des cultures) ou les pratiques culturales (traitements contre les ravageurs) qui ont lieu pendant ces périodes phénologiques ;
3. nous allons identifier les ***effets climatiques*** importants sur ces périodes (c'est-à-dire le stress thermique, le stress hydrique...) (Figure 3) ;
4. nous identifions parmi les ***indicateurs*** disponibles dans une bibliothèque, ceux qui nous permettent de calculer les effets du climat sur la culture (Figure 4).

**Figure 3.- Les différents effets climatiques pris en compte dans la méthode d'évaluation**

![Diagramme](images/what-fig3.fr.png)

**Figure 4.- Indicateurs écoclimatiques caractérisant les différents effets climatiques**

![Diagramme](images/what-fig4.fr.png)

Nous allons donc normaliser les indicateurs et agréger les informations pour calculer un indice global de faisabilité du climat (*Global Index of Climate Suitability - GICS*).

**Figure 5.- Méthode de normalisation et d'agrégation**

![Diagramme](images/what-fig5.fr.png)

La méthode de normalisation permet d'obtenir des indices normalisés allant de 0 à 1. Ces indices normalisés (figure 6) sont ensuite agrégés à l'aide de règles prédéfinies (Holzkämper et al., 2013, Caubel et al., 2015).

**Figure 6.- Fonctions de normalisation disponibles dans GETARI**

![Diagramme](images/what-fig6.png)

Des agrégations successives seront effectuées à différents niveaux (au niveau des effets climatiques, au niveau des processus écophysiologiques et entre les phases phénologiques) afin d'obtenir un indice global de faisabilité du climat (GICS) (figure 7).

**Figure 7.- Exemple d'arbre d'évaluation de la faisabilité climatique du maïs en termes d'écophysiologie de la culture (d'après Caubel et al., 2015)**

![Diagramme](images/what-fig7.png)

## Références

- Garcia De Cortazar Atauri, Inaki; Maury, Olivier, 2019, "GETARI : Generic Evaluation Tool of AgRoclimatic Indicators", <https://doi.org/10.15454/IZUFAP>, Portail Data INRAE, V1
- Caubel, J., Garcia de Cortazar-Atauri, I., Launay, M., De Noblet-Ducoudré, N., Huard, F., Bertuzzi, P., Graux, A-I. (2015). Broadening the scope for ecoclimatic indicators to assess crop climate suitability according to ecophysiological, technical and quality criteria. DOI [10.1016/j.agrformet.2015.02.005](http://doi.org/10.1016/j.agrformet.2015.02.005).
- Holzkämper, A., Calanca, P., Fuhrer, J., 2013. Identifying climatic limitations to grain maize yield potentials using a suitability evaluation approach. Agricultural and forest meteorology 168, 149–159. DOI [10.1016/j.agrformet.2012.09.004](https://doi.org/10.1016/j.agrformet.2012.09.004).
- Holzkämper, A., Calanca, P., Fuhrer, J., 2011. Analyzing climate effects on agriculture in time and space. Procedia Environmental Sciences 3, 58–62. DOI [10.1016/j.proenv.2011.02.011](https://doi.org/10.1016/j.proenv.2011.02.011).

----

Olivier Maury, Iñaki Garcia de Cortazar Atauri
19/03/2020

----
