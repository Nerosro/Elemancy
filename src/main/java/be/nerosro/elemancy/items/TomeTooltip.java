package be.nerosro.elemancy.items;

import java.util.function.Consumer;

import com.mojang.serialization.Codec;

import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

/**
 * Adds the Tome's bound owner to the modern component-based item tooltip.
 */
public record TomeTooltip() implements TooltipProvider {
    public static final TomeTooltip INSTANCE = new TomeTooltip();
    public static final Codec<TomeTooltip> CODEC = Codec.STRING.xmap(_ -> INSTANCE, _ -> "");
    public static final StreamCodec<RegistryFriendlyByteBuf, TomeTooltip> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> builder, TooltipFlag flag,
                             DataComponentGetter components) {
        builder.accept(Component.literal("§7Bound to §f" + TomeItem.getOwnerName(components)));
    }
}