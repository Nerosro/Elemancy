package be.nerosro.elemancy.spell.casting;

import java.util.LinkedHashMap;
import java.util.Map;

import be.nerosro.elemancy.Elemancy;
import be.nerosro.elemancy.ElemancyTags;
import be.nerosro.elemancy.skilltree.EquippedSpellUtil;
import be.nerosro.elemancy.spell.SpellCastHandler;
import be.nerosro.elemancy.spell.SpellEntry;
import be.nerosro.elemancy.spell.SpellRegistry;
import be.nerosro.elemancy.spell.SpellShape;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/**
 * Central spell dispatcher. Routes right-click events to the appropriate
 * spell handler based on the player's currently equipped spell.
 * <p>
 * Wand must be in offhand. The equipped spell determines which handler is called.
 * Infusion spells are handled by InfusionEvents (they need an item entity target).
 * Attack spells fire on any right-click and are routed through SpellRegistry.
 * <p>
 * Priority chain (mirrors vanilla off-hand behavior):
 * 1. Main hand block placement always wins.
 * 2. Interactable block wins when not sneaking.
 * 3. Spell cast fires otherwise (sneak forces past interactable blocks).
 */
@EventBusSubscriber(modid = Elemancy.MOD_ID)
public class SpellDispatcher {

    private static final Map<Identifier, SpellCastHandler> HANDLERS = new LinkedHashMap<>();

    /**
     * Register a one-shot spell handler.
     * Only spells dispatched via right-click (projectile, beam) need a handler here.
     * Continuous spells are routed through ContinuousCaster instead.
     */
    public static void registerHandler(Identifier id, SpellCastHandler handler) {
        HANDLERS.put(id, handler);
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (event.getLevel().isClientSide()) return;
        if (!isHoldingWand(event.getEntity())) return;
        if (isMainHandUsable(event.getEntity())) return;

        if (dispatchAttackSpell(event.getEntity())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel().isClientSide()) return;
        if (!isHoldingWand(event.getEntity())) return;
        if (isMainHandUsable(event.getEntity())) return;

        Player player = event.getEntity();

        // Main hand block placement always takes priority
        if (player.getMainHandItem().getItem() instanceof BlockItem) return;

        // Not sneaking: interactable blocks take priority
        if (!player.isShiftKeyDown() && isInteractableBlock(event.getLevel(), event.getPos())) return;

        if (dispatchAttackSpell(player)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getLevel().isClientSide()) return;
        if (!isHoldingWand(event.getEntity())) return;
        if (isMainHandUsable(event.getEntity())) return;

        if (dispatchAttackSpell(event.getEntity())) {
            event.setCanceled(true);
        }
    }

    private static boolean isHoldingWand(Player player) {
        ItemStack offhand = player.getOffhandItem();
        return offhand.is(ElemancyTags.WANDS);
    }

    /**
     * Mainhand item has a use action (food, potion, bow, etc.) — takes priority over offhand casting.
     */
    private static boolean isMainHandUsable(Player player) {
        ItemStack mainhand = player.getMainHandItem();
        return !mainhand.isEmpty() && mainhand.getUseDuration(player) > 0;
    }

    private static boolean isInteractableBlock(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();

        // Containers and GUI blocks (crafting table, furnace, chest, etc.)
        if (state.getMenuProvider(level, pos) != null) return true;

        // Mechanical interaction blocks
        return switch (block) {
            case DoorBlock doorBlock -> true;
            case TrapDoorBlock trapDoorBlock -> true;
            case FenceGateBlock fenceGateBlock -> true;
            case ButtonBlock buttonBlock -> true;
            case LeverBlock leverBlock -> true;
            case BedBlock bedBlock -> true;
            default -> false;
        };

    }

    /**
     * Routes attack spells through the handler map.
     * If the equipped spell is tagged "attack" and has a registered handler, it is invoked.
     * If the player has Spellwarp, the delivery shape is randomized.
     */
    private static boolean dispatchAttackSpell(Player player) {
        Identifier equipped = EquippedSpellUtil.getEquippedSpell(player);
        if (equipped == null) return false;
        if (!SpellRegistry.isAttack(equipped)) return false;

        // Spellwarp: randomize the delivery shape
        if (SpellwarpResolver.hasSpellwarp(player)) {
            SpellShape rolledShape = SpellwarpResolver.rollShape(player);

            // Continuous shape is handled by WandItem.use() — let it through
            if (rolledShape == SpellShape.CONTINUOUS) return false;

            SpellEntry entry = SpellRegistry.get(equipped);
            if (entry == null) return false;

            SpellCastHandler handler = SpellwarpResolver.getHandlerForShape(rolledShape, entry.element());
            if (handler == null) return false;
            return handler.tryCast(player);
        }

        // Normal dispatch: look up registered handler
        SpellCastHandler handler = HANDLERS.get(equipped);
        if (handler == null) return false;

        return handler.tryCast(player);
    }
}






