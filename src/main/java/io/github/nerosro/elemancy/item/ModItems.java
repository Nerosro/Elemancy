package io.github.nerosro.elemancy.item;


import io.github.nerosro.elemancy.Elemancy;
import io.github.nerosro.elemancy.block.ModBlocks;
import io.github.nerosro.elemancy.item.custom.FuelItem;
import io.github.nerosro.elemancy.item.custom.IceCreamItem;
import io.github.nerosro.elemancy.item.custom.ModArmorItem;
import io.github.nerosro.elemancy.item.custom.WandItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static io.github.nerosro.elemancy.item.ModCreativeModeTab.addToTab;
/**
 * Created by Nerosro on 3/07/2023.
 */
public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Elemancy.MOD_ID);

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
    public static final RegistryObject<Item> ENERGIZED_STICK = addToTab(
            ITEMS.register("energized_stick",
                    () -> new WandItem(new Item.Properties()
                            .durability(10))
            )
    );

    public static final RegistryObject<Item> ICECREAM_COCOA = addToTab(
            ITEMS.register("icecream_cocoa",
                    () -> new IceCreamItem(new Item.Properties()
                            .food(ModFoods.ICECREAM)
                            .stacksTo(1)
                    )
            )
    );

    public static final RegistryObject<Item> STRAWBERRY = addToTab(
            ITEMS.register("strawberry",
                    () -> new Item(new Item.Properties()
                            .food(ModFoods.STRAWBERRY)
                    )
            )
    );
    public static final RegistryObject<Item> STRAWBERRY_SEEDS = addToTab(
            ITEMS.register("strawberry_seeds",
                    () -> new ItemNameBlockItem(ModBlocks.STRAWBERRY_CROP.get(), new Item.Properties())
            )
    );

    public static final RegistryObject<Item> INFUSED_METAL = addToTab(
            ITEMS.register("infused_metal",
                    () -> new Item(new Item.Properties().stacksTo(1))
            )
    );

    public static final RegistryObject<Item> PINE_CONE =addToTab(
            ITEMS.register("pine_cone",
                    () -> new FuelItem(new Item.Properties(),400))
    );

    public static final RegistryObject<ArmorItem> ROBE_HELMET = addToTab(
            ITEMS.register("robe_helmet",
                    () -> new ModArmorItem(ModArmorMaterials.INFUSED_WOOL, ArmorItem.Type.HELMET, new Item.Properties())
            )
    );

    public static final RegistryObject<ArmorItem> ROBE_CHESTPLATE = addToTab(
            ITEMS.register("robe_chestplate",
                    () -> new ModArmorItem(ModArmorMaterials.INFUSED_WOOL, ArmorItem.Type.CHESTPLATE, new Item.Properties())
            )
    );

    public static final RegistryObject<ArmorItem> ROBE_LEGGINGS = addToTab(
            ITEMS.register("robe_leggings",
                    () -> new ModArmorItem(ModArmorMaterials.INFUSED_WOOL, ArmorItem.Type.LEGGINGS, new Item.Properties())
            )
    );

    public static final RegistryObject<ArmorItem> ROBE_BOOTS = addToTab(
            ITEMS.register("robe_boots",
                    () -> new ModArmorItem(ModArmorMaterials.INFUSED_WOOL, ArmorItem.Type.BOOTS, new Item.Properties())
            )
    );
}
