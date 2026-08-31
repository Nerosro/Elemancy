package be.nerosro.elemancy.items.tools.firesword;

import java.util.List;

import be.nerosro.elemancy.items.ItemDataUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.CustomModelData;

/** Per-stack Heat state and playtest-tunable decay timings for Fire Sword. */
public final class FireSwordHeat {
    public static final int MAX_HEAT = 3;

    private static final int DECAY_FROM_THREE_TICKS = 80;
    private static final int DECAY_FROM_TWO_TICKS = 120;
    private static final int DECAY_FROM_ONE_TICKS = 160;

    public static final CustomModelData DEFAULT_MODEL_DATA = modelData(0);

    private static final String TAG_HEAT = "fire_sword_heat";
    private static final String TAG_DECAY_STARTED_AT = "fire_sword_decay_started_at";
    private static final String TAG_FOURTH_HIT_CONSUMED_AT = "fire_sword_fourth_hit_consumed_at";

    private FireSwordHeat() {
    }

    public static int get(ItemStack stack) {
        CompoundTag tag = ItemDataUtil.getCustomTag(stack);
        return tag == null ? 0 : Math.clamp(tag.getInt(TAG_HEAT).orElse(0), 0, MAX_HEAT);
    }

    public static void addSuccessfulHit(ItemStack stack, long gameTime) {
        CompoundTag tag = ItemDataUtil.getOrCreateCustomTag(stack);
        if (tag.getLong(TAG_FOURTH_HIT_CONSUMED_AT).orElse(-1L) == gameTime) {
            tag.remove(TAG_FOURTH_HIT_CONSUMED_AT);
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
            return;
        }

        set(stack, Math.min(get(stack) + 1, MAX_HEAT), gameTime);
    }

    public static void consumeFourthHit(ItemStack stack, long gameTime) {
        set(stack, 0, gameTime);
        CompoundTag tag = ItemDataUtil.getOrCreateCustomTag(stack);
        tag.putLong(TAG_FOURTH_HIT_CONSUMED_AT, gameTime);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    public static void reset(ItemStack stack) {
        CompoundTag tag = ItemDataUtil.getOrCreateCustomTag(stack);
        tag.remove(TAG_HEAT);
        tag.remove(TAG_DECAY_STARTED_AT);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        stack.set(DataComponents.CUSTOM_MODEL_DATA, DEFAULT_MODEL_DATA);
    }

    public static void tickDecay(ItemStack stack, long gameTime) {
        int heat = get(stack);
        if (heat == 0) {
            clearExpiredFourthHitMarker(stack, gameTime);
            return;
        }

        CompoundTag tag = ItemDataUtil.getOrCreateCustomTag(stack);
        long decayStartedAt = tag.getLong(TAG_DECAY_STARTED_AT).orElse(gameTime);
        if (gameTime - decayStartedAt < decayTicks(heat)) {
            return;
        }

        set(stack, heat - 1, gameTime);
    }

    private static void clearExpiredFourthHitMarker(ItemStack stack, long gameTime) {
        CompoundTag tag = ItemDataUtil.getCustomTag(stack);
        if (tag != null && tag.getLong(TAG_FOURTH_HIT_CONSUMED_AT).orElse(gameTime) < gameTime) {
            tag.remove(TAG_FOURTH_HIT_CONSUMED_AT);
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }
    }

    private static void set(ItemStack stack, int heat, long gameTime) {
        CompoundTag tag = ItemDataUtil.getOrCreateCustomTag(stack);
        if (heat == 0) {
            tag.remove(TAG_HEAT);
            tag.remove(TAG_DECAY_STARTED_AT);
        } else {
            tag.putInt(TAG_HEAT, heat);
            tag.putLong(TAG_DECAY_STARTED_AT, gameTime);
        }
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        stack.set(DataComponents.CUSTOM_MODEL_DATA, modelData(heat));
    }

    private static int decayTicks(int heat) {
        return switch (heat) {
            case 3 -> DECAY_FROM_THREE_TICKS;
            case 2 -> DECAY_FROM_TWO_TICKS;
            case 1 -> DECAY_FROM_ONE_TICKS;
            default -> throw new IllegalArgumentException("Invalid Fire Sword Heat: " + heat);
        };
    }

    private static CustomModelData modelData(int heat) {
        return new CustomModelData(List.of(), List.of(), List.of(Integer.toString(heat)), List.of());
    }
}