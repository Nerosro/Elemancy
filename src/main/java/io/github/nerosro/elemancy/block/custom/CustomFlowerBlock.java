package io.github.nerosro.elemancy.block.custom;

import io.github.nerosro.elemancy.effects.ModEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.extensions.IForgeBlock;
import net.minecraftforge.common.extensions.IForgeBlockEntity;
import net.minecraftforge.common.extensions.IForgeBlockState;

import java.util.List;
import java.util.function.Supplier;

public class CustomFlowerBlock extends FlowerBlock {
    public CustomFlowerBlock(Supplier<MobEffect> effectSupplier, int pEffectDuration, Properties pProperties) {
        super(effectSupplier, pEffectDuration, pProperties);
    }

    @Override
    public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
        if (!pLevel.isClientSide) {
            System.out.println("Player is in Mana Flower");
            if (pEntity instanceof Player) {
                var aabb = (new AABB(pPos)).inflate(5).expandTowards(0.0D, 0.0D, 0.0D);
                System.out.println(aabb);
                List<Player> list = pLevel.getEntitiesOfClass(Player.class, aabb);

                for(Player player : list) {
                    super.entityInside(pState, pLevel, pPos, pEntity);
                    player.addEffect(new MobEffectInstance(ModEffects.MANA_REGEN.get(), 40, 0, true, true));
                }
            }
        }
    }

    /*private static void applyEffects(Level pLevel, BlockPos pPos, Entity pEntity) {

    }*/
}
