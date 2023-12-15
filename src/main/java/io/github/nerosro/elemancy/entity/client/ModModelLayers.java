package io.github.nerosro.elemancy.entity.client;

import io.github.nerosro.elemancy.Elemancy;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

/**
 * Created by Nerosro on 13/11/2023.
 */
public class ModModelLayers {
    public static final ModelLayerLocation ELEMENTAL_SHAPE_LAYER = new ModelLayerLocation(
            new ResourceLocation(Elemancy.MOD_ID, "elemental_shape_layer"), "main");
}
