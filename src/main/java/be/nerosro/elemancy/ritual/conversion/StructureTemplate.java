package be.nerosro.elemancy.ritual.conversion;

import static be.nerosro.elemancy.ritual.attunement.StructureTemplate.PILLARS;
import static be.nerosro.elemancy.ritual.attunement.StructureTemplate.STANDING_TILE_OFFSET;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import be.nerosro.elemancy.block.ElemancyBlocks;
import be.nerosro.elemancy.ritual.attunement.StructureTemplate.Pillar;
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

/**
 * Conversion Ritual structure: the Attunement layout with an Elemetal center block and five
 * reusable target-element capstones.
 */
public final class StructureTemplate {
    private static final BlockState STONE_BRICK_SLAB = Blocks.STONE_BRICK_SLAB.defaultBlockState()
        .setValue(SlabBlock.TYPE, SlabType.BOTTOM);

    private StructureTemplate() {
    }

    public static final StructureRotationTemplate TEMPLATE = build();

    private static boolean isBottomStoneBrickSlab(Level level, BlockState state, BlockPos position) {
        return state.is(Blocks.STONE_BRICK_SLAB) && state.getValue(SlabBlock.TYPE) == SlabType.BOTTOM;
    }

    private static boolean isAshenLog(Level level, BlockState state, BlockPos position) {
        return state.is(ElemancyBlocks.ASHEN_LOG.get());
    }

    private static boolean isElemetalBlock(Level level, BlockState state, BlockPos position) {
        return ElemancyBlocks.getElemetalElement(state).isPresent();
    }

    private static boolean isValidCapstone(Level level, BlockState state, BlockPos position) {
        return resolveCapstoneElement(state).isPresent();
    }

    /**
     * Resolves the target element represented by one reusable capstone.
     */
    public static Optional<Element> resolveCapstoneElement(BlockState state) {
        if (state.is(Blocks.BAMBOO_BLOCK)) return Optional.of(SoulmarkElements.AIR.get());
        if (state.is(BlockTags.ICE)) return Optional.of(SoulmarkElements.WATER.get());
        if (state.is(Blocks.OBSIDIAN)) return Optional.of(SoulmarkElements.EARTH.get());
        if (state.is(Blocks.RED_NETHER_BRICKS)) return Optional.of(SoulmarkElements.FIRE.get());
        if (state.is(Blocks.GLOWSTONE)) return Optional.of(SoulmarkElements.LIGHT.get());
        if (state.is(Blocks.SCULK)) return Optional.of(SoulmarkElements.DARK.get());
        return Optional.empty();
    }

    /**
     * Resolves the common target element of all five capstones. Missing, invalid, or mixed
     * capstones return empty.
     */
    public static Optional<Element> resolveTargetElement(Level level, BlockPos anchor, int rotation) {
        Element targetElement = null;
        for (BlockPos capstonePos : capstonePositions(anchor, rotation)) {
            Element capstoneTarget = resolveCapstoneElement(level.getBlockState(capstonePos)).orElse(null);
            if (capstoneTarget == null) return Optional.empty();
            if (targetElement == null) {
                targetElement = capstoneTarget;
            } else if (targetElement != capstoneTarget) {
                return Optional.empty();
            }
        }
        return Optional.ofNullable(targetElement);
    }

    /**
     * Returns the five capstone positions in stable clockwise order for sigil presentation.
     */
    public static List<BlockPos> capstonePositions(BlockPos anchor, int rotation) {
        List<BlockPos> positions = new ArrayList<>(5);
        for (Pillar pillar : PILLARS) {
            positions.add(anchor.offset(StructureRotationTemplate.rotateOffset(
                pillar.dx(), pillar.capstoneDy(), pillar.dz(), rotation)));
        }
        positions.add(anchor.offset(StructureRotationTemplate.rotateOffset(
            STANDING_TILE_OFFSET.getX(), STANDING_TILE_OFFSET.getY(), STANDING_TILE_OFFSET.getZ(), rotation)));
        positions.sort(Comparator.comparingDouble(pos -> Math.atan2(
            pos.getZ() - anchor.getZ(), pos.getX() - anchor.getX())));
        return List.copyOf(positions);
    }

    private static StructureRotationTemplate build() {
        List<StructureRotationTemplate.Entry> entries = new ArrayList<>();

        entries.add(fixedSlab(-1, -1));
        entries.add(fixedSlab(0, -1));
        entries.add(fixedSlab(1, -1));
        entries.add(fixedSlab(-1, 0));
        entries.add(new StructureRotationTemplate.Entry(BlockPos.ZERO, StructureTemplate::isElemetalBlock, null, true));
        entries.add(fixedSlab(1, 0));
        entries.add(fixedSlab(-1, 1));
        entries.add(fixedSlab(0, 1));
        entries.add(fixedSlab(1, 1));

        entries.add(new StructureRotationTemplate.Entry(STANDING_TILE_OFFSET, StructureTemplate::isValidCapstone, null, true));
        for (Pillar pillar : PILLARS) {
            addPillar(entries, pillar);
        }

        return new StructureRotationTemplate(entries);
    }

    private static StructureRotationTemplate.Entry fixedSlab(int x, int z) {
        return new StructureRotationTemplate.Entry(
            new BlockPos(x, 0, z),
            StructureTemplate::isBottomStoneBrickSlab,
            STONE_BRICK_SLAB,
            false
        );
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
            StructureTemplate::isValidCapstone,
            null,
            true
        ));
    }
}