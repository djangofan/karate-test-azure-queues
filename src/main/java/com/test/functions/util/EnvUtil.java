package com.test.functions.util;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.FileInputStream;


/**
 * Will make a call to System.getenv , and if it fails, will fail-over to local.settings.json .
 * Uses this class when you need support for local.settings.json in your project configs.
 */
@Slf4j
@NoArgsConstructor
public final class EnvUtil {

    public static final String LOCAL_SETTINGS_FILE = "local.settings.json";
    public static final String TEST_LOCAL_SETTINGS_FILE = "local.settings.json.sample";
    private static final String DEFAULT_LOCAL_SETTINGS = "{\"IsEncrypted\": false,\"Values\": {}}";
    public static final String ENV_SETTINGS_KEY = "Values";
    private static final String ENCODING = "UTF-8";

    private String localSettingsFile;

    public EnvUtil(String fileName) {
        this.localSettingsFile = fileName;
    }

    /*  Variables for queues and topics generally won't be in this list since they are annotated variables, but there are exceptions to the rule. */
    public enum AppVar {
        APIM_SUBSCRIPTION_KEY,
        ServiceBusConnection__fullyQualifiedNamespace,
        TOPIC0_NAME,
        TOPIC0_SUBSCRIPTION_NAME_IN,
        TOPIC0_SUBSCRIPTION_NAME_OUT,
        TOPIC1_NAME,
        TOPIC1_SUBSCRIPTION_NAME_IN,
        TOPIC1_SUBSCRIPTION_NAME_OUT,
        GATEWAY_URL
    }

    public String getProperty(AppVar propertyName) {
        Optional<String> optionalConnectionString = Optional.ofNullable(System.getenv(propertyName.name()));
        return optionalConnectionString.orElse(readPropertyFromLocalConfig(propertyName.name(), ENV_SETTINGS_KEY));
    }

    public String readPropertyFromLocalConfig(String propertyName, String parentKey) {
        String activeLocalSettings = Objects.isNull(localSettingsFile) ? LOCAL_SETTINGS_FILE : localSettingsFile;
        try (FileInputStream fis = new FileInputStream(activeLocalSettings)) {
            Optional<String> optionalData = Optional.ofNullable(IOUtils.toString(fis, ENCODING));
            JSONObject jsonObject = new JSONObject(optionalData.orElse(DEFAULT_LOCAL_SETTINGS)).getJSONObject(parentKey);
            if (jsonObject.has(propertyName)) {
                return (String)jsonObject.get(propertyName);
            }
            return StringUtils.EMPTY;
        } catch (JSONException | IOException e) {
            log.debug("ENV property '%s' is not defined in file '%s'.  Must load from system environment instead.", propertyName, localSettingsFile);
        }
        return StringUtils.EMPTY;
    }

}
