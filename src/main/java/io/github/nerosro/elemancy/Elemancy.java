package io.github.nerosro.elemancy;

import io.github.nerosro.elemancy.block.ModBlocks;
import io.github.nerosro.elemancy.effects.ModEffects;
import io.github.nerosro.elemancy.item.ModCreativeModeTab;
import io.github.nerosro.elemancy.item.ModItems;
import io.github.nerosro.elemancy.loot.MobLootModifiers;
import io.github.nerosro.elemancy.networking.ModMessages;
import io.github.nerosro.elemancy.recipe.ModRecipes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Created by Nerosro on 3/07/2023.
 */
@Mod(Elemancy.MOD_ID)
public class Elemancy {
    public static final String MOD_ID = "elemancy";

    public Elemancy() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModCreativeModeTab.register(modEventBus);
        ModRecipes.register(modEventBus);
        MobLootModifiers.register(modEventBus);
        ModEffects.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ModMessages.register();
            ((FlowerPotBlock)Blocks.FLOWER_POT).addPlant(ModBlocks.MANA_FLOWER.getId(), ModBlocks.POTTED_MANA_FLOWER);

        });
    }
}
