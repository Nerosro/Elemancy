package be.nerosro.elemancy.datagen;

import java.util.concurrent.CompletableFuture;

import be.nerosro.elemancy.Elemancy;
import be.nerosro.elemancy.block.ElemancyBlocks;
import be.nerosro.elemancy.items.ElemancyItems;
import be.nerosro.soulmark.element.SoulmarkElements;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

public class ElemancyRecipeProvider extends RecipeProvider {
    public ElemancyRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    /**
     * Vanilla's helper uses unqualified recipe names, which default to the minecraft namespace.
     * Keep its recipe shape and unlock criteria while assigning Elemancy-owned recipe keys.
     */
    @Override
    protected void nineBlockStorageRecipes(
        RecipeCategory unpackedCategory,
        ItemLike unpacked,
        RecipeCategory packedCategory,
        ItemLike packed
    ) {
        shapeless(unpackedCategory, unpacked, 9)
            .requires(packed)
            .unlockedBy(getHasName(packed), has(packed))
            .save(output, recipeKey(getItemName(unpacked)));

        shaped(packedCategory, packed)
            .define('#', unpacked)
            .pattern("###")
            .pattern("###")
            .pattern("###")
            .unlockedBy(getHasName(unpacked), has(unpacked))
            .save(output, recipeKey(getItemName(packed)));
    }

    private static ResourceKey<net.minecraft.world.item.crafting.Recipe<?>> recipeKey(String path) {
        return ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, path));
    }

    @Override
    protected void buildRecipes() {
        // Ashen Log -> 4 Ashen Planks (shapeless, like vanilla)
        shapeless(RecipeCategory.BUILDING_BLOCKS, ElemancyBlocks.ASHEN_PLANKS_ITEM.get(), 4)
            .requires(ElemancyBlocks.ASHEN_LOG_ITEM.get())
            .unlockedBy("has_ashen_log", has(ElemancyBlocks.ASHEN_LOG_ITEM.get()))
            .save(this.output);

        // 2 Ashen Planks -> 4 Ashen Sticks (shaped, like vanilla sticks)
        shaped(RecipeCategory.MISC, ElemancyItems.ASHEN_STICK.get(), 4)
            .pattern("P")
            .pattern("P")
            .define('P', ElemancyBlocks.ASHEN_PLANKS_ITEM.get())
            .unlockedBy("has_ashen_planks", has(ElemancyBlocks.ASHEN_PLANKS_ITEM.get()))
            .save(this.output);

        // Ashen Wand (shaped 3x3)
        shaped(RecipeCategory.TOOLS, ElemancyItems.ASHEN_WAND.get(), 1)
            .pattern(" HA")
            .pattern(" SH")
            .pattern("S  ")
            .define('H', ElemancyItems.PROPOLIS.get())
            .define('A', Items.AMETHYST_SHARD)
            .define('S', ElemancyItems.ASHEN_STICK.get())
            .unlockedBy("has_ashen_stick", has(ElemancyItems.ASHEN_STICK.get()))
            .save(this.output);

        // Arcane Vessel (shaped: glass block base with amethyst and propolis)
        shaped(RecipeCategory.MISC, ElemancyItems.ARCANE_VESSEL.get())
            .pattern(" A ")
            .pattern("GHG")
            .pattern(" G ")
            .define('A', Items.AMETHYST_SHARD)
            .define('H', ElemancyItems.PROPOLIS.get())
            .define('G', Blocks.GLASS)
            .unlockedBy("has_amethyst_shard", has(Items.AMETHYST_SHARD))
            .save(this.output);

        // === Infused Metal Block ===
        nineBlockStorageRecipes(RecipeCategory.BUILDING_BLOCKS, ElemancyItems.INFUSED_INGOT.get(),
            RecipeCategory.MISC, ElemancyBlocks.INFUSED_METAL_BLOCK_ITEM.get());

        // === Elemetal Block <-> Ingot (1 block <-> 9 ingots), one pair per base element ===
        for (var element : SoulmarkElements.baseElements()) {
            nineBlockStorageRecipes(RecipeCategory.BUILDING_BLOCKS, ElemancyItems.getElemetalIngot(element).get(),
                RecipeCategory.MISC, ElemancyBlocks.getElemetalBlockItem(element).get());
        }

        // === Infused Robes (leather armor shapes, Infused Wool instead of leather) ===
        shaped(RecipeCategory.COMBAT, ElemancyItems.ROBE_HELMET.get())
            .pattern("WWW")
            .pattern("W W")
            .define('W', ElemancyBlocks.INFUSED_WOOL_ITEM.get())
            .unlockedBy("has_infused_wool", has(ElemancyBlocks.INFUSED_WOOL_ITEM.get()))
            .save(this.output);

        shaped(RecipeCategory.COMBAT, ElemancyItems.ROBE_CHESTPLATE.get())
            .pattern("W W")
            .pattern("WWW")
            .pattern("WWW")
            .define('W', ElemancyBlocks.INFUSED_WOOL_ITEM.get())
            .unlockedBy("has_infused_wool", has(ElemancyBlocks.INFUSED_WOOL_ITEM.get()))
            .save(this.output);

        shaped(RecipeCategory.COMBAT, ElemancyItems.ROBE_LEGGINGS.get())
            .pattern("WWW")
            .pattern("W W")
            .pattern("W W")
            .define('W', ElemancyBlocks.INFUSED_WOOL_ITEM.get())
            .unlockedBy("has_infused_wool", has(ElemancyBlocks.INFUSED_WOOL_ITEM.get()))
            .save(this.output);

        shaped(RecipeCategory.COMBAT, ElemancyItems.ROBE_BOOTS.get())
            .pattern("W W")
            .pattern("W W")
            .define('W', ElemancyBlocks.INFUSED_WOOL_ITEM.get())
            .unlockedBy("has_infused_wool", has(ElemancyBlocks.INFUSED_WOOL_ITEM.get()))
            .save(this.output);

        // === Infused Pickaxe ===
        shaped(RecipeCategory.TOOLS, ElemancyItems.INFUSED_PICKAXE.get())
            .pattern("III")
            .pattern(" S ")
            .pattern(" S ")
            .define('I', ElemancyItems.INFUSED_INGOT.get())
            .define('S', ElemancyItems.ASHEN_STICK.get())
            .unlockedBy("has_infused_ingot", has(ElemancyItems.INFUSED_INGOT.get()))
            .save(this.output);

        // === Dark Bucket ===
        shaped(RecipeCategory.TOOLS, ElemancyItems.DARK_BUCKET.get())
            .pattern("D D")
            .pattern(" D ")
            .define('D', ElemancyItems.getElemetalIngot(SoulmarkElements.DARK.get()).get())
            .unlockedBy("has_dark_elemetal", has(ElemancyItems.getElemetalIngot(SoulmarkElements.DARK.get()).get()))
            .save(this.output);

        // === Wood Derivatives ===
        Ingredient planks = Ingredient.of(ElemancyBlocks.ASHEN_PLANKS_ITEM.get());

        stairBuilder(ElemancyBlocks.ASHEN_STAIRS_ITEM.get(), planks)
            .group("wooden_stairs")
            .unlockedBy("has_ashen_planks", has(ElemancyBlocks.ASHEN_PLANKS_ITEM.get()))
            .save(this.output);

        slabBuilder(RecipeCategory.BUILDING_BLOCKS, ElemancyBlocks.ASHEN_SLAB_ITEM.get(), planks)
            .group("wooden_slab")
            .unlockedBy("has_ashen_planks", has(ElemancyBlocks.ASHEN_PLANKS_ITEM.get()))
            .save(this.output);

        fenceBuilder(ElemancyBlocks.ASHEN_FENCE_ITEM.get(), planks)
            .group("wooden_fence")
            .unlockedBy("has_ashen_planks", has(ElemancyBlocks.ASHEN_PLANKS_ITEM.get()))
            .save(this.output);

        fenceGateBuilder(ElemancyBlocks.ASHEN_FENCE_GATE_ITEM.get(), planks)
            .group("wooden_fence_gate")
            .unlockedBy("has_ashen_planks", has(ElemancyBlocks.ASHEN_PLANKS_ITEM.get()))
            .save(this.output);

        doorBuilder(ElemancyBlocks.ASHEN_DOOR_ITEM.get(), planks)
            .group("wooden_door")
            .unlockedBy("has_ashen_planks", has(ElemancyBlocks.ASHEN_PLANKS_ITEM.get()))
            .save(this.output);

        trapdoorBuilder(ElemancyBlocks.ASHEN_TRAPDOOR_ITEM.get(), planks)
            .group("wooden_trapdoor")
            .unlockedBy("has_ashen_planks", has(ElemancyBlocks.ASHEN_PLANKS_ITEM.get()))
            .save(this.output);

        pressurePlateBuilder(RecipeCategory.REDSTONE, ElemancyBlocks.ASHEN_PRESSURE_PLATE_ITEM.get(), planks)
            .group("wooden_pressure_plate")
            .unlockedBy("has_ashen_planks", has(ElemancyBlocks.ASHEN_PLANKS_ITEM.get()))
            .save(this.output);

        buttonBuilder(ElemancyBlocks.ASHEN_BUTTON_ITEM.get(), planks)
            .group("wooden_button")
            .unlockedBy("has_ashen_planks", has(ElemancyBlocks.ASHEN_PLANKS_ITEM.get()))
            .save(this.output);

        // === Consumables ===
        shaped(RecipeCategory.FOOD, ElemancyItems.ICECREAM_COCOA.get())
            .pattern(" C ")
            .pattern(" S ")
            .pattern(" B ")
            .define('C', Items.COCOA_BEANS)
            .define('S', Items.SNOWBALL)
            .define('B', Items.BOWL)
            .unlockedBy("has_cocoa_beans", has(Items.COCOA_BEANS))
            .save(this.output);
    }

    public static class Runner extends RecipeProvider.Runner {

        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
            super(output, registries);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new ElemancyRecipeProvider(registries, output);
        }

        @Override
        public String getName() {
            return "Elemancy Recipes";
        }
    }
}
