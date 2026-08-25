package be.nerosro.elemancy;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import be.nerosro.elemancy.block.ElemancyBlocks;
import be.nerosro.elemancy.client.SpellRadialHandler;
import be.nerosro.elemancy.datagen.DataGenerators;
import be.nerosro.elemancy.effects.ElemancyEffects;
import be.nerosro.elemancy.entity.EntityTypes;
import be.nerosro.elemancy.infusion.InfusionRecipes;
import be.nerosro.elemancy.items.ElemancyCreativeTabs;
import be.nerosro.elemancy.items.ElemancyDataComponents;
import be.nerosro.elemancy.items.ElemancyItems;
import be.nerosro.elemancy.items.tools.darkbucket.DarkBucketFluidCapabilities;
import be.nerosro.elemancy.items.trinket.TrinketBonuses;
import be.nerosro.elemancy.loot.ElemancyLootModifiers;
import be.nerosro.elemancy.network.ElemancyNetwork;
import be.nerosro.elemancy.particle.ElemancyParticles;
import be.nerosro.elemancy.skilltree.Attachments;
import be.nerosro.elemancy.skilltree.SkillTreeEntries;
import be.nerosro.elemancy.spell.ElemancySpells;
import be.nerosro.elemancy.traits.ElemancyTraits;
import be.nerosro.soulmark.network.ManaModifiers;
import be.nerosro.soulmark.network.SoulmarkNetwork;
import be.nerosro.soulmark.skilltree.SkillTreeOpeners;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(Elemancy.MOD_ID)
public class Elemancy {
    public static final String MOD_ID = "elemancy";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Elemancy(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        ElemancyBlocks.register(modEventBus);
        ElemancyDataComponents.register(modEventBus);
        ElemancyItems.register(modEventBus);
        ElemancyCreativeTabs.register(modEventBus);

        ElemancyTraits.register(modEventBus);

        SkillTreeEntries.register(modEventBus);
        Attachments.register(modEventBus);
        EntityTypes.register(modEventBus);
        ElemancyEffects.register(modEventBus);
        ElemancyParticles.register(modEventBus);
        ElemancyLootModifiers.register(modEventBus);

        modEventBus.addListener(ElemancyNetwork::register);
        modEventBus.addListener(DarkBucketFluidCapabilities::register);

        modEventBus.addListener(DataGenerators::gatherData);

        modContainer.registerConfig(ModConfig.Type.COMMON, ElemancyConfig.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        InfusionRecipes.register();
        ElemancySpells.register();

        // Register trinket provider with Soulmark for accurate mana stat breakdown
        SoulmarkNetwork.setManaModifierProvider(player -> new ManaModifiers(
            TrinketBonuses.poolBoost(player),
            TrinketBonuses.regenBoost(player)
        ));

        // Register the Tome as the opener for the Elemancy skill tree
        // Register the Energized Stick as a radial menu opener
        event.enqueueWork(() -> {
            SkillTreeOpeners.register(ElemancyItems.TOME.get(), SkillTreeEntries.TREE_ID);
            SpellRadialHandler.registerOpeners();
        });
    }
}
