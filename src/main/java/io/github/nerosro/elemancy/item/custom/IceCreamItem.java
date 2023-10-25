package io.github.nerosro.elemancy.item.custom;

import io.github.nerosro.elemancy.Elemancy;
import io.github.nerosro.elemancy.mana.PlayerManaProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BowlFoodItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by Nerosro on 22/07/2023.
 */
public class IceCreamItem extends BowlFoodItem {
    public IceCreamItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pEntityLiving) {

        pEntityLiving.getCapability(PlayerManaProvider.PLAYER_MANA)
                .ifPresent(playerMana -> playerMana.increaseMana(30));
        return super.finishUsingItem(pStack, pLevel, pEntityLiving);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        super.appendHoverText(stack, level, components, flag);

        components.add(Component.translatable("tooltip.elemancy.icecream_tooltip"));
    }

}
