package be.nerosro.elemancy.client.structureprojection;

import java.util.ArrayList;
import java.util.List;

import org.jspecify.annotations.Nullable;

import be.nerosro.elemancy.Elemancy;
import be.nerosro.elemancy.ritual.shared.StructureRotationTemplate;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ExtractLevelRenderStateEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.SubmitCustomGeometryEvent;

/** Client-local construction preview for any registered structure blueprint. */
public final class StructureProjectionPreview {
    private static final ContextKey<RenderData> RENDER_DATA = new ContextKey<>(
        Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "structure_projection")
    );
    private static final int FULL_BRIGHT = 0xF000F0;
    private static final int VARIABLE_OUTLINE_COLOR = 0xFF55FFFF;
    private static final BlockState VARIABLE_SLOT_STATE = Blocks.LIGHT_BLUE_STAINED_GLASS.defaultBlockState();

    @Nullable
    private static Preview preview;

    private StructureProjectionPreview() {
    }

    public static boolean show(@Nullable String id) {
        if (id == null) return false;

        Minecraft minecraft = Minecraft.getInstance();
        if (!(minecraft.level instanceof ClientLevel level) || !(minecraft.hitResult instanceof BlockHitResult hit)) {
            return false;
        }

        try {
            StructureProjectionDefinition definition = StructureProjectionRegistry.get(Identifier.parse(id)).orElse(null);
            if (definition == null) return false;
            preview = new Preview(level.dimension(), hit.getBlockPos().relative(hit.getDirection()), definition, 0, false);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    public static boolean handleProjectionUse() {
        if (preview == null) return false;
        preview = preview.toggleAnchor();
        return true;
    }

    public static boolean cancel() {
        if (preview == null) return false;
        clear();
        return true;
    }

    public static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (preview == null || preview.anchored() || minecraft.screen != null || minecraft.player == null
            || !minecraft.player.isShiftKeyDown() || event.getScrollDeltaY() == 0.0D) {
            return;
        }

        preview = preview.rotate(event.getScrollDeltaY() < 0.0D ? 1 : -1);
        event.setCanceled(true);
    }

    public static void onClientTick(ClientTickEvent.Post event) {
        if (preview == null) return;

        Minecraft minecraft = Minecraft.getInstance();
        if (!(minecraft.level instanceof ClientLevel level) || minecraft.player == null || !minecraft.player.isAlive()
            || level.dimension() != preview.dimension()) {
            clear();
            return;
        }

        if (preview.definition().dismissWhenSatisfied()
            && isSatisfied(level, preview.anchor(), preview.definition().template(), preview.rotation())) {
            clear();
            return;
        }

        if (!preview.anchored()) {
            if (minecraft.hitResult instanceof BlockHitResult hit) {
                preview = preview.moveTo(hit.getBlockPos().relative(hit.getDirection()));
            }
        } else if (horizontalDistanceSquared(minecraft.player.blockPosition(), preview.anchor())
            > preview.definition().maximumAnchorDistance() * preview.definition().maximumAnchorDistance()) {
            clear();
        }
    }

    public static void onExtractLevelRenderState(ExtractLevelRenderStateEvent event) {
        Preview activePreview = preview;
        if (activePreview == null || event.getLevel().dimension() != activePreview.dimension()) return;

        List<RenderBlock> blocks = new ArrayList<>();
        for (StructureRotationTemplate.Entry entry : activePreview.definition().template().rotated(activePreview.rotation())) {
            BlockPos position = activePreview.anchor().offset(entry.offset());
            if (isSatisfied(event.getLevel(), position, entry)) continue;

            BlockState displayState = entry.variableSlot() ? VARIABLE_SLOT_STATE : entry.displayState();
            if (displayState == null) continue;
            BlockModelRenderState model = new BlockModelRenderState();
            Minecraft.getInstance().getBlockModelResolver().update(model, displayState, BlockDisplayContext.create());
            blocks.add(new RenderBlock(position, model, entry.variableSlot()));
        }
        event.getRenderState().setRenderData(RENDER_DATA, new RenderData(blocks));
    }

    public static void onSubmitCustomGeometry(SubmitCustomGeometryEvent event) {
        RenderData renderData = event.getLevelRenderState().getRenderData(RENDER_DATA);
        if (renderData == null) return;

        PoseStack poseStack = event.getPoseStack();
        SubmitNodeCollector collector = event.getSubmitNodeCollector();
        Vec3 cameraPosition = event.getLevelRenderState().cameraRenderState.pos;
        for (RenderBlock block : renderData.blocks()) {
            poseStack.pushPose();
            poseStack.translate(
                block.position().getX() - cameraPosition.x,
                block.position().getY() - cameraPosition.y,
                block.position().getZ() - cameraPosition.z
            );
            block.model().submitMultiLayer(poseStack, collector, FULL_BRIGHT, 0,
                block.variableSlot() ? VARIABLE_OUTLINE_COLOR : 0);
            poseStack.popPose();
        }
    }

    public static void clear() {
        preview = null;
    }

    private static boolean isSatisfied(Level level, BlockPos anchor, StructureRotationTemplate template, int rotation) {
        for (StructureRotationTemplate.Entry entry : template.rotated(rotation)) {
            if (!isSatisfied(level, anchor.offset(entry.offset()), entry)) return false;
        }
        return true;
    }

    private static boolean isSatisfied(Level level, BlockPos position, StructureRotationTemplate.Entry entry) {
        return entry.predicate().test(level, level.getBlockState(position), position);
    }

    private static int horizontalDistanceSquared(BlockPos first, BlockPos second) {
        int x = first.getX() - second.getX();
        int z = first.getZ() - second.getZ();
        return x * x + z * z;
    }

    private record Preview(net.minecraft.resources.ResourceKey<Level> dimension, BlockPos anchor,
                           StructureProjectionDefinition definition, int rotation, boolean anchored) {
        private Preview rotate(int amount) {
            return new Preview(dimension, anchor, definition, Math.floorMod(rotation + amount, 4), false);
        }

        private Preview moveTo(BlockPos target) {
            return new Preview(dimension, target, definition, rotation, false);
        }

        private Preview toggleAnchor() {
            return new Preview(dimension, anchor, definition, rotation, !anchored);
        }
    }

    private record RenderData(List<RenderBlock> blocks) {
    }

    private record RenderBlock(BlockPos position, BlockModelRenderState model, boolean variableSlot) {
    }
}