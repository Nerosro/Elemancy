package be.nerosro.elemancy.events;

import be.nerosro.elemancy.Elemancy;
import be.nerosro.elemancy.items.ElemancyItems;
import be.nerosro.elemancy.items.WandItem;
import be.nerosro.elemancy.jobpoint.MilestoneEvents;
import be.nerosro.elemancy.mana.depth.CastResolution;
import be.nerosro.elemancy.mana.depth.ManaDepthSystem;
import be.nerosro.elemancy.skilltree.EquippedSpellUtil;
import be.nerosro.elemancy.skilltree.SkillTreeEntries;
import be.nerosro.elemancy.spell.SpellContext;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = Elemancy.MOD_ID)
public class WandEvents {

    private static final float ENERGIZE_COST = 0f;

    /**
     * Fires on both sides when the player right-clicks air with an item.
     * If a vanilla stick is in the offhand, spend mana and attempt to transform it into an Energized Stick.
     */
    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (tryEnergize(event.getEntity(), event.getHand())) {
            event.setCanceled(true);
        }
    }

    /**
     * Fires on both sides when the player right-clicks a block.
     * Same energize logic applies if a stick is in the offhand.
     */
    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (tryEnergize(event.getEntity(), event.getHand())) {
            event.setCanceled(true);
        }
    }

    private static boolean tryEnergize(Player player, InteractionHand hand) {
        if (hand != InteractionHand.OFF_HAND) return false;
        if (player.level().isClientSide()) return false;

        ItemStack offhand = player.getOffhandItem();
        if (!offhand.is(Items.STICK)) return false;

        // Only allow conversion when exactly one stick is held to avoid consuming a full stack.
        if (offhand.getCount() != 1) return false;

        CastResolution resolution = ManaDepthSystem.attemptCast(player, ENERGIZE_COST, SpellContext.INFUSION);
        if (!resolution.castConsumed()) return false;

        player.swing(InteractionHand.OFF_HAND, true);

        if (!resolution.spellResolved()) {
            player.getCooldowns().addCooldown(offhand, WandItem.COOLDOWN_TICKS);
            return true;
        }

        player.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(ElemancyItems.ENERGIZED_STICK.get()));

        // First energized stick: auto-equip Elementize spell
        if (EquippedSpellUtil.getEquippedSpell(player) == null) {
            EquippedSpellUtil.setEquippedSpell(player, SkillTreeEntries.ELEMENTIZE_ID);
        }

        MilestoneEvents.onEnergizedStickCreated(player);

        return true;
    }
}
