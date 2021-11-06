package com.credex.fs.digital.service;

import com.microsoft.azure.cognitiveservices.vision.computervision.ComputerVisionClient;
import com.microsoft.azure.cognitiveservices.vision.computervision.ComputerVisionManager;
import com.microsoft.azure.cognitiveservices.vision.computervision.models.ImageAnalysis;
import com.microsoft.azure.cognitiveservices.vision.computervision.models.ImageTag;
import com.microsoft.azure.cognitiveservices.vision.computervision.models.VisualFeatureTypes;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.Base64;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

@Service
@Slf4j
public class ComputerVisionService {

    public static final String SUBSCRIPTION_KEY = "7ae8610ead254691bd4a08dd8cd334ff";

    public static final String ENDPOINT = "https://smarthack-2021-fantastic4.cognitiveservices.azure.com/";

    private final ComputerVisionClient computerVisionClient;

    public ComputerVisionService() {
        this.computerVisionClient = ComputerVisionManager.authenticate(SUBSCRIPTION_KEY).withEndpoint(ENDPOINT);
    }

    public List<ImageTag> analyzeImage() {
        String pathToLocalImage = "test1.jpg";

        List<VisualFeatureTypes> featuresToExtractFromLocalImage = new ArrayList<>();
        featuresToExtractFromLocalImage.add(VisualFeatureTypes.TAGS);

        try {
            // Need a byte array for analyzing a local image.
            File rawImage = ResourceUtils.getFile("classpath:" + pathToLocalImage);
            byte[] imageByteArray = Files.readAllBytes(rawImage.toPath());

            // Call the Computer Vision service and tell it to analyze the loaded image.
            ImageAnalysis analysis = computerVisionClient
                .computerVision()
                .analyzeImageInStream()
                .withImage(imageByteArray)
                .withVisualFeatures(featuresToExtractFromLocalImage)
                .execute();

            return analysis.tags();
        } catch (IOException e) {
            log.error("error", e);
        }

        return Collections.emptyList();
    }

    public List<ImageTag> analyzeImage(String b64) {
        List<VisualFeatureTypes> featuresToExtractFromLocalImage = new ArrayList<>();
        featuresToExtractFromLocalImage.add(VisualFeatureTypes.TAGS);

        // Need a byte array for analyzing a local image.
        byte[] imageByteArray = Base64.decode(b64);

        // Call the Computer Vision service and tell it to analyze the loaded image.
        ImageAnalysis analysis = computerVisionClient
            .computerVision()
            .analyzeImageInStream()
            .withImage(imageByteArray)
            .withVisualFeatures(featuresToExtractFromLocalImage)
            .execute();

        return analysis.tags();
    }
}
