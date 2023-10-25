package io.github.nerosro.elemancy.loot;

import com.mojang.serialization.Codec;
import io.github.nerosro.elemancy.Elemancy;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Created by Nerosro on 11/10/2023.
 */
public class MobLootModifiers {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIER_SERIALIZER =
            DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, Elemancy.MOD_ID);

    public static final RegistryObject<Codec <? extends IGlobalLootModifier>> ADD_ITEM =
            LOOT_MODIFIER_SERIALIZER.register("add_item", AddItemModifier.CODEC);

    public static final RegistryObject<Codec <? extends IGlobalLootModifier>> ADD_SUSPICIOUS_SAND_ITEM =
            LOOT_MODIFIER_SERIALIZER.register("add_suspicious_sand_item", AddSuspiciousSandItemModifier.CODEC);
    public static void register(IEventBus eventBus) {
        LOOT_MODIFIER_SERIALIZER.register(eventBus);
    }


}
