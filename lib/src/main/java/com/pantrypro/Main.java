package com.pantrypro;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oaigptconnector.model.exception.OpenAIGPTException;
import com.pantrypro.connectionpool.SQLConnectionPoolInstance;
import com.pantrypro.core.Server;
import com.pantrypro.keys.Keys;
import com.pantrypro.model.exceptions.ResponseStatusException;
import com.pantrypro.model.http.server.ResponseStatus;
import com.pantrypro.model.http.server.response.BodyResponse;
import com.pantrypro.model.http.server.response.GetIAPStuffResponse;
import com.pantrypro.model.http.server.response.GetImportantConstantsResponse;

import java.sql.DriverManager;
import java.sql.SQLException;

import static spark.Spark.*;

public class Main {

    private static final int MAX_THREADS = 4;
    private static final int MIN_THREADS = 1;
    private static final int TIMEOUT_MS = -1; //30000;

    private static final int DEFAULT_PORT = 800;

    public static void main(String... args) throws SQLException {
        // Set up MySQL Driver
        try {
            DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Configure web sockets
        configureWebSockets();

        // Set up SQLConnectionPoolInstance
        SQLConnectionPoolInstance.create(Constants.MYSQL_URL, Keys.MYSQL_USER, Keys.MYSQL_PASS, MAX_THREADS * 4);

        // Set up Policy static file location
        staticFiles.location("/policies");

        // Set up Spark thread pool and port
//        threadPool(MAX_THREADS, MIN_THREADS, TIMEOUT_MS);
        port(DEFAULT_PORT);

        // Set up SSL
        secure("chitchatserver.com.jks", Keys.sslPassword, null, null);

        // Set up https v1 path
        path("/v1", () -> configureHttpEndpoints());

        // Set up https dev path
        path("/dev", () -> configureHttpEndpoints(true));

        // Set up legacy / path, though technically I think configureHttp() can be just left plain there in the method without the path call
        configureHttpEndpoints();

        // Exception Handling
        exception(ResponseStatusException.class, (e, req, res) -> {
            // If it is a ResponseStatusException, then send the response status to the client
            res.body(Server.getSimpleExceptionHandlerResponseStatusJSON(e.getResponseStatus(), e.getResponseMessage()));
        });

        exception(JsonMappingException.class, (error, req, res) -> {
            System.out.println("The request: " + req.body());
            error.printStackTrace();

            res.body(Server.getSimpleExceptionHandlerResponseStatusJSON(ResponseStatus.JSON_ERROR, "JSON Format Issue"));
        });

        exception(OpenAIGPTException.class, (error, req, res) -> {
            System.out.println("The request: " + req.body());
            error.printStackTrace();

            res.body(Server.getSimpleExceptionHandlerResponseStatusJSON(ResponseStatus.OAIGPT_ERROR, "Issue with our GPT provider."));
        });

        exception(IllegalArgumentException.class, (error, req, res) -> {
            System.out.println("The request: " + req.body());
            error.printStackTrace();

            res.body(Server.getSimpleExceptionHandlerResponseStatusJSON(ResponseStatus.ILLEGAL_ARGUMENT, "Illegal argument"));
        });

        exception(Exception.class, (error, req, res) -> {
            error.printStackTrace();
            System.out.println("The request: " + req.body());
            error.printStackTrace();

            res.body(Server.getSimpleExceptionHandlerResponseStatusJSON(ResponseStatus.UNHANDLED_ERROR, "Unhandled error! :O"));
        });

        // Handle not found (404)
        notFound((req, res) -> {
            System.out.println("The request: " + req.body());
            System.out.println(req.uri() + " 404 Not Found!");

            System.out.println(activeThreadCount());
            res.status(404);
            return "<html><a href=\"" + Constants.SHARE_URL + "\">Download WriteSmith</a></html>";
        });
    }

    private static void configureWebSockets() {
        // TODO: Do constants and make this better :O
        /* v1 */
        final String v1Path = "/v1";

        /* dev */
        final String devPath = "/dev";

    }

    private static void configureHttpEndpoints() {
        configureHttpEndpoints(false);
    }

    private static void configureHttpEndpoints(boolean dev) {
        // POST Functions
        post(Constants.URIs.CATEGORIZE_INGREDIENTS, Server.Func::categorizeIngredients);
        post(Constants.URIs.CREATE_RECIPE_IDEA, Server.Func::createRecipeIdea);
        post(Constants.URIs.MAKE_RECIPE, Server.Func::makeRecipe);
        post(Constants.URIs.REGENERATE_RECIPE_DIRECTIONS_AND_IDEA_RECIPE_INGREDIENTS, Server.Func::regenerateRecipeDirectionsAndIdeaRecipeIngredients);
        post(Constants.URIs.TAG_RECIPE_IDEA, Server.Func::tagRecipeIdea);

        post(Constants.URIs.GET_ALL_TAGS_URI, Server::getAllTags);
        post(Constants.URIs.GET_IS_PREMIUM_URI, Server::getIsPremium);
        post(Constants.URIs.GET_REMAINING_URI, Server::getRemainingIdeaRecipes);
        post(Constants.URIs.REGISTER_TRANSACTION_URI, Server::registerTransaction);
        post(Constants.URIs.REGISTER_USER_URI, Server::registerUser);
        post(Constants.URIs.VALIDATE_AUTH_TOKEN_URI, Server::validateAuthToken);

        // Get Constants
        post(Constants.URIs.GET_IMPORTANT_CONSTANTS_URI, (req, res) -> new ObjectMapper().writeValueAsString(new BodyResponse(ResponseStatus.SUCCESS, new GetImportantConstantsResponse())));
        post(Constants.URIs.GET_IAP_STUFF_URI, (req, res) -> new ObjectMapper().writeValueAsString(new BodyResponse(ResponseStatus.SUCCESS, new GetIAPStuffResponse())));

        // dev functions
        if (dev) {

        }
    }


}
