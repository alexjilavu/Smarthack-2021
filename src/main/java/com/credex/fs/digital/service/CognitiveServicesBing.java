package com.credex.fs.digital.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;

@Service
public class CognitiveServicesBing {

    private static String endpoint = "https://api.bing.microsoft.com/v7.0/images/visualsearch";
    private static String subscriptionKey = "8667b1382d8d42e09f4ffb79fc505c49";
    private static String imagePath = "test1.jpg";

    private RestTemplate restTemplate = new RestTemplate();

    public static void main(String[] args) throws IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        HttpEntity entity = MultipartEntityBuilder.create().addBinaryBody("image", ResourceUtils.getFile("classpath:" + imagePath)).build();

        HttpPost httpPost = new HttpPost(endpoint);
        httpPost.setHeader("Ocp-Apim-Subscription-Key", subscriptionKey);
        httpPost.setEntity(entity);

        HttpResponse response = httpClient.execute(httpPost);
        InputStream stream = response.getEntity().getContent();

        String json = new Scanner(stream).useDelimiter("\\A").next();

        System.out.println(prettify(json));
    }

    public static String prettify(String json_text) {
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(json_text).getAsJsonObject();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(json);
    }
}
