package com.test.functions.service;

import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.test.functions.exception.DocumentException;
import com.test.functions.model.DocumentRequest;

public interface DocumentService {

    HttpResponseMessage processDocument(HttpRequestMessage<DocumentRequest> httpRequestMessage) throws DocumentException;

}
