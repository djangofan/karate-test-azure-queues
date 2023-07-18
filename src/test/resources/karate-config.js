
function fn() {
    const env = karate.env;

    const config = {
        env: env,
        oauth2_secret: karate.properties['sic_oauth_integration_client_secret'],
        oauth2_id: karate.properties['sic_oauth_integration_client_id'],
        subscription_key: karate.properties['pas_function_subscription_key'],
        cosmos_key: karate.properties['cosmos_key'],
        servicebus_key: karate.properties['servicebus_key'],
        global: read("classpath:itGlobals.json")[env],
        cloud_envs: ["playground", "dev", "nonprod", "uat"]
    };

    return config;
}
