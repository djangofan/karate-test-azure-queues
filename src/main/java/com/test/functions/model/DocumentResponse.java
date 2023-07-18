package com.test.functions.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Jacksonized
@JsonInclude
@Builder
@Generated
@JsonIgnoreProperties(ignoreUnknown = true)
public class DocumentResponse {

    public DocumentResponse(String body) {
        this.body = body;
    }

    @JsonProperty("document_id")
    private String document_id;
    @JsonProperty("entity_type")
    private String entity_type;
    @JsonProperty("document_type")
    private String document_type;
    @JsonProperty("document_file_name")
    private String document_file_name;
    @JsonProperty("code")
    private String code;
    @JsonProperty("body")
    private String body;

}
