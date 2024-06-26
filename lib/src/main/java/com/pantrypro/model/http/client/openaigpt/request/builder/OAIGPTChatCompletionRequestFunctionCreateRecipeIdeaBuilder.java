package com.pantrypro.model.http.client.openaigpt.request.builder;

import com.oaigptconnector.model.request.chat.completion.function.OAIGPTChatCompletionRequestFunction;
import com.oaigptconnector.model.request.chat.completion.function.objects.OAIGPTChatCompletionRequestFunctionObjectArray;
import com.oaigptconnector.model.request.chat.completion.function.objects.OAIGPTChatCompletionRequestFunctionObjectObject;
import com.oaigptconnector.model.request.chat.completion.function.objects.OAIGPTChatCompletionRequestFunctionObjectString;
import com.pantrypro.model.http.client.openaigpt.request.OAIGPTChatCompletionRequestFunctionObjectPropertiesCreateRecipeIdea;

import java.util.List;

public class OAIGPTChatCompletionRequestFunctionCreateRecipeIdeaBuilder {

//    private static final String defaultFunctionDescription = "Creates a recipe idea from ingredients, adding as necessary";
    private static final String defaultIngredientsDescription = "All of the ingredients needed, no measurements, adding as necessary";
//    private static final String defaultEquipmentDescription = "The equipment needed to make the recipe";
    private static final String defaultNameDescription = "An interesting and fitting name for the recipe";
    private static final String defaultSummaryDescription = "A short 10 word engaging summary for the recipe";
    private static final String defaultCuisineTypeDescription = "A 1-5 word cuisine type for the recipe";

    public static OAIGPTChatCompletionRequestFunction build(String functionDescription) {
        return build(functionDescription, defaultIngredientsDescription, defaultNameDescription, defaultSummaryDescription, defaultCuisineTypeDescription);
    }

    public static OAIGPTChatCompletionRequestFunction build(String functionDescription, String ingredientsDescription, String nameDescription, String summaryDescription, String cuisineTypeDescription) {
        // Create the OAIGPTChatCompletionRequestFunctionObjectPropertiesCreateRecipeIdea
        OAIGPTChatCompletionRequestFunctionObjectArray ingredients, equipment;
        OAIGPTChatCompletionRequestFunctionObjectString name, short10To40WordEngagingInterestingSummary, cuisineType;

        ingredients = new OAIGPTChatCompletionRequestFunctionObjectArray(
                ingredientsDescription,
                new OAIGPTChatCompletionRequestFunctionObjectString()
        );

//        equipment = new OAIGPTChatCompletionRequestFunctionObjectArray(
//                equipmentDescription,
//                new OAIGPTChatCompletionRequestFunctionObjectString()
//        );

        name = new OAIGPTChatCompletionRequestFunctionObjectString(nameDescription);
        short10To40WordEngagingInterestingSummary = new OAIGPTChatCompletionRequestFunctionObjectString(summaryDescription);
        cuisineType = new OAIGPTChatCompletionRequestFunctionObjectString(cuisineTypeDescription);

        // Create OAIGPTChatCompletionRequestFunctionObjectPropertiesCreateRecipeIdea
        OAIGPTChatCompletionRequestFunctionObjectPropertiesCreateRecipeIdea r = new OAIGPTChatCompletionRequestFunctionObjectPropertiesCreateRecipeIdea(
                ingredients,
                name,
                short10To40WordEngagingInterestingSummary,
                cuisineType
        );

        // Create the OAIGPTChatCompletionRequestFunctionObjectObject as container with required functions
        OAIGPTChatCompletionRequestFunctionObjectObject rContainer = new OAIGPTChatCompletionRequestFunctionObjectObject(r, null, List.of(
                "ingredients",
//                "equipment",
                "name",
                "short10To40WordEngagingInterestingSummary",
                "cuisineType"
        ));

        // Create OAIGPTChatCompletionRequestFunction
        OAIGPTChatCompletionRequestFunction rFunction = new OAIGPTChatCompletionRequestFunction(
                "create_recipe_idea",
                functionDescription,
                rContainer
        );

        return rFunction;
    }

}
