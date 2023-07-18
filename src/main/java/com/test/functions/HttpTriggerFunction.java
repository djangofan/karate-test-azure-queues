package com.test.functions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatusType;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.test.functions.service.DocumentService;
import com.test.functions.service.DocumentServiceImpl;
import com.test.functions.util.EnvUtil;
import lombok.Generated;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.util.Optional;

@Slf4j
@Generated
public class HttpTriggerFunction {

    private static EnvUtil envUtil = new EnvUtil();
    private DocumentService documentService;

    final ObjectMapper objectMapper = new ObjectMapper();

    public HttpTriggerFunction() throws Exception {
        //String apimKey = envUtil.getProperty(EnvUtil.AppVar.APIM_SUBSCRIPTION_KEY);
        //String appConfigEndpoint = envUtil.getProperty(EnvUtil.AppVar.GATEWAY_URL) + envUtil.getProperty(EnvUtil.AppVar.WHATEVER);
        this.documentService = new DocumentServiceImpl();
    }

    public HttpTriggerFunction(DocumentService documentService) {
        this.documentService = documentService;
    }

    @FunctionName("hello")
    public HttpResponseMessage getHello(
            @HttpTrigger(
                    name = "getHello",
                    methods = {HttpMethod.GET},
                    route = "hello",
                    authLevel = AuthorizationLevel.ANONYMOUS)
                    HttpRequestMessage<Optional<String>> request) {
        HttpResponseMessage httpResponseMessage = new HttpResponseMessage() {
            @Override
            public HttpStatusType getStatus() {
                return HttpStatusType.custom(201);
            }

            @Override
            public String getHeader(String s) {
                return null;
            }

            @Override
            public Object getBody() {
                JSONObject helloBody = new JSONObject().put("message", "hello");
                try {
                    return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(helloBody);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        return httpResponseMessage;
    }
}

