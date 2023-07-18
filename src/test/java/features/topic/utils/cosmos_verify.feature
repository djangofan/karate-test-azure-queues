
@ignore
Feature: Reusable Function To Verify Cosmos Error Log Data

    Description: Function to read cosmos errors table

    # Usage example:
    #  * def cosmos_verify = read('classpath:features/utils/cosmos_verify.feature')
    #  * def args_object2 = { doc_id: '#(doc_id)' }
    #  * json results = call cosmos_verify args_object2
    #  * json arr = results.result
    #  And assert karate.sizeOf(arr) > 0
    #  * json first_result = arr[0]
    #  And match first_result.documentId == expected_doc_id

  Scenario: Azure Cosmos Read Data And Return Result Array
    * eval if (cosmos_key == null) throw 'env variable for cosmos_key is missing'
    * def CosmosReadClient = Java.type('features.topic.utils.CosmosReadClient')
    * def result = CosmosReadClient.searchCosmosDocId(doc_id, global.cosmos_url, 24, 5, 1)
