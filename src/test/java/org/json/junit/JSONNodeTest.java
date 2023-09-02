package org.json.junit;

import org.json.JSONArray;
import org.json.JSONNode;
import org.json.JSONObject;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

public class JSONNodeTest {

    FilenameFilter patternFileDirectoryFilter = new FilenameFilter() {
        @Override
        public boolean accept(File f, String name) {
            return name.startsWith("pattern");
        }
    };

    @Test
    //@Ignore
    //To be used only for generating new pattern samples
    public void generateRandomPatterns() throws IOException {
        int maxPatternCount = 1;
        int maxValuesCount = 4;
        int patternStartIndex = 0;
        int patternEndIndex = 0;
        File samplesDirectory = Paths.get("src", "test", "resources", "diff-samples").toFile();
        for (File patternDirectory : samplesDirectory.listFiles(patternFileDirectoryFilter)) {
            patternStartIndex = Math.max(patternStartIndex, Integer.valueOf(patternDirectory.getName().replace("pattern", "")));
        }
        patternEndIndex = patternStartIndex + maxPatternCount;
        patternStartIndex++;
        for (; patternStartIndex <= patternEndIndex; patternStartIndex++) {
            String patternString = "pattern" + String.format("%06d", patternStartIndex);
            File patternDirectory = new File(samplesDirectory.getAbsolutePath() + File.separator + patternString);
            patternDirectory.mkdir();
            String generatedPatternFilePath = samplesDirectory.getAbsolutePath() + File.separator + patternString + File.separator + "generated.json";
            Files.write(Paths.get(generatedPatternFilePath), generateRandomJSON(null, 1, maxValuesCount).toString().getBytes());
        }
    }

    Object generateRandomJSON(Object parentJSON, int valueStartIndex, int maxValuesCount) {
        Random random = new Random();
        if (parentJSON == null) {
            boolean generateJSONObject=random.nextBoolean();
            if (generateJSONObject) {
                parentJSON = new JSONObject();
            } else {
                parentJSON = new JSONArray();
            }
        }
        while (valueStartIndex <= maxValuesCount) {
            Object childJSON = null;
            int allowedValues = maxValuesCount - valueStartIndex + 1;
            int valuesChunk = random.nextInt(allowedValues) + 1;
            int generateValueIndex = random.nextInt(4);
            switch (generateValueIndex) {
                case 1:
                    childJSON = new JSONObject();
                    while (maxValuesCount > 0) {
                        ((JSONObject) childJSON).put("key" + String.format("%06d", valueStartIndex), "value" + String.format("%06d", valueStartIndex));
                        valueStartIndex++;
                        maxValuesCount--;
                    }
                    return childJSON;
                case 2:
                    childJSON = new JSONArray();
                    while (maxValuesCount > 0) {
                        ((JSONArray) childJSON).put("value" + String.format("%06d", valueStartIndex));
                        valueStartIndex++;
                        maxValuesCount--;
                    }
                    return childJSON;
                case 3:
                    childJSON = generateRandomJSON(parentJSON, valueStartIndex, valuesChunk);
                    if (parentJSON instanceof JSONObject) {
                        ((JSONObject) parentJSON).put("key" + String.format("%06d", valueStartIndex), childJSON);
                    } else if (parentJSON instanceof JSONArray) {
                        ((JSONArray) parentJSON).put(childJSON);
                    }
            }
            valueStartIndex = valueStartIndex + valuesChunk;
        }
        return parentJSON;
    }


    @Test
    @Ignore
    public void verifyLoading() throws IOException {
        for (File patternDirectory : Paths.get("src", "test", "resources", "diff-samples").toFile().listFiles(patternFileDirectoryFilter)) {
            System.out.println("Verifying loading of " + patternDirectory.getName());
            String jsonString = new String(Files.readAllBytes(Paths.get(patternDirectory.getAbsolutePath() + File.separator + "generated.json")));
            JSONNode node = new JSONNode(jsonString);
        }
    }
}
