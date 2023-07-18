package features.topic.utils;

import com.azure.core.credential.TokenCredential;
import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusReceiverClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SBClientReader extends SBClientBase {

//    public static void main(String[] args) {
//        //testInput1(args);
//        //testOutput0(args);
//        //testInput0(args);
//    }

//    public static void testInput1(String[] args) {
//        String namespace = "pas-limo-playground.servicebus.windows.net";
//        System.setProperty("servicebus_key", "5878Q~TvnSvuj~fE.xXC2IWhYViXctn_paccEbtQ");
//        String inputDoc = "{ \"v3DocId\": \"127194\" }";
//        try {
//            SBClientReader.write(inputDoc, "pas-document-finalize-topic", namespace, "e7296f9a-475e-418e-ac1a-a3775a3f169b");
//            Map<String, String> readResult = SBClientReader.topicReadAll(inputDoc, "pas-document-finalize-topic",
//                    "test-pas-document-finalize-in", namespace, "e7296f9a-475e-418e-ac1a-a3775a3f169b");
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

//    public static void testInput0(String[] args) {
//        String namespace = "pas-limo-playground.servicebus.windows.net";
//        System.setProperty("servicebus_key", "5878Q~TvnSvuj~fE.xXC2IWhYViXctn_paccEbtQ");
//        String inputDoc = "{\"id\":\"35090b87-74dc-4ffd-a25e-0a741d3f5d5b\",\"v3DocId\":\"127194\",\"status\":\"Finalized\",\"_ts\":1689021661,\"checkoutUser\":null,\"checkoutDate\":null,\"metadataMap\":{\"documentDescription\":\"PLAYGROUND 999990-127194\",\"finalizedDate\":\"1689021660\",\"v3EntityType\":\"INSTITUTION\",\"documentType\":\"AI_POLICY\",\"v3EntityBEKId\":\"425068\",\"policyPlanId\":\"A, \",\"documentFileName\":\"AI Policy\",\"v3EmployerId\":\"14469\",\"policyId\":\"999990\",\"mpwFilenetDocId\":\"{218147a8-0c28-477c-86e9-e298304537ac}\",\"appId\":\"56-480778689-232815953-1073582509\",\"exstreamDocId\":\"ea89c3c0-190c-493e-8431-72dbb1919bd2\",\"filenetDocId\":\"{dff6b3ba-6f2f-4da0-8bfb-47ead954f895}\",\"v3CarrierId\":\"423216\",\"refinalizeHistory\":\"[]\"},\"notes\":[]}";
//        try {
//            SBClientReader.write(inputDoc, "document-refinalize", namespace, "e7296f9a-475e-418e-ac1a-a3775a3f169b");
//            Map<String, String> readResult = SBClientReader.topicReadAll(inputDoc, "document-refinalize",
//                    "test-document-refinalize-in", namespace, "e7296f9a-475e-418e-ac1a-a3775a3f169b");
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

//    public static void testOutput0(String[] args) {
//        String namespace = "pas-limo-playground.servicebus.windows.net";
//        System.setProperty("servicebus_key", "5878Q~TvnSvuj~fE.xXC2IWhYViXctn_paccEbtQ");
//        String outputDoc = "{ \"data\": { \"documentId\": \"127194\" }, \"eventType\": \"DOCUMENT_FINALIZE\", \"metadataMap\": { \"fileType\": \"pdf\" } }";
//        try {
//            SBClientReader.write(outputDoc, "filenet-document-archive-topic", namespace, "e7296f9a-475e-418e-ac1a-a3775a3f169b");
//            Map<String, String> readResult = SBClientReader.topicReadAll(outputDoc, "filenet-document-archive-topic",
//                    "test-pas-document-finalize-out", namespace, "e7296f9a-475e-418e-ac1a-a3775a3f169b");
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

    public static Map<String, String> topicReadAll(String expectedDoc, String topic, String sub, String namespace, String clientId) {

        log.info(String.format("SEARCH ARGS: %s, %s, %s, %s, %s", expectedDoc.replace("\n", ""), topic, sub, namespace, clientId));

        Stopwatch timer = Stopwatch.createStarted();
        final int NUMBER_OF_INTERVALS = 18;
        final int WAIT_INTERVAL_SECONDS = 5;

        TokenCredential credential = getTokenCredential(clientId);
        ServiceBusReceiverClient receiverClient = new ServiceBusClientBuilder()
                .credential(namespace, credential)
                .receiver()
                .topicName(topic)
                .subscriptionName(sub)
                .buildClient();
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        Callable<Map<String, String>> taskReadAll = new TopicSearcher(expectedDoc, MessageDirection.fromString(sub), receiverClient);

        log.info("----- Start search among " + sub + " messages...");

        Map<String, String> result = new HashMap<>();
        int totalCount = 0;
        Future<Map<String, String>> future;
        int iteration = 0;
        do {
            iteration = iteration + 1;
            log.info("----- Match attempt " + iteration + " on " + sub + " message...");
            future = executor.schedule(taskReadAll, 0, TimeUnit.SECONDS);
            try {
                Map<String, String> iterationResult = future.get();
                totalCount = totalCount + Integer.parseInt(iterationResult.get("messageCount"));
                if (Boolean.parseBoolean(iterationResult.get("found"))) {
                    System.out.println("----- Document Found");
                    result.putAll(iterationResult);
                    break;
                }
                result.putAll(iterationResult);
                log.info(String.format("----- Search result iteration %s %s topic completed: %s", iteration, sub, OBJECT_MAPPER.writeValueAsString(result)));
                TimeUnit.SECONDS.sleep(WAIT_INTERVAL_SECONDS);
            } catch (Exception e) {
                log.info(String.format("----- Search attempt %s %s failed.", iteration, sub));
            } finally {
                future.cancel(true);
            }
        } while (iteration <= NUMBER_OF_INTERVALS);

        executor.shutdown();
        receiverClient.close();
        try {
            System.out.println("----- Result:\n" + OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(result));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        result.put("elapsedSeconds", String.valueOf(timer.stop().elapsed(TimeUnit.SECONDS)));
        result.put("totalCount", String.valueOf(totalCount));
        return result;
    }

}
