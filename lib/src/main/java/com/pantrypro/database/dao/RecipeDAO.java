package com.pantrypro.database.dao;

import com.dbclient.DBManager;
import com.pantrypro.DBRegistry;
import com.pantrypro.database.objects.recipe.Recipe;
import com.pantrypro.database.objects.recipe.RecipeDirection;
import com.pantrypro.database.objects.recipe.RecipeMeasuredIngredient;
import com.pantrypro.database.objects.recipe.RecipeTag;
import sqlcomponentizer.dbserializer.DBSerializerException;
import sqlcomponentizer.dbserializer.DBSerializerPrimaryKeyMissingException;
import sqlcomponentizer.preparedstatement.component.PSComponent;
import sqlcomponentizer.preparedstatement.component.condition.SQLOperatorCondition;
import sqlcomponentizer.preparedstatement.component.condition.SQLOperators;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class RecipeDAO {

    public static Long countForToday(Connection conn, Integer userID) throws DBSerializerException, SQLException, InterruptedException {
        // Get from date as yesterday's date to count all ideaRecipes after it
        LocalDateTime fromDate = LocalDateTime.now().minus(Duration.ofDays(1));

        // Build SQL Conditions
        SQLOperatorCondition userIDCondition = new SQLOperatorCondition(
                DBRegistry.Table.Recipe.user_id,
                SQLOperators.EQUAL,
                userID
        );
        SQLOperatorCondition dateCondition = new SQLOperatorCondition(
                DBRegistry.Table.Recipe.creation_date,
                SQLOperators.GREATER_THAN,
                fromDate
        );
        List<PSComponent> sqlConditions = List.of(
                userIDCondition,
                dateCondition
        );

        // Count where userID
        return DBManager.countObjectWhere(
                conn,
                Recipe.class,
                sqlConditions
        );
    }

    public static void insert(Connection conn, Recipe recipe) throws DBSerializerPrimaryKeyMissingException, DBSerializerException, SQLException, InterruptedException, InvocationTargetException, IllegalAccessException {
        DBManager.insert(conn, recipe);
    }

    public static void insertMeasuredIngredients(Connection conn, List<RecipeMeasuredIngredient> measuredIngredients) throws DBSerializerPrimaryKeyMissingException, DBSerializerException, SQLException, InterruptedException, InvocationTargetException, IllegalAccessException {
        for (RecipeMeasuredIngredient measuredIngredient: measuredIngredients)
            insertMeasuredIngredient(conn, measuredIngredient);
    }

    public static void insertMeasuredIngredient(Connection conn, RecipeMeasuredIngredient measuredIngredient) throws DBSerializerPrimaryKeyMissingException, DBSerializerException, SQLException, InterruptedException, InvocationTargetException, IllegalAccessException {
        DBManager.insert(conn, measuredIngredient);
    }

    public static void insertDirections(Connection conn, List<RecipeDirection> directions) throws DBSerializerPrimaryKeyMissingException, DBSerializerException, SQLException, InterruptedException, InvocationTargetException, IllegalAccessException {
        for (RecipeDirection direction: directions)
            insertDirection(conn, direction);
    }

    public static void insertDirection(Connection conn, RecipeDirection direction) throws DBSerializerPrimaryKeyMissingException, DBSerializerException, SQLException, InterruptedException, InvocationTargetException, IllegalAccessException {
        DBManager.insert(conn, direction);
    }

    public static void insertTags(Connection conn, List<RecipeTag> tags) throws DBSerializerPrimaryKeyMissingException, DBSerializerException, SQLException, InterruptedException, InvocationTargetException, IllegalAccessException {
        for (RecipeTag tag: tags)
            insertTag(conn, tag);
    }

    public static void insertTag(Connection conn, RecipeTag tag) throws DBSerializerPrimaryKeyMissingException, DBSerializerException, SQLException, InterruptedException, InvocationTargetException, IllegalAccessException {
        DBManager.insert(conn, tag);
    }

    public static Recipe get(Connection conn, Integer recipeID) throws DBSerializerException, SQLException, InterruptedException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        // Get all recipes for ideaID, and if there is at least one, return the first
        List<Recipe> allRecipesForID = DBManager.selectAllWhere(
                conn,
                Recipe.class,
                DBRegistry.Table.Recipe.recipe_id,
                SQLOperators.EQUAL,
                recipeID
        );

        if (allRecipesForID.size() > 0)
            return allRecipesForID.get(0);

        return null;
    }

    public static List<RecipeMeasuredIngredient> getMeasuredIngredients(Connection conn, Integer recipeID) throws DBSerializerException, SQLException, InterruptedException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        // TODO: This can be an inner join from ideaID - SELECT * FROM pantrypro_schema.Recipe INNER JOIN pantrypro_schema.RecipeMeasuredIngredient ON pantrypro_schema.Recipe.recipe_id = pantrypro_schema.RecipeMeasuredIngredient.recipe_id WHERE idea_id = 72226; Maybe how this will work is that instead of SELECT * it uses SELECT fromObjectField1, fromObjectField2,...,fromObjectFieldn
        // Get all recipe measured ingredients for recipeID
        List<RecipeMeasuredIngredient> recipeMeasuredIngredients = DBManager.selectAllWhere(
                conn,
                RecipeMeasuredIngredient.class,
                DBRegistry.Table.RecipeMeasuredIngredient.recipe_id,
                SQLOperators.EQUAL,
                recipeID
        );

        return recipeMeasuredIngredients;
    }

    public static List<RecipeDirection> getDirections(Connection conn, Integer recipeID) throws DBSerializerException, SQLException, InterruptedException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        // Get all directions for recipeID
        List<RecipeDirection> recipeDirections = DBManager.selectAllWhere(
                conn,
                RecipeDirection.class,
                DBRegistry.Table.RecipeMeasuredIngredient.recipe_id,
                SQLOperators.EQUAL,
                recipeID
        );

        return recipeDirections;
    }

    public static void updateEstimatedServings(Connection conn, Integer recipeID, Integer estimatedServings) throws DBSerializerException, SQLException, InterruptedException {
        DBManager.updateWhere(
                conn,
                Recipe.class,
                DBRegistry.Table.Recipe.estimated_servings,
                estimatedServings,
                DBRegistry.Table.Recipe.recipe_id,
                SQLOperators.EQUAL,
                recipeID
        );
    }

    public static void updateEstimatedTotalCalories(Connection conn, Integer recipeID, Integer estimatedTotalCalories) throws DBSerializerException, SQLException, InterruptedException {
        DBManager.updateWhere(
                conn,
                Recipe.class,
                DBRegistry.Table.Recipe.estimated_total_calories,
                estimatedTotalCalories,
                DBRegistry.Table.Recipe.recipe_id,
                SQLOperators.EQUAL,
                recipeID
        );
    }

    public static void updateEstimatedTotalMinutes(Connection conn, Integer recipeID, Integer estimatedTotalMinutes) throws DBSerializerException, SQLException, InterruptedException {
        DBManager.updateWhere(
                conn,
                Recipe.class,
                DBRegistry.Table.Recipe.estimated_total_minutes,
                estimatedTotalMinutes,
                DBRegistry.Table.Recipe.recipe_id,
                SQLOperators.EQUAL,
                recipeID
        );
    }

    public static void updateFeasibility(Connection conn, Integer recipeID, Integer feasibility) throws DBSerializerException, SQLException, InterruptedException {
        DBManager.updateWhere(
                conn,
                Recipe.class,
                DBRegistry.Table.Recipe.feasibility,
                feasibility,
                DBRegistry.Table.Recipe.recipe_id,
                SQLOperators.EQUAL,
                recipeID
        );
    }

    public static void updateModificationDate(Connection conn, Integer recipeID) throws DBSerializerException, SQLException, InterruptedException {
        updateModificationDate(conn, recipeID, LocalDateTime.now());
    }

    public static void updateModificationDate(Connection conn, Integer recipeID, LocalDateTime modifyDate) throws DBSerializerException, SQLException, InterruptedException {
        DBManager.updateWhere(
                conn,
                Recipe.class,
                DBRegistry.Table.Recipe.modify_date,
                modifyDate,
                DBRegistry.Table.Recipe.recipe_id,
                SQLOperators.EQUAL,
                recipeID
        );
    }

    public static void updateDirections(Connection conn, Integer recipeID, List<RecipeDirection> recipeDirections) throws DBSerializerException, SQLException, InterruptedException, DBSerializerPrimaryKeyMissingException, InvocationTargetException, IllegalAccessException {
        // Delete all directions for recipeID
        deleteAllDirections(conn, recipeID);

        // Insert new directions using recipeID
        for (RecipeDirection recipeDirection: recipeDirections) {
            // Set recipeID as the one in the function parameter as the expected functionality is to associate it with this recipeDirection and in the current use case the recipeID should be null at this point anyways
            recipeDirection.setRecipeID(recipeID);
            DBManager.insert(conn, recipeDirection);
        }
    }

    public static void updateMeasuredIngredients(Connection conn, Integer recipeID, List<RecipeMeasuredIngredient> measuredIngredients) throws DBSerializerException, SQLException, InterruptedException, DBSerializerPrimaryKeyMissingException, InvocationTargetException, IllegalAccessException {
        // Delete all measured ingredients for recipeID
        deleteAllMeasuredIngredients(conn, recipeID);

        // Insert new measured ingredients using recipeID
        for (RecipeMeasuredIngredient measuredIngredient: measuredIngredients) {
            // Set recipeID as the one in the function parameter as the expected functionality is to associate it with this measuredIngredient and in the current use case the recipeID should be null at this point anyways
            measuredIngredient.setRecipeID(recipeID);
            DBManager.insert(conn, measuredIngredient);
        }
    }

//    public static Integer countForToday(Integer userID) throws DBSerializerException, SQLException, InterruptedException {
//        // Get from date as yesterday's date to count all ideaRecipes after it
//        LocalDateTime fromDate = LocalDateTime.now().minus(Duration.ofDays(1));
//
//        // Count where userID
//        return countObjectByColumnWhere(
//                Recipe.class,
//                DBRegistry.Table.Recipe.recipe_id,
//                DBRegistry.Table.Recipe.date,
//                SQLOperators.GREATER_THAN,
//                fromDate
//        );
//    }

    private static void deleteAllDirections(Connection conn, Integer recipeID) throws DBSerializerException, SQLException, InterruptedException {
        DBManager.deleteWhere(
                conn,
                RecipeDirection.class,
                DBRegistry.Table.RecipeDirection.recipe_id,
                SQLOperators.EQUAL,
                recipeID
        );
    }

    private static void deleteAllMeasuredIngredients(Connection conn, Integer recipeID) throws DBSerializerException, SQLException, InterruptedException {
        DBManager.deleteWhere(
                conn,
                RecipeMeasuredIngredient.class,
                DBRegistry.Table.RecipeMeasuredIngredient.recipe_id,
                SQLOperators.EQUAL,
                recipeID
        );
    }

    public static boolean isUserAssociatedWithRecipe(Connection conn, Integer userID, Integer recipeID) throws DBSerializerException, SQLException, InterruptedException {
        // Count associated recipes with recipe ID and user ID to verify user recipe association
        Long userRecipeAssociationCount = DBManager.countObjectWhere(
                conn,
                Recipe.class,
                Map.of(
                        DBRegistry.Table.Recipe.user_id, userID,
                        DBRegistry.Table.Recipe.recipe_id, recipeID
                ),
                SQLOperators.EQUAL
        );

        return userRecipeAssociationCount > 0;
    }

}