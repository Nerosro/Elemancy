package be.nerosro.elemancy.datagen;

import java.util.Optional;

import be.nerosro.elemancy.Elemancy;
import be.nerosro.elemancy.block.ElemancyBlocks;
import be.nerosro.elemancy.client.AffinityPaperTintSource;
import be.nerosro.elemancy.items.ElemancyItems;
import be.nerosro.elemancy.items.tools.darkbucket.DarkBucketContents;
import be.nerosro.soulmark.element.Element;
import be.nerosro.soulmark.element.SoulmarkElements;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.client.renderer.item.properties.select.CustomModelDataProperty;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
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
            .door(ElemancyBlocks.ASHEN_DOOR.get())
            .pressurePlate(ElemancyBlocks.ASHEN_PRESSURE_PLATE.get())
            .button(ElemancyBlocks.ASHEN_BUTTON.get())
            .trapdoor(ElemancyBlocks.ASHEN_TRAPDOOR.get());

        // === Leaves ===
        blockModels.createTintedLeaves(ElemancyBlocks.ASHEN_LEAVES.get(), TexturedModel.LEAVES, 0x55FFFF);

        // === Sapling ===
        blockModels.createCrossBlockWithDefaultItem(ElemancyBlocks.ASHEN_SAPLING.get(), BlockModelGenerators.PlantType.NOT_TINTED);

        // === Infused Metal ===
        blockModels.createTrivialCube(ElemancyBlocks.INFUSED_METAL_BLOCK.get());

        // === Elemetal Blocks (placeholder self-named texture, like other not-yet-textured
        // content in this file - one per base element) ===
        for (Element element : SoulmarkElements.baseElements()) {
            blockModels.createTrivialCube(ElemancyBlocks.getElemetalBlock(element).get());
        }

        // === Infused Wool ===
        blockModels.createTrivialCube(ElemancyBlocks.INFUSED_WOOL.get());

        // === Mirror (double-block with facing, placeholder cube model) ===
        TextureMapping mirrorTexture = TextureMapping.cube(ElemancyBlocks.INFUSED_METAL_BLOCK.get());
        Identifier mirrorBottomModel = ModelTemplates.CUBE_ALL.createWithSuffix(
            ElemancyBlocks.MIRROR.get(), "_bottom", mirrorTexture, blockModels.modelOutput);
        Identifier mirrorTopModel = ModelTemplates.CUBE_ALL.createWithSuffix(
            ElemancyBlocks.MIRROR.get(), "_top", mirrorTexture, blockModels.modelOutput);
        MultiVariant mirrorBottom = BlockModelGenerators.plainVariant(mirrorBottomModel);
        MultiVariant mirrorTop = BlockModelGenerators.plainVariant(mirrorTopModel);
        blockModels.blockStateOutput.accept(
            MultiVariantGenerator.dispatch(ElemancyBlocks.MIRROR.get())
                .with(PropertyDispatch.initial(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.DOUBLE_BLOCK_HALF)
                    .select(Direction.NORTH, DoubleBlockHalf.LOWER, mirrorBottom)
                    .select(Direction.SOUTH, DoubleBlockHalf.LOWER, mirrorBottom.with(BlockModelGenerators.Y_ROT_180))
                    .select(Direction.EAST, DoubleBlockHalf.LOWER, mirrorBottom.with(BlockModelGenerators.Y_ROT_90))
                    .select(Direction.WEST, DoubleBlockHalf.LOWER, mirrorBottom.with(BlockModelGenerators.Y_ROT_270))
                    .select(Direction.NORTH, DoubleBlockHalf.UPPER, mirrorTop)
                    .select(Direction.SOUTH, DoubleBlockHalf.UPPER, mirrorTop.with(BlockModelGenerators.Y_ROT_180))
                    .select(Direction.EAST, DoubleBlockHalf.UPPER, mirrorTop.with(BlockModelGenerators.Y_ROT_90))
                    .select(Direction.WEST, DoubleBlockHalf.UPPER, mirrorTop.with(BlockModelGenerators.Y_ROT_270))
                )
        );

        // === Paradox Flower ===
        blockModels.createCrossBlockWithDefaultItem(ElemancyBlocks.PARADOX_FLOWER.get(), BlockModelGenerators.PlantType.NOT_TINTED);

        // === Soft Glow (invisible light block - just use simple cube) ===
        blockModels.createTrivialCube(ElemancyBlocks.SOFT_GLOW.get());

        // === Items ===
        itemModels.generateFlatItem(ElemancyItems.ASHEN_STICK.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ElemancyItems.ASHEN_WAND.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ElemancyItems.ENERGIZED_STICK.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ElemancyItems.INFUSED_INGOT.get(), ModelTemplates.FLAT_ITEM);
        // === Elemetal Ingots (placeholder self-named texture, one per base element) ===
        for (Element element : SoulmarkElements.baseElements()) {
            itemModels.generateFlatItem(ElemancyItems.getElemetalIngot(element).get(), ModelTemplates.FLAT_ITEM);
        }
        itemModels.generateFlatItem(ElemancyItems.PROPOLIS.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ElemancyItems.ARCANE_VESSEL.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ElemancyItems.TOME.get(), ModelTemplates.FLAT_ITEM);

        // Affinity Paper has a standard single-layer model, so datagen creates both its model and tint-aware item definition.
        Identifier affinityPaperModel = ModelTemplates.FLAT_ITEM.create(
            ElemancyItems.AFFINITY_PAPER.get(),
            TextureMapping.layer0(ElemancyItems.AFFINITY_PAPER.get()),
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

        itemModels.generateFlatItem(ElemancyItems.ROBE_HELMET.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ElemancyItems.ROBE_CHESTPLATE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ElemancyItems.ROBE_LEGGINGS.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ElemancyItems.ROBE_BOOTS.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ElemancyItems.INFUSED_PICKAXE.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(ElemancyItems.LIGHT_SHEARS.get(), ModelTemplates.FLAT_ITEM);

        // Dark Bucket uses authored variants for familiar vanilla contents and NeoForge's
        // fluid-container renderer for compatible modded fluids.
        Identifier darkBucketEmpty = darkBucketModel(itemModels, "empty");
        Identifier darkBucketWater = darkBucketModel(itemModels, "water");
        Identifier darkBucketLava = darkBucketModel(itemModels, "lava");
        Identifier darkBucketMilk = darkBucketModel(itemModels, "milk");
        Identifier darkBucketPowderSnow = darkBucketModel(itemModels, "powder_snow");
        Material darkBucketFrame = new Material(Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "item/dark_bucket_empty"));
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
        itemModels.generateFlatItem(ElemancyItems.ICECREAM_COCOA.get(), ModelTemplates.FLAT_ITEM);

        // === Trinkets ===
        itemModels.generateFlatItem(ElemancyItems.AMULET_OF_DEEP_FOCUS.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ElemancyItems.CHARM_OF_STEADY_FLOW.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ElemancyItems.BRACELET_OF_ENDURING_MANA.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ElemancyItems.BELT_OF_ROLLING_TIDES.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ElemancyItems.NECKLACE_OF_SUNKEN_RESERVES.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ElemancyItems.GAUNTLET_OF_SUBTLE_WEAVE.get(), ModelTemplates.FLAT_ITEM);
    }

    private static Identifier darkBucketModel(ItemModelGenerators itemModels, String variant) {
        Identifier modelId = Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "item/dark_bucket_" + variant);
        return ModelTemplates.FLAT_ITEM.create(modelId, TextureMapping.layer0(new Material(modelId)), itemModels.modelOutput);
    }
}
