package features.topic.utils;

import com.azure.core.credential.TokenCredential;
import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusReceivedMessage;
import com.azure.messaging.servicebus.ServiceBusReceiverClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class SBClientPeek extends SBClientBase {

    public static Map<String, String> topicPeek(String expectedDocument, String topic, String sub, long maxTries, String namespace, String clientId) {
        Map<String, String> result = new HashMap<>();
        TokenCredential credential = getTokenCredential(clientId);
        ServiceBusReceiverClient receiverClient = new ServiceBusClientBuilder()
                .credential(namespace, credential)
                .receiver()
                .topicName(topic)
                .subscriptionName(sub)
                .buildClient();
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        AtomicBoolean expectedDocumentSeen = new AtomicBoolean(false); // requires a fast enough peek
        AtomicBoolean expectedDocumentDequeued = new AtomicBoolean(false); // requires some time has passed

        Runnable taskQuickPeek = () -> {
            try {
                ServiceBusReceivedMessage message = receiverClient.peekMessage();
                if (message != null) {
                    String document = message.getBody().toString();
                    if (isExpectedJson(expectedDocument, document)) {
                        expectedDocumentSeen.set(true);
                        System.out.println("----- Expected document found.");
                        receiverClient.complete(message);
                    }
                }
            } catch (Exception e) {
                System.err.println("----- Error while peeking message: " + e.getMessage());
            }
        };

        long initialDelay = 0L;
        long period = 250L;
        TimeUnit unit = TimeUnit.MILLISECONDS;
        long durationOfPeeking = initialDelay + period * maxTries;
        long stopTimeMillis = System.currentTimeMillis() + TimeUnit.MILLISECONDS.toMillis(durationOfPeeking);

        while (System.currentTimeMillis() < stopTimeMillis && !expectedDocumentSeen.get()) {
            executor.schedule(taskQuickPeek, initialDelay, unit);
            initialDelay += period;
            try {
                TimeUnit.MILLISECONDS.sleep(period);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (!expectedDocumentSeen.get()) {
            System.out.println("----- Expected document not seen within the time limit.");
        }
        if (expectedDocumentDequeued.get()) {
            System.out.println("----- Expected document appears to be dequeued or was never there at all.");
        } else {
            System.out.println("----- Document might be stuck in queue. Restart function that subscribes to the queue.");
        }
        executor.shutdown();
        receiverClient.close();

        result.put("seen", Boolean.toString(expectedDocumentSeen.get()));
        result.put("dequeued", Boolean.toString(expectedDocumentDequeued.get()));
        return result;
    }

    private static boolean isExpectedJson(String expectedJson, String actualJson) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode expectedNode = objectMapper.readTree(expectedJson);
            JsonNode actualNode = objectMapper.readTree(actualJson);

            boolean areEqual = expectedNode.equals(actualNode);
            if (areEqual) {
                System.out.println("----- JSON objects are equal.");
            } else {
                System.out.println("----- JSON objects are not equal.");
                System.out.println("----- EXPECTED: " + expectedNode + ", ACTUAL: " + actualNode);
            }

            return areEqual;
        } catch (IOException e) {
            System.err.println("----- Error while parsing JSON: " + e.getMessage());
            return false;
        }
    }

}
