package io.github.nerosro.elemancy.block;

import io.github.nerosro.elemancy.Elemancy;
import io.github.nerosro.elemancy.block.custom.CustomFlowerBlock;
import io.github.nerosro.elemancy.block.custom.StrawberryCropBlock;
import io.github.nerosro.elemancy.item.ModItems;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

import static io.github.nerosro.elemancy.item.ModCreativeModeTab.addToTab;

/**
 * Created by Nerosro on 2/10/2023.
 */
public class ModBlocks{
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, Elemancy.MOD_ID);

    public static void register(IEventBus eventBus){
        BLOCKS.register(eventBus);
    }

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block){
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    //This registers your blocks as items as well, so you don't need to make an item version for it in ModItems
    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
        //Additional addToTab() adds the newly created ITEM to the creative tab menu as well
        return addToTab(ModItems.ITEMS.register(name,
                () -> new BlockItem(block.get(), new Item.Properties())));
    }


    /**---------------------
     * | Custom items here |
     * ---------------------
     * Order of items matters for Creative menu sorting
     */

    public static final RegistryObject<Block> ASHEN_LOG = registerBlock("ashen_log",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LOG))
    );
    public static final RegistryObject<Block> ASHEN_WOOD = registerBlock("ashen_planks",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS))
    );
    /*
    public static final RegistryObject<Block> STRIPPED_ASHEN_LOG = registerBlock("stripped_ashen_log",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.STRIPPED_OAK_LOG).strength(3f)));
    public static final RegistryObject<Block> STRIPPED_ASHEN_WOOD = registerBlock("stripped_ashen_wood",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.STRIPPED_OAK_WOOD).strength(3f)));
    */
    public static final RegistryObject<Block> ASHEN_STAIRS = registerBlock("ashen_stairs",
            () -> new StairBlock(() -> ModBlocks.ASHEN_WOOD.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.OAK_STAIRS))
    );
    public static final RegistryObject<Block> ASHEN_SLAB = registerBlock("ashen_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(Blocks.OAK_SLAB))
    );
    public static final RegistryObject<Block> ASHEN_BUTTON = registerBlock("ashen_button",
            () -> new ButtonBlock(BlockBehaviour.Properties.copy(Blocks.OAK_BUTTON), BlockSetType.OAK, 10, true)
    );
    public static final RegistryObject<Block> ASHEN_PRESSURE_PLATE = registerBlock("ashen_pressure_plate",
            () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, BlockBehaviour.Properties.copy(Blocks.OAK_PRESSURE_PLATE), BlockSetType.OAK)
    );
    public static final RegistryObject<Block> ASHEN_FENCE = registerBlock("ashen_fence",
            () -> new FenceBlock(BlockBehaviour.Properties.copy(Blocks.OAK_STAIRS))
    );
    public static final RegistryObject<Block> ASHEN_FENCE_GATE = registerBlock("ashen_fence_gate",
            () -> new FenceGateBlock(BlockBehaviour.Properties.copy(Blocks.OAK_FENCE_GATE), SoundEvents.WOODEN_DOOR_OPEN, SoundEvents.WOODEN_DOOR_CLOSE)
    );
    public static final RegistryObject<Block> ASHEN_DOOR = registerBlock("ashen_door",
            () -> new DoorBlock(BlockBehaviour.Properties.copy(Blocks.OAK_DOOR), BlockSetType.OAK)
    );
    public static final RegistryObject<Block> ASHEN_TRAPDOOR = registerBlock("ashen_trapdoor",
            () -> new TrapDoorBlock(BlockBehaviour.Properties.copy(Blocks.OAK_TRAPDOOR), BlockSetType.OAK)
    );

    public static final RegistryObject<Block> STRAWBERRY_CROP = BLOCKS.register("strawberry_crop",
            () -> new StrawberryCropBlock(BlockBehaviour.Properties.copy(Blocks.CARROTS).noOcclusion().noCollission())
    );

    public static final RegistryObject<Block> INFUSED_WOOL = registerBlock("infused_wool",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.LIGHT_BLUE_WOOL)
                    .lightLevel(state -> 8)
            )
    );

    public static final RegistryObject<Block> MANA_FLOWER = registerBlock("mana_flower",
            () -> new CustomFlowerBlock(() -> MobEffects.DARKNESS, 5,  //TODO make own custom Mana regen effect here
                    BlockBehaviour.Properties.copy(Blocks.ALLIUM)
                            .noOcclusion()
                            .noCollission()
            )
    );
    public static final RegistryObject<Block> POTTED_MANA_FLOWER = BLOCKS.register("potted_mana_flower",
            () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, ModBlocks.MANA_FLOWER,
                    BlockBehaviour.Properties.copy(Blocks.POTTED_ALLIUM)
            )
    );
}
