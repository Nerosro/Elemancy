package be.nerosro.elemancy.datagen;

import java.util.List;
import java.util.Optional;

import be.nerosro.elemancy.Elemancy;
import be.nerosro.elemancy.block.ElemancyBlocks;
import be.nerosro.elemancy.client.AffinityPaperTintSource;
import be.nerosro.elemancy.client.lightshield.LightShieldSpecialRenderer;
import be.nerosro.elemancy.items.ElemancyItems;
import be.nerosro.elemancy.items.tools.darkbucket.DarkBucketContents;
import be.nerosro.soulmark.element.Element;
import be.nerosro.soulmark.element.ElementRegistry;
import be.nerosro.soulmark.element.SoulmarkElements;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.client.renderer.item.properties.select.CustomModelDataProperty;
import net.minecraft.client.renderer.item.properties.select.DisplayContext;
import net.minecraft.client.renderer.special.ShieldSpecialRenderer;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmlandBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.model.item.DynamicFluidContainerModel;

public class ElemancyModelProvider extends ModelProvider {

    public ElemancyModelProvider(PackOutput output) {
        super(output, Elemancy.MOD_ID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        // === Logs & Wood ===
        blockModels.woodProvider(ElemancyBlocks.ASHEN_LOG.get())
            .logWithHorizontal(ElemancyBlocks.ASHEN_LOG.get())
            .wood(ElemancyBlocks.ASHEN_WOOD.get());
        blockModels.woodProvider(ElemancyBlocks.STRIPPED_ASHEN_LOG.get())
            .logWithHorizontal(ElemancyBlocks.STRIPPED_ASHEN_LOG.get())
            .wood(ElemancyBlocks.STRIPPED_ASHEN_WOOD.get());

        // === Planks + Family ===
        blockModels.family(ElemancyBlocks.ASHEN_PLANKS.get())
            .stairs(ElemancyBlocks.ASHEN_STAIRS.get())
            .slab(ElemancyBlocks.ASHEN_SLAB.get())
            .fence(ElemancyBlocks.ASHEN_FENCE.get())
            .fenceGate(ElemancyBlocks.ASHEN_FENCE_GATE.get())
            .pressurePlate(ElemancyBlocks.ASHEN_PRESSURE_PLATE.get())
            .button(ElemancyBlocks.ASHEN_BUTTON.get())
            .trapdoor(ElemancyBlocks.ASHEN_TRAPDOOR.get());

        // Door separately because of texture location, .door() assumes texture is item/ashen_door, cannot use wood/ subfolder
        Block ashenDoor = ElemancyBlocks.ASHEN_DOOR.get();
        TextureMapping doorTextures = TextureMapping.door(ashenDoor);
        blockModels.blockStateOutput.accept(BlockModelGenerators.createDoor(ashenDoor,
            doorVariant(blockModels, ashenDoor, ModelTemplates.DOOR_BOTTOM_LEFT, doorTextures),
            doorVariant(blockModels, ashenDoor, ModelTemplates.DOOR_BOTTOM_LEFT_OPEN, doorTextures),
            doorVariant(blockModels, ashenDoor, ModelTemplates.DOOR_BOTTOM_RIGHT, doorTextures),
            doorVariant(blockModels, ashenDoor, ModelTemplates.DOOR_BOTTOM_RIGHT_OPEN, doorTextures),
            doorVariant(blockModels, ashenDoor, ModelTemplates.DOOR_TOP_LEFT, doorTextures),
            doorVariant(blockModels, ashenDoor, ModelTemplates.DOOR_TOP_LEFT_OPEN, doorTextures),
            doorVariant(blockModels, ashenDoor, ModelTemplates.DOOR_TOP_RIGHT, doorTextures),
            doorVariant(blockModels, ashenDoor, ModelTemplates.DOOR_TOP_RIGHT_OPEN, doorTextures)));
        generateFlatItem(itemModels, ElemancyBlocks.ASHEN_DOOR_ITEM.get(), "wood/ashen_door");

        // === Leaves ===
        blockModels.createTintedLeaves(ElemancyBlocks.ASHEN_LEAVES.get(), TexturedModel.LEAVES, 0x55FFFF);

        // === Sapling ===
        blockModels.createCrossBlockWithDefaultItem(ElemancyBlocks.ASHEN_SAPLING.get(), BlockModelGenerators.PlantType.NOT_TINTED);

        // === Infused Metal ===
        blockModels.createTrivialCube(ElemancyBlocks.INFUSED_METAL_BLOCK.get());

        // === Elemetal Blocks (self-named texture - one per base element) ===
        for (Element element : SoulmarkElements.baseElements()) {
            blockModels.createTrivialCube(ElemancyBlocks.getElemetalBlock(element).get());
        }

        // === Infused Wool ===
        blockModels.createTrivialCube(ElemancyBlocks.INFUSED_WOOL.get());

        // === Mirror ===
        MultiVariant mirror = BlockModelGenerators.plainVariant(
            Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "block/mirror"));
        MultiVariant mirrorUpper = BlockModelGenerators.plainVariant(
            Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "block/mirror_upper"));
        blockModels.blockStateOutput.accept(
            MultiVariantGenerator.dispatch(ElemancyBlocks.MIRROR.get())
                .with(PropertyDispatch.initial(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.DOUBLE_BLOCK_HALF)
                    .select(Direction.NORTH, DoubleBlockHalf.LOWER, mirror)
                    .select(Direction.SOUTH, DoubleBlockHalf.LOWER, mirror.with(BlockModelGenerators.Y_ROT_180))
                    .select(Direction.EAST, DoubleBlockHalf.LOWER, mirror.with(BlockModelGenerators.Y_ROT_90))
                    .select(Direction.WEST, DoubleBlockHalf.LOWER, mirror.with(BlockModelGenerators.Y_ROT_270))
                    .select(Direction.NORTH, DoubleBlockHalf.UPPER, mirrorUpper)
                    .select(Direction.SOUTH, DoubleBlockHalf.UPPER, mirrorUpper.with(BlockModelGenerators.Y_ROT_180))
                    .select(Direction.EAST, DoubleBlockHalf.UPPER, mirrorUpper.with(BlockModelGenerators.Y_ROT_90))
                    .select(Direction.WEST, DoubleBlockHalf.UPPER, mirrorUpper.with(BlockModelGenerators.Y_ROT_270))
                )
        );

        // === Paradox Flower ===
        blockModels.createCrossBlockWithDefaultItem(ElemancyBlocks.PARADOX_FLOWER.get(), BlockModelGenerators.PlantType.NOT_TINTED);

        // === Enriched farmland ===
        TextureMapping farmlandDry = new TextureMapping()
            .put(TextureSlot.DIRT, TextureMapping.getBlockTexture(Blocks.DIRT))
            .put(TextureSlot.TOP, new Material(Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "block/enriched_farmland")));
        TextureMapping farmlandWet = new TextureMapping()
            .put(TextureSlot.DIRT, TextureMapping.getBlockTexture(Blocks.DIRT))
            .put(TextureSlot.TOP, new Material(Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "block/enriched_farmland_moist")));
        MultiVariant dryFarmland = BlockModelGenerators.plainVariant(ModelTemplates.FARMLAND.create(
            ElemancyBlocks.ENRICHED_FARMLAND.get(), farmlandDry, blockModels.modelOutput));
        MultiVariant wetFarmland = BlockModelGenerators.plainVariant(ModelTemplates.FARMLAND.createWithSuffix(
            ElemancyBlocks.ENRICHED_FARMLAND.get(), "_moist", farmlandWet, blockModels.modelOutput));
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(ElemancyBlocks.ENRICHED_FARMLAND.get())
            .with(BlockModelGenerators.createEmptyOrFullDispatch(FarmlandBlock.MOISTURE, 7, wetFarmland, dryFarmland)));

        generateCrop(blockModels, ElemancyBlocks.STRAWBERRY.get(), "strawberry_stage",
            new int[]{0, 1, 2, 3, 3, 4, 4, 5});
        generateCrop(blockModels, ElemancyBlocks.YAM.get(), "yam");
        generateCrop(blockModels, ElemancyBlocks.MANA_CARROT.get(), "mana_carrot");

        // === Soft Glow (invisible light block - just use simple cube) ===
        blockModels.createTrivialCube(ElemancyBlocks.SOFT_GLOW.get());

        // === Items ===
        generateFlatItem(itemModels, ElemancyItems.STRAWBERRY.get(), "food/strawberry");
        generateFlatItem(itemModels, ElemancyItems.STRAWBERRY_SEEDS.get(), "food/strawberry_seeds");
        generateFlatItem(itemModels, ElemancyItems.YAM.get(), "food/yam");
        generateFlatItem(itemModels, ElemancyItems.MANA_CARROT.get(), "food/mana_carrot");
        generateFlatItem(itemModels, ElemancyItems.ASHEN_STICK.get(), "wood/ashen_stick");
        generateFlatItem(itemModels, ElemancyItems.ASHEN_WAND.get(), "wands/ashen_wand");
        generateFlatItem(itemModels, ElemancyItems.ENERGIZED_STICK.get(), "wands/energized_stick");
        generateFlatItem(itemModels, ElemancyItems.INFUSED_INGOT.get(), "materials/infused_ingot");
        // === Elemetal Ingots (self-named texture, one per base element) ===
        for (Element element : SoulmarkElements.baseElements()) {
            Identifier elementId = ElementRegistry.ELEMENT_REGISTRY.getKey(element);
            if (elementId != null) {
                generateFlatItem(itemModels, ElemancyItems.getElemetalIngot(element).get(), "materials/elemetal_ingot_" + elementId.getPath());
            }
        }
        generateFlatItem(itemModels, ElemancyItems.PROPOLIS.get(), "utility/propolis");
        generateFlatItem(itemModels, ElemancyItems.ARCANE_VESSEL.get(), "utility/arcane_vessel");
        generateFlatItem(itemModels, ElemancyItems.TOME.get(), "utility/tome");

        // Affinity Paper has a standard single-layer model, so datagen creates both its model and tint-aware item definition.
        Identifier affinityPaperModel = ModelTemplates.FLAT_ITEM.create(
            ElemancyItems.AFFINITY_PAPER.get(),
            TextureMapping.layer0(material("utility/affinity_paper")),
            itemModels.modelOutput);
        itemModels.itemModelOutput.accept(ElemancyItems.AFFINITY_PAPER.get(),
            ItemModelUtils.tintedModel(affinityPaperModel, new AffinityPaperTintSource()));

        // Soulvials share a hand-authored layered model. Each registered variant gets a fixed
        // content tint so no per-element textures or stack data are needed.
        Identifier soulvialModel = Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "item/soulvial");
        itemModels.itemModelOutput.accept(ElemancyItems.SOULVIAL.get(),
            ItemModelUtils.tintedModel(soulvialModel,
                ItemModelUtils.constantTint(-1), ItemModelUtils.constantTint(-1)));
        for (Element element : SoulmarkElements.baseElements()) {
            itemModels.itemModelOutput.accept(ElemancyItems.getAttunedSoulvial(element).get(),
                ItemModelUtils.tintedModel(soulvialModel,
                    ItemModelUtils.constantTint(-1), ItemModelUtils.constantTint(element.argb())));
        }

        // === Robes/armour ===
        generateFlatItem(itemModels, ElemancyItems.ROBE_HELMET.get(), "apparel/robe_helmet");
        generateFlatItem(itemModels, ElemancyItems.ROBE_CHESTPLATE.get(), "apparel/robe_chestplate");
        generateFlatItem(itemModels, ElemancyItems.ROBE_LEGGINGS.get(), "apparel/robe_leggings");
        generateFlatItem(itemModels, ElemancyItems.ROBE_BOOTS.get(), "apparel/robe_boots");

        // === Elemetal tools ===
        generateHandheldItem(itemModels, ElemancyItems.INFUSED_PICKAXE.get(), "tools/infused_pickaxe");

        // Fire
        generateFlatItem(itemModels, ElemancyItems.FIRE_STRIKER.get(), "tools/fire_striker");

        Identifier fireSwordUnlit = fireSwordModel(itemModels, 0);
        Identifier fireSwordOneHeat = fireSwordModel(itemModels, 1);
        Identifier fireSwordTwoHeat = fireSwordModel(itemModels, 2);
        Identifier fireSwordThreeHeat = fireSwordModel(itemModels, 3);
        itemModels.itemModelOutput.accept(ElemancyItems.FIRE_SWORD.get(),
            ItemModelUtils.select(new CustomModelDataProperty(0), ItemModelUtils.plainModel(fireSwordUnlit),
                ItemModelUtils.when("1", ItemModelUtils.plainModel(fireSwordOneHeat)),
                ItemModelUtils.when("2", ItemModelUtils.plainModel(fireSwordTwoHeat)),
                ItemModelUtils.when("3", ItemModelUtils.plainModel(fireSwordThreeHeat))));

        // Earth
        generateHandheldItem(itemModels, ElemancyItems.EARTH_PICKAXE.get(), "tools/earth_pickaxe");
        generateHandheldItem(itemModels, ElemancyItems.EARTH_SHOVEL.get(), "tools/earth_shovel");

        // Water
        generateHandheldItem(itemModels, ElemancyItems.WATER_HOE.get(), "tools/water_hoe");

        // Air
        generateHandheldItem(itemModels, ElemancyItems.AIR_AXE.get(), "tools/air_axe");
        Identifier airSpearModel = ModelTemplates.FLAT_ITEM.create(
            ElemancyItems.AIR_SPEAR.get(), TextureMapping.layer0(material("tools/air_spear")), itemModels.modelOutput);
        Identifier airSpearInHandModel = ModelTemplates.SPEAR_IN_HAND.create(
            Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "item/air_spear_in_hand"),
            TextureMapping.layer0(material("tools/air_spear_in_hand")), itemModels.modelOutput);
        itemModels.itemModelOutput.accept(ElemancyItems.AIR_SPEAR.get(), ItemModelUtils.select(
            new DisplayContext(),
            ItemModelUtils.plainModel(airSpearInHandModel),
            ItemModelUtils.when(List.of(
                ItemDisplayContext.GUI,
                ItemDisplayContext.GROUND,
                ItemDisplayContext.FIXED,
                ItemDisplayContext.ON_SHELF
            ), ItemModelUtils.plainModel(airSpearModel))
        ));

        // Light
        var lightShieldModel = ItemModelUtils.specialModel(
            Identifier.withDefaultNamespace("item/shield"), LightShieldSpecialRenderer.Unbaked.INSTANCE);
        var lightShieldBlockingModel = ItemModelUtils.specialModel(
            Identifier.withDefaultNamespace("item/shield_blocking"), LightShieldSpecialRenderer.Unbaked.INSTANCE);
        itemModels.itemModelOutput.accept(ElemancyItems.LIGHT_SHIELD.get(), ItemModelUtils.conditional(
            ShieldSpecialRenderer.DEFAULT_TRANSFORMATION,
            ItemModelUtils.isUsingItem(),
            lightShieldBlockingModel,
            lightShieldModel
        ));
        generateFlatItem(itemModels, ElemancyItems.LIGHT_SHEARS.get(), "tools/light_shears");

        // Dark
        // Dark Bucket uses authored variants for familiar vanilla contents and NeoForge's
        // fluid-container renderer for compatible modded fluids.
        Identifier darkBucketEmpty = darkBucketModel(itemModels, "empty");
        Identifier darkBucketWater = darkBucketModel(itemModels, "water");
        Identifier darkBucketLava = darkBucketModel(itemModels, "lava");
        Identifier darkBucketMilk = darkBucketModel(itemModels, "milk");
        Identifier darkBucketPowderSnow = darkBucketModel(itemModels, "powder_snow");
        Material darkBucketFrame = material("tools/dark_bucket_empty");
        Material fluidMask = new Material(Identifier.fromNamespaceAndPath("neoforge", "item/mask/bucket_fluid"));
        var dynamicFluidModel = new DynamicFluidContainerModel.Unbaked(
            new DynamicFluidContainerModel.Textures(
                Optional.of(darkBucketFrame), Optional.empty(), Optional.of(fluidMask), Optional.of(darkBucketFrame)),
            Fluids.EMPTY, false, false, true);
        itemModels.itemModelOutput.accept(ElemancyItems.DARK_BUCKET.get(),
            ItemModelUtils.select(new CustomModelDataProperty(0), dynamicFluidModel,
                ItemModelUtils.when(DarkBucketContents.MODEL_EMPTY, ItemModelUtils.plainModel(darkBucketEmpty)),
                ItemModelUtils.when(DarkBucketContents.MODEL_WATER, ItemModelUtils.plainModel(darkBucketWater)),
                ItemModelUtils.when(DarkBucketContents.MODEL_LAVA, ItemModelUtils.plainModel(darkBucketLava)),
                ItemModelUtils.when(DarkBucketContents.MODEL_MILK, ItemModelUtils.plainModel(darkBucketMilk)),
                ItemModelUtils.when(DarkBucketContents.MODEL_POWDER_SNOW, ItemModelUtils.plainModel(darkBucketPowderSnow))));

        // === Consumables ===
        generateFlatItem(itemModels, ElemancyItems.ICECREAM_COCOA.get(), "utility/icecream_cocoa");
        generateFlatItem(itemModels, ElemancyItems.ICECREAM_STRAWBERRY.get(), "utility/icecream_strawberry");

        // === Trinkets ===
        generateFlatItem(itemModels, ElemancyItems.AMULET_OF_DEEP_FOCUS.get(), "trinkets/amulet_of_deep_focus");
        generateFlatItem(itemModels, ElemancyItems.CHARM_OF_STEADY_FLOW.get(), "trinkets/charm_of_steady_flow");
        generateFlatItem(itemModels, ElemancyItems.BRACELET_OF_ENDURING_MANA.get(), "trinkets/bracelet_of_enduring_mana");
        generateFlatItem(itemModels, ElemancyItems.BELT_OF_ROLLING_TIDES.get(), "trinkets/belt_of_rolling_tides");
        generateFlatItem(itemModels, ElemancyItems.NECKLACE_OF_SUNKEN_RESERVES.get(), "trinkets/necklace_of_sunken_reserves");
        generateFlatItem(itemModels, ElemancyItems.GAUNTLET_OF_SUBTLE_WEAVE.get(), "trinkets/gauntlet_of_subtle_weave");
    }

    // === Helper methods ===
    //TODO helper method might become redundant, check to remove later
    private static void generateCrop(BlockModelGenerators blockModels, Block crop, String name) {
        generateCrop(blockModels, crop, name + "_stage_", new int[]{0, 0, 1, 1, 2, 2, 2, 3});
    }

    private static void generateCrop(BlockModelGenerators blockModels, Block crop, String texturePrefix, int[] ageToStage) {
        MultiVariant[] stages = new MultiVariant[ageToStage[CropBlock.MAX_AGE] + 1];
        for (int stage = 0; stage < stages.length; stage++) {
            Identifier texture = Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "block/" + texturePrefix + stage);
            stages[stage] = BlockModelGenerators.plainVariant(ModelTemplates.CROP.create(texture,
                new TextureMapping().put(TextureSlot.CROP, new Material(texture)), blockModels.modelOutput));
        }

        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(crop)
            .with(PropertyDispatch.initial(CropBlock.AGE).generate(age -> stages[ageToStage[age]])));
    }

    private static MultiVariant doorVariant(BlockModelGenerators blockModels, Block door, ModelTemplate template, TextureMapping textures) {
        return BlockModelGenerators.plainVariant(template.create(door, textures, blockModels.modelOutput));
    }

    private static void generateFlatItem(ItemModelGenerators itemModels, Item item, String texturePath) {
        Identifier modelId = ModelTemplates.FLAT_ITEM.create(item, TextureMapping.layer0(material(texturePath)), itemModels.modelOutput);
        itemModels.itemModelOutput.accept(item, ItemModelUtils.plainModel(modelId));
    }

    private static void generateHandheldItem(ItemModelGenerators itemModels, Item item, String texturePath) {
        Identifier modelId = ModelTemplates.FLAT_HANDHELD_ITEM.create(item, TextureMapping.layer0(material(texturePath)), itemModels.modelOutput);
        itemModels.itemModelOutput.accept(item, ItemModelUtils.plainModel(modelId));
    }

    private static Material material(String path) {
        return new Material(Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "item/" + path));
    }

    private static Identifier darkBucketModel(ItemModelGenerators itemModels, String variant) {
        Identifier modelId = Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "item/dark_bucket_" + variant);
        return ModelTemplates.FLAT_ITEM.create(modelId, TextureMapping.layer0(material("tools/dark_bucket_" + variant)), itemModels.modelOutput);
    }

    private static Identifier fireSwordModel(ItemModelGenerators itemModels, int heat) {
        Identifier modelId = Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "item/fire_sword_" + heat);
        return ModelTemplates.FLAT_HANDHELD_ITEM.create(modelId, TextureMapping.layer0(material("tools/fire_sword_" + heat)), itemModels.modelOutput);
    }
}
