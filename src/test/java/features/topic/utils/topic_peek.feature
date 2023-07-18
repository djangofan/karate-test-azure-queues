
@ignore
Feature: Reusable Function To Fast Peek At ServiceBus Topic

    Description: Function to write a document to Cosmos topic

    # Usage example:
    #  * def topic_peek = read('classpath:features/topic/utils/topic_peek.feature')
    #  * def args_object1 = { doc: '999121212', topic: 'pas-document-status-topic', sub: 'pas-fn-v3locity-documents-api' }
    #  * def response1 = call topic_peek args_object1
    #  * assert response1.result == true

  Scenario: Fast Peek At ServiceBus Topic
    * def tries = 40
    * eval if (servicebus_key == null) throw 'env variable for servicebus_key is missing'
    * def SBClientPeek = Java.type('features.topic.utils.SBClientPeek')
    * def result = SBClientPeek.topicPeek(doc, topic, sub, tries, global.sb_namespace, global.sb_clientid)
