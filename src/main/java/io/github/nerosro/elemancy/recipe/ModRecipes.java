package io.github.nerosro.elemancy.recipe;

import io.github.nerosro.elemancy.Elemancy;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Created by Nerosro on 27/09/2023.
 */
public class ModRecipes {

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Elemancy.MOD_ID);

    public static final RegistryObject<RecipeSerializer<ElemancyInfusingRecipe>> ELEMANCY_INFUSING_SERIALIZER =
            SERIALIZERS.register("infusion_crafting", () -> ElemancyInfusingRecipe.Serializer.INSTANCE);
    public static void register(IEventBus eventBus){
        SERIALIZERS.register(eventBus);
    }
}
