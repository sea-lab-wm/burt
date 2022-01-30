package sealab.burt.qualitychecker.similarity;

import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/*
For using this class, the python server needs to be running
 */
public class EmbeddingSimilarityComputer {

    private static Gson gson = new Gson();
    private static final String ENDPOINT = "http://127.0.0.1:9000/embed_cosine_multiple/";

    public static List<Double> computeSimilarities(String query, List<String> corpus) throws Exception {
        // create a client

        var client = HttpClient.newHttpClient();
        HttpRequest.BodyPublisher bodyPublisher = createRequestBody(query, corpus);

        // create a request
        var request = HttpRequest.newBuilder()
                .uri(URI.create(ENDPOINT))
                .header("Content-Type", "application/json")
                .POST(bodyPublisher)
                .build();

        // use the client to send the request
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200)
            throw new RuntimeException("The server didn't return the sim. scores: " + response.body());

        Map scores = gson.fromJson(response.body(), Map.class);

        return (List<Double>) scores.get("cos_scores");
    }

    private static HttpRequest.BodyPublisher createRequestBody(String query, List<String> corpus) {
        Map<String, Object> params = Map.of("query", query, "corpus", corpus);
        String inputJson = gson.toJson(params);
        return HttpRequest.BodyPublishers.ofString(inputJson);
    }

    public static void main(String[] args) throws Exception {
        List<Double> similarities = computeSimilarities("i click the added podcast in the subscription",
                Arrays.asList("I clicked the podcast"));
        System.out.println(similarities);
        //"got an error when delete a token", Arrays.asList("'I choose event token'", " 'I check event token'", " 'I select event token'",
        //                "'I choose time token'", " 'I select time token'", " 'I check time token'"
    }
}
