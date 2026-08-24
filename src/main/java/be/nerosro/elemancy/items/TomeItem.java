package be.nerosro.elemancy.items;

import java.util.UUID;

import be.nerosro.elemancy.client.tome.data.TomeClientHooks;
import be.nerosro.elemancy.tome.TomeDiscoveryEvents;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

/**
 * Player-bound tome item used by Elementize and future progression systems.
 */
public class TomeItem extends Item {

    private static final String TAG_OWNER_UUID = "owner_uuid";
    private static final String TAG_OWNER_NAME = "owner_name";

    private static final String[] REJECTION_MESSAGE_KEYS = {
        "message.elemancy.tome.reject.1",
        "message.elemancy.tome.reject.2",
        "message.elemancy.tome.reject.3",
        "message.elemancy.tome.reject.4",
        "message.elemancy.tome.reject.5",
        "message.elemancy.tome.reject.6"
    };

    public TomeItem(Properties properties) {
        super(properties);
    }

    public static void bindToPlayer(ItemStack stack, Player player) {
        CompoundTag tag = getOrCreateCustomTag(stack);
        String ownerName = player.getGameProfile().name();
        tag.putString(TAG_OWNER_UUID, player.getUUID().toString());
        tag.putString(TAG_OWNER_NAME, ownerName);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        stack.set(DataComponents.CUSTOM_NAME, Component.literal(ownerName + "'s Tome"));
    }

    /**
     * Returning > 0 lets SpellDispatcher.isMainHandUsable() recognize this item as
     * "usable", which suppresses off-hand wand casting when the tome is in the main hand.
     * The duration is never actually reached because use() returns SUCCESS immediately.
     */
    @Override
    public int getUseDuration(ItemStack stack, LivingEntity user) {
        return 1;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!isOwner(stack, player)) {
            if (!level.isClientSide()) {
                player.sendSystemMessage(randomRejectionMessage(player));
            }
            return InteractionResult.FAIL;
        }

        if (level.isClientSide()) {
            // Don't open screen if discovery just happened - let player see the message
            if (!TomeDiscoveryEvents.hadRecentDiscovery(player)) {
                TomeClientHooks.openTomeScreen(player, hand);
            }
        }

        return InteractionResult.SUCCESS;
    }

    public static boolean isOwner(ItemStack stack, Player player) {
        CompoundTag tag = getCustomTag(stack);
        if (tag == null) return false;
        String ownerUuid = tag.getString(TAG_OWNER_UUID).orElse("");
        if (ownerUuid.isEmpty()) return false;

        try {
            return player.getUUID().equals(UUID.fromString(ownerUuid));
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    private static Component randomRejectionMessage(Player player) {
        int index = player.getRandom().nextInt(REJECTION_MESSAGE_KEYS.length);
        return Component.translatable(REJECTION_MESSAGE_KEYS[index]);
    }

    private static CompoundTag getOrCreateCustomTag(ItemStack stack) {
        return ItemDataUtil.getOrCreateCustomTag(stack);
    }

    static String getOwnerName(DataComponentGetter components) {
        CustomData customData = components.get(DataComponents.CUSTOM_DATA);
        return customData == null ? "Unknown" : customData.copyTag().getString(TAG_OWNER_NAME).orElse("Unknown");
    }

    private static CompoundTag getCustomTag(ItemStack stack) {
        return ItemDataUtil.getCustomTag(stack);
    }
}

