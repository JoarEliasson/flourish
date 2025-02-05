package se.myhappyplants.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import se.myhappyplants.server.db.DefaultQueryExecutor;
import se.myhappyplants.server.db.MySQLDatabaseConnection;
import se.myhappyplants.server.db.PlantRepository;
import se.myhappyplants.server.db.QueryExecutor;
import se.myhappyplants.shared.Plant;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
     * Fetches one page of plant data from the Trefle API.
     *
     * @return a list of PlantObj representing one page of plant species data.
     * @throws IOException if an I/O error occurs.
     * @throws InterruptedException if the request is interrupted.
     */
    public List<PlantObj> fetchOnePage() throws IOException, InterruptedException {
        String url = String.format("%s/api/v1/plants?token=%s", BASE_URL, apiToken);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            TrefleApiResponse apiResponse = objectMapper.readValue(response.body(), TrefleApiResponse.class);
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
     * @throws IOException if an I/O error occurs.
     * @throws InterruptedException if the request is interrupted.
     */
    public List<PlantObj> fetchAllPlants() throws IOException, InterruptedException {
        String currentUrl = String.format("%s/api/v1/plants?token=%s", BASE_URL, apiToken);
        List<PlantObj> allPlants = new ArrayList<>();
        int requestCount = 0;
        long periodStartTime = System.currentTimeMillis();
        final int REQUEST_THRESHOLD = 100;
        final long ONE_MINUTE_MS = 60000;

        while (currentUrl != null) {
            // Enforce rate limiting
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

            TrefleApiResponse apiResponse = objectMapper.readValue(response.body(), TrefleApiResponse.class);
            if (apiResponse.getData() != null) {
                allPlants.addAll(apiResponse.getData());
            }
            System.out.println("Progress: " + allPlants.size() + " plants retrieved so far...");

            // Determine the next URL:
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
            // Gentle delay between pages.
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
     * @throws IOException if an I/O error occurs.
     * @throws InterruptedException if the request is interrupted.
     */
    public TrefleApiResponse fetchPageResponse(String url) throws IOException, InterruptedException {
        java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                .uri(java.net.URI.create(url))
                .GET()
                .build();

        java.net.http.HttpResponse<String> response = httpClient.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), TrefleApiResponse.class);
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
            // Instantiate the API client using the token from configuration.
            TrefleApiClient apiClient = new TrefleApiClient(ApiConfig.TREFLE_API_TOKEN);

            // Fetch one page of plant species data from the Trefle API.
            List<PlantObj> plantObjs = apiClient.fetchOnePage();
            System.out.println("Retrieved " + plantObjs.size() + " plant species from the API (one page).");

            // Set up the database connection and repository.
            MySQLDatabaseConnection connection = MySQLDatabaseConnection.getInstance();
            QueryExecutor queryExecutor = new DefaultQueryExecutor(connection);
            PlantRepository plantRepository = new PlantRepository(queryExecutor);

            // Process each plant species from the API response.
            for (PlantObj pObj : plantObjs) {
                if (!plantRepository.speciesExists(pObj.getId())) {
                    // Convert API response to domain model.
                    Plant plant = new Plant(
                            pObj.getId(),
                            pObj.getCommonName(),
                            pObj.getScientificName(),
                            pObj.getGenus(),
                            pObj.getFamily(),
                            pObj.getImageUrl(),
                            pObj.getSynonyms()
                    );
                    // Insert the new species into the database.
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
