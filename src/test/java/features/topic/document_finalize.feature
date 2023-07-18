@regression
@topic
Feature: Verify Processing Of Document Finalize Request From Topic

  Background:
    * configure report = { showLog: true, showAllSteps: true}
    * def topic_writer = read('classpath:features/topic/utils/topic_write.feature')
    * def input_reader = read('classpath:features/topic/utils/topic_read.feature')
    * def output_reader = read('classpath:features/topic/utils/topic_read.feature')
    * def doc_update = read('classpath:features/topic/utils/cosmos_status_update.feature')
    * configure readTimeout = 55000
    * eval
    """
    var tokResponse = karate.call('utils/oauth2.feature')
    var emptyResponse = {accessToken: ''}
    if (tokResponse) {
      karate.set('oauth', tokResponse)
    } else {
      karate.set('oauth', emptyResponse)
    }
    """
    * def bearer = cloud_envs.includes(env) ? 'Bearer ' + oauth.accessToken : ''
    * eval if (typeof subscription_key !== 'string') karate.fail('Missing env variable for subscription key.')
    * url global.base_url

  @bvt
  @positive
  @finalize
  Scenario: Test That pas-document-finalize-topic Topic Can Be Written To
      Business Rules:
        1. Verifies that the error document will be processed from the pas-document-finalize-topic topic.
        2. For test to succeed, the initial document state must be Finalized, not Draft nor Re-Finalizing.
        3. The end state of this test is that the document is Finalized
    # 1. query metadata db for refinalize request body
    * path global.meta_uri, "metaDoc"
    * header Content-Type = 'application/json'
    * header Authorization = bearer
    * header Ocp-Apim-Subscription-Key = subscription_key
    * request ""
    * params { v3DocId: "#(global.finalize_doc_id)" }
    * method get
    * status 200
    * def refinalize_doc_arg = response[0]
    #* refinalize_doc_arg.metadataMap.refinalizeHistory = []
    #* json results = call doc_update { document: '#(refinalize_doc_arg)' }
    # 2. re-open the test document
    * string refinalize_doc_arg_string = karate.pretty(refinalize_doc_arg)
    * def topic_setup = call topic_writer { doc: "#(refinalize_doc_arg_string)", src: "document-refinalize" }
    * print topic_setup
    * assert topic_setup.result == true
    # 3. wait 1 whole minute before testing Finalize inputs  TODO fix later to be dynamic wait
    * def sleep = function(millis){ java.lang.Thread.sleep(millis) }
    * sleep(60000)
    # 4. place finalize docId reference on topic to start Finalize test
    * def finalize_doc_arg = { v3DocId: "#(global.finalize_doc_id)" }
    * string finalize_doc_arg_string = karate.pretty(finalize_doc_arg)
    * def topic_response = call topic_writer { doc: "#(finalize_doc_arg_string)", src: "pas-document-finalize-topic" }
    * print topic_response
    When assert topic_response.result == true
    # 5. verify doc is put on subscription pas-document-finalize/test-pas-document-finalize-in
    * def args_object1 = { expected: "#(finalize_doc_arg_string)", topic: "pas-document-finalize-topic", sub: "test-pas-document-finalize-in" }
    * string args_object1_string = karate.pretty(args_object1)
    * print args_object1_string
    * def response1 = call input_reader args_object1
    * print response1
    Then assert response1.result.found == 'true'
    # 6.1. wait for processing before testing the outputs  TODO fix later to be dynamic wait
    * sleep(20000)
    # 7. after processing, verify doc on filenet-document-archive-topic/test-pas-document-finalize-out
    * string doc_out = { data:{ documentId: "#(global.finalize_doc_id)" }, eventType: "DOCUMENT_FINALIZE", metadataMap: { fileType: "pdf" } }
    * print doc_out
    * def args_object2 = { expected: "#(doc_out)", topic: "filenet-document-archive-topic", sub: "test-pas-document-finalize-out" }
    * print args_object2
    * def response2 = call output_reader args_object2
    * print response2
    Then assert response2.result.found == 'true'
    # 8. verify filenet document result queue
    * string fn_out = { documentId: "#(global.finalize_doc_id)", eventType: "DOCUMENT_FINALIZE", metadataMap: { fileType: "pdf" } }
    * print fn_out
    * def args_object3 = { expected: "#(fn_out)", topic: "filenet-document-archive-result", sub: "test-filenet-doc-archive-result-out" }
    * print args_object3
    * def response3 = call output_reader args_object3
    * print response3
    Then assert response3.result.found == 'true'
