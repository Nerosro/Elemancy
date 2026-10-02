package be.nerosro.elemancy.items.tools.lightshield;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.Level;

/**
 * Light Elemetal shield.
 */
public final class LightShieldItem extends ShieldItem {

    public LightShieldItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        return super.use(level, player, hand);
    }
}