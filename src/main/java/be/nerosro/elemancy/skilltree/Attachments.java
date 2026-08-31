package be.nerosro.elemancy.skilltree;

import java.util.function.Supplier;

import be.nerosro.elemancy.Elemancy;
import be.nerosro.elemancy.items.tools.firestriker.FireStrikerState;
import be.nerosro.elemancy.jobpoint.JobPointData;
import be.nerosro.elemancy.passives.VitalCurrentsState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * Registers Elemancy-owned data attachments.
 */
public class Attachments {

    private static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
        DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Elemancy.MOD_ID);

    /**
     * Tracks which spell the player currently has equipped (selected via radial menu).
     */
    public static final Supplier<AttachmentType<EquippedSpellData>> EQUIPPED_SPELL =
        ATTACHMENTS.register("equipped_spell", () ->
            AttachmentType.serializable(EquippedSpellData::new)
                .copyOnDeath()
                .build());

    /**
     * Stores Elemancy's own Job Point balance and milestone claims.
     */
    public static final Supplier<AttachmentType<JobPointData>> JOB_POINTS =
        ATTACHMENTS.register("job_points", () ->
            AttachmentType.serializable(JobPointData::new)
                .copyOnDeath()
                .build());

    public static final Supplier<AttachmentType<FireStrikerState>> FIRE_STRIKER_STATE =
        ATTACHMENTS.register("fire_striker_state", () ->
            AttachmentType.serializable(FireStrikerState::new).build());

    public static final Supplier<AttachmentType<VitalCurrentsState>> VITAL_CURRENTS_STATE =
        ATTACHMENTS.register("vital_currents_state", () ->
            AttachmentType.serializable(VitalCurrentsState::new).build());

    public static void register(IEventBus modEventBus) {
        ATTACHMENTS.register(modEventBus);
    }
}

