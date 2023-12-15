package io.github.nerosro.elemancy.entity;

import io.github.nerosro.elemancy.Elemancy;
import io.github.nerosro.elemancy.entity.client.ElementalShapeModel;
import io.github.nerosro.elemancy.entity.custom.ElementalShapeEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Created by Nerosro on 13/11/2023.
 */
public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Elemancy.MOD_ID);

    public static final RegistryObject<EntityType<ElementalShapeEntity>> ELEMENTAL_SHAPE =
            ENTITY_TYPES.register("elemental_shape", () -> EntityType.Builder.of(ElementalShapeEntity::new, MobCategory.AMBIENT)
                    .sized(1f,1f).build("elemental_shape"));

    public static void register(IEventBus eventBus){
        ENTITY_TYPES.register(eventBus);
    }

}

