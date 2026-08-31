package be.nerosro.elemancy.ritual.attunement;

import java.util.ArrayList;
import java.util.List;

import be.nerosro.elemancy.block.ElemancyBlocks;
import be.nerosro.elemancy.ritual.shared.StructureRotationTemplate;
import be.nerosro.soulmark.element.Element;
import be.nerosro.soulmark.element.SoulmarkElements;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.material.Fluids;

/**
 * The concrete Attunement Ritual structure template, per the ritual layout defined in
 * Attunement.md: a 3x3 stone brick slab platform (element block center, infused metal standing
 * tile at the front) and 4 Ashen Log + Infused Metal capstone pillars in an asymmetric "fan"
 * arrangement.
 * <p>
 * All offsets are relative to the center (element) block at (0, 0, 0).
 */
public final class StructureTemplate {
    private static final BlockState STONE_BRICK_SLAB = Blocks.STONE_BRICK_SLAB.defaultBlockState()
        .setValue(SlabBlock.TYPE, SlabType.BOTTOM);


    private StructureTemplate() {
    }

    /**
     * Offset (relative to the center block) of the Infused Metal tile the player must stand on
     * to trigger the ritual. Exposed so trigger logic can locate it at whatever rotation the
     * detector matched, via {@link StructureRotationTemplate#rotateOffset(int, int, int, int)}.
     */
    public static final BlockPos STANDING_TILE_OFFSET = new BlockPos(0, 0, 4);

    /**
     * The 4 pillars' local geometry from the center block at rotation 0. Exposed so the cutscene
     * engine can locate each pillar's capstone at whatever rotation the detector matched, via
     * {@link StructureRotationTemplate#rotateOffset(int, int, int, int)}.
     */
    public static final Pillar[] PILLARS = {
        new Pillar(-2, -3, 2),
        new Pillar(2, -3, 2),
        new Pillar(-4, 1, 1),
        new Pillar(4, 1, 1)
    };

    public record Pillar(int dx, int dz, int capstoneDy) {
    }

    private static boolean isWaterSource(Level level, BlockPos pos) {
        var fluidState = level.getFluidState(pos);
        return fluidState.isSource() && fluidState.is(Fluids.WATER);
    }

    private static boolean isBottomStoneBrickSlab(Level level, net.minecraft.world.level.block.state.BlockState state, BlockPos position) {
        return state.is(Blocks.STONE_BRICK_SLAB) && state.getValue(SlabBlock.TYPE) == SlabType.BOTTOM;
    }

    private static boolean isAshenLog(Level level, net.minecraft.world.level.block.state.BlockState state, BlockPos position) {
        return state.is(ElemancyBlocks.ASHEN_LOG.get());
    }

    private static boolean isInfusedMetalBlock(Level level, net.minecraft.world.level.block.state.BlockState state, BlockPos position) {
        return state.is(ElemancyBlocks.INFUSED_METAL_BLOCK.get());
    }

    private static boolean isValidCenterBlock(Level level, net.minecraft.world.level.block.state.BlockState state, BlockPos position) {
        return resolveElement(level, position) != null;
    }

    /**
     * Resolves which element the player chose, by inspecting the center block at the given
     * anchor. Glass resolves to Light by day and Dark by night at Elementize-cast time.
     */
    public static Element resolveElement(Level level, BlockPos anchor) {
        var state = level.getBlockState(anchor);
        if (state.is(Blocks.MAGMA_BLOCK)) return SoulmarkElements.FIRE.get();
        if (state.is(Blocks.DEEPSLATE)) return SoulmarkElements.EARTH.get();
        if (state.is(BlockTags.LEAVES)) return SoulmarkElements.AIR.get();
        if (state.is(Blocks.GLASS)) {
            boolean isDay = level.getOverworldClockTime() % 24000L < 12000L;
            return isDay ? SoulmarkElements.LIGHT.get() : SoulmarkElements.DARK.get();
        }
        if (isWaterSource(level, anchor)) return SoulmarkElements.WATER.get();
        return null;
    }

    public static final StructureRotationTemplate TEMPLATE = build();

    private static StructureRotationTemplate build() {
        List<StructureRotationTemplate.Entry> entries = new ArrayList<>();

        // Platform (dy = 0): 3x3 slab ring around the center.
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos offset = new BlockPos(dx, 0, dz);
                entries.add(new StructureRotationTemplate.Entry(
                    offset,
                    offset.equals(BlockPos.ZERO)
                        ? StructureTemplate::isValidCenterBlock
                        : StructureTemplate::isBottomStoneBrickSlab,
                    offset.equals(BlockPos.ZERO) ? null : STONE_BRICK_SLAB,
                    offset.equals(BlockPos.ZERO)
                ));
            }
        }
        entries.add(new StructureRotationTemplate.Entry(
            STANDING_TILE_OFFSET,
            StructureTemplate::isInfusedMetalBlock,
            ElemancyBlocks.INFUSED_METAL_BLOCK.get().defaultBlockState(),
            false
        ));

        for (Pillar pillar : PILLARS) {
            addPillar(entries, pillar);
        }

        return new StructureRotationTemplate(entries);
    }

    private static void addPillar(List<StructureRotationTemplate.Entry> entries, Pillar pillar) {
        for (int dy = 0; dy < pillar.capstoneDy(); dy++) {
            entries.add(new StructureRotationTemplate.Entry(
                new BlockPos(pillar.dx(), dy, pillar.dz()),
                StructureTemplate::isAshenLog,
                ElemancyBlocks.ASHEN_LOG.get().defaultBlockState(),
                false
            ));
        }
        entries.add(new StructureRotationTemplate.Entry(
            new BlockPos(pillar.dx(), pillar.capstoneDy(), pillar.dz()),
            StructureTemplate::isInfusedMetalBlock,
            ElemancyBlocks.INFUSED_METAL_BLOCK.get().defaultBlockState(),
            false
        ));
    }
}
