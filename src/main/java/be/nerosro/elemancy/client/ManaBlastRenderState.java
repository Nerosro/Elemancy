package be.nerosro.elemancy.client;

import be.nerosro.elemancy.spell.data.SpellVisual;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

/**
 * Render state for mana blast projectiles.
 * Carries visual type, crescent angle, and travel direction from entity to renderer.
 */
public class ManaBlastRenderState extends EntityRenderState {
    public SpellVisual visualType = SpellVisual.PROJECTILE_PARTICLE_TRAIL;
    public float crescentAngle = 0f;
    public float yRot = 0f;
    public float xRot = 0f;
}
