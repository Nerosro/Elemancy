package io.github.nerosro.elemancy.datagen;

import io.github.nerosro.elemancy.Elemancy;
import io.github.nerosro.elemancy.block.ModBlocks;
import io.github.nerosro.elemancy.block.custom.StrawberryCropBlock;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Function;
/**
 * Created by Nerosro on 8/10/2023.
 */
public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Elemancy.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        blockWithItem(ModBlocks.INFUSED_WOOL);
        //blockWithItem(ModBlocks.ASHEN_LOG);
        blockWithItem(ModBlocks.ASHEN_WOOD);

        logBlock((RotatedPillarBlock) ModBlocks.ASHEN_LOG.get());
        //axisBlock(((RotatedPillarBlock) ModBlocks.ASHEN_WOOD.get()), blockTexture(ModBlocks.ASHEN_LOG.get()), blockTexture(ModBlocks.ASHEN_LOG.get()));

        /*axisBlock(((RotatedPillarBlock) ModBlocks.STRIPPED_ASHEN_LOG.get()), blockTexture(ModBlocks.STRIPPED_ASHEN_LOG.get()),
                new ResourceLocation(Elemancy.MOD_ID, "block/stripped_ashen_log_top"));

        axisBlock(((RotatedPillarBlock) ModBlocks.STRIPPED_ASHEN_WOOD.get()), blockTexture(ModBlocks.STRIPPED_ASHEN_LOG.get()),
                blockTexture(ModBlocks.STRIPPED_ASHEN_LOG.get()));
*/
        stairsBlock(((StairBlock) ModBlocks.ASHEN_STAIRS.get()), blockTexture(ModBlocks.ASHEN_WOOD.get()));
        slabBlock(((SlabBlock) ModBlocks.ASHEN_SLAB.get()), blockTexture(ModBlocks.ASHEN_WOOD.get()), blockTexture(ModBlocks.ASHEN_WOOD.get()));

        buttonBlock(((ButtonBlock) ModBlocks.ASHEN_BUTTON.get()), blockTexture(ModBlocks.ASHEN_WOOD.get()));
        pressurePlateBlock(((PressurePlateBlock) ModBlocks.ASHEN_PRESSURE_PLATE.get()), blockTexture(ModBlocks.ASHEN_WOOD.get()));

        fenceBlock(((FenceBlock) ModBlocks.ASHEN_FENCE.get()), blockTexture(ModBlocks.ASHEN_WOOD.get()));
        fenceGateBlock(((FenceGateBlock) ModBlocks.ASHEN_FENCE_GATE.get()), blockTexture(ModBlocks.ASHEN_WOOD.get()));

        doorBlockWithRenderType(((DoorBlock) ModBlocks.ASHEN_DOOR.get()), modLoc("block/ashen_door_bottom"), modLoc("block/ashen_door_top"), "cutout");
        trapdoorBlockWithRenderType(((TrapDoorBlock) ModBlocks.ASHEN_TRAPDOOR.get()), modLoc("block/ashen_trapdoor"), true, "cutout");
        makeStrawberryCrop((CropBlock) ModBlocks.STRAWBERRY_CROP.get(), "strawberry_stage", "strawberry_stage");

        blockItem(ModBlocks.ASHEN_LOG);
        blockItem(ModBlocks.ASHEN_WOOD);
        //blockItem(ModBlocks.STRIPPED_ASHEN_LOG);
        //blockItem(ModBlocks.STRIPPED_ASHEN_WOOD);

        simpleBlockWithItem(ModBlocks.MANA_FLOWER.get(), models().cross(blockTexture(ModBlocks.MANA_FLOWER.get()).getPath(),
                blockTexture(ModBlocks.MANA_FLOWER.get())).renderType("cutout"));
        simpleBlockWithItem(ModBlocks.POTTED_MANA_FLOWER.get(), models().singleTexture("potted_mana_flower",
                new ResourceLocation("flower_pot_cross"), "plant",
                blockTexture(ModBlocks.MANA_FLOWER.get())).renderType("cutout"));
    }

    private void blockWithItem(RegistryObject<Block> blockRegistryObject){
        simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()));
    }

    private void blockItem(RegistryObject<Block> blockRegistryObject) {
        simpleBlockItem(blockRegistryObject.get(), new ModelFile.UncheckedModelFile(Elemancy.MOD_ID +
                ":block/" + ForgeRegistries.BLOCKS.getKey(blockRegistryObject.get()).getPath()));
    }

    private ConfiguredModel[] strawberryStates(BlockState blockState, CropBlock cropBlock, String modelName, String textureName){
        ConfiguredModel[] models = new ConfiguredModel[1];
        models[0] = new ConfiguredModel(models().crop(modelName + blockState.getValue(((StrawberryCropBlock) cropBlock).getAgeProperty()),
                        new ResourceLocation(Elemancy.MOD_ID, "block/" + textureName + blockState.getValue(((StrawberryCropBlock) cropBlock).getAgeProperty())))
                .renderType("cutout"));

        return models;
    }

    public void makeStrawberryCrop (CropBlock cropBlock, String modelName, String textureName){
        Function<BlockState, ConfiguredModel[]> function = state -> strawberryStates(state, cropBlock, modelName, textureName);

        getVariantBuilder(cropBlock).forAllStates(function);
    }
}
