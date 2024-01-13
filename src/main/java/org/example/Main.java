package org.example;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;


public class Main {

    static UsernameGenerator nameGenerator = new UsernameGenerator();
    static ImageLoader imageLoader = new ImageLoader();

    static String USER_SERVER = "http://localhost:9080";
    static String FEED_SERVER = "http://localhost:8080";
    static String IMAGE_SERVER = "http://localhost:6080";

    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
        String baseurl = System.getenv("SNS_DATA_GENERATOR_BASEURL");
        String userServer = System.getenv("SNS_DATA_GENERATOR_USER_SERVER");
        String feedServer = System.getenv("SNS_DATA_GENERATOR_FEED_SERVER");
        String imageServer = System.getenv("SNS_DATA_GENERATOR_IMAGE_SERVER");
        if (baseurl == null) {
            System.out.println("No baseurl is defined in the environment variable. Using the local environment.");
        } else {
            System.out.println("Data generate to " + baseurl);
            USER_SERVER = baseurl;
            FEED_SERVER = baseurl;
            IMAGE_SERVER = baseurl;
        }

        if (userServer != null) {
            USER_SERVER = userServer;
        }

        if (feedServer != null) {
            FEED_SERVER = feedServer;
        }

        if (imageServer != null) {
            IMAGE_SERVER = imageServer;
        }

        System.out.println("===========================================================");
        System.out.println("Server URL");
        System.out.println("User Server : " + userServer);
        System.out.println("Feed Server : " + feedServer);
        System.out.println("Image Server : " + imageServer);
        System.out.println("===========================================================");

        System.out.println("Image Count : " + imageLoader.countImage());
        String count = args.length > 0 ? args[0] : null;
        int genCount = (count == null || count.isBlank()) ? imageLoader.countImage() : Integer.parseInt(count);
        System.out.println("Generate " + genCount + " data");
        while (genCount > 0) {
            int generated = genUserAndPost();
            genCount -= generated;
        }
    }

    private static int genUserAndPost() throws URISyntaxException, IOException, InterruptedException {
        String name = nameGenerator.getUsername();
        String userId = null;
        HttpRequest userCheckRequest = HttpRequest.newBuilder()
                .uri(new URI(USER_SERVER + "/api/users/name/" + name))
                .GET()
                .build();
        HttpResponse<String> userCheckResponse = HttpClient
                .newBuilder()
                .build()
                .send(userCheckRequest, HttpResponse.BodyHandlers.ofString());
        if (userCheckResponse.statusCode() == 200) {
            System.out.println("user exists " + userCheckResponse.body());
            JsonObject jsonObject = new JsonParser().parse(userCheckResponse.body()).getAsJsonObject();
            userId = jsonObject.get("userId").getAsString();
        } else {
            HttpRequest userRequest = HttpRequest.newBuilder()
                    .uri(new URI(USER_SERVER + "/api/users"))
                    .POST(HttpRequest.BodyPublishers.ofString("{\"username\":\"" + name + "\", \"email\":\"" + name + "@example.com\", \"plainPassword\": \"a!\"}"))
                    .header("Content-Type", "application/json")
                    .build();
            HttpResponse<String> response = HttpClient
                    .newBuilder()
                    .build()
                    .send(userRequest, HttpResponse.BodyHandlers.ofString());

            System.out.println("user created " + response.body());

            if (response.statusCode() == 200) {
                JsonObject jsonObject = new JsonParser().parse(response.body()).getAsJsonObject();
                userId = jsonObject.get("userId").getAsString();
            }
        }

        if (userId == null) {
            return 0;
        }

        int remainImage = imageLoader.countImage();
        if (remainImage == 0) {
            imageLoader.loadImages();
            remainImage = imageLoader.countImage();
        }
        int postCount = Math.min(remainImage, new Random().nextInt(5) + 1);
        for (int i = 0; i < postCount; i++) {
            TestImage testImage = imageLoader.pickImage();
            HttpClient client = HttpClient.newHttpClient();
            MultipartBodyPublisher publisher = new MultipartBodyPublisher()
                    .addPart("image", testImage.image.toPath());
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(IMAGE_SERVER + "/api/images/upload"))
                    .header("Content-Type", "multipart/form-data; boundary=" + publisher.getBoundary())
                    .POST(publisher.build())
                    .build();
            HttpResponse<String> imageResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            String imageId = imageResponse.body();
            String postRequest = "{\"imageId\":\"" + imageId + "\",\"uploaderId\":" + userId + ", \"contents\":\"" + testImage.getContents() + "\"}";

            HttpRequest feedPostRequest = HttpRequest.newBuilder()
                    .uri(URI.create(FEED_SERVER + "/api/feeds"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest
                            .BodyPublishers
                            .ofString(postRequest)
                    ).build();

            HttpResponse<String> postResponse = HttpClient
                    .newBuilder()
                    .build()
                    .send(feedPostRequest, HttpResponse.BodyHandlers.ofString());

            if (postResponse.statusCode() == 200) {
                System.out.println("name : " + name + " / post : " + testImage.getContents());
            }


        }
        return postCount;
    }
}