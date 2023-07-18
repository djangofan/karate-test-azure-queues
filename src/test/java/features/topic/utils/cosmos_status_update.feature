
@ignore
Feature: Reusable Function To Update Status Of A Record In Cosmos

    Description: Function To Update Status Of A Record In Cosmos

    # Usage example:
    #  * def status_update = read('classpath:features/utils/cosmos_status_update.feature')
    #  * json results = call status_update { document: '#(document)' }

  Scenario: Update Status Of A Record In Cosmos
    * url global.base_url
    * path global.meta_uri, 'metaDoc'
    * header Content-Type = 'application/json'
    * header Authorization = bearer
    * header Ocp-Apim-Subscription-Key = subscription_key
    * header Accept = 'application/json'
    * print "Updated doc: #(document)"
    * request document
    * method put
    * status 200
