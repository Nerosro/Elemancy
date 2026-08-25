package be.nerosro.elemancy.ritual.conversion;

import java.util.Optional;

import be.nerosro.elemancy.Elemancy;
import be.nerosro.elemancy.ElemancyTags;
import be.nerosro.elemancy.block.ElemancyBlocks;
import be.nerosro.elemancy.effects.CastEffects;
import be.nerosro.elemancy.items.wands.WandCastFeedback;
import be.nerosro.elemancy.items.wands.WandItem;
import be.nerosro.elemancy.mana.depth.CastResolution;
import be.nerosro.elemancy.mana.depth.ManaDepthSystem;
import be.nerosro.elemancy.ritual.shared.RitualStructureDetector;
import be.nerosro.elemancy.skilltree.EquippedSpellUtil;
import be.nerosro.elemancy.skilltree.SkillTreeEntries;
import be.nerosro.elemancy.spell.SpellContext;
import be.nerosro.soulmark.element.Element;
import be.nerosro.soulmark.skilltree.SkillTreeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/**
 * Activates Elemetal Conversion by casting Elementize directly on its center block.
 */
@EventBusSubscriber(modid = Elemancy.MOD_ID)
public final class TriggerEvents {
    public static final float MANA_COST = 50.0f;

    private TriggerEvents() {
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getHand() == InteractionHand.MAIN_HAND && tryStart(event.getEntity(), event.getPos())) {
            event.setCanceled(true);
        }
    }

    private static boolean tryStart(Player player, BlockPos anchor) {
        if (!(player.level() instanceof ServerLevel level)) return false;

        Optional<Element> source = ElemancyBlocks.getElemetalElement(level.getBlockState(anchor));
        if (source.isEmpty()) return false;

        Identifier equipped = EquippedSpellUtil.getEquippedSpell(player);
        if (!SkillTreeEntries.ELEMENTIZE_ID.equals(equipped)) return false;
        if (!SkillTreeUtil.hasNode(player, SkillTreeEntries.CONVERSION_RITUAL_ID)) return false;

        ItemStack offhand = player.getOffhandItem();
        if (!offhand.is(ElemancyTags.WANDS) || !WandItem.canCast(offhand, SpellContext.INFUSION)) return false;

        Optional<Integer> rotation = RitualStructureDetector.detect(level, anchor, StructureTemplate.TEMPLATE);
        if (rotation.isEmpty()) return false;

        Optional<Element> target = StructureTemplate.resolveTargetElement(level, anchor, rotation.get());
        if (target.isEmpty() || target.get() == source.get() || CutsceneEngine.isActive(level, anchor)) return false;

        CastResolution resolution = ManaDepthSystem.attemptCast(player, MANA_COST, SpellContext.INFUSION);
        if (!resolution.castConsumed()) return false;

        WandCastFeedback.castWithWear(player, offhand);
        if (!resolution.spellResolved()) {
            CastEffects.fizzle(level, player);
            return true;
        }

        CutsceneEngine.start(level, anchor, rotation.get(), source.get(), target.get());
        return true;
    }
}