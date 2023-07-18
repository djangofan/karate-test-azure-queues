
@ignore
Feature: Reusable Function To Call OAuth

  Description: Function to call OAuth2 endpoint for a JWT token

  # Usage example:
  #  * def authenticator = read('classpath:features/utils/oauth2.feature')
  #  * def oauth = call authenticator
  #  * def bearer = oauth.accessToken

  Scenario: OAuth Call
    * eval if (oauth2_id == null) throw 'env variable for sic_oauth_integration_client_id is missing'
    * eval if (oauth2_secret == null) throw 'env variable for sic_oauth_integration_client_secret is missing'
    * url global.oauth2_url
    * form field grant_type = 'client_credentials'
    * form field client_id = oauth2_id
    * form field client_secret = oauth2_secret
    * method post
    * status 200
    * def accessToken = response.access_token
