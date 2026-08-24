package be.nerosro.elemancy.items;

import org.jspecify.annotations.Nullable;

import be.nerosro.elemancy.mana.depth.ManaDepthSystem;
import be.nerosro.soulmark.affinity.AffinityUtil;
import be.nerosro.soulmark.element.Element;
import be.nerosro.soulmark.element.ElementRegistry;
import be.nerosro.soulmark.network.SoulmarkNetwork;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

/**
 * Affinity Paper — structure loot that reveals the player's innate affinity on pickup.
 * Once activated, the paper becomes tinted to the player's affinity color and serves as a keepsake.
 */
public class AffinityPaperItem extends Item {

    private static final String TAG_AFFINITY = "revealed_affinity";

    public AffinityPaperItem(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, @Nullable EquipmentSlot slot) {
        if (!(entity instanceof Player player)) return;
        // Only auto-reveal on pickup if the player hasn't discovered their affinity yet.
        if (!AffinityUtil.isAffinityRevealed(player)) {
            tryReveal(stack, player);
        }
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) return InteractionResult.PASS;
        ItemStack stack = player.getItemInHand(hand);
        if (tryReveal(stack, player)) {
            return InteractionResult.SUCCESS_SERVER;
        }
        return InteractionResult.PASS;
    }

    /**
     * Attempts to reveal the player's affinity and bind it to this paper.
     * Returns true if the paper was bound, false if skipped (already bound).
     */
    private static boolean tryReveal(ItemStack stack, Player player) {
        // Already activated — nothing to do.
        if (hasRevealedAffinity(stack)) return false;

        // Player has no affinity rolled yet (shouldn't happen, but guard).
        Element affinity = AffinityUtil.getAffinity(player);
        if (affinity == null) return false;

        // Mark affinity as discovered if not already known.
        boolean firstReveal = !AffinityUtil.isAffinityRevealed(player);
        if (firstReveal) {
            AffinityUtil.revealAffinity(player);
            if (player instanceof ServerPlayer sp) {
                SoulmarkNetwork.syncMana(sp, ManaDepthSystem.hasExperiencedManaCollapse(sp));
            }
        }

        // Bind affinity data to this paper.
        Identifier affinityId = ElementRegistry.ELEMENT_REGISTRY.getKey(affinity);
        if (affinityId == null) return false;
        setRevealedAffinity(stack, affinityId);

        // Update the item name to reflect the revealed affinity.
        String displayName = affinityId.getPath().substring(0, 1).toUpperCase()
            + affinityId.getPath().substring(1);
        int color = affinity.rgb();
        stack.set(DataComponents.CUSTOM_NAME,
            Component.literal("Affinity Paper (")
                .append(Component.literal(displayName).withStyle(Style.EMPTY.withColor(color)))
                .append(Component.literal(")")));

        return true;
    }

    /**
     * Returns the ARGB tint color for this paper's layer0 based on the revealed affinity.
     * Used by the item color handler on the client side.
     */
    public static int getTintColor(ItemStack stack) {
        CompoundTag tag = getCustomTag(stack);
        if (tag == null) return 0xFFFFFFFF;
        String affinityName = tag.getString(TAG_AFFINITY).orElse("");
        if (affinityName.isEmpty()) return 0xFFFFFFFF;
        ResourceKey<Element> key = ResourceKey.create(ElementRegistry.ELEMENT_REGISTRY_KEY, Identifier.parse(affinityName));
        Element element = ElementRegistry.ELEMENT_REGISTRY.getValue(key);
        return element != null ? element.argb() : 0xFFFFFFFF;
    }

    public static boolean hasRevealedAffinity(ItemStack stack) {
        CompoundTag tag = getCustomTag(stack);
        if (tag == null) return false;
        return !tag.getString(TAG_AFFINITY).orElse("").isEmpty();
    }

    private static void setRevealedAffinity(ItemStack stack, Identifier affinityId) {
        CompoundTag tag = getCustomTag(stack);
        if (tag == null) tag = new CompoundTag();
        tag.putString(TAG_AFFINITY, affinityId.toString());
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    private static CompoundTag getCustomTag(ItemStack stack) {
        return ItemDataUtil.getCustomTag(stack);
    }
}
