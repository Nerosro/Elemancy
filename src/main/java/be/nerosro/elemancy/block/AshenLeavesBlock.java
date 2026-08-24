package be.nerosro.elemancy.block;

import com.mojang.serialization.MapCodec;

import be.nerosro.elemancy.ElemancyColors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;

/**
 * Ashen Leaves — the foliage of the Ashen Tree.
 * Visually tinted with a cyan color via BlockTintSource (declared in model JSON).
 * Behaves identically to vanilla leaves (decays, waterloggable, etc.).
 */
public class AshenLeavesBlock extends LeavesBlock {

    public static final MapCodec<AshenLeavesBlock> CODEC = simpleCodec(AshenLeavesBlock::new);

    /**
     * Chance per tick that a falling leaf particle spawns.
     */
    private static final float FALLING_LEAVES_CHANCE = 0.0125f;

    public AshenLeavesBlock(Properties properties) {
        super(FALLING_LEAVES_CHANCE, properties);
    }

    @Override
    public MapCodec<? extends LeavesBlock> codec() {
        return CODEC;
    }

    @Override
    protected void spawnFallingLeavesParticle(Level level, BlockPos pos, RandomSource random) {
        double x = pos.getX() + random.nextDouble();
        double y = pos.getY() - 0.05;
        double z = pos.getZ() + random.nextDouble();
        level.addParticle(
            ColorParticleOption.create(ParticleTypes.TINTED_LEAVES,
                ElemancyColors.ASHEN_LEAVES.red(), ElemancyColors.ASHEN_LEAVES.green(), ElemancyColors.ASHEN_LEAVES.blue()),
            x, y, z, 0.0, 0.0, 0.0);
    }
}

