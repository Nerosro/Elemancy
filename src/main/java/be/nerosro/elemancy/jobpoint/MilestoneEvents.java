package be.nerosro.elemancy.jobpoint;

import be.nerosro.elemancy.Elemancy;
import be.nerosro.elemancy.items.ElemancyItems;
import be.nerosro.elemancy.network.ElemancyNetwork;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/**
 * Awards Elemancy Job Point milestones when the player reaches key progression beats.
 */
@EventBusSubscriber(modid = Elemancy.MOD_ID)
public final class MilestoneEvents {

    private static final String ASHEN_WAND_MILESTONE = "elemancy:ashen_wand";
    private static final String ATTUNEMENT_MILESTONE = "elemancy:attunement_ritual";
    private static final String ENERGIZED_STICK_MILESTONE = "elemancy:energized_stick";

    private MilestoneEvents() {
    }

    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;

        if (event.getCrafting().is(ElemancyItems.ASHEN_WAND.get())) {
            awardAshenWandMilestone(player);
        }
    }

    private static void awardAshenWandMilestone(Player player) {
        if (JobPointUtil.awardOnce(player, ASHEN_WAND_MILESTONE, 6)) {
            player.sendSystemMessage(Component.literal("Crafting of the Ashen wand rewards you with 6 Elemancy job Points").withStyle(ChatFormatting.GREEN));
            if (player instanceof ServerPlayer serverPlayer) {
                ElemancyNetwork.syncJobPoints(serverPlayer);
            }
        }
    }

    public static void onAttunementCompleted(ServerPlayer player) {
        if (JobPointUtil.awardOnce(player, ATTUNEMENT_MILESTONE, 5)) {
            player.sendSystemMessage(Component.literal("Attunement rewards you with 5 Elemancy job points").withStyle(ChatFormatting.GREEN));
            ElemancyNetwork.syncJobPoints(player);
        }
    }

    //Created through offhand click with a stick, cannot move into the onItemCrafted subscribeEvent on top
    public static void onEnergizedStickCreated(Player player) {
        if (JobPointUtil.awardOnce(player, ENERGIZED_STICK_MILESTONE, 1)
                && player instanceof ServerPlayer serverPlayer) {
            ElemancyNetwork.syncJobPoints(serverPlayer);
        }
    }
}