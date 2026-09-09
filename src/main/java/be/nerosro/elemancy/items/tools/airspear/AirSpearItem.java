package be.nerosro.elemancy.items.tools.airspear;

import be.nerosro.elemancy.Elemancy;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Air Elemetal spear with locked brace and running stances.
 */
public class AirSpearItem extends Item {
    public static final int HOLD_DURATION_TICKS = 210;
    private static final Identifier RUNNING_SPEED_ID = Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "air_spear_running_speed");
    private static final float NORMAL_KNOCKBACK = 0.65F;
    private static final double NORMAL_LIFT = 0.10D;
    private static final double NORMAL_RECOIL = 0.15D;
    private static final double RUNNING_SPEED_BONUS = 0.20D;

    public AirSpearItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        InteractionResult result = super.use(level, player, hand);
        if (!level.isClientSide() && result.consumesAction()) {
            AirSpearStance.begin(player);
        }
        return result;
    }

    static void tickUse(Player player) {
        if (AirSpearStance.is(player, AirSpearStance.Type.PENDING)) {
            boolean movedHorizontally = AirSpearStance.updateAndHasMovedHorizontally(player);
            AirSpearStance.Type stance = movedHorizontally
                ? AirSpearStance.Type.RUNNING
                : AirSpearStance.Type.BRACE;
            AirSpearStance.set(player, stance);
            return;
        }

        if (AirSpearStance.is(player, AirSpearStance.Type.BRACE)) {
            removeRunningSpeed(player);
            int elapsedTicks = player.getTicksUsingItem();
            if (elapsedTicks >= HOLD_DURATION_TICKS) {
                AirSpearVacuum.burst((ServerLevel) player.level(), player);
                AirSpearStance.set(player, AirSpearStance.Type.EXHAUSTED);
                return;
            }
            boolean movedHorizontally = AirSpearStance.updateAndHasMovedHorizontally(player);
            if (!player.onGround() || movedHorizontally) {
                AirSpearStance.set(player, AirSpearStance.Type.INTERRUPTED);
                return;
            }
            if (elapsedTicks % AirSpearVacuum.PULSE_INTERVAL_TICKS == 0) {
                AirSpearVacuum.pulse((ServerLevel) player.level(), player);
            }
            return;
        }

        if (AirSpearStance.is(player, AirSpearStance.Type.RUNNING)) {
            if (player.getTicksUsingItem() >= HOLD_DURATION_TICKS) {
                removeRunningSpeed(player);
                AirSpearStance.set(player, AirSpearStance.Type.EXHAUSTED);
                return;
            }
            if (AirSpearStance.updateAndHasMovedHorizontally(player)) {
                applyRunningSpeed(player);
            } else {
                removeRunningSpeed(player);
            }
        }
    }

    @Override
    public boolean releaseUsing(ItemStack stack, Level level, LivingEntity entity, int remainingTime) {
        if (!level.isClientSide() && entity instanceof Player player) {
            if (AirSpearStance.is(player, AirSpearStance.Type.BRACE)) {
                AirSpearStance.set(player, AirSpearStance.Type.BRACE_RELEASED);
            } else {
                clearState(player);
            }
        }
        return super.releaseUsing(stack, level, entity, remainingTime);
    }

    @Override
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        super.hurtEnemy(stack, target, attacker);
        if (!(attacker instanceof Player player) || player.level().isClientSide()) {
            return;
        }

        if (!player.isUsingItem()) {
            pushTarget(player, target);
            recoilPlayer(player, target);
        }
    }

    private static void pushTarget(Player player, LivingEntity target) {
        Vec3 pushDirection = target.position().subtract(player.position()).multiply(1.0D, 0.0D, 1.0D).normalize();
        target.push(pushDirection.x * NORMAL_KNOCKBACK, NORMAL_LIFT, pushDirection.z * NORMAL_KNOCKBACK);
    }

    private static void recoilPlayer(Player player, LivingEntity target) {
        Vec3 targetOffset = target.position().subtract(player.position());
        player.knockback(NORMAL_RECOIL, targetOffset.x, targetOffset.z);
        player.hurtMarked = true;
    }

    private static void applyRunningSpeed(Player player) {
        AttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed == null) {
            return;
        }
        speed.removeModifier(RUNNING_SPEED_ID);
        speed.addTransientModifier(new AttributeModifier(
            RUNNING_SPEED_ID, RUNNING_SPEED_BONUS, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    }

    private static void removeRunningSpeed(Player player) {
        AttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null) {
            speed.removeModifier(RUNNING_SPEED_ID);
        }
    }

    static void clearState(Player player) {
        removeRunningSpeed(player);
        AirSpearStance.clear(player);
    }
}