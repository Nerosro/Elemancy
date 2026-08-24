package be.nerosro.elemancy.loot;

import java.util.function.Supplier;

import com.mojang.serialization.MapCodec;

import be.nerosro.elemancy.Elemancy;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * Registers Elemancy's global loot modifier codecs.
 */
public class ElemancyLootModifiers {

    private static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> GLM =
        DeferredRegister.create(NeoForgeRegistries.GLOBAL_LOOT_MODIFIER_SERIALIZERS, Elemancy.MOD_ID);

    public static final Supplier<MapCodec<CopperFortuneModifier>> COPPER_FORTUNE =
        GLM.register("copper_fortune", () -> CopperFortuneModifier.CODEC);

    public static final Supplier<MapCodec<AddItemLootModifier>> ADD_ITEM =
        GLM.register("add_item", () -> AddItemLootModifier.CODEC);

    public static void register(IEventBus modEventBus) {
        GLM.register(modEventBus);
    }
}

