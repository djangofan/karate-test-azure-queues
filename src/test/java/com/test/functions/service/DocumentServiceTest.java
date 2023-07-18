package com.test.functions.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.functions.exception.DocumentException;
import com.test.functions.model.DocumentRequest;
import com.test.functions.util.EnvUtil;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    private DocumentServiceImpl velocityDocumentsServiceUnderTest;
    private DocumentServiceImpl velocityDocumentsServiceUnderTestMockMapper;
    private EnvUtil envUtil = new EnvUtil();
    private String emptyResponseBodyStub = StringUtil.EMPTY_STRING;

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    final ObjectMapper mockMapper = mock(ObjectMapper.class);

    @BeforeEach
    public void setUp() {

    }

    @Test
    @DisplayName("velocityUpdateDocumentMetadata, Should not throw error")
    public void testUpdateDocumentMetadata_Payload() throws DocumentException {
        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put("status", "S");

        DocumentRequest documentMetadataUpdate = new DocumentRequest("");

        documentMetadataUpdate.setDocumentId("O123456");
        documentMetadataUpdate.setMetadataMap(metadata);

        when(velocityFacadeFunctionsClientMock.updateDocumentMetadata(any())).thenReturn(null);

        assertDoesNotThrow(() -> velocityDocumentsServiceUnderTest.velocityUpdateDocumentMetadata(OBJECT_MAPPER.writeValueAsString(documentMetadataUpdate)));
    }

}

