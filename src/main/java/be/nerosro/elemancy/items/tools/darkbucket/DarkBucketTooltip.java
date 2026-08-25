package be.nerosro.elemancy.items.tools.darkbucket;

import java.util.function.Consumer;

import com.mojang.serialization.Codec;

import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

/**
 * Adds Dark Bucket compartment contents to the modern component-based item tooltip.
 */
public record DarkBucketTooltip() implements TooltipProvider {
    public static final DarkBucketTooltip INSTANCE = new DarkBucketTooltip();
    public static final Codec<DarkBucketTooltip> CODEC = Codec.STRING.xmap(_ -> INSTANCE, _ -> "");
    public static final StreamCodec<RegistryFriendlyByteBuf, DarkBucketTooltip> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> builder, TooltipFlag flag,
                             DataComponentGetter components) {
        int selected = DarkBucketContents.getSelectedCompartment(components);
        for (int compartment = 0; compartment < DarkBucketContents.COMPARTMENT_COUNT; compartment++) {
            ItemStack content = DarkBucketContents.getCompartment(components, compartment);
            if (content.isEmpty()) continue;

            String marker = compartment == selected ? "> " : "  ";
            builder.accept(Component.literal(marker + (compartment + 1) + ". ")
                .append(DarkBucketItem.contentName(content)));
        }
    }
}