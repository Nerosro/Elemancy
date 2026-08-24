package be.nerosro.elemancy.block;

import java.util.Optional;

import be.nerosro.elemancy.Elemancy;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

/**
 * Ashen Sapling — the mutated form of an Acacia Sapling.
 * Created by prolonged exposure to a Paradox Flower.
 * <p>
 * Grows into an Ashen Tree using the Acacia trunk/foliage shape
 * with vanilla logs and leaves (until custom Ashen blocks are added).
 */
public class AshenSaplingBlock extends SaplingBlock {

    /**
     * Resource key for the configured feature that generates an Ashen tree.
     */
    public static final ResourceKey<ConfiguredFeature<?, ?>> ASHEN_TREE_FEATURE = ResourceKey.create(
        Registries.CONFIGURED_FEATURE,
        Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "ashen_tree"));

    /**
     * Tree grower that uses our custom configured feature.
     */
    public static final TreeGrower ASHEN_TREE_GROWER = new TreeGrower(
        Elemancy.MOD_ID + ":ashen_tree",
        Optional.empty(),
        Optional.of(ASHEN_TREE_FEATURE),
        Optional.empty());

    public AshenSaplingBlock(Properties properties) {
        super(ASHEN_TREE_GROWER, properties);
    }
}


