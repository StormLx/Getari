+++
# $Id$
title = "Indicateurs"
author = ""
description = "Liste des indicateurs disponibles"
keywords = ["indicateur","agroclimatique","écoclimatique"]
date = 2021-04-20
toc = true
+++

Depuis la version 1.1.0, GETARI fournit des indicateurs au pas de temps horaire en plus des indicateurs au pas de temps journalier.

## Indicateurs journaliers
| Id | Nom | Description | Variables | Paramètres |
|:---|:-----|:------------|:----------|:-----------|
| **Froid gélif** |
| cdaystmin | Jours froids Tmin | Nombre de jours froids (létalité : tmin < -4.0 °C). | tmin |  |
| vcdaystmin | Jours très froids Tmin | Nombre de jours très froids (létalité : tmin < -4,0 °C). | tmin |  |
| cdaystmean | Jours froids Tmoy | Nombre de jours froids (tmoy < -1,0 °C). | tmean |  |
| cfreqtmin | % de jours froids Tmin | Fréquence (%) de jours froids (tmin < -10,0 °C). | tmin |  |
| vcfreqtmin | % de jours très froids Tmin | Fréquence (%) de jours très froids (mortalité : tmin < -10,0 °C). | tmin |  |
| cfreqtmean | % de jours froids Tmoy | Fréquence (%) de jours froids (mortalité : tmoy < -10,0 °C). | tmean |  |
| numbcoldwav | Vagues de froid | Nombre de vagues de froid de {Spellfrost} jours (tmin < 0,0 °C). | tmin | Spellfrost |
| coldsum | Somme de froid Tmoy | Somme des températures moyennes (tmoy < 0,0 °C). | tmean |  |
| coldsumtmin | Somme de froid Tmin | Somme des températures minimales (tmin < 0,0 °C). | tmin |  |
| froststart | Début du risque de gel | Premier jour de gel en jour julien. | tmin |  |
| frostend | Fin du risque de gel | Dernier jour de gel en jour julien. | tmin |  |
| frostdaystmin | Jours de gel | Nombre de jours de gel (Tmin < 0 °C). | tmin |  |
| verndays | Jours vernalisants | Nombre de jours pour lesquels {tMinVern} °C < tmoy < {tMaxVern} °C. | tmean | tMaxVern, tMinVern |
| vernfreq | % de jours vernalisants | Fréquence (%) de jours pour lesquels {tMinVern} °C < tmoy < {tMaxVern} °C. | tmean | tMaxVern, tMinVern |
| verndaysfill | Rapport jours vernalisants et besoin | Rapport entre le nombre de jours vernalisants et le nombre de jours vernalisants nécessaires à la variété. | tmean | tMaxVern, tMinVern, vernalizingDays |
| **Chaleur** |
| hsdays | Jours de stress thermique | Nombre de jours de stress thermique (tmax > {Theat} °C). | tmax | Theat |
| xhsdays | Jours de stress thermique extrême | Nombre de jours de grand stress thermique (tmax > {Theat} °C). | tmax | Theat |
| hsfreq | % de jours de stress thermique | Fréquence (%) de jours de stress thermique (tmax > {Theat} °C). | tmax | Theat |
| hdaystmax | Jours chauds Tmax | Nombre de jours chauds (tmax > 25,0 °C). | tmax |  |
| hdaystmean | Jours chauds Tmoy | Nombre de jours chauds (tmoy > 35,0 °C). | tmean |  |
| xhsfreq | % de jours de stress thermique extrême | Fréquence (%) de jours d'extrême chaleur (tmax > {Theat} °C). | tmax | Theat |
| numbheatwav | Vagues de chaleur | Nombre de vagues de chaleur de plus de {Spellheat} jours (tmax > {Theat} °C). | tmax | Spellheat, Theat |
| heatsumtmean | Somme des températures moyennes chaudes | Somme des températures moyennes, les jours où tmoy > 25,0 °C. | tmean |  |
| heatsumtmax | Somme des températures maximales chaudes | Somme des températures maximales, les jours où tmax > 25,0 °C. | tmax |  |
| heatstart | Début stress thermique | Premier jour d'échaudage (tmax > {Theat} °C) en jour julien. | tmax | Theat |
| heatend | Fin stress thermique | Dernier jour d'échaudage (tmax > {Theat} °C) en jour julien. | tmax | Theat |
| **Conditions thermiques** |
| mint | Moyenne des températures minimales | Moyenne des températures minimales pour chaque phase de développement. | tmin |  |
| maxt | Moyenne des températures maximales | Moyenne des températures maximales pour chaque phase de développement. | tmax |  |
| meant | Moyenne des températures moyennes | Moyenne des températures moyennes pour chaque phase de développement. | tmean |  |
| ranget | Moyenne des amplitudes thermiques | Amplitude thermique journalière moyenne pour chaque phase de développement. | tmax, tmin |  |
| photothermalquotient | Coefficient photothermique | Cumul de rayonnement (MJ/m²) / cumul de température positive (°C.j). | radiation, tmean |  |
| **Conditions d'humidité** |
| humavg | Moyenne des humidités | Humidité moyenne journalière. | rh |  |
| wetdays | Jours humides | Nombre de jours humides (RH > 60%). | rh |  |
| drydays | Jours secs | Nombre de jours secs (RH < 40%). | rh |  |
| wetfreq | % de jours humides | Fréquence (%) de jours humides (RH > 60%). | rh |  |
| dryfreq | % de jours secs | Fréquence (%) de jours secs (RH < 40%). | rh |  |
| highhumsum | Somme des humidités élevées | Somme des humidités élevées (rh > 60%). | rh |  |
| lowhumsum | Somme des humidités basses | Somme des humidités basses (rh < 40%). | rh |  |
| numbwetwav | Vagues d'humidités élevées | Nombre de vagues d'humidité (7 jours consécutifs où rh > 60%). | rh |  |
| numbdrywav | Vagues d'humidités basses | Nombre de vagues de faible humidité (7 jours consécutifs où rh < 40%). | rh |  |
| **Déficit en eau** |
| raidays | Jours de pluie | Nombre de jours de pluie. | rain |  |
| raifreq | % de jours de pluie | Fréquence (%) de jours de pluie. | rain |  |
| defraidays | Jours sans pluie | Nombre de jours en déficit de pluie. | rain |  |
| defraifreq | % de jours sans pluie | Fréquence (%) de jours en déficit de pluie. | rain |  |
| watsdays | Jours avec déficit hydrique | Nombre de jours avec teneur en eau du sol < 9,0. | soilwatercontent |  |
| watsfreq | % de jours avec déficit hydrique | Fréquence (%) de jours avec teneur en eau du sol < 9,0. | soilwatercontent |  |
| sumwd | Somme des déficits en eau | Somme des déficits en eau. | etp, rain |  |
| wddays | Jours de déficits en eau | Nombre de jours où l'ETP est supérieure aux précipitations. | etp, rain |  |
| numbdroughtwav | Vagues de sécheresse | Nombre de vagues de sécheresse (20 jours consécutifs sans pluie). | rain | Spelldrought |
| rainsum | Somme des pluies | Cumul des précipitations. | rain |  |
| rainavg | Moyenne des précipitations | Pluie moyenne journalière. | rain |  |
| swccc | Rapport teneur en eau du sol sur capacité au champ | Rapport entre l'humidité du sol et l'humidité à la capacité au champ. | soilwatercontent | hcc |
| **Excès d'eau** |
| wetsdays | Jours avec le sol très humide | Nombre de jours avec SWC > 25 %. | soilwatercontent |  |
| wetsfreq | % de jours avec le sol très humide | Fréquence (%) de jours avec teneur en eau du sol > 25 %. | soilwatercontent |  |
| wetsoildays | Jours d'ennoiement | Nombre de jours où l'humidité du sol est supérieure à la capacité au champ (SWC > {hcc} %). | soilwatercontent | hcc |
| wetsoilfreq | % de jours d'ennoiement | Fréquence (%) de jours où l'humidité du sol est supérieure à la capacité au champ (SWC > {hcc} %). | soilwatercontent | hcc |
| excraidays | Jours de pluie en excès | Nombre de jours de pluie en excès. | rain |  |
| excraifreq | % de jours de pluie en excès | Fréquence (%) de jours de pluie en excès. | rain |  |
| sumwe | Somme des excès en eau | Déficit hydrique (mm) : pluviométrie - évapotranspiration de référence. | etp, rain |  |
| excrainsum | Somme des précipitations en excès | Somme des pluies en excès. | rain |  |
| **Dégâts d'eau** |
| hraidays | Jours de fortes pluies | Nombre de jours de fortes pluies (> {heavyRain} mm). | rain | heavyRain |
| shraidays | Jours de très fortes pluies | Nombre de jours de très fortes pluies (> {seriousHeavyRain} mm). | rain | seriousHeavyRain |
| hrainpart | % des fortes pluies sur la pluie totale | Contribution (%) des fortes pluies (pourcentage de pluviométrie attribuée à des jours de fortes pluies) (> {heavyRain} mm). | rain | heavyRain |
| shrainpart | % des très fortes pluies sur la pluie total | Contribution (%) des très fortes pluies (pourcentage de pluviométrie attribuée à des jours de fortes pluies > {seriousHeavyRain} mm). | rain | seriousHeavyRain |
| hraifreq | % de jours de très fortes pluies | Fréquence (%) de jours de très fortes pluies (> {seriousHeavyRain} mm). | rain | seriousHeavyRain |
| **Déficit de rayonnement** |
| avsorad | Moyenne des rayonnements journaliers | Rayonnement moyen journalier (MJ/m²). | radiation |  |
| sumsorad | Rayonnement cumulé | Rayonnement cumulé (MJ/m²). | radiation |  |
| rsdays | Jours à faible rayonnement | Nombre de jours de stress radiatif (rayonnement < 17,28 MJ/m²/jour). | radiation |  |
| coldshadeddays | Jours froids à faible rayonnement | Nombre de jours froids de stress radiatif (tmoy < -1,0 °C et rayonnement < 17,28 MJ/m²/jour). | radiation, tmean |  |
| **Conditions de vent** |
| hwinddays | Jours de grand vent | Nombre de jours de grand vent (vitesse de vent à 10m > 5,28 m/s). | wind |  |
| hwindfreq | % de jours de grand vent | Fréquence (%) de jours de grand vent (vitesse de vent à 10m > 5,28 m/s). | wind |  |
| **Durée de la phase** |
| phalen | Durée de la phase | Durée de la phase. | tmean |  |
| **Autres indicateurs complexes** |
| sowfreq | % de jours potentiels de semis | Fréquence des jours où le semis est possible selon des conditions sur la température et la teneur en eau du sol après le semis (faisabilité écophysiologique) et selon la teneur en eau du sol le jour du semis (faisabilité technique du semis). | rain, soilwatercontent, tmean, tmin | DaysAfterSow, Tmeansow, Tminsow, minimalSoilWaterContent |
| monilia | Risque moniliose sur abricotier | Risque cumulé de moniliose sur rameaux d'abricotiers pour la phase de floraison (Tresson, 2018 https://dx.doi.org/10.1016/j.eja.2019.125960). | rain, tmean |  |

### Paramètres
| Id | Description |
|:---|:------------|
| DaysAfterSow | Nombre de jours consécutifs après le semis pendant lesquels les conditions sont observées pour la décision de semis. |
| hcc | Humidité à la capacité au champ (%). |
| heavyRain | Pluviométrie journalière pour les fortes pluies (mm). |
| seriousHeavyRain | Pluviométrie journalière pour les très fortes pluies (mm). |
| minimalSoilWaterContent | Teneur en eau du sol minimale pour la levée. |
| Theat | Température au-dessus de laquelle la chaleur affecte la culture (°C) (par rapport à Tmax). |
| Spelldrought | Nombre de jours consécutifs spécifiant une période de sécheresse. |
| Spellfrost | Nombre de jours consécutifs définissant une période de gel. |
| Spellheat | Nombre de jours consécutifs spécifiant une période de choc thermique. |
| tMaxVern | Température minimale de vernalisation (°C). |
| Tmeansow | Température moyenne pour le semis (°C). |
| Tminsow | Température minimale pour le semis (°C). |
| tMinVern | Température minimale de vernalisation (°C). |
| vernalizingDays | Nombre de jours de vernalisation. |

### Variables
| Id | Description |
|:---|:------------|
| etp | Évapotranspiration [mm/d]. |
| radiation | Rayonnnement global [W/m²]. |
| rain | Précipitation [mm]. |
| rh | Humidité relative [%]. |
| soilwatercontent | Teneur en eau du sol [% massique]. |
| tmax | Température maximale de l'air [°C]. |
| tmean | Température moyenne de l'air [°C]. |
| tmin | Température minimale de l'air [°C]. |
| wind | Vitesse du vent [m/s]. |

## Indicateurs horaires
| Id | Nom | Description | Variables | Paramètres |
|:---|:-----|:------------|:----------|:-----------|
| **Froid gélif** |
| coldhours | Heures froides | Nombre d'heures froides (létalité : TH < -4.0 °C). | th |  |
| vcoldhours | Heures très froides | Nombre d'heures très froides (létalité : TH < -10.0 °C). | th |  |
| coldfreq | % d'heures froides | Fréquence (%) d'heures froides (TH < -4,0 °C). | th |  |
| vcoldfreq | % d'heures très froides | Fréquence (%) d'heures froides (TH < -10,0 °C). | th |  |
| maxfrosthours | Heures consécutives de gel | Nombre maximal d'heures consécutives de gel (TH < 0,0 °C). | th |  |
| sumfrosthours | Somme de gel | Somme des températures négatives (TH < 0,0 °C). | th |  |
| froststart | Début du risque de gel | Date et heure du premier gel en jour julien. | th |  |
| frostend | Fin du risque de gel | Date et heure du dernier gel en jour julien. | th |  |
| frosthours | Heures de gel | Nombre d'heures de gel (TH < 0 °C). | th |  |
| vernhours | Heures vernalisantes | Nombre d'heures pour lesquelles {tMinVern} °C < TH < {tMaxVern} °C. | th | tMaxVern, tMinVern |
| **Chaleur** |
| hshours | Heures de stress thermique | Nombre d'heures de stress thermique (TH > {Theat} °C). | th | Theat |
| hsfreq | % d'heures de stress thermique | Fréquence (%) d'heures de stress thermique (TH > {Theat} °C). | th | Theat |
| maxheathours | Heures consécutives d'échaudage | Nombre maximal d'heures consécutives chaudes (TH > {Theat} °C). | th | Theat |
| sumheathours | Somme des températures horaires chaudes | Somme des températures horaires chaudes (TH > {Theat} °C). | th | Theat |
| heatstart | Début stress thermique | Date et heure du premier échaudage (TH > {Theat} °C) en jour julien. | th | Theat |
| heatend | Fin stress thermique | Date et heure du dernier échaudage (TH > {Theat} °C) en jour julien. | th | Theat |
| **Conditions thermiques** |
| avgth | Moyenne des températures horaires | Moyenne des températures horaires pour chaque phase de développement. | th |  |
| **Conditions d'humidité** |
| wethours | Heures humides | Nombre d'heures humides (RH > 60%). | rh |  |
| dryhours | Heures sèches | Nombre d'heures sèches (RH < 40%). | rh |  |
| wetfreq | % d'heures humides | Fréquence (%) d'heures humides (RH > 60%). | rh |  |
| dryfreq | % d'heures sèches | Fréquence (%) d'heures sèches (RH < 40%). | rh |  |
| maxwethours | Heures humides consécutives | Nombre maximal d'heures consécutives humides (RH > 60%). | rh |  |
| maxdryhours | Heures sèches consécutives | Nombre maximal d'heures consécutives sèches (RH < 40%). | rh |  |
| **Dégâts d'eau** |
| raihours | Heures pluvieuses | Nombre d'heures pluvieuses. | rain |  |
| raifreq | % d'heures pluvieuses | Fréquence (%) d'heures pluvieuses. | rain |  |
| noraihours | Heures sans pluie | Nombre d'heures sans pluie. | rain |  |
| noraifreq | % d'heures sans pluie | Fréquence (%) d'heures sans pluie. | rain |  |
| hraihours | Heures de forte pluie | Nombre d'heures de forte pluie (> {heavyRain} mm). | rain | heavyRain |
| hraifreq | % d'heures de forte pluie | Fréquence (%) d'heures de forte pluie (> {heavyRain} mm). | rain | heavyRain |
| shraihours | Heures de très forte pluie | Nombre d'heures de très forte pluie (> {seriousHeavyRain} mm). | rain | seriousHeavyRain |
| shraifreq | % d'heures de très forte pluie | Fréquence (%) d'heures de très forte pluie (> {seriousHeavyRain} mm). | rain | seriousHeavyRain |
| **Conditions de vent** |
| hwindhours | Heures de grand vent | Nombre d'heures de grand vent (vitesse de vent à 10m > 5,28 m/s). | wind |  |

### Paramètres
| Id | Description |
|:---|:------------|
| heavyRain | Pluviométrie journalière pour les fortes pluies (mm). |
| seriousHeavyRain | Pluviométrie journalière pour les très fortes pluies (mm). |
| Theat | Température au-dessus de laquelle la chaleur affecte la culture (°C) (par rapport à Tmax). |
| tMaxVern | Température minimale de vernalisation (°C). |
| tMinVern | Température minimale de vernalisation (°C). |

### Variables
| Id | Description |
|:---|:------------|
| rain | Précipitation [mm]. |
| rh | Humidité relative [%]. |
| th | Température horaire instantanée de l'air [°C]. |
| wind | Vitesse du vent [m/s]. |
