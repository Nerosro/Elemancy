package io.github.nerosro.elemancy.item;

import net.minecraft.world.food.FoodProperties;

/**
 * Created by Nerosro on 7/10/2023.
 */
public class ModFoods {
    public static final FoodProperties ICECREAM = new FoodProperties.Builder()
            .nutrition(5)
            .saturationMod(0.2F)
            .build();

    public static final FoodProperties STRAWBERRY = new FoodProperties.Builder()
            .nutrition(1)
            .saturationMod(0.2F)
            .fast()
            .build();
}
