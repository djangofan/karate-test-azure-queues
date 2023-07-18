package com.test.functions.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Getter
@Setter
@ToString
@AllArgsConstructor
@EqualsAndHashCode
@Jacksonized
@JsonInclude
@Builder
@Generated
@JsonIgnoreProperties(ignoreUnknown = true)
public class DocumentRequest {

    @JsonProperty("document_id")
    private String document_id;
    @JsonProperty("entity_type")
    private String entity_type;
    @JsonProperty("document_type")
    private String document_type;
    @JsonProperty("document_file_name")
    private String document_file_name;
    @JsonProperty("body")
    private String body;

}
