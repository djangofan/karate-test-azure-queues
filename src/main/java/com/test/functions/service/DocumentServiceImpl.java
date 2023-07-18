package com.test.functions.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.test.functions.exception.DocumentException;
import com.test.functions.model.DocumentRequest;
import com.test.functions.model.DocumentResponse;
import com.test.functions.util.EnvUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DocumentServiceImpl implements DocumentService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static EnvUtil envUtil = new EnvUtil();
    private static String GW_URL = envUtil.getProperty(EnvUtil.AppVar.GATEWAY_URL);
    private static String APIM_KEY = envUtil.getProperty(EnvUtil.AppVar.APIM_SUBSCRIPTION_KEY);
    private static String SERVICE_BUS_CONNECTION = envUtil.getProperty(EnvUtil.AppVar.ServiceBusConnection__fullyQualifiedNamespace);

    @Override
    public HttpResponseMessage processDocument(HttpRequestMessage<DocumentRequest> httpRequestMessage) throws DocumentException {
        try {
            log.info("input0: httpRequestMessage: {}", httpRequestMessage);
            DocumentResponse documentPayloadResponse = new DocumentResponse(httpRequestMessage.getBody().getBody());
            String documentPayloadResponseToJson = OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(documentPayloadResponse);
            String docIntakeTopicName = envUtil.getProperty(EnvUtil.AppVar.TOPIC0_NAME);
            String docIntakeTopicSubscriptionName = envUtil.getProperty(EnvUtil.AppVar.TOPIC0_SUBSCRIPTION_NAME_IN);

            return httpRequestMessage.createResponseBuilder(HttpStatus.ACCEPTED)
                    .body(null)
                    .header("Content-Type", "application/json")
                    .build();

        } catch (Exception e) {
            log.error("sendToDocIntakeTopic: exception: ", e);
            return httpRequestMessage.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage())
                    .build();
        }
    }

}
