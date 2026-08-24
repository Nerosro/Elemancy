package be.nerosro.elemancy.items;

import be.nerosro.elemancy.Elemancy;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registers Elemancy-owned item data components.
 */
public final class ElemancyDataComponents {
    private static final DeferredRegister<DataComponentType<?>> COMPONENTS =
        DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, Elemancy.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<DarkBucketTooltip>> DARK_BUCKET_TOOLTIP =
        COMPONENTS.register("dark_bucket_tooltip", () -> DataComponentType.<DarkBucketTooltip>builder()
            .persistent(DarkBucketTooltip.CODEC)
            .networkSynchronized(DarkBucketTooltip.STREAM_CODEC)
            .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<TomeTooltip>> TOME_TOOLTIP =
        COMPONENTS.register("tome_tooltip", () -> DataComponentType.<TomeTooltip>builder()
            .persistent(TomeTooltip.CODEC)
            .networkSynchronized(TomeTooltip.STREAM_CODEC)
            .build());

    private ElemancyDataComponents() {
    }

    public static void register(IEventBus modEventBus) {
        COMPONENTS.register(modEventBus);
    }
}