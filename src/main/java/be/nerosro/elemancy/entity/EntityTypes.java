package be.nerosro.elemancy.entity;

import be.nerosro.elemancy.Elemancy;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registers all Elemancy entity types.
 */
public class EntityTypes {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
        DeferredRegister.create(Registries.ENTITY_TYPE, Elemancy.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<ManaBlastProjectile>> MANA_BLAST =
        ENTITY_TYPES.register("mana_blast", () ->
            EntityType.Builder.<ManaBlastProjectile>of(ManaBlastProjectile::new, MobCategory.MISC)
                .sized(0.25f, 0.25f)
                .clientTrackingRange(4)
                .updateInterval(10)
                .build(ResourceKey.create(Registries.ENTITY_TYPE,
                    Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "mana_blast"))));

    public static final DeferredHolder<EntityType<?>, EntityType<RitualLightningEntity>> RITUAL_LIGHTNING =
        ENTITY_TYPES.register("ritual_lightning", () ->
            EntityType.Builder.of(RitualLightningEntity::new, MobCategory.MISC)
                .sized(0.1f, 0.1f)
                .clientTrackingRange(32)
                .updateInterval(1)
                .build(ResourceKey.create(Registries.ENTITY_TYPE,
                    Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "ritual_lightning"))));

    public static final DeferredHolder<EntityType<?>, EntityType<RitualSigilEntity>> RITUAL_SIGIL =
        ENTITY_TYPES.register("ritual_sigil", () ->
            EntityType.Builder.of(RitualSigilEntity::new, MobCategory.MISC)
                .sized(0.1f, 0.1f)
                .clientTrackingRange(32)
                .updateInterval(1)
                .build(ResourceKey.create(Registries.ENTITY_TYPE,
                    Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "ritual_sigil"))));

    public static final DeferredHolder<EntityType<?>, EntityType<LightShieldDecoyEntity>> LIGHT_SHIELD_DECOY =
        ENTITY_TYPES.register("light_shield_decoy", () ->
            EntityType.Builder.of(LightShieldDecoyEntity::new, MobCategory.MISC)
                .sized(0.6f, 1.8f)
                .clientTrackingRange(32)
                .updateInterval(1)
                .build(ResourceKey.create(Registries.ENTITY_TYPE,
                    Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "light_shield_decoy"))));

    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(LIGHT_SHIELD_DECOY.get(), Player.createAttributes().build());
    }

    public static void register(IEventBus modEventBus) {
        ENTITY_TYPES.register(modEventBus);
    }
}
