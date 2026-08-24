package be.nerosro.elemancy.client;

import org.jspecify.annotations.Nullable;

import com.mojang.serialization.MapCodec;

import be.nerosro.elemancy.items.AffinityPaperItem;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * Custom ItemTintSource that reads the revealed affinity from an Affinity Paper
 * and returns the corresponding tint color.
 * Returns white (no tint) for unactivated papers.
 */
public record AffinityPaperTintSource() implements ItemTintSource {

    public static final MapCodec<AffinityPaperTintSource> MAP_CODEC = MapCodec.unit(AffinityPaperTintSource::new);

    @Override
    public int calculate(ItemStack itemStack, @Nullable ClientLevel level, @Nullable LivingEntity owner) {
        return AffinityPaperItem.getTintColor(itemStack);
    }

    @Override
    public MapCodec<AffinityPaperTintSource> type() {
        return MAP_CODEC;
    }
}

