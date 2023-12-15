package io.github.nerosro.elemancy.event;

import io.github.nerosro.elemancy.Elemancy;
import io.github.nerosro.elemancy.entity.client.ElementalShapeModel;
import io.github.nerosro.elemancy.entity.client.ModModelLayers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Created by Nerosro on 13/11/2023.
 */
@Mod.EventBusSubscriber(modid = Elemancy.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEventBusClientEvents {
    @SubscribeEvent
    public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event){
        event.registerLayerDefinition(ModModelLayers.ELEMENTAL_SHAPE_LAYER, ElementalShapeModel::createBodyLayer);
    }
}
