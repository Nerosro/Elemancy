package io.github.nerosro.elemancy.event;

import io.github.nerosro.elemancy.Elemancy;
import io.github.nerosro.elemancy.entity.ModEntities;
import io.github.nerosro.elemancy.entity.custom.ElementalShapeEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Created by Nerosro on 13/11/2023.
 */
@Mod.EventBusSubscriber(modid = Elemancy.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event){
        event.put(ModEntities.ELEMENTAL_SHAPE.get(), ElementalShapeEntity.createAttributes().build());
    }
}
