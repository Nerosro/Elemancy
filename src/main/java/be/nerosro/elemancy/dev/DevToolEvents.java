package be.nerosro.elemancy.dev;

import java.util.List;
import java.util.Optional;

import be.nerosro.elemancy.Elemancy;
import be.nerosro.elemancy.jobpoint.JobPointData;
import be.nerosro.elemancy.jobpoint.JobPointUtil;
import be.nerosro.elemancy.mana.depth.DepthTier;
import be.nerosro.elemancy.mana.depth.ManaDepthSystem;
import be.nerosro.elemancy.mana.depth.ScarType;
import be.nerosro.elemancy.network.ElemancyNetwork;
import be.nerosro.elemancy.passives.PassiveEffects;
import be.nerosro.elemancy.ritual.attunement.StructureTemplate;
import be.nerosro.elemancy.ritual.shared.RitualStructureDetector;
import be.nerosro.elemancy.skilltree.Attachments;
import be.nerosro.elemancy.skilltree.SkillTreeEntries;
import be.nerosro.soulmark.affinity.AffinityUtil;
import be.nerosro.soulmark.attunement.AttunementUtil;
import be.nerosro.soulmark.capability.SoulmarkAttachments;
import be.nerosro.soulmark.element.Element;
import be.nerosro.soulmark.element.SoulmarkElements;
import be.nerosro.soulmark.mana.ManaData;
import be.nerosro.soulmark.mana.ManaUtil;
import be.nerosro.soulmark.network.SoulmarkNetwork;
import be.nerosro.soulmark.skilltree.SkillTreeData;
import be.nerosro.soulmark.traits.TraitUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/**
 * Dev-only debug tools for inspecting depth and scar state.
 * Gold Ingot = depth/scars, Diamond = clear/reset, Emerald = reset affinity discovery,
 * Quartz = Attunement Ritual structure detection test, Iron Ingot = cycle attunement
 * (shift-click = instantly reset attunement).
 * TODO delete all dev related stuff
 */
@EventBusSubscriber(modid = Elemancy.MOD_ID)
public class DevToolEvents {

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (event.getLevel().isClientSide()) return;
        if (event.getHand() != InteractionHand.MAIN_HAND) return;

        Player player = event.getEntity();
        var item = player.getMainHandItem();

        if (item.is(Items.GOLD_INGOT)) {
            showDepthAndScars(player);
        } else if (item.is(Items.DIAMOND)) {
            clearScarsAndReset(player);
        } else if (item.is(Items.EMERALD)) {
            resetAffinityDiscovery(player);
            TraitUtil.resetTraitsReveal(player);
            if (player instanceof ServerPlayer sp) {
                SoulmarkNetwork.syncMana(sp, ManaDepthSystem.hasExperiencedManaCollapse(sp));
            }
            player.sendSystemMessage(Component.literal("[Dev] Affinity and traits discovery reset.").withStyle(ChatFormatting.YELLOW));
        } else if (item.is(Items.NETHER_STAR) && player.isShiftKeyDown()) {
            JobPointUtil.award(player, 7);
            player.sendSystemMessage(Component.literal("+7 Elemancy Job Points").withStyle(ChatFormatting.GREEN));
            if (player instanceof ServerPlayer serverPlayer) {
                ElemancyNetwork.syncJobPoints(serverPlayer);
            }
        } else if (item.is(Items.LAPIS_LAZULI)) {
            resetSkillTree(player);
        } else if (item.is(Items.IRON_INGOT)) {
            if (player.isShiftKeyDown()) {
                AttunementUtil.clearAttunement(player);
                if (player instanceof ServerPlayer serverPlayer) {
                    SoulmarkNetwork.syncAttunement(serverPlayer);
                }
                player.sendSystemMessage(Component.literal("[Dev] Attunement reset.").withStyle(ChatFormatting.YELLOW));
            } else {
                cycleAttunement(player);
            }
        } else if (item.is(Items.QUARTZ)) {
            testRitualDetection(player);
        }
    }

    private static void testRitualDetection(Player player) {
        // hitFluids=true so aiming at a water source (a valid CENTER_BLOCK element choice)
        // reports the water block as the hit, instead of the ray passing through it.
        HitResult hit = player.pick(20.0, 0f, true);
        if (hit.getType() != HitResult.Type.BLOCK || !(hit instanceof BlockHitResult blockHit)) {
            player.sendSystemMessage(Component.literal("[Ritual] No block targeted.").withStyle(ChatFormatting.RED));
            return;
        }

        BlockPos anchor = blockHit.getBlockPos();
        Optional<Integer> result = RitualStructureDetector.detect(player.level(), anchor, StructureTemplate.TEMPLATE);

        if (result.isPresent()) {
            player.sendSystemMessage(Component.literal("[Ritual] Structure detected! Matched rotation: " + result.get())
                .withStyle(ChatFormatting.GREEN));
        } else {
            player.sendSystemMessage(Component.literal("[Ritual] Structure not detected at targeted block.")
                .withStyle(ChatFormatting.RED));
        }
    }

    private static void showDepthAndScars(Player player) {
        ManaData mana = ManaUtil.getMana(player);
        DepthTier tier = DepthTier.fromMana(mana.getCurrentMana());

        player.sendSystemMessage(Component.literal("[Depth] ").withStyle(ChatFormatting.GOLD)
            .append(Component.literal("Tier: " + tier.name()).withStyle(ChatFormatting.WHITE)));

        CompoundTag scars = ManaDepthSystem.copyScarData(player);
        if (scars.isEmpty()) {
            player.sendSystemMessage(Component.literal("[Scars] ").withStyle(ChatFormatting.GOLD)
                .append(Component.literal("None active.").withStyle(ChatFormatting.GRAY)));
            return;
        }

        player.sendSystemMessage(Component.literal("[Scars] ").withStyle(ChatFormatting.GOLD)
            .append(Component.literal("Active:").withStyle(ChatFormatting.WHITE)));
        for (ScarType scar : ScarType.values()) {
            printScar(player, scars, scar);
        }
    }

    private static void printScar(Player player, CompoundTag scars, ScarType scar) {
        int ticks = scars.getInt(scar.tickKey()).orElse(0);
        if (scar.stackKey() != null) {
            int stacks = scars.getInt(scar.stackKey()).orElse(0);
            if (ticks > 0 || stacks > 0) {
                player.sendSystemMessage(Component.literal("  ").append(Component.translatable(scar.translationKey())).withStyle(ChatFormatting.YELLOW)
                    .append(Component.literal(" - " + stacks + " stacks, " + ticks / 20 + "s remaining").withStyle(ChatFormatting.GRAY)));
            }
        } else if (ticks > 0) {
            player.sendSystemMessage(Component.literal("  ").append(Component.translatable(scar.translationKey())).withStyle(ChatFormatting.YELLOW)
                .append(Component.literal(" - " + ticks / 20 + "s remaining").withStyle(ChatFormatting.GRAY)));
        }
    }

    private static void clearScarsAndReset(Player player) {
        // Clear all scars
        ManaDepthSystem.loadScarData(player, new CompoundTag());

        // Reset mana to full
        ManaData mana = ManaUtil.getMana(player);
        if (mana.isInitialized()) {
            mana.setMaxPool(mana.getOriginMaxPool());
            mana.setRegenRate(mana.getOriginRegenRate());
            mana.setCurrentMana(mana.getMaxPool());
        }

        player.sendSystemMessage(Component.literal("[Dev] Scars cleared, mana reset to full.").withStyle(ChatFormatting.GREEN));
    }

    private static void resetAffinityDiscovery(Player player) {
        AffinityUtil.resetAffinityReveal(player);
    }

    private static void resetSkillTree(Player player) {
        // Preserve earned Job Points so the player can re-spec.
        int earned = JobPointUtil.getTotalEarned(player);

        // Remove passive attribute modifiers before clearing the tree
        PassiveEffects.removeAllPassiveModifiers(player);

        // Clear tree and Job Points.
        SkillTreeData freshTree = new SkillTreeData();
        freshTree.discoverTree(SkillTreeEntries.TREE_ID); // Restore tree visibility
        freshTree.unlock(SkillTreeEntries.ELEMENTIZE_ID); // Keep root ability
        player.setData(SoulmarkAttachments.SKILL_TREE.get(), freshTree);
        player.setData(Attachments.JOB_POINTS.get(), new JobPointData());

        // Refund earned Job Points.
        if (earned > 0) {
            JobPointUtil.award(player, earned);
        }

        player.sendSystemMessage(Component.literal("[Dev] Skill tree reset. Elementize kept, " + earned + " Job Point(s) refunded.").withStyle(ChatFormatting.GREEN));
        if (player instanceof ServerPlayer serverPlayer) {
            SoulmarkNetwork.syncSkillTree(serverPlayer);
            ElemancyNetwork.syncJobPoints(serverPlayer);
        }
    }

    private static void cycleAttunement(Player player) {
        List<Element> elements = SoulmarkElements.baseElements();
        Element current = AttunementUtil.getAttunement(player);

        Element next;
        if (current == null) {
            next = elements.getFirst();
        } else {
            int idx = elements.indexOf(current);
            if (idx < 0 || idx == elements.size() - 1) {
                AttunementUtil.clearAttunement(player);
                if (player instanceof ServerPlayer serverPlayer) {
                    SoulmarkNetwork.syncAttunement(serverPlayer);
                }
                player.sendSystemMessage(Component.literal("[Dev] Attunement cleared.").withStyle(ChatFormatting.YELLOW));
                return;
            }
            next = elements.get(idx + 1);
        }

        AttunementUtil.setAttunement(player, next);
        if (player instanceof ServerPlayer serverPlayer) {
            SoulmarkNetwork.syncAttunement(serverPlayer);
        }
        String name = elementName(next);
        player.sendSystemMessage(Component.literal("[Dev] Attuned to: " + name).withStyle(ChatFormatting.GREEN));
    }

    private static String elementName(Element element) {
        if (element == SoulmarkElements.FIRE.get()) return "Fire";
        if (element == SoulmarkElements.WATER.get()) return "Water";
        if (element == SoulmarkElements.EARTH.get()) return "Earth";
        if (element == SoulmarkElements.AIR.get()) return "Air";
        if (element == SoulmarkElements.LIGHT.get()) return "Light";
        if (element == SoulmarkElements.DARK.get()) return "Dark";
        return "Unknown";
    }
}

