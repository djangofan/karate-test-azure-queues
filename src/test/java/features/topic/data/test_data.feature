@ignore
Feature: Generate An Input Document For Queue
    NOTE: JSON Keys must always be quoted
  Scenario:
    * def Faker = Java.type('com.github.javafaker.Faker')
    * def faker = new Faker()
    * def doc_id = "919" + faker.numerify("######")
    * def input_doc =
    """
    {
      "entityType": "INSTITUTION",
      "docId": "#(doc_id)",
      "docType": "AI_CERTIFICATE",
      "documentFileName": "Test",
      "carrierId": "423611",
      "employerId": "14692",
      "policyNumber": "999990",
      "policyPlanId": "A",
      "documentDescription": "Document Description"
    }
    """
    * string document = input_doc
