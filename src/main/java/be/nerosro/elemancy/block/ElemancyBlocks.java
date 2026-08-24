package be.nerosro.elemancy.block;

import be.nerosro.elemancy.Elemancy;
import be.nerosro.elemancy.element.ElemancyElementKeys;
import be.nerosro.soulmark.element.Element;
import be.nerosro.soulmark.element.ElementRegistry;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class ElemancyBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Elemancy.MOD_ID);
    public static final DeferredRegister.Items BLOCK_ITEMS = DeferredRegister.createItems(Elemancy.MOD_ID);

    // ==================== PARADOX FLOWER ====================

    public static final DeferredBlock<ParadoxFlowerBlock> PARADOX_FLOWER = BLOCKS.registerBlock("paradox_flower",
            props -> new ParadoxFlowerBlock(plantProperties(props, SoundType.LEAF_LITTER)));

    // ==================== ASHEN TREE ====================

    public static final DeferredBlock<AshenSaplingBlock> ASHEN_SAPLING = BLOCKS.registerBlock("ashen_sapling",
            props -> new AshenSaplingBlock(plantProperties(props, SoundType.CHERRY_SAPLING)));

    public static final DeferredBlock<AshenLeavesBlock> ASHEN_LEAVES = BLOCKS.registerBlock("ashen_leaves",
            props -> new AshenLeavesBlock(props
                    .mapColor(MapColor.COLOR_CYAN)
                    .strength(0.2f)
                    .randomTicks()
                    .sound(SoundType.GRASS)
                    .noOcclusion()
                    .isValidSpawn(Blocks::ocelotOrParrot)
                    .isSuffocating((_, _, _) -> false)
                    .isViewBlocking((_, _, _) -> false)
                    .ignitedByLava()
                    .pushReaction(PushReaction.DESTROY)
            ));

    public static final DeferredBlock<RotatedPillarBlock> ASHEN_LOG = BLOCKS.registerBlock("ashen_log",
            props -> new RotatedPillarBlock(logProperties(props, MapColor.COLOR_GRAY, MapColor.TERRACOTTA_GRAY)));

    public static final DeferredBlock<RotatedPillarBlock> ASHEN_WOOD = BLOCKS.registerBlock("ashen_wood",
            props -> new RotatedPillarBlock(logProperties(props, MapColor.TERRACOTTA_GRAY, MapColor.TERRACOTTA_GRAY)));

    public static final DeferredBlock<RotatedPillarBlock> STRIPPED_ASHEN_LOG = BLOCKS.registerBlock("stripped_ashen_log",
            props -> new RotatedPillarBlock(logProperties(props, MapColor.COLOR_LIGHT_GRAY, MapColor.COLOR_LIGHT_GRAY)));

    public static final DeferredBlock<RotatedPillarBlock> STRIPPED_ASHEN_WOOD = BLOCKS.registerBlock("stripped_ashen_wood",
            props -> new RotatedPillarBlock(logProperties(props, MapColor.COLOR_LIGHT_GRAY, MapColor.COLOR_LIGHT_GRAY)));

    public static final DeferredBlock<Block> ASHEN_PLANKS = BLOCKS.registerBlock("ashen_planks",
            props -> new Block(props
                    .mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .strength(2.0f, 3.0f)
                    .sound(SoundType.WOOD)
                    .ignitedByLava()
            ));

    // ==================== ASHEN WOOD DERIVATIVES ====================

    public static final DeferredBlock<StairBlock> ASHEN_STAIRS = BLOCKS.registerBlock("ashen_stairs",
            props -> new StairBlock(ASHEN_PLANKS.get().defaultBlockState(), props
                    .mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .strength(2.0f, 3.0f)
                    .sound(SoundType.WOOD)
                    .ignitedByLava()
            ));

    public static final DeferredBlock<SlabBlock> ASHEN_SLAB = BLOCKS.registerBlock("ashen_slab",
            props -> new SlabBlock(props
                    .mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .strength(2.0f, 3.0f)
                    .sound(SoundType.WOOD)
                    .ignitedByLava()
            ));

    public static final DeferredBlock<FenceBlock> ASHEN_FENCE = BLOCKS.registerBlock("ashen_fence",
            props -> new FenceBlock(props
                    .mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .strength(2.0f, 3.0f)
                    .sound(SoundType.WOOD)
                    .ignitedByLava()
            ));

    public static final DeferredBlock<FenceGateBlock> ASHEN_FENCE_GATE = BLOCKS.registerBlock("ashen_fence_gate",
            props -> new FenceGateBlock(WoodType.OAK, props
                    .mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .strength(2.0f, 3.0f)
                    .sound(SoundType.WOOD)
                    .ignitedByLava()
            ));

    public static final DeferredBlock<DoorBlock> ASHEN_DOOR = BLOCKS.registerBlock("ashen_door",
            props -> new DoorBlock(BlockSetType.OAK, props
                    .mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .strength(3.0f)
                    .sound(SoundType.WOOD)
                    .noOcclusion()
                    .pushReaction(PushReaction.DESTROY)
                    .ignitedByLava()
            ));

    public static final DeferredBlock<TrapDoorBlock> ASHEN_TRAPDOOR = BLOCKS.registerBlock("ashen_trapdoor",
            props -> new TrapDoorBlock(BlockSetType.OAK, props
                    .mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .strength(3.0f)
                    .sound(SoundType.WOOD)
                    .noOcclusion()
                    .isValidSpawn(Blocks::never)
                    .ignitedByLava()
            ));

    public static final DeferredBlock<PressurePlateBlock> ASHEN_PRESSURE_PLATE = BLOCKS.registerBlock("ashen_pressure_plate",
            props -> new PressurePlateBlock(BlockSetType.OAK, props
                    .mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .strength(0.5f)
                    .sound(SoundType.WOOD)
                    .noCollision()
                    .pushReaction(PushReaction.DESTROY)
                    .ignitedByLava()
            ));

    public static final DeferredBlock<ButtonBlock> ASHEN_BUTTON = BLOCKS.registerBlock("ashen_button",
            props -> new ButtonBlock(BlockSetType.OAK, 30, props
                    .noCollision()
                    .strength(0.5f)
                    .pushReaction(PushReaction.DESTROY)
            ));

    // ==================== INFUSED METAL ====================

    public static final DeferredBlock<Block> INFUSED_METAL_BLOCK = BLOCKS.registerBlock("infused_metal_block",
            props -> new Block(props
                    .mapColor(MapColor.COLOR_ORANGE)
                    .strength(3.0f, 6.0f)
                    .sound(SoundType.IRON)
                    .requiresCorrectToolForDrops()
            ));

    // ==================== INFUSED WOOL ====================

    public static final DeferredBlock<Block> INFUSED_WOOL = BLOCKS.registerBlock("infused_wool",
            props -> new Block(props
                    .mapColor(MapColor.COLOR_CYAN)
                    .strength(0.8f)
                    .sound(SoundType.WOOL)
                    .ignitedByLava()
            ));

    // ==================== MIRROR ====================

    public static final DeferredBlock<MirrorBlock> MIRROR = BLOCKS.registerBlock("mirror",
            props -> new MirrorBlock(props
                    .mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .strength(2.0f, 3.0f)
                    .sound(SoundType.WOOD)
                    .noOcclusion()
                    .pushReaction(PushReaction.DESTROY)
            ));

    // ==================== UTILITY (no item form) ====================

    public static final DeferredBlock<SoftGlowBlock> SOFT_GLOW = BLOCKS.registerBlock("soft_glow",
            props -> new SoftGlowBlock(props
                    .replaceable()
                    .noCollision()
                    .noOcclusion()
                    .noLootTable()
                    .pushReaction(PushReaction.DESTROY)
                    .lightLevel(state -> state.getValue(SoftGlowBlock.ENHANCED)
                            ? SoftGlowBlock.ENHANCED_LIGHT_LEVEL
                            : SoftGlowBlock.BASE_LIGHT_LEVEL)
            ));

    // ==================== BLOCK ITEMS ====================

    public static final DeferredItem<BlockItem> PARADOX_FLOWER_ITEM = BLOCK_ITEMS.registerSimpleBlockItem(PARADOX_FLOWER);
    public static final DeferredItem<BlockItem> ASHEN_SAPLING_ITEM = BLOCK_ITEMS.registerSimpleBlockItem(ASHEN_SAPLING);
    public static final DeferredItem<BlockItem> ASHEN_LEAVES_ITEM = BLOCK_ITEMS.registerSimpleBlockItem(ASHEN_LEAVES);
    public static final DeferredItem<BlockItem> ASHEN_LOG_ITEM = BLOCK_ITEMS.registerSimpleBlockItem(ASHEN_LOG);
    public static final DeferredItem<BlockItem> ASHEN_WOOD_ITEM = BLOCK_ITEMS.registerSimpleBlockItem(ASHEN_WOOD);
    public static final DeferredItem<BlockItem> STRIPPED_ASHEN_LOG_ITEM = BLOCK_ITEMS.registerSimpleBlockItem(STRIPPED_ASHEN_LOG);
    public static final DeferredItem<BlockItem> STRIPPED_ASHEN_WOOD_ITEM = BLOCK_ITEMS.registerSimpleBlockItem(STRIPPED_ASHEN_WOOD);
    public static final DeferredItem<BlockItem> ASHEN_PLANKS_ITEM = BLOCK_ITEMS.registerSimpleBlockItem(ASHEN_PLANKS);
    public static final DeferredItem<BlockItem> ASHEN_STAIRS_ITEM = BLOCK_ITEMS.registerSimpleBlockItem(ASHEN_STAIRS);
    public static final DeferredItem<BlockItem> ASHEN_SLAB_ITEM = BLOCK_ITEMS.registerSimpleBlockItem(ASHEN_SLAB);
    public static final DeferredItem<BlockItem> ASHEN_FENCE_ITEM = BLOCK_ITEMS.registerSimpleBlockItem(ASHEN_FENCE);
    public static final DeferredItem<BlockItem> ASHEN_FENCE_GATE_ITEM = BLOCK_ITEMS.registerSimpleBlockItem(ASHEN_FENCE_GATE);
    public static final DeferredItem<BlockItem> ASHEN_DOOR_ITEM = BLOCK_ITEMS.registerSimpleBlockItem(ASHEN_DOOR);
    public static final DeferredItem<BlockItem> ASHEN_TRAPDOOR_ITEM = BLOCK_ITEMS.registerSimpleBlockItem(ASHEN_TRAPDOOR);
    public static final DeferredItem<BlockItem> ASHEN_PRESSURE_PLATE_ITEM = BLOCK_ITEMS.registerSimpleBlockItem(ASHEN_PRESSURE_PLATE);
    public static final DeferredItem<BlockItem> ASHEN_BUTTON_ITEM = BLOCK_ITEMS.registerSimpleBlockItem(ASHEN_BUTTON);
    public static final DeferredItem<BlockItem> INFUSED_METAL_BLOCK_ITEM = BLOCK_ITEMS.registerSimpleBlockItem(INFUSED_METAL_BLOCK);
    public static final DeferredItem<BlockItem> INFUSED_WOOL_ITEM = BLOCK_ITEMS.registerSimpleBlockItem(INFUSED_WOOL);
    public static final DeferredItem<BlockItem> MIRROR_ITEM = BLOCK_ITEMS.registerSimpleBlockItem(MIRROR);

    // ==================== ELEMETAL BLOCKS ====================
    // One block per base element (Fire/Water/Earth/Air/Light/Dark), not a single block with
    // hidden state - future recipes need to require a *specific* element's Elemetal, and
    // Minecraft's crafting system matches ingredients by item identity. All 6 share the exact
    // same displayed name ("Elemetal Block") - see Attunement.md's "Elemetal Items" section.

    private static final Map<String, DeferredBlock<Block>> ELEMETAL_BLOCKS_BY_KEY = new LinkedHashMap<>();
    private static final Map<String, DeferredItem<BlockItem>> ELEMETAL_BLOCK_ITEMS_BY_KEY = new LinkedHashMap<>();

    static {
        for (String key : ElemancyElementKeys.BASE_ELEMENT_KEYS) {
            DeferredBlock<Block> block = BLOCKS.registerBlock("elemetal_block_" + key,
                    props -> new Block(props
                            .mapColor(MapColor.COLOR_ORANGE)
                            .strength(3.0f, 6.0f)
                            .sound(SoundType.IRON)
                            .requiresCorrectToolForDrops()
                    ));
            ELEMETAL_BLOCKS_BY_KEY.put(key, block);
            ELEMETAL_BLOCK_ITEMS_BY_KEY.put(key, BLOCK_ITEMS.registerSimpleBlockItem(block));
        }
    }

    private static String elementKey(Element element) {
        Identifier id = ElementRegistry.ELEMENT_REGISTRY.getKey(element);
        return id != null ? id.getPath() : "unknown";
    }

    /** Returns the Elemetal Block matching the given element, or null if not a base element. */
    public static DeferredBlock<Block> getElemetalBlock(Element element) {
        return ELEMETAL_BLOCKS_BY_KEY.get(elementKey(element));
    }

    /** Returns the Elemetal Block's item form matching the given element, or null if not a base element. */
    public static DeferredItem<BlockItem> getElemetalBlockItem(Element element) {
        return ELEMETAL_BLOCK_ITEMS_BY_KEY.get(elementKey(element));
    }

    /** Resolves the base element represented by an Elemetal Block state. */
    public static Optional<Element> getElemetalElement(BlockState state) {
        return ElemancyElementKeys.BASE_ELEMENT_KEYS.stream()
                .filter(key -> ELEMETAL_BLOCKS_BY_KEY.get(key).get() == state.getBlock())
                .map(key -> ElementRegistry.ELEMENT_REGISTRY.getValue(
                        Identifier.fromNamespaceAndPath("soulmark", key)))
                .filter(Objects::nonNull)
                .findFirst();
    }

    /** All registered Elemetal Blocks, keyed by their element registry key string - for datagen looping without needing to resolve Element objects. */
    public static Map<String, DeferredBlock<Block>> getElemetalBlocksByKey() {
        return ELEMETAL_BLOCKS_BY_KEY;
    }

    // ==================== HELPERS ====================

    /** Shared properties for fragile magical plants (no collision, instabreak, random ticks). */
    private static BlockBehaviour.Properties plantProperties(BlockBehaviour.Properties props, SoundType soundType) {
        return props
                .mapColor(MapColor.PLANT)
                .noCollision()
                .instabreak()
                .sound(soundType)
                .pushReaction(PushReaction.DESTROY)
                .randomTicks();
    }

    /** Shared properties for log/wood blocks. */
    private static BlockBehaviour.Properties logProperties(BlockBehaviour.Properties props, MapColor topColor, MapColor sideColor) {
        return props
                .mapColor(state -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? topColor : sideColor)
                .strength(2.0f)
                .sound(SoundType.WOOD)
                .ignitedByLava();
    }

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        BLOCK_ITEMS.register(modEventBus);
    }
}





