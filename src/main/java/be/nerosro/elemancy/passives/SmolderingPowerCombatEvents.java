package be.nerosro.elemancy.passives;

import be.nerosro.elemancy.Elemancy;
import be.nerosro.elemancy.entity.ManaBlastProjectile;
import be.nerosro.elemancy.skilltree.SkillTreeEntries;
import be.nerosro.soulmark.attunement.AttunementUtil;
import be.nerosro.soulmark.element.SoulmarkElements;
import be.nerosro.soulmark.skilltree.SkillTreeUtil;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

/** Applies Fire-attuned Smoldering Power's occasional ignition effect. */
@EventBusSubscriber(modid = Elemancy.MOD_ID)
public final class SmolderingPowerCombatEvents {
    private static final float IGNITE_CHANCE = 0.05F;
    private static final int IGNITE_DURATION_SECONDS = 2;

    private SmolderingPowerCombatEvents() {
    }

    @SubscribeEvent
    public static void onDamage(LivingDamageEvent.Post event) {
        if (event.getNewDamage() <= 0.0F || event.getEntity().level().isClientSide()) {
            return;
        }

        DamageSource source = event.getSource();
        if (!(source.getEntity() instanceof Player player)
            || !SkillTreeUtil.hasNode(player, SkillTreeEntries.SMOLDERING_POWER_ID)
            || !AttunementUtil.isAttunedTo(player, SoulmarkElements.FIRE.get())
            || !isEligible(source)) {
            return;
        }

        if (player.getRandom().nextFloat() < IGNITE_CHANCE) {
            event.getEntity().igniteForSeconds(IGNITE_DURATION_SECONDS);
        }
    }

    private static boolean isEligible(DamageSource source) {
        return source.is(DamageTypeTags.IS_PLAYER_ATTACK)
            || source.getDirectEntity() instanceof ManaBlastProjectile;
    }
}