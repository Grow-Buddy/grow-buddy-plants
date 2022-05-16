package app.growbuddy.plants;

import app.growbuddy.plants.crawling.SitemapUrlExtractor;
import app.growbuddy.plants.model.GsonAdaptersPlantInfo;
import app.growbuddy.plants.model.ImmutableRawPlantInfo;
import app.growbuddy.plants.model.PlantInfo;
import app.growbuddy.plants.model.PlantInfoSource;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jsoup.Jsoup;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class PlantsDatabaseCreator {

    private static final Type PLANT_INFOS_TYPE = new TypeToken<Set<PlantInfo>>() {
    }.getType();

    private static final int DEFAULT_FETCH_TIMEOUT = 5000;

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapterFactory(new GsonAdaptersPlantInfo())
            .create();

    public static void main(String[] args) throws Exception {
        File database = new File(args[0]);
        File cacheBasePath = new File(args[1]);
        FileUtils.forceMkdir(cacheBasePath);

        Map<String, PlantInfo> plantInfos = new ConcurrentHashMap<>();
        if (database.exists()) {
            try (Reader reader = new FileReader(database)) {
                Set<PlantInfo> existingEntries = GSON.fromJson(reader, PLANT_INFOS_TYPE);
                plantInfos.putAll(existingEntries.stream().collect(Collectors.toMap(PlantInfo::getDetailLink, plantInfo -> plantInfo)));
            }
        }

        Arrays.stream(PlantInfoSource.values())
                .flatMap(plantInfoSource -> new SitemapUrlExtractor().fetchContentUrls(plantInfoSource.getBaseUrl()).stream()
                        .map(contentUrl -> Pair.of(plantInfoSource, contentUrl)))
                .parallel()
                .forEach(pair -> {
                    try {
                        var plantInfoSource = pair.getLeft();
                        var contentEntry = pair.getRight();

                        if (!plantInfoSource.getRelevantContentUrlPattern().matcher(contentEntry.toString()).matches()) {
                            return;
                        }

                        var cacheEntryName = contentEntry.toString();
                        cacheEntryName = cacheEntryName.replace(plantInfoSource.getBaseUrl(), "");
                        cacheEntryName = cacheEntryName.replaceAll("/$", "");
                        cacheEntryName = cacheEntryName.replaceAll("^/", "");
                        cacheEntryName = cacheEntryName.replaceAll("/", "_");
                        cacheEntryName = cacheEntryName + ".html";

                        var cacheEntry = new File(cacheBasePath, cacheEntryName);
                        if (!cacheEntry.exists()) {
                            try (InputStream inputStream = Jsoup.connect(contentEntry.toString())
                                    .timeout(DEFAULT_FETCH_TIMEOUT)
                                    .execute()
                                    .bodyStream();
                                 OutputStream outputStream = new FileOutputStream(cacheEntry)) {

                                IOUtils.copy(inputStream, outputStream);
                            }
                        }

                        var html = FileUtils.readFileToString(cacheEntry, StandardCharsets.UTF_8);

                        var rawPlantInfo = ImmutableRawPlantInfo.builder()
                                .url(contentEntry.toString())
                                .html(html)
                                .source(plantInfoSource)
                                .build();

                        var plantInfo = plantInfoSource.getPlantInfoExtractor().extractPlantInfo(rawPlantInfo);
                        if (plantInfo != null) {
                            plantInfos.put(plantInfo.getDetailLink(), plantInfo);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });

        var databaseEntries = new ArrayList<>(plantInfos.values());
        databaseEntries.sort((o1, o2) -> ObjectUtils.compare(o1.getName(), o2.getName()));

        try (Writer writer = new FileWriter(database)) {
            GSON.toJson(databaseEntries, writer);
        }
    }


}
