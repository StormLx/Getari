+++
# $Id$
title = "Indicators"
author = ""
description = "List of available indicators"
keywords = ["indicator","agroclimatic","ecoclimatic"]
date = 2021-04-20
toc = true
+++

Since version 1.1.0, GETARI offers new indicators at hourly time scale in addition to daily indicators.

## Daily indicators
| Id | Name | Description | Variables | Parameters |
|:---|:-----|:------------|:----------|:-----------|
| **Cold Frost** |
| cdaystmin | Cold days Tmin | Number of cold days (tmin < -4.0 °C). | tmin |  |
| vcdaystmin | Very Cold days Tmin | Number of extreme cold days (tmin < -4.0 °C). | tmin |  |
| cdaystmean | Cold days Tmean | Number of cold days (tmean < -1.0 °C). | tmean |  |
| cfreqtmin | Cold days freq Tmin | Frequency (%) of extreme cold days (tmin < -10.0 °C). | tmin |  |
| vcfreqtmin | Very Cold days freq Tmin | Frequency (%) of very cold days (tmin < -10.0 °C). | tmin |  |
| cfreqtmean | Cold days freq Tmean | Frequency (%) of cold days (tmean < -10.0 °C). | tmean |  |
| numbcoldwav | Number of cold waves | Number of cold waves of {Spellfrost} days (tmin < 0.0 °C). | tmin | Spellfrost |
| coldsum | Cold sum Tmean | Sum of average temperatures (tmean < 0.0 °C). | tmean |  |
| coldsumtmin | Cold sum Tmin | Sum of minimal temperatures (tmin < 0.0 °C). | tmin |  |
| froststart | Frost start | First frost day (Julian day). | tmin |  |
| frostend | Frost end | Last frost day (Julian day). | tmin |  |
| frostdaystmin | Frost days | Number of frost days (Tmin < 0 °C). | tmin |  |
| verndays | Vernalizing days | Number of days when {tMinVern} °C < tmean < {tMaxVern} °C. | tmean | tMaxVern, tMinVern |
| vernfreq | Vernalizing days frequency | Days frequency where {tMinVern} °C < tmean < {tMaxVern} °C. | tmean | tMaxVern, tMinVern |
| verndaysfill | Vernalizing days / needed days ratio | Ratio between the number of vernalizing days and the number of vernalizing days required for the variety. | tmean | tMaxVern, tMinVern, vernalizingDays |
| **Heat** |
| hsdays | Heat stress days | Number of heat stress days (tmax > {Theat} °C). | tmax | Theat |
| xhsdays | Extreme Heat stress days | Number of extreme heat stress days (tmax > {Theat} °C). | tmax | Theat |
| hsfreq | Heat stress days frequency | Frequency of heat stress days (tmax > {Theat} °C). | tmax | Theat |
| hdaystmax | Hot days Tmax | Number of hot days (tmax > 25.0 °C). | tmax |  |
| hdaystmean | Hot days Tmean | Number of hot days (tmean > 35.0 °C). | tmean |  |
| xhsfreq | Extreme heat stress days frequency | Frequency of extreme heat stress days (tmax > {Theat} °C). | tmax | Theat |
| numbheatwav | Heat waves | Number of heat waves (more than {Spellheat} days with tmax > {Theat} °C). | tmax | Spellheat, Theat |
| heatsumtmean | Heat sum Tmean | Sum of average temperatures, when tmean > 25.0 °C. | tmean |  |
| heatsumtmax | Heat sum Tmax | Sum of maximum temperatures, when tmax > 25.0 °C. | tmax |  |
| heatstart | Heat start | First day of Heat shock (tmax > {Theat} °C) in julian day. | tmax | Theat |
| heatend | Heat end | Last day of Heat schock (tmax > {Theat} °C) in julian day. | tmax | Theat |
| **Thermal conditions** |
| mint | Average daily min temp | Average Tmin for each period. | tmin |  |
| maxt | Average daily max temp | Average Tmax for each period. | tmax |  |
| meant | Average daily mean temp | Average Tmean for each period. | tmean |  |
| ranget | Average daily range temp | Average Thermal amplitude for each period. | tmax, tmin |  |
| photothermalquotient | Photothermal quotient | Radiation sum (MJ/m²) / positive temperature sum (°C.j). | radiation, tmean |  |
| **Humidity conditions** |
| humavg | Average daily humidity | Average daily humidity. | rh |  |
| wetdays | Wet days | Number of wet days (RH > 60%). | rh |  |
| drydays | Dry days | Number of dry days (RH < 40%). | rh |  |
| wetfreq | Wet days freq | Frequency (%) of wet days (RH > 60%). | rh |  |
| dryfreq | Dry days freq | Frequency (%) of dry days (RH < 40%). | rh |  |
| highhumsum | High humidities sum | Sum of high humidities (rh > 60%). | rh |  |
| lowhumsum | Low humidities sum | Sum of low humidities (rh < 40%). | rh |  |
| numbwetwav | Wet waves | Number of wet waves (7 consecutive days with rh > 60%). | rh |  |
| numbdrywav | Dry waves | Number of dry waves (7 consecutive days with rh < 40%). | rh |  |
| **Water deficit** |
| raidays | Rainy days | Number of rainy days. | rain |  |
| raifreq | Rainy days freq | Frequency (%) of rainy days. | rain |  |
| defraidays | Rainy days (Deficit) | Number of days with rain deficit. | rain |  |
| defraifreq | Rainy days freq (Deficit) | Frequency (%) of days with rain deficit. | rain |  |
| watsdays | Water stress days | Number of days with soil water content < 9.0. | soilwatercontent |  |
| watsfreq | Water stress days freq | Frequency (%) of days with soil water content < 9.0. | soilwatercontent |  |
| sumwd | Sum of water deficit | Sum of water deficit. | etp, rain |  |
| wddays | Days with water deficit | Number of days when ETP is greater than precipitation. | etp, rain |  |
| numbdroughtwav | Drought waves | Number of drought waves (20 consecutive days without rain). | rain | Spelldrought |
| rainsum | Rain sum | Rain cumulation. | rain |  |
| rainavg | Average daily rain | Average daily rain. | rain |  |
| swccc | Soil water content on field capacity ratio | Ratio between the actual soil humidity and the field capacity. | soilwatercontent | hcc |
| **Water excess** |
| wetsdays | Wet soil days | Number of days with soil water content > 25 %. | soilwatercontent |  |
| wetsfreq | Wet soil days freq | Frequency (%) of days with soil water content > 25 %. | soilwatercontent |  |
| wetsoildays | Flooding days | Number of days with soil moisture above soil capacity (SWC > {hcc} %). | soilwatercontent | hcc |
| wetsoilfreq | Flooding days frequency | Frequency (%) of days with soil moisture above soil capacity (SWC > {hcc} %). | soilwatercontent | hcc |
| excraidays | Rainy days (Excess) | Number of rainy days in excess. | rain |  |
| excraifreq | Rainy days freq (Excess) | Frequency (%) of rainy days in excess. | rain |  |
| sumwe | Sum of water excess | Water deficit (mm): rain - ETR. | etp, rain |  |
| excrainsum | Rain sum (Excess) | Sum of rain in excess. | rain |  |
| **Water damage** |
| hraidays | Heavy rain days | Number of heavy rainy days (> {heavyRain} mm). | rain | heavyRain |
| shraidays | Severe heavy rain days | Number of severe heavy rainy days (> {seriousHeavyRain} mm). | rain | seriousHeavyRain |
| hrainpart | Heavy rain part | Percentage (%) of rainfall attributed to days of heavy rains (> {heavyRain} mm). | rain | heavyRain |
| shrainpart | Severe heavy rain part | Percentage (%) of rainfall attributed to days of severe heavy rains (> {seriousHeavyRain} mm). | rain | seriousHeavyRain |
| hraifreq | Heavy rain freq | Percentage (%) of severe heavy rainy days (> {heavyRain} mm). | rain | seriousHeavyRain |
| **Radiation deficit** |
| avsorad | Average daily solar radiation | Average daily radiation (MJ/m²). | radiation |  |
| sumsorad | Sum of daily solar radiation | Cumulated daily radiation (MJ/m²). | radiation |  |
| rsdays | Radiative stress days | Number of days with radiation stress (radiation < 17.28 MJ/m²/day). | radiation |  |
| coldshadeddays | Cold and low radiation days | Number of days froids with radiation stress (tmean < -1.0 °C and radiation < 17.28 MJ/m²/day). | radiation, tmean |  |
| **Wind conditions** |
| hwinddays | High wind days | Number of days with high wind (wind speed at 10m > 5.28 m/s). | wind |  |
| hwindfreq | High wind days freq | Frequency (%) of days with high wind (wind speed at 10m > 5.28 m/s). | wind |  |
| **Phase length** |
| phalen | Phase length | Phase length. | tmean |  |
| **Other complex indicators** |
| sowfreq | Potential sowing days freq | Frequency of days when sowing is possible under conditions of temperature and soil moisture content after sowing (ecophysiological feasibility) and soil moisture content at sowing day (technical feasibility of sowing). | rain, soilwatercontent, tmean, tmin | DaysAfterSow, Tmeansow, Tminsow, minimalSoilWaterContent |
| monilia | Monilia risk on apricot | Cumulative risk of moniliosis on apricot twigs during the flowering phase (Tresson, 2018 https://dx.doi.org/10.1016/j.eja.2019.125960). | rain, tmean |  |

### Parameters
| Id | Description |
|:---|:------------|
| DaysAfterSow | Number of consecutive days after sowing during which conditions are observed for sowing decision. |
| hcc | Humidity at field capacity (%). |
| heavyRain | Daily pluviometry specifying an heavy rain (mm). |
| seriousHeavyRain | Daily pluviometry specifying a serious heavy rain (mm). |
| minimalSoilWaterContent | Minimum soil water content for emergence. |
| Theat | Temperature above which heat affects the crop (°C) (compared to Tmax). |
| Spelldrought | Number of consecutive days specifying a drought spell. |
| Spellfrost | Number of consecutive days specifying a frost spell. |
| Spellheat | Number of consecutive days specifying a heatshock spell. |
| tMaxVern | Maximal temperature for vernalizing (°C). |
| Tmeansow | Average temperature for sowing (°C). |
| Tminsow | Minimal temperature for sowing (°C). |
| tMinVern | Minimal temperature for vernalizing (°C). |
| vernalizingDays | Number of vernalizing days. |

### Variables
| Id | Description |
|:---|:------------|
| etp | Evapotranspiration [mm/d]. |
| radiation | Global radiation [W/m²]. |
| rain | Rain precipitation [mm]. |
| rh | Relative humidity [%]. |
| soilwatercontent | Soil water content [% mass]. |
| tmax | Maximal air temperature [°C]. |
| tmean | Average air temperature [°C]. |
| tmin | Minimal air temperature [°C]. |
| wind | Wind speed [m/s]. |

## Hourly indicators
| Id | Name | Description | Variables | Parameters |
|:---|:-----|:------------|:----------|:-----------|
| **Cold Frost** |
| coldhours | Cold hours | Number of cold hours (TH < -4.0 °C). | th |  |
| vcoldhours | Very cold hours | Number of very cold hours (TH < -10.0 °C). | th |  |
| coldfreq | Cold hours freq | Frequency (%) of cold hours (TH < -4.0 °C). | th |  |
| vcoldfreq | Very cold hours freq | Frequency (%) of very cold hours (TH < -10.0 °C). | th |  |
| maxfrosthours | Consecutive frost hours | Maximal number of consecutives frost hours (TH < 0.0 °C). | th |  |
| sumfrosthours | Frost sum | Sum of negative temperatures (TH < 0.0 °C). | th |  |
| froststart | Frost start | Date and time of first frost (Julian day). | th |  |
| frostend | Frost end | Date and time of last frost (Julian day). | th |  |
| frosthours | Frost hours | Number of frost hours (TH < 0 °C). | th |  |
| vernhours | Vernalisation hours | Number of hours when {tMinVern} °C < TH < {tMaxVern} °C. | th | tMaxVern, tMinVern |
| **Heat** |
| hshours | Heat stress hours | Number of heat stress hours (TH > {Theat} °C). | th | Theat |
| hsfreq | Heat stress hours frequency | Frequency of heat stress hours (TH > {Theat} °C). | th | Theat |
| maxheathours | Max consecutive heat hours | Maximal number of consecutives shock hours (TH > {Theat} °C). | th | Theat |
| sumheathours | Heat sum temperature | Sum of hourly temperatures (TH > {Theat} °C). | th | Theat |
| heatstart | Heat start | Day and hour of first Heat shock (TH > {Theat} °C) in julian day. | th | Theat |
| heatend | Heat end | Day and hour of last Heat shock (TH > {Theat} °C) in julian day. | th | Theat |
| **Thermal conditions** |
| avgth | Average hourly temperature | Average hourly temperature for each period. | th |  |
| **Humidity conditions** |
| wethours | Wet hours | Number of wet hours (RH > 60%). | rh |  |
| dryhours | Dry hours | Number of dry hours (RH < 40%). | rh |  |
| wetfreq | Wet hours freq | Frequency (%) of wet hours (RH > 60%). | rh |  |
| dryfreq | Dry hours freq | Frequency (%) of dry hours (RH < 40%). | rh |  |
| maxwethours | Consecutive wet hours | Maximal number of consecutives wet hours (RH > 60%). | rh |  |
| maxdryhours | Consecutive dry hours | Maximal number of consecutives dry hours (RH < 40%). | rh |  |
| **Water damage** |
| raihours | Rainy hours | Number of rainy hours. | rain |  |
| raifreq | Rainy hours freq | Frequency (%) of rainy hours. | rain |  |
| noraihours | Rainy hours | Number of no rainy hours. | rain |  |
| noraifreq | No rainy hours freq | Frequency (%) of no rainy hours. | rain |  |
| hraihours | Heavy rainy hours | Number of heavy rainy hours (> {heavyRain} mm). | rain | heavyRain |
| hraifreq | Heavy rainy hours freq | Frequency (%) of heavy rainy hours (> {heavyRain} mm). | rain | heavyRain |
| shraihours | Severe heavy rainy hours | Number of severe heavy rainy hours (> {seriousHeavyRain} mm). | rain | seriousHeavyRain |
| shraifreq | Severe heavy rainy hours freq | Frequency (%) of severe heavy rainy hours (> {seriousHeavyRain} mm). | rain | seriousHeavyRain |
| **Wind conditions** |
| hwindhours | High wind hours | Number of hours with high wind (wind speed at 10m > 5.28 m/s). | wind |  |

### Parameters
| Id | Description |
|:---|:------------|
| heavyRain | Daily pluviometry specifying an heavy rain (mm). |
| seriousHeavyRain | Daily pluviometry specifying a serious heavy rain (mm). |
| Theat | Temperature above which heat affects the crop (°C) (compared to Tmax). |
| tMaxVern | Maximal temperature for vernalisation (°C). |
| tMinVern | Minimal temperature for vernalisation (°C). |

### Variables
| Id | Description |
|:---|:------------|
| rain | Rain precipitation [mm]. |
| rh | Relative humidity [%]. |
| th | Instantaneous hourly air temperature [°C]. |
| wind | Wind speed [m/s]. |
