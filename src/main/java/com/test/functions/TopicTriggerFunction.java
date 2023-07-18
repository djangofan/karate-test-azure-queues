package com.test.functions;

import com.microsoft.azure.functions.OutputBinding;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.ServiceBusTopicOutput;
import com.microsoft.azure.functions.annotation.ServiceBusTopicTrigger;
import com.test.functions.service.DocumentService;
import com.test.functions.service.DocumentServiceImpl;
import lombok.Generated;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Generated
public class TopicTriggerFunction {

    private DocumentService documentsService;

    public TopicTriggerFunction() throws Exception {
        this.documentsService = new DocumentServiceImpl();
        log.info("TopicTriggerFunction constructor");
    }

    @FunctionName("processDocument")
    public void processDocument(
            @ServiceBusTopicTrigger(
                    name = "input0Trigger",
                    topicName = "%TOPIC0_NAME%",
                    subscriptionName = "%TOPIC0_SUBSCRIPTION_NAME_IN%",
                    connection = "ServiceBusConnection") String message,
            @ServiceBusTopicOutput(
                    name = "outMessage",
                    topicName = "%TOPIC1_NAME%",
                    subscriptionName = "%TOPIC1_SUBSCRIPTION_NAME_OUT%",
                    connection = "ServiceBusConnection") OutputBinding<String> outMessage) {
            this.documentsService.processDocument(message, outMessage);
    }

}
