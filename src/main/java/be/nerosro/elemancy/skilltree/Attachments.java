package be.nerosro.elemancy.skilltree;

import java.util.function.Supplier;

import be.nerosro.elemancy.Elemancy;
import be.nerosro.elemancy.jobpoint.JobPointData;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * Registers Elemancy-owned data attachments for players.
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

    public static void register(IEventBus modEventBus) {
        ATTACHMENTS.register(modEventBus);
    }
}

