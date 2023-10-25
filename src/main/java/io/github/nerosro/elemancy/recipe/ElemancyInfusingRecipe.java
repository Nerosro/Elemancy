package io.github.nerosro.elemancy.recipe;

import com.google.gson.JsonObject;
import io.github.nerosro.elemancy.Elemancy;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Nerosro on 27/09/2023.
 */
public class ElemancyInfusingRecipe implements Recipe<CraftingContainer> {

    private final ResourceLocation id;
    private final ItemStack output;
    private final NonNullList<Ingredient> recipeItem;
    private final int mana;

    public ElemancyInfusingRecipe(ResourceLocation id, ItemStack output, NonNullList<Ingredient> recipeItem, int mana){
        this.id = id;
        this.output = output;
        this.recipeItem = recipeItem;
        this.mana = mana;
    }

    @Override
    public boolean matches(CraftingContainer pContainer, Level pLevel) {
        if(pLevel.isClientSide){
            return false;
        }

        /*
        System.out.println(recipeItem.get(0));
        System.out.println(pContainer.getItem(0));
        System.out.println(recipeItem.get(0).test(pContainer.getItem(0)));
        */
        return recipeItem.get(0).test(pContainer.getItem(0)); //Testing against container slot 0 since it's a fake container and only has 1 slot
    }

    @Override
    public ItemStack assemble(CraftingContainer pContainer, RegistryAccess pRegistryAccess) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return output.copy();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public int getManaUsage (){
        return mana;
    }

    public static class Type implements RecipeType<ElemancyInfusingRecipe>{
        public static final Type INSTANCE = new Type();
        public static final String ID = "infusion_crafting";
    }


    public static class Serializer implements RecipeSerializer<ElemancyInfusingRecipe>{
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(Elemancy.MOD_ID, "infusion_crafting");


        @Override
        public ElemancyInfusingRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            var output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "output"));
            int mana = GsonHelper.getAsInt(pSerializedRecipe, "mana");

            var ingredients = GsonHelper.getAsJsonArray(pSerializedRecipe, "ingredients");
            var inputs = NonNullList.withSize(1, Ingredient.EMPTY);

            for(var i =0; i<inputs.size(); i++){
                inputs.set(i, Ingredient.fromJson(ingredients.get(i)));
            }
            return new ElemancyInfusingRecipe(pRecipeId, output, inputs, mana);
        }

        @Override
        public @Nullable ElemancyInfusingRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {

            var inputs = NonNullList.withSize(pBuffer.readInt(), Ingredient.EMPTY);

            for(var i =0; i<inputs.size(); i++){
                inputs.set(i, Ingredient.fromNetwork(pBuffer));
            }

            var mana = pBuffer.readInt();
            var output = pBuffer.readItem();

            return new ElemancyInfusingRecipe(pRecipeId, output, inputs, mana);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, ElemancyInfusingRecipe pRecipe) {

            pBuffer.writeInt(pRecipe.getIngredients().size());

            for (var ingredient : pRecipe.getIngredients()){
                ingredient.toNetwork(pBuffer);
            }

            pBuffer.writeInt(pRecipe.mana);
            pBuffer.writeItemStack(pRecipe.getResultItem(null), false);
        }
    }
}
