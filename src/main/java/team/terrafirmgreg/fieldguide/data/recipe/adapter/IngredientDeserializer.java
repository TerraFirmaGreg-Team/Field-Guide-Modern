package team.terrafirmgreg.fieldguide.data.recipe.adapter;

import com.google.gson.*;
import team.terrafirmgreg.fieldguide.data.recipe.Ingredient;
import team.terrafirmgreg.fieldguide.data.recipe.ingredient.*;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;

@Slf4j
public class IngredientDeserializer implements JsonDeserializer<Ingredient> {
    
    @Override
    public Ingredient deserialize(JsonElement json, Type typeOfT,
                                  JsonDeserializationContext context) throws JsonParseException {
        
        if (json.isJsonPrimitive()) {
            String value = json.getAsString();
            if (value.startsWith("#")) {
                TagIngredient tagIngredient = new TagIngredient();
                tagIngredient.setTag(value.substring(1));
                return tagIngredient;
            } else {
                ItemIngredient itemIngredient = new ItemIngredient();
                itemIngredient.setItem(value);
                return itemIngredient;
            }
        } else if (json.isJsonArray()) {
            return parseArrayIngredient(json.getAsJsonArray(), context);
        } else {

            JsonObject obj = json.getAsJsonObject();

            if (obj.has("type")) {
                String type = obj.get("type").getAsString();
                return parseCompoundIngredient(type, obj, context);
            }

            if (obj.has("item")) {
                ItemIngredient ingredient = new ItemIngredient();
                ingredient.setItem(obj.get("item").getAsString());
                if (obj.has("count")) {
                    ingredient.setCount(obj.get("count").getAsInt());
                }
                return ingredient;
            }

            if (obj.has("tag")) {
                TagIngredient ingredient = new TagIngredient();
                ingredient.setTag(obj.get("tag").getAsString());
                return ingredient;
            }

            if (obj.has("fluid_ingredient")) {
                
                return parseFluidItemIngredient(obj, context);
            }

            if (obj.has("fluid")) {
                
                return parseFluidIngredient(obj, context);
            }

            if (obj.has("ingredient") && obj.has("count")) {
                
                return parseCountedIngredient(obj, context);
            }

            if (json.isJsonArray()) {
                return parseArrayIngredient(json.getAsJsonArray(), context);
            }
        }
        
        throw new JsonParseException("无法解析的成分格式: " + json);
    }
    
    private Ingredient parseCompoundIngredient(String type, JsonObject obj, JsonDeserializationContext context) {
        switch (type) {
            case "tfc:has_trait":
            case "tfc:lacks_trait":
                TraitIngredient traitIngredient = new TraitIngredient();
                traitIngredient.setType(type);
                traitIngredient.setTrait(obj.get("trait").getAsString());
                traitIngredient.setIngredient(context.deserialize(obj.get("ingredient"), Ingredient.class));
                return traitIngredient;
                
            case "tfc:not_rotten":
                NotRottenIngredient notRotten = new NotRottenIngredient();
                notRotten.setType(type);
                notRotten.setIngredient(context.deserialize(obj.get("ingredient"), Ingredient.class));
                return notRotten;
                
            case "tfc:and":
                ListIngredient list = new ListIngredient();
                list.setType(type);
                JsonArray children = obj.getAsJsonArray("children");
                for (JsonElement child : children) {
                    list.add(context.deserialize(child, Ingredient.class));
                }
                return list;
                
            case "tfc:fluid_item":
                return parseFluidItemIngredient(obj, context);
                
            case "tfc:fluid_content":
                return parseFluidIngredient(obj, context);
                
            default:
                throw new JsonParseException("未知的成分类型: " + type);
        }
    }
    
    private Ingredient parseFluidItemIngredient(JsonObject obj, JsonDeserializationContext context) {
        FluidItemIngredient fluidItem = new FluidItemIngredient();
        if (obj.has("type")) {
            fluidItem.setType(obj.get("type").getAsString());
        }
        JsonObject fluidIngredientObj = obj.getAsJsonObject("fluid_ingredient");
        FluidIngredient fluidIngredient = new FluidIngredient();
        
        if (fluidIngredientObj.has("ingredient")) {
            fluidIngredient.setIngredient(fluidIngredientObj.get("ingredient").getAsString());
        }
        if (fluidIngredientObj.has("amount")) {
            fluidIngredient.setAmount(fluidIngredientObj.get("amount").getAsInt());
        }
        
        fluidItem.setFluidIngredient(fluidIngredient);
        return fluidItem;
    }
    
    private Ingredient parseFluidIngredient(JsonObject obj, JsonDeserializationContext context) {
        
        FluidItemIngredient fluidItem = new FluidItemIngredient();

        FluidIngredient fluidIngredient = new FluidIngredient();
        
        JsonObject fluidObj = obj.getAsJsonObject("fluid");
        if (fluidObj.has("fluid")) {
            fluidIngredient.setIngredient(fluidObj.get("fluid").getAsString());
        }
        if (fluidObj.has("amount")) {
            fluidIngredient.setAmount(fluidObj.get("amount").getAsInt());
        }
        
        fluidItem.setFluidIngredient(fluidIngredient);
        return fluidItem;
    }
    
    private Ingredient parseCountedIngredient(JsonObject obj, JsonDeserializationContext context) {
        
        JsonElement ingredientElement = obj.get("ingredient");
        Ingredient innerIngredient;
        if (ingredientElement.isJsonArray()) {
            innerIngredient = parseArrayIngredient(ingredientElement.getAsJsonArray(), context);
        } else {
            innerIngredient = context.deserialize(ingredientElement, Ingredient.class);
        }
        int count = obj.get("count").getAsInt();
        
        if (innerIngredient instanceof ItemIngredient) {
            ((ItemIngredient) innerIngredient).setCount(count);
            return innerIngredient;
        }
        
        return innerIngredient;
    }
    
    private Ingredient parseArrayIngredient(JsonArray array, JsonDeserializationContext context) {
        
        ListIngredient list = new ListIngredient();
        for (JsonElement element : array) {
            list.add(context.deserialize(element, Ingredient.class));
        }
        return list;
    }
}