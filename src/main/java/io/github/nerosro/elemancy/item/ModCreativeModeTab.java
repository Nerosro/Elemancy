package io.github.nerosro.elemancy.item;

import io.github.nerosro.elemancy.Elemancy;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Created by Nerosro on 2/07/2023.
 */
@Mod.EventBusSubscriber(modid = Elemancy.MOD_ID, bus= Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModCreativeModeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Elemancy.MOD_ID);

    public static void register(IEventBus eventBus){
        CREATIVE_MODE_TABS.register(eventBus);
    }
    public static final List<Supplier<? extends ItemLike>> ELEMANCY_TAB_ITEMS = new ArrayList<>();

    public static final RegistryObject<CreativeModeTab> ELEMANCY_TAB = CREATIVE_MODE_TABS.register("elemancy_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("creativetab.elemancy_tab"))
                    //.icon(() -> new ItemStack(ItemInit.ELEMANCY_TAB.get())) //One way to solve this
                    .icon(ModItems.ENERGIZED_STICK.get()::getDefaultInstance) //Same as above but get().getDefaultInstance() method reference
                    .displayItems((displayParams, output) ->{
                        //output.accept(ItemInit.ELEMANCY_TAB.get()); //need to do this for every item
                        ELEMANCY_TAB_ITEMS.forEach(itemLike -> output.accept(itemLike.get())); //Because of addToTab used in ItemInit, gets added easier
                    })
                    .build()
    );

    public static <T extends Item> RegistryObject<T> addToTab(RegistryObject<T> itemLike){
        ELEMANCY_TAB_ITEMS.add(itemLike);
        return itemLike;
    }
}
