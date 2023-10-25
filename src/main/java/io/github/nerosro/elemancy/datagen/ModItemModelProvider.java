package io.github.nerosro.elemancy.datagen;

import io.github.nerosro.elemancy.Elemancy;
import io.github.nerosro.elemancy.block.ModBlocks;
import io.github.nerosro.elemancy.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Created by Nerosro on 8/10/2023.
 */
public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Elemancy.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        handheldItem(ModItems.ENERGIZED_STICK);
        simpleItem(ModItems.ICECREAM_COCOA);
        simpleItem(ModItems.STRAWBERRY);
        simpleItem(ModItems.STRAWBERRY_SEEDS);
        simpleItem(ModItems.INFUSED_METAL);
        simpleItem(ModItems.PINE_CONE);
        simpleBlockItemFromBlockTexture(ModBlocks.MANA_FLOWER);

        simpleBlockItem(ModBlocks.ASHEN_DOOR);
        fenceItem(ModBlocks.ASHEN_FENCE, ModBlocks.ASHEN_WOOD);
        buttonItem(ModBlocks.ASHEN_BUTTON, ModBlocks.ASHEN_WOOD);

        evenSimplerBlockItem(ModBlocks.ASHEN_STAIRS);
        evenSimplerBlockItem(ModBlocks.ASHEN_SLAB);
        evenSimplerBlockItem(ModBlocks.ASHEN_PRESSURE_PLATE);
        evenSimplerBlockItem(ModBlocks.ASHEN_FENCE_GATE);

        trapdoorItem(ModBlocks.ASHEN_TRAPDOOR);

        simpleArmorItem(ModItems.ROBE_HELMET);
        simpleArmorItem(ModItems.ROBE_CHESTPLATE);
        simpleArmorItem(ModItems.ROBE_LEGGINGS);
        simpleArmorItem(ModItems.ROBE_BOOTS);
    }

    private ItemModelBuilder simpleItem(RegistryObject<Item> item){
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(Elemancy.MOD_ID, "item/" + item.getId().getPath())
        );
    }

    private ItemModelBuilder handheldItem(RegistryObject<Item> item){
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/handheld")).texture("layer0",
                new ResourceLocation(Elemancy.MOD_ID, "item/" + item.getId().getPath())
        );
    }

    private ItemModelBuilder simpleArmorItem(RegistryObject<ArmorItem> item){
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/handheld")).texture("layer0",
                new ResourceLocation(Elemancy.MOD_ID, "item/" + item.getId().getPath())
        );
    }

    public void evenSimplerBlockItem(RegistryObject<Block> block) {
        this.withExistingParent(Elemancy.MOD_ID + ":" + ForgeRegistries.BLOCKS.getKey(block.get()).getPath(),
                modLoc("block/" + ForgeRegistries.BLOCKS.getKey(block.get()).getPath()));
    }

    public void trapdoorItem(RegistryObject<Block> block) {
        this.withExistingParent(ForgeRegistries.BLOCKS.getKey(block.get()).getPath(),
                modLoc("block/" + ForgeRegistries.BLOCKS.getKey(block.get()).getPath() + "_bottom"));
    }

    public void fenceItem(RegistryObject<Block> block, RegistryObject<Block> baseBlock) {
        this.withExistingParent(ForgeRegistries.BLOCKS.getKey(block.get()).getPath(), mcLoc("block/fence_inventory"))
                .texture("texture",  new ResourceLocation(Elemancy.MOD_ID, "block/" + ForgeRegistries.BLOCKS.getKey(baseBlock.get()).getPath()));
    }

    public void buttonItem(RegistryObject<Block> block, RegistryObject<Block> baseBlock) {
        this.withExistingParent(ForgeRegistries.BLOCKS.getKey(block.get()).getPath(), mcLoc("block/button_inventory"))
                .texture("texture",  new ResourceLocation(Elemancy.MOD_ID, "block/" + ForgeRegistries.BLOCKS.getKey(baseBlock.get()).getPath()));
    }

    public void wallItem(RegistryObject<Block> block, RegistryObject<Block> baseBlock) {
        this.withExistingParent(ForgeRegistries.BLOCKS.getKey(block.get()).getPath(), mcLoc("block/wall_inventory"))
                .texture("wall",  new ResourceLocation(Elemancy.MOD_ID, "block/" + ForgeRegistries.BLOCKS.getKey(baseBlock.get()).getPath()));
    }

    private ItemModelBuilder simpleBlockItem(RegistryObject<Block> item) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(Elemancy.MOD_ID,"item/" + item.getId().getPath()));
    }

    private ItemModelBuilder simpleBlockItemFromBlockTexture(RegistryObject<Block> item) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(Elemancy.MOD_ID,"block/" + item.getId().getPath()));
    }
}
