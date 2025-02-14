package com.flourish.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flourish.server.db.DefaultQueryExecutor;
import com.flourish.server.db.MySQLDatabaseConnection;
import com.flourish.server.db.PlantRepository;
import com.flourish.server.db.QueryExecutor;
import com.flourish.shared.Plant;
import com.flourish.shared.PlantDetails;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * TrefleApiClient is a client for the Trefle API, which provides plant species data.
 * <p>
 * The client can fetch one page of plant data, fetch all plant data, and fetch a page of data
 * from a specified URL. It also provides a main method for testing the integration of API retrieval
 * and database insertion.
 * <p>
 *
 * @author Joar Eliasson
 * @since 2025-02-04
 */
public class TrefleApiClient {

    public static final String BASE_URL = "https://trefle.io";
    private final String apiToken;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    /**
     * Constructs a new TrefleApiClientImpl.
     *
     * @param apiToken your Trefle API token
     */
    public TrefleApiClient(String apiToken) {
        this.apiToken = apiToken;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }


    /**
     * Retrieves detailed data for a plant species from Trefle.
     *
     * @param speciesId the unique species identifier.
     * @return a PlantDetails object containing detailed information.
     * @throws IOException          if an I/O error occurs.
     * @throws InterruptedException if the request is interrupted.
     */
    public PlantDetails fetchPlantDetails(int speciesId) throws IOException, InterruptedException {
        String url = String.format("%s/api/v1/plants/%d?token=%s", BASE_URL, speciesId, apiToken);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("Failed to fetch plant details, status code: " + response.statusCode());
        }
        PlantDetailsResponse detailsResponse = objectMapper.readValue(response.body(), PlantDetailsResponse.class);
        PlantDetailsDto dto = detailsResponse.getData();

        String genusName = (dto.getGenus() != null) ? dto.getGenus().getName() : null;
        String familyName = (dto.getFamily() != null) ? dto.getFamily().getName() : null;

        return new PlantDetails(genusName, dto.getScientificName(), dto.getLight(), dto.getWaterFrequency(), familyName);
    }

    /**
     * Fetches one page of plant data from the Trefle API.
     *
     * @return a list of PlantObj representing one page of plant species data.
     * @throws IOException          if an I/O error occurs.
     * @throws InterruptedException if the request is interrupted.
     */
    public List<PlantDto> fetchOnePage() throws IOException, InterruptedException {
        String url = String.format("%s/api/v1/plants?token=%s", BASE_URL, apiToken);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            PlantDataResponse apiResponse = objectMapper.readValue(response.body(), PlantDataResponse.class);
            return apiResponse.getData();
        } else {
            throw new IOException("Failed to fetch page, status code: " + response.statusCode());
        }
    }

    /**
     * Fetches all plant species data from the Trefle API, one page at a time,
     * while respecting rate limits.
     *
     * @return a list of PlantObj representing all species retrieved.
     * @throws IOException          if an I/O error occurs.
     * @throws InterruptedException if the request is interrupted.
     */
    public List<PlantDto> fetchAllPlants() throws IOException, InterruptedException {
        String currentUrl = String.format("%s/api/v1/plants?token=%s", BASE_URL, apiToken);
        List<PlantDto> allPlants = new ArrayList<>();
        int requestCount = 0;
        long periodStartTime = System.currentTimeMillis();
        final int REQUEST_THRESHOLD = 100;
        final long ONE_MINUTE_MS = 60000;

        while (currentUrl != null) {
            if (requestCount >= REQUEST_THRESHOLD) {
                long elapsed = System.currentTimeMillis() - periodStartTime;
                if (elapsed < ONE_MINUTE_MS) {
                    long sleepTime = ONE_MINUTE_MS - elapsed;
                    System.out.println("Rate limit reached; sleeping for " + (sleepTime / 1000) + " seconds...");
                    Thread.sleep(sleepTime);
                }
                requestCount = 0;
                periodStartTime = System.currentTimeMillis();
            }

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(currentUrl))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            requestCount++;

            if (response.statusCode() == 429) {
                System.out.println("Too many requests; sleeping for 10 seconds...");
                Thread.sleep(10000);
                continue;
            }
            if (response.statusCode() != 200) {
                System.out.println("Error: Received status code " + response.statusCode());
                break;
            }

            PlantDataResponse apiResponse = objectMapper.readValue(response.body(), PlantDataResponse.class);
            if (apiResponse.getData() != null) {
                allPlants.addAll(apiResponse.getData());
            }
            System.out.println("Progress: " + allPlants.size() + " plants retrieved so far...");

            if (apiResponse.getLinks() != null && apiResponse.getLinks().containsKey("next")) {
                String nextUrl = apiResponse.getLinks().get("next");
                if (nextUrl != null && !nextUrl.isEmpty()) {
                    if (!nextUrl.startsWith("http")) {
                        nextUrl = BASE_URL + nextUrl;
                    }
                    if (!nextUrl.contains("token=")) {
                        nextUrl += (nextUrl.contains("?") ? "&" : "?") + "token=" + apiToken;
                    }
                    currentUrl = nextUrl;
                } else {
                    currentUrl = null;
                }
            } else {
                currentUrl = null;
            }
            Thread.sleep(1000);
        }
        System.out.println("Total plants retrieved: " + allPlants.size());
        return allPlants;
    }

    /**
     * Fetches a page of data from the specified URL and returns the parsed API response.
     *
     * @param url the full URL for the API request.
     * @return the TrefleApiResponse.
     * @throws IOException          if an I/O error occurs.
     * @throws InterruptedException if the request is interrupted.
     */
    public PlantDataResponse fetchPageResponse(String url) throws IOException, InterruptedException {
        java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                .uri(java.net.URI.create(url))
                .GET()
                .build();

        java.net.http.HttpResponse<String> response = httpClient.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), PlantDataResponse.class);
        } else if (response.statusCode() == 429) {
            System.out.println("API rate limit reached. Waiting 10 seconds before retrying...");
            Thread.sleep(10000);
            return fetchPageResponse(url);
        } else {
            throw new IOException("Failed to fetch page, status code: " + response.statusCode());
        }
    }

    /**
     * Returns the Trefle API token.
     *
     * @return the API token.
     */
    public String getApiToken() {
        return apiToken;
    }

    /**
     * Main method for testing the integration of API retrieval and database insertion.
     *
     * @param args command-line arguments (not used).
     */
    public static void main(String[] args) {
        try {
            TrefleApiClient apiClient = new TrefleApiClient(ApiConfig.TREFLE_API_TOKEN);

            List<PlantDto> plantDtos = apiClient.fetchOnePage();
            System.out.println("Retrieved " + plantDtos.size() + " plant species from the API (one page).");

            MySQLDatabaseConnection connection = MySQLDatabaseConnection.getInstance();
            QueryExecutor queryExecutor = new DefaultQueryExecutor(connection);
            PlantRepository plantRepository = new PlantRepository(queryExecutor, apiClient);

            for (PlantDto pObj : plantDtos) {
                if (!plantRepository.speciesExists(pObj.getId())) {
                    Plant plant = new Plant(
                            pObj.getId(),
                            pObj.getCommonName(),
                            pObj.getScientificName(),
                            pObj.getGenus(),
                            pObj.getFamily(),
                            pObj.getImageUrl(),
                            pObj.getSynonyms()
                    );
                    plantRepository.insertSpecies(plant);
                    System.out.println("Inserted new species: " + plant);
                } else {
                    System.out.println("Species already exists: " + pObj.getScientificName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
