package com.test.functions.util;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EnvUtilTest {
    private static final String TEST_SETTINGS_FILE = "local.settings.json.sample";

    private String stubInvalidJsonSubNode = "null";

    private EnvUtil classUnderTest = new EnvUtil(TEST_SETTINGS_FILE);

    @Test
    public void testGetPropertyWithException() throws Exception {
        assertTrue(isBlank(classUnderTest.readPropertyFromLocalConfig(EnvUtil.AppVar.TOPIC0.name(), stubInvalidJsonSubNode)));
    }

    @Test
    public void testGetPropertyNotExist() throws Exception {
        assertTrue(isBlank(classUnderTest.readPropertyFromLocalConfig(EnvUtil.AppVar.CODE_ERROR.name() + "X", EnvUtil.ENV_SETTINGS_KEY)));
    }

    @DisplayName("Should pass non-null enum values as method parameters")
    @ParameterizedTest(name = "Iteration {index} => input = ''{0}''")
    @EnumSource(EnvUtil.AppVar.class)
    public void testGetProperty(EnvUtil.AppVar appVar) throws Exception {
        final EnvironmentVariables env = new EnvironmentVariables();
        env.set(appVar, appVar + "_value").execute(() -> {
            final String property = classUnderTest.getProperty(appVar);
            assertTrue(StringUtils.isNotBlank(property));
        });
    }

}
