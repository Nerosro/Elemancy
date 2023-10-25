package io.github.nerosro.elemancy.block.custom;

import io.github.nerosro.elemancy.effects.ModEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class CustomFlowerBlock extends FlowerBlock {
    public CustomFlowerBlock(Supplier<MobEffect> effectSupplier, int pEffectDuration, Properties pProperties) {
        super(effectSupplier, pEffectDuration, pProperties);
    }


    public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
        if (!pLevel.isClientSide) {
            System.out.println("Player is in Mana Flower");
            if (pEntity instanceof Player) {
                Player player = (Player)pEntity;
                //var aabb = (new AABB(pPos)).inflate(5);
                //if (player.getPosition(1).)) {
                    player.addEffect(new MobEffectInstance(ModEffects.MANA_REGEN.get(), 40 ));
                //}
            }
        }
    }
}
