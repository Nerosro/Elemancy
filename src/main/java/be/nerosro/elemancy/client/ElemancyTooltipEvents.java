package be.nerosro.elemancy.client;

import be.nerosro.elemancy.items.ElemancyDataComponents;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.component.TooltipProvider;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

/** Renders Elemancy-owned custom tooltip components without deprecated item callbacks. */
public final class ElemancyTooltipEvents {
    private ElemancyTooltipEvents() {
    }

    public static void onTooltip(ItemTooltipEvent event) {
        appendTooltip(event, ElemancyDataComponents.TOME_TOOLTIP.get());
        appendTooltip(event, ElemancyDataComponents.DARK_BUCKET_TOOLTIP.get());
    }

    private static <T extends TooltipProvider> void appendTooltip(ItemTooltipEvent event, DataComponentType<T> type) {
        TooltipDisplay display = event.getItemStack().getOrDefault(
            DataComponents.TOOLTIP_DISPLAY,
            TooltipDisplay.DEFAULT
        );
        if (!display.shows(type)) {
            return;
        }

        T tooltip = event.getItemStack().get(type);
        if (tooltip != null) {
            tooltip.addToTooltip(event.getContext(), event.getToolTip()::add, event.getFlags(), event.getItemStack());
        }
    }
}