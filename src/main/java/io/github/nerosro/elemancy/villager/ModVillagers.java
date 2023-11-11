package io.github.nerosro.elemancy.villager;

import com.google.common.collect.ImmutableSet;
import io.github.nerosro.elemancy.Elemancy;
import io.github.nerosro.elemancy.block.ModBlocks;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


/**
 * Created by Nerosro on 10/11/2023.
 */
public class ModVillagers {
    public static final DeferredRegister<PoiType> POI_TYPES =
            DeferredRegister.create(ForgeRegistries.POI_TYPES, Elemancy.MOD_ID); //Point of Interest
    public static final DeferredRegister<VillagerProfession> VILLAGER_PROFESSIONS=
            DeferredRegister.create(ForgeRegistries.VILLAGER_PROFESSIONS, Elemancy.MOD_ID);

    public static final RegistryObject<PoiType> ELEMANCER_POI = POI_TYPES.register("elemancy_poi",
            () -> new PoiType(ImmutableSet.copyOf(ModBlocks.INFUSED_WOOL.get().getStateDefinition().getPossibleStates()),
                    1,1 ));

    public static final RegistryObject<VillagerProfession> ELEMANCER =
            VILLAGER_PROFESSIONS.register("elemancer",
                    () -> new VillagerProfession("elemancer", holder -> holder.get() == ELEMANCER_POI.get(),
                            holder -> holder.get() == ELEMANCER_POI.get(),
                            ImmutableSet.of(), ImmutableSet.of(), SoundEvents.VILLAGER_WORK_CLERIC)
                    );


    public static void register(IEventBus eventBus) {
        POI_TYPES.register(eventBus);
        VILLAGER_PROFESSIONS.register(eventBus);

    }
}
