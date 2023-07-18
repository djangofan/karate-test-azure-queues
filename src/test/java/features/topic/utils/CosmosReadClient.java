package features.topic.utils;

import com.azure.cosmos.ConsistencyLevel;
import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosDatabase;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * For a CosmosClient connection, we are allowed to use a local authentication method with a Connection String, as documented
 *  here: https://standard.atlassian.net/wiki/spaces/CCOE/pages/1945993800/Azure+Cosmos+Document+DB+-+Default+Security+Baselines+and+Controls
 */
@Slf4j
public class CosmosReadClient {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final int MAX_AGE_SECONDS = 300; // 5 minutes, in seconds

    private static final String ERROR_DATABASE = "pas-doc-metadata-db";
    private static final String DOC_METADATA_CNT = "pas-doc-metadata-cnt";
    private static final String COSMOS_QUERY_DOCID = "SELECT * FROM c WHERE c.v3DocId = '%s'";
    // private static final String COSMOS_QUERY_DOCID = "SELECT * FROM c WHERE c.v3DocId = '%s' AND c._ts > %d";

    private static int CURRENT_TIME_IN_SECONDS = (int) (System.currentTimeMillis() / 1000);
    private static int LOWER_BOUNDS_TIMESTAMP = CURRENT_TIME_IN_SECONDS - MAX_AGE_SECONDS;


    // Method main() is commented out just for debugging purposes if you need it.
//     public static void main(String[] args) {
//         System.setProperty("cosmos_key", "");
//         System.setProperty("servicebus_key", "");
//         CosmosReadClient.searchCosmosDocId("126291", "https://pas-cosmosdb-acct-akvvea-playground.documents.azure.com:443", 5, 5, 1);
//     }

    public static String getCosmosKey() {
        return Optional.ofNullable(System.getProperty("cosmos_key")).orElseThrow();
    }

    public static List<String> searchCosmosDocId(String documentId, String url, int numRetries, int timeToWaitSeconds, int maxResults) {
        List<String> resultList = null;
        CosmosClientBuilder clientBuilder = new CosmosClientBuilder()
                .endpoint(url)
                .key(getCosmosKey())
                .consistencyLevel(ConsistencyLevel.SESSION);
        CosmosClient cosmosClient = clientBuilder.buildClient();
        CosmosDatabase cosmosDatabase = cosmosClient.getDatabase(ERROR_DATABASE);
        CosmosContainer cosmosContainer = cosmosDatabase.getContainer(DOC_METADATA_CNT);
        do {
            try {
                fastFailOnMaxRetries(numRetries, timeToWaitSeconds);
                numRetries--;
                resultList = callForDocId(cosmosClient, cosmosDatabase, cosmosContainer, documentId, maxResults);
                System.out.println("--- " + resultList);
            } catch (Exception e) {
                log.error("Failure while waiting for JSON response.", e);
                break;
            }
        } while (resultList.size() == 0);
        cosmosClient.close();
        return resultList;
    }

    public static List<String> callForDocId(CosmosClient cosmosClient, CosmosDatabase cosmosDatabase, CosmosContainer cosmosContainer, String documentId, int maxResults) throws JsonProcessingException {

        List<String> resultList = new ArrayList<>();
        CosmosQueryRequestOptions options = new CosmosQueryRequestOptions();
        final String formattedQuery = String.format(COSMOS_QUERY_DOCID, documentId, LOWER_BOUNDS_TIMESTAMP);
        System.out.println("FORMATTED QUERY: " + formattedQuery);
        CosmosPagedIterable<Object> results = cosmosClient.getDatabase(cosmosDatabase.getId())
                .getContainer(cosmosContainer.getId())
                .queryItems(formattedQuery, options, Object.class);

        Iterator<Object> iterator = results.iterator();
        for (int i = 0; i < maxResults && iterator.hasNext(); i++) {
            resultList.add(OBJECT_MAPPER.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(iterator.next()));
        }
        return resultList;
    }

    public static void fastFailOnMaxRetries(int numRetries, int timeToWaitSeconds) throws Exception {
        log.info("Number of retries left: " + numRetries);
        if ( !shouldRetry(numRetries) ) {
            throw new Exception("Retry limit exceeded for Karate test.");
        }
        waitUntilNextTry(TimeUnit.SECONDS.toMillis(timeToWaitSeconds));
    }

    public static void waitUntilNextTry(long timeToWaitMs)
    {
        try {
            Thread.sleep(timeToWaitMs);
        } catch (InterruptedException iex) {
            // do nothing
        }
    }

    public static boolean shouldRetry(int numRetries) {
        return (numRetries > 0);
    }

}
