package be.nerosro.elemancy.items.mirror;

import java.util.function.Consumer;

import com.mojang.serialization.Codec;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

/**
 * Adds the Mirror's fixed flavor text through the standard tooltip component path.
 */
public record MirrorTooltip() implements TooltipProvider {
    public static final MirrorTooltip INSTANCE = new MirrorTooltip();
    public static final Codec<MirrorTooltip> CODEC = Codec.STRING.xmap(_ -> INSTANCE, _ -> "");
    public static final StreamCodec<RegistryFriendlyByteBuf, MirrorTooltip> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> builder, TooltipFlag flag,
                             DataComponentGetter components) {
        builder.accept(Component.translatable("tooltip.elemancy.mirror").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
    }
}