package features.topic.utils;

import com.azure.messaging.servicebus.ServiceBusReceivedMessage;
import com.azure.messaging.servicebus.ServiceBusReceiverClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@Slf4j
public class TopicSearcher implements Callable<Map<String, String>> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final String expectedDoc;
    private final MessageDirection direction;
    private final ServiceBusReceiverClient receiverClient;

    public TopicSearcher(String expectedDoc, MessageDirection direction, ServiceBusReceiverClient receiverClient) {
        this.expectedDoc = expectedDoc;
        this.direction = direction;
        this.receiverClient = receiverClient;
        log.info("----- TopicSearcher created for " + direction.getDirection() + " direction.");
    }
 
    @Override
    public Map<String, String> call() throws Exception {
        Map<String, String> result = new HashMap<>();
        result.put("found", "false");
        result.put("messageCount", "-1");
        List<ServiceBusReceivedMessage> messages;
        int loopCount = 0;
        try {
            //log.info("----- Calling for messages...");
            messages = receiverClient.receiveMessages(10, Duration.ofSeconds(5L)).stream().collect(Collectors.toList());
            int messageCount = messages.size();
            result.put("messageCount", Integer.toString(messageCount));
            if (messageCount == 0) {
                log.info("----- No messages found in topic.");
                return result;
            }
            for (ServiceBusReceivedMessage message : messages) {
                loopCount = loopCount + 1;
                String document = message.getBody().toString();
                log.info("----- Read " + direction.getDirection() + " direction, try " + loopCount + ": " + document);
                if (doesJsonMatch(expectedDoc, document, direction.getDirection())) {
                    result.put("found", "true");
                    result.put("document", OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(document)); // if more than one is found, will only return last one found
                    receiverClient.complete(message);
                    //break; // Exit the loop after the first matching document is found
                }
                receiverClient.complete(message);
            }
        } catch (Exception e) {
            log.error("-----\n ERROR: " + e.getMessage() + "\n-----");
            return result;
        }
        log.info("----- Read All: " + result);
        return result;
    }

    public boolean doesJsonMatch(String expectedJson, String actualJson, String direction) {
        JsonNode actualNode;
        JsonNode expectedNode;
        try {
            actualNode = OBJECT_MAPPER.readTree(actualJson);
            expectedNode = OBJECT_MAPPER.readTree(expectedJson);
            switch(direction) {
                case "test-document-refinalize-in":
                    return inboundContainsExpectedFields(expectedNode, actualNode);
                case "test-pas-document-finalize-in":
                    return inboundContainsExpectedFields(expectedNode, actualNode);
                case "test-pas-document-finalize-out":
                    return outboundContainsExpectedPdfFields(expectedNode, actualNode) & containsOutboundDocumentFinalizedDate(actualNode);
                case "test-filenet-doc-archive-result-out":
                    return outboundContainsExpectedFields(expectedNode, actualNode);
                default:
                    return false;
            }
        } catch (Exception e) {
            // do nothing, default to false
        }
        return false;
    }

    public boolean inboundContainsExpectedFields(JsonNode expectedNode, JsonNode actualNode) {
        if (!expectedNode.has("v3DocId")) {
            log.info("----- Inbound Expected Document Missing Required v3DocId");
            return false;
        }
        log.info("----- Inbound Document: " + actualNode.get("v3DocId").asText());
        if (!actualNode.has("v3DocId")) {
            log.info("----- Inbound Document Missing Required v3DocId");
            return false;
        }
        String actualDocumentId = actualNode.get("v3DocId").asText();
        String expectedDocumentId = expectedNode.get("v3DocId").asText();
        boolean idMatches = expectedDocumentId.equals(actualDocumentId);
        if (idMatches) {
            log.info(String.format("----- Inbound Document ID Match Positive: %s == %s", expectedDocumentId, actualDocumentId));
        } else {
            log.info(String.format("----- Inbound Document ID Match Error: %s != %s", expectedDocumentId, actualDocumentId));
        }
        return idMatches;
    }

    public boolean outboundContainsExpectedPdfFields(JsonNode expectedNode, JsonNode actualNode) {
        if (!expectedNode.has("metadataMap")) {
            log.info("----- Outbound Expected Document Missing Required 'metadataMap' field");
            return false;
        }
        if (!actualNode.has("metadataMap")) {
            log.info("----- Outbound Document Missing Required 'metadataMap' field");
            return false;
        }
        if (!expectedNode.has("data")) {
            log.info("----- Outbound Expected Document Missing Required 'data' field");
            return false;
        }
        if (!actualNode.has("data")) {
            log.info("----- Outbound Document Missing Required 'data' field");
            return false;
        }
        ObjectNode expectedObject = (ObjectNode) expectedNode;
        String expectedDocumentId = expectedObject.get("data").get("documentId").asText();
        String expectedEventType = expectedObject.get("eventType").asText();
        String expectedFileType = "pdf";
        ObjectNode actualObject = (ObjectNode) actualNode;
        String actualDocumentId = actualObject.get("data").get("documentId").asText();
        String actualEventType = actualObject.get("eventType").asText();
        String actualFileType = actualObject.get("metadataMap").get("fileType").asText();
        boolean isFound = expectedEventType.equals(actualEventType) && expectedDocumentId.equals(actualDocumentId) && expectedFileType.equals(actualFileType);
        if (isFound) {
            log.info("----- Matched: " + actualEventType + " " + actualDocumentId + " " + actualFileType);
        } else {
            log.info("----- No Match: " + actualEventType + " " + actualDocumentId + " " + actualFileType);
        }
        return isFound;
    }

    public long getEpochTimeNowWithOffset(int minutesOffset) {
        return Instant.now().minus(Duration.ofMinutes(minutesOffset)).getEpochSecond();
    }

    public boolean outboundContainsExpectedFields(JsonNode expectedNode, JsonNode actualNode) {
        if (!expectedNode.has("metadataMap")) {
            log.info("----- Outbound Expected Document Missing Required 'metadataMap' field");
            return false;
        }
        if (!actualNode.has("metadataMap")) {
            log.info("----- Outbound Document Missing Required 'metadataMap' field");
            return false;
        }
        if (!expectedNode.has("documentId")) {
            log.info("----- Outbound Expected Document Missing Required 'documentId' field");
            return false;
        }
        if (!actualNode.has("documentId")) {
            log.info("----- Outbound Document Missing Required 'documentId' field");
            return false;
        }
        ObjectNode expectedObject = (ObjectNode) expectedNode;
        String expectedDocumentId = expectedObject.get("documentId").asText();
        String expectedEventType = expectedObject.get("eventType").asText();
        String expectedFileType = "pdf";
        ObjectNode actualObject = (ObjectNode) actualNode;
        String actualDocumentId = actualObject.get("documentId").asText();
        String actualEventType = actualObject.get("eventType").asText();
        String actualFileType = actualObject.get("metadataMap").get("fileType").asText();
        boolean isFound = expectedEventType.equals(actualEventType) && expectedDocumentId.equals(actualDocumentId) && expectedFileType.equals(actualFileType);
        if (isFound) {
            log.info("----- Matched: " + actualEventType + " " + actualDocumentId + " " + actualFileType);
        } else {
            log.info("----- No Match: " + actualEventType + " " + actualDocumentId + " " + actualFileType);
        }
        return isFound;
    }

    public boolean containsOutboundDocumentFinalizedDate(JsonNode actualNode) {
        ObjectNode actualObject = (ObjectNode) actualNode;
        return StringUtils.isNotBlank(actualObject.get("metadataMap").get("finalizedDate").asText());
    }

}
