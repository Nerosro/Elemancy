package be.nerosro.elemancy.client;

import java.util.function.Consumer;

import org.joml.Vector3fc;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;

import be.nerosro.elemancy.Elemancy;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.equipment.ShieldModel;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;

/**
 * Fixed-texture special renderer for the Light Elemetal Shield.
 */
public final class LightShieldSpecialRenderer implements SpecialModelRenderer<DataComponentMap> {
    private static final SpriteId TEXTURE = new SpriteId(
        Sheets.SHIELD_SHEET,
        Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "entity/shield/light_shield")
    );

    private final SpriteGetter sprites;
    private final ShieldModel model;

    private LightShieldSpecialRenderer(SpriteGetter sprites, ShieldModel model) {
        this.sprites = sprites;
        this.model = model;
    }

    @Override
    public @NonNull DataComponentMap extractArgument(ItemStack stack) {
        return stack.immutableComponents();
    }

    @Override
    public void submit(
        @Nullable DataComponentMap components,
        @NonNull PoseStack poseStack,
        SubmitNodeCollector submitNodeCollector,
        int lightCoords,
        int overlayCoords,
        boolean hasFoil,
        int outlineColor
    ) {
        submitNodeCollector.submitModel(this.model, Unit.INSTANCE, poseStack, lightCoords, overlayCoords, -1, TEXTURE, this.sprites, outlineColor, null);
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        this.model.root().getExtentsForGui(new PoseStack(), output);
    }

    public record Unbaked() implements SpecialModelRenderer.Unbaked<DataComponentMap> {
        public static final Unbaked INSTANCE = new Unbaked();
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(INSTANCE);

        @Override
        public @NonNull MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public LightShieldSpecialRenderer bake(SpecialModelRenderer.BakingContext context) {
            return new LightShieldSpecialRenderer(context.sprites(), new ShieldModel(context.entityModelSet().bakeLayer(ModelLayers.SHIELD)));
        }
    }
}