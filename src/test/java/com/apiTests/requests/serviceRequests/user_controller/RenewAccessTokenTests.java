package com.apiTests.requests.serviceRequests.user_controller;

import com.apiTests.models.user_controller.renewAccessToken.RenewAccessTokenResponse;
import com.apiTests.requests.serviceRequests.BaseTest;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.apiTests.constants.ContentType.*;
import static com.apiTests.constants.Endpoint.RENEW_ACCESS_TOKEN_ENDPOINT;
import static com.apiTests.constants.Language.*;
import static com.apiTests.requests.HelperMethod.requestBodyLoader;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class RenewAccessTokenTests extends BaseTest {

    public static String accessToken;

    // Logger for tracking actions and output
    private static final Logger logger = LogManager.getLogger(RenewAccessTokenTests.class);

    /**
     * Send a renew access token request and returns the response.
     *
     * @param statusCode      The expected status code of the response.
     * @param accessTokenPath Path to the accessToken that is uses in header.
     */
    @Step("the user tries to renew access token")
    public RenewAccessTokenResponse renewAccessToken(int statusCode, String accessTokenPath) {

        // Load the access token from the specified file path
        String accessToken = requestBodyLoader(accessTokenPath);

        // Send the renew access token request to the specified endpoint with headers
        Response response = given(spec)
                .when().header("Authorization", "Bearer " + accessToken)
                .header(language, en)
                .contentType(ContentType.JSON)
                .post(RENEW_ACCESS_TOKEN_ENDPOINT);

        // Validate the status code of the response
        response.then().statusCode(statusCode);
        // Retrieve the content type of the response
        String contentType = response.getContentType();

        // Log the response details
        logger.info("Response received: {}", response.asString());
        logger.info("Status Code: {}", response.getStatusCode());

        // Check if the content type is JSON and return the deserialized response
        if (contentType != null && contentType.contains(json)) {
            response.then().assertThat().body(matchesJsonSchemaInClasspath("renewAccessTokenResponseSchema.json"));
            return response.as(RenewAccessTokenResponse.class);
        } else {
            // Log an error if the content type is unexpected and return null
            logger.info("Unexpected content type: {}", contentType);
            return null;
        }
    }
}