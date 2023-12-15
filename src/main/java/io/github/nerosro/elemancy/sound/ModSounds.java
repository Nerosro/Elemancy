package io.github.nerosro.elemancy.sound;

import io.github.nerosro.elemancy.Elemancy;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Created by Nerosro on 11/11/2023.
 */
public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENT =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Elemancy.MOD_ID);

    public static final RegistryObject<SoundEvent> INFUSED_WAND_CRAFT_SUCCESS = registerSoundEvents("infused_wand_craft_success");

    public static void register(IEventBus eventBus){
        SOUND_EVENT.register(eventBus);
    }

    private static RegistryObject<SoundEvent> registerSoundEvents(String name) {
        return SOUND_EVENT.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Elemancy.MOD_ID, name)));
    }
}
