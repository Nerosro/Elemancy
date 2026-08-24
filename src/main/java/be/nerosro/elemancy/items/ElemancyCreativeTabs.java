package be.nerosro.elemancy.items;

import be.nerosro.elemancy.Elemancy;
import be.nerosro.elemancy.block.ElemancyBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ElemancyCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Elemancy.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ELEMANCY_TAB = CREATIVE_TABS.register(
        "elemancy",
        () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.elemancy"))
            .icon(() -> new ItemStack(ElemancyItems.ASHEN_WAND.get()))
            .displayItems((parameters, output) -> {
                for (DeferredHolder<Item, ? extends Item> item : ElemancyItems.ITEMS.getEntries()) {
                    if (item.get() != ElemancyItems.TOME.get() && !ElemancyItems.isAttunedSoulvial(item.get())) {
                        output.accept(item.get());
                    }
                }
                for (DeferredHolder<Item, ? extends Item> item : ElemancyBlocks.BLOCK_ITEMS.getEntries()) {
                    output.accept(item.get());
                }
            })
            .build()
    );

    public static void register(IEventBus modEventBus) {
        CREATIVE_TABS.register(modEventBus);
    }
}

