package features.topic.utils;

import com.azure.core.credential.TokenCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Optional;

@Slf4j
public class SBClientBase {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static final String TENANT_ID = "9efd56dd-e9db-43bf-8447-60d4413b594a";

    public static String getServiceBusClientKey() {
        return Optional.ofNullable(System.getProperty("servicebus_key")).orElseThrow();
    }

    public static TokenCredential getTokenCredential(String clientId) {
        return new ClientSecretCredentialBuilder()
                .clientId(clientId)
                .clientSecret(getServiceBusClientKey())
                .tenantId(TENANT_ID)
                .build();
    }

    public static boolean write(String document, String source, String namespace, String clientId) {
        boolean isValid = false;
        TokenCredential credential = getTokenCredential(clientId);
        ServiceBusSenderClient sender = new ServiceBusClientBuilder()
                .credential(namespace, credential)
                .sender()
                .topicName(source)
                .buildClient();
        ServiceBusMessage message = new ServiceBusMessage(document);
        message.setContentType("application/json");
        message.setSubject(String.format("Karate TopicTest - %s", source));
        message.setTimeToLive(Duration.ofMinutes(5));
        sender.sendMessage(message);
        sender.close();
        isValid = true;
        return isValid;
    }

}
