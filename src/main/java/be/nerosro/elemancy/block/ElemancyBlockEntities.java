package be.nerosro.elemancy.block;

import java.util.Set;

import be.nerosro.elemancy.Elemancy;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registers block entity types owned by Elemancy blocks.
 */
public final class ElemancyBlockEntities {

    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
        DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Elemancy.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MirrorBlockEntity>> MIRROR =
        BLOCK_ENTITIES.register("mirror", () ->
            new BlockEntityType<>(MirrorBlockEntity::new, Set.of(ElemancyBlocks.MIRROR.get()), false));

    private ElemancyBlockEntities() {
    }

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}