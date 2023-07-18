package com.test.functions;

import com.github.javafaker.Faker;
import com.test.functions.model.DocumentRequest;

public class DataHelper {

    public static final Faker FAKER = new Faker();

    public static DocumentRequest getVelocityInboundRequestStub() {
        String policyId = FAKER.number().digits(6);
        String policyPlanId = "A";
        DocumentRequest inboundRequestStub =
                DocumentRequest.builder()
                        .entity_type("INSTITUTION")
                        .document_id("1398dc6b-dd38-4d16-bba3-722df590f1c7")
                        .document_description("Policy: 921142 - HI 1, Eff: 02012022")
                        .document_file_name("XX Certificate")
                        .carrier_id("SI")
                        .employer_id(FAKER.number().digits(5))
                        .policy_number(policyId)
                        .policy_plan_id(policyPlanId)
                        .document_description("Policy: " + policyId + " - " + policyPlanId + ", Eff: mmddyyyy")
                        .policy_version_number(FAKER.number().digits(10))
                        .build();
        return inboundRequestStub;
    }


}
