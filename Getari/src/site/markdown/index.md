**Generic Evaluation Tool of AgRoclimatic Indicators**

GETARI is a generic evaluation tool to calculate and study climate suitability of areas for cultivation of specific crops.
Crops are very dependent of the climatic effects happening during their cycle and those define climate suitability areas for cultivation.
Agroclimatic indicators (i.e., heat degree days, frost days and the amount of rainfall over specific periods) are classically used providing synthetic information on the effects of climate on crop functioning. Recent studies have used agroclimatic indicators calculated over phenological periods (hereby referred to as ecoclimatic indicators) (Holzkämper et al., 2013, 2011; Caubel et al., 2015). Holzkämper et al. (2013, 2011), developed an evaluation method where several indicators are calculated over the crop cycle, normalized and aggregated to derive a global index of climate suitability.
Recently, Caubel et al., 2015 develop a new assessment method of climate suitability for agriculture derived from Holzkämper et al. (2013) by improving its genericity and flexibility enough to address agronomical questions concerning climate suitability for crop cultivation according to ecophysiological criteria, days available to carry out cultural practices and yield quality.

**Figure 1.- More accurate information about the effects of climate on particular plant processes occurring during specific crop development phases**

![Diagramme](images/what-fig1.png)

# Evaluation method description

The generic evaluation method is described in Caubel et al., 2015. This method is based on the aggregation of ecoclimatic indicators first developed by Holzkämper et al. (2013). Ecoclimatic indicators are agroclimatic indicators that are calculated at the scale of the crop cycle. These indicators can provide information about crop response to climate through ecophysiological or agronomic thresholds. The method developed allows designing evaluation trees (Figure 2).

**Figure 2.- Method description**

![Diagramme](images/what-fig2.png)

For this, we need to identify necessary information at different levels to constitute the evaluation of climate suitability:

1. we will define the ***phenological periods*** affected by detrimental climate effects;
2. we will identify ***ecophysiological processes*** (crop growth, mortality, quality) or cultural practices (pest treatments) that take place during these phenological periods;
3. we will relate the ***climatic effects*** on them (i.e., heat stress, water stress…) (Figure 3);
4. we identify among the ***indicators*** available in a library, those that allow us to calculate the effects of climate on the crop (Figure 4).

**Figure 3.- The different climatic effects considered in the evaluation method**

![Diagramme](images/what-fig3.png)

**Figure 4.- Ecoclimatic indicators characterizing the different climatic effects**

![Diagramme](images/what-fig4.png)

Therefore we will normalize and aggregate the information to compute a Global Index of Climate Suitability (GICS).

**Figure 5.- Normalization and aggregation method**

![Diagramme](images/what-fig5.png)

This normalization will allow getting normalized indices of climate suitability ranging from 0 to 1. These normalized indices (Figure 6) are then aggregated using pre-defined rules (Holzkämper et al., 2013, Caubel et al., 2015).

**Figure 6.- Normalization functions available in GETARI**

![Diagramme](images/what-fig6.png)

Successive aggregations will be performed at different levels (at climatic effect level, at ecophysiological processes level, and between phenological phases) in order to get a final Global Index of Climate Suitability (GICS) (Figure 7).

**Figure 7.- Example of the evaluation tree of the climate suitability for maize in terms of crop ecophysiology (from Caubel et al., 2015)**

![Diagramme](images/what-fig7.png)

## References

- Garcia De Cortazar Atauri, Inaki; Maury, Olivier, 2019, "GETARI : Generic Evaluation Tool of AgRoclimatic Indicators", <https://doi.org/10.15454/IZUFAP>, Portail Data INRAE, V1
- Caubel, J., Garcia de Cortazar-Atauri, I., Launay, M., De Noblet-Ducoudré, N., Huard, F., Bertuzzi, P., Graux, A-I. (2015). Broadening the scope for ecoclimatic indicators to assess crop climate suitability according to ecophysiological, technical and quality criteria. DOI [10.1016/j.agrformet.2015.02.005](http://doi.org/10.1016/j.agrformet.2015.02.005).
- Holzkämper, A., Calanca, P., Fuhrer, J., 2013. Identifying climatic limitations to grain maize yield potentials using a suitability evaluation approach. Agricultural and forest meteorology 168, 149–159. DOI [10.1016/j.agrformet.2012.09.004](https://doi.org/10.1016/j.agrformet.2012.09.004).
- Holzkämper, A., Calanca, P., Fuhrer, J., 2011. Analyzing climate effects on agriculture in time and space. Procedia Environmental Sciences 3, 58–62. DOI [10.1016/j.proenv.2011.02.011](https://doi.org/10.1016/j.proenv.2011.02.011).

----

David Delannoy, Julie Caubel, Iñaki Garcia de Cortazar Atauri
16/06/2015

----
