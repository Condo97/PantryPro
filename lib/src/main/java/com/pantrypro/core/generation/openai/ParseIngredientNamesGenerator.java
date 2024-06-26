package com.pantrypro.core.generation.openai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oaigptconnector.core.OpenAIGPTHttpsClientHelper;
import com.oaigptconnector.model.exception.OpenAIGPTException;
import com.oaigptconnector.model.generation.OpenAIGPTModels;
import com.oaigptconnector.model.request.chat.completion.OAIGPTChatCompletionRequest;
import com.oaigptconnector.model.request.chat.completion.function.OAIGPTChatCompletionRequestFunction;
import com.oaigptconnector.model.response.chat.completion.http.OAIGPTChatCompletionResponse;
import com.pantrypro.Constants;
import com.pantrypro.core.generation.OpenAIGPTChatCompletionRequestFactory;
import com.pantrypro.keys.Keys;
import com.pantrypro.model.http.client.openaigpt.request.builder.OAIGPTChatCompletionRequestFunctionParseIngredientNamesBuilder;
import com.pantrypro.model.http.client.openaigpt.response.functioncall.OAIGPTFunctionCallResponseParseIngredientNames;
import sqlcomponentizer.dbserializer.DBSerializerException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

public class ParseIngredientNamesGenerator {

    public static OAIGPTFunctionCallResponseParseIngredientNames getParsedIngredientNames(List<String> ingredientsWithMeasurements, int characterLimit, int responseTokenLimit) throws DBSerializerException, SQLException, InterruptedException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException, OpenAIGPTException, IOException {
        // Create parse ingredient names
        OAIGPTChatCompletionRequestFunction parseIngredientNamesFunction = OAIGPTChatCompletionRequestFunctionParseIngredientNamesBuilder.build();

        // Create get ingredients without measurements input
        String parseIngredientNamesInput = parseInput(ingredientsWithMeasurements);

        // Create request
        OAIGPTChatCompletionRequest request = OpenAIGPTChatCompletionRequestFactory.with(
                parseIngredientNamesInput,
                characterLimit,
                OpenAIGPTModels.GPT_4_0613,
                Constants.DEFAULT_TEMPERATURE,
                responseTokenLimit,
                parseIngredientNamesFunction
        );

        // Get response from Open AI
        OAIGPTChatCompletionResponse response = OpenAIGPTHttpsClientHelper.postChatCompletion(request, Keys.openAiAPI);

        // Parse response's choice message function call arguments to OAIGPTFunctionCallResponseParseIngredientNames and return
        OAIGPTFunctionCallResponseParseIngredientNames functionCallResponse = parseParseIngredientNamesResponse(response.getChoices()[0].getMessage().getFunction_call().getArguments());

        return functionCallResponse;
    }

    private static String parseInput(List<String> ingredientsWithMeasurements) {
        // ingredientWithMeasurement, ingredientWithMeasurement,..., ingredientWithMeasurement
        final String commaSpaceDelimiterString = ", ";

        StringBuilder sb = new StringBuilder();

        ingredientsWithMeasurements.forEach(ingredientWithMeasurement -> {
            sb.append(ingredientWithMeasurement);
            sb.append(commaSpaceDelimiterString);
        });

        // If the end of sb is commaSpaceDelimiterString, remove it
        if (sb.substring(sb.length() - commaSpaceDelimiterString.length(), sb.length()).equals(commaSpaceDelimiterString)) {
            sb.delete(sb.length() - commaSpaceDelimiterString.length(), sb.length());
        }

        return sb.toString();
    }

    private static OAIGPTFunctionCallResponseParseIngredientNames parseParseIngredientNamesResponse(String functionCall) throws IOException {
        // Parse with object mapper
        return new ObjectMapper().readValue(functionCall, OAIGPTFunctionCallResponseParseIngredientNames.class);
    }

}
