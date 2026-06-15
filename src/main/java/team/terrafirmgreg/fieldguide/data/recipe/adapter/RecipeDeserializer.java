package team.terrafirmgreg.fieldguide.data.recipe.adapter;

import com.google.gson.*;
import team.terrafirmgreg.fieldguide.data.recipe.Recipe;
import team.terrafirmgreg.fieldguide.data.recipe.ShapedCraftingRecipe;
import team.terrafirmgreg.fieldguide.data.recipe.ShapelessCraftingRecipe;
import team.terrafirmgreg.fieldguide.data.recipe.misc.DryingRecipe;
import team.terrafirmgreg.fieldguide.data.recipe.misc.UnknownRecipe;
import team.terrafirmgreg.fieldguide.data.recipe.misc.WaterflasksHealRecipe;
import team.terrafirmgreg.fieldguide.data.recipe.tfc.*;

import java.lang.reflect.Type;

public class RecipeDeserializer implements JsonDeserializer<Recipe> {
    @Override
    public Recipe deserialize(JsonElement json, Type typeOfT,
                              JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        String type = obj.get("type").getAsString();
        
        return context.deserialize(json, getRecipeClass(type));
    }
    
    private Class<? extends Recipe> getRecipeClass(String type) {
        return switch (type) {
            
            case "minecraft:crafting_shaped" -> ShapedCraftingRecipe.class;
            case "minecraft:crafting_shapeless" -> ShapelessCraftingRecipe.class;

            case "tfc:damage_inputs_shaped_crafting" -> DamageInputsShapedCraftingRecipe.class;
            case "tfc:damage_inputs_shapeless_crafting" -> DamageInputsShapelessCraftingRecipe.class;

            case "tfc:heating" -> HeatingRecipe.class;
            case "tfc:quern" -> QuernRecipe.class;
            case "tfc:anvil" -> AnvilRecipe.class;
            case "tfc:loom" -> LoomRecipe.class;
            case "tfc:glassworking" -> GlassworkingRecipe.class;
            
            case "tfc:barrel_sealed" -> BarrelSealedRecipe.class;
            case "tfc:barrel_instant" -> BarrelInstantRecipe.class;
            
            case "tfc:knapping" -> KnappingRecipe.class;
            case "tfc:rock_knapping" -> RockKnappingRecipe.class;
            case "tfc:clay_knapping" -> ClayKnappingRecipe.class;
            case "tfc:fire_clay_knapping" -> FireClayKnappingRecipe.class;
            case "tfc:leather_knapping" -> LeatherKnappingRecipe.class;
            
            case "firmalife:drying" -> DryingRecipe.class;
            case "waterflasks:heal_flask" -> WaterflasksHealRecipe.class;
            
            default -> UnknownRecipe.class;
        };
    }
}