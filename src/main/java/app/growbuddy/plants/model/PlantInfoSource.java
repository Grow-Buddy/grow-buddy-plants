package app.growbuddy.plants.model;

import app.growbuddy.plants.extractor.MeinSchoenerGartenPlantInfoExtractor;
import app.growbuddy.plants.extractor.PflanzenFuerUnsereGaertenPlantInfoExtractor;
import app.growbuddy.plants.extractor.PlantInfoExtractor;

import java.util.regex.Pattern;

public enum PlantInfoSource {

    MEIN_SCHOENER_GARTEN("Mein schöner Garten",
            "https://www.mein-schoener-garten.de",
            "/pflanzen/.*",
            new MeinSchoenerGartenPlantInfoExtractor()),

    PFLANZEN_FUER_UNSERE_GAERTEN("Pflanzen für unsere Gärten",
            "https://www.pflanzen-fuer-unsere-gaerten.ch",
            "/de-ch/artikel/.*",
            new PflanzenFuerUnsereGaertenPlantInfoExtractor());

    private final String niceName;
    private final String baseUrl;
    private final PlantInfoExtractor plantInfoExtractor;
    private final Pattern relevantContentUrlPattern;

    PlantInfoSource(String niceName, String baseUrl, String relativeRelevantContentUrlPattern, PlantInfoExtractor plantInfoExtractor) {
        this.niceName = niceName;
        this.baseUrl = baseUrl;
        this.plantInfoExtractor = plantInfoExtractor;

        String quotedBaseUrl = Pattern.quote(baseUrl);
        relevantContentUrlPattern = Pattern.compile(quotedBaseUrl + relativeRelevantContentUrlPattern);
    }

    public String getNiceName() {
        return niceName;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public Pattern getRelevantContentUrlPattern() {
        return relevantContentUrlPattern;
    }

    public PlantInfoExtractor getPlantInfoExtractor() {
        return plantInfoExtractor;
    }

}
