
@ignore
Feature: Reusable Function To Read ServiceBus Topic Test Input

    Description: Function to read a document from Topic Test Input

    # Usage example:
    #  * def input_reader = read('classpath:features/topic/utils/topic_read.feature')
    #  * def doc_in = { v3DocId: '999121212' }
    #  * def args_object1 = { expected: '#(doc_in)', topic: 'pas-document-finalize-topic', sub: 'test-pas-document-finalize-in' }
    #  * def response1 = call input_reader args_object1
    #  * assert response1.result == true

  Scenario: Read ServiceBus Topic
    * eval if (servicebus_key == null) throw 'env variable for servicebus_key is missing'
    * def SBClientReader = Java.type('features.topic.utils.SBClientReader')
    * def result = SBClientReader.topicReadAll(expected, topic, sub, global.sb_namespace, global.sb_clientid)
