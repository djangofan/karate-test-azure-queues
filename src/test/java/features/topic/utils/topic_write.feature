
@ignore
Feature: Reusable Function To Write To Topic

    Description: Function to write a document to ServiceBus topic.

    # Usage example:
    #  * def topic = read('classpath:features/utils/topic_write.feature')
    #  * def document_object =
    #  """
    #    {"v3DocId": "126291"}
    #  """
    #  * string document_string = document_object
    #  * def args_object = { doc: '#(document_string)', src: '#(source)' }
    #  * def response = call topic args_object
    #  * assert response.result == true

  Scenario: Write To Topic
    * eval if (servicebus_key == null) throw 'env variable for servicebus_key is missing'
    * def SBClientBase = Java.type('features.topic.utils.SBClientBase')
    * def result = SBClientBase.write(doc, src, global.sb_namespace, global.sb_clientid)
