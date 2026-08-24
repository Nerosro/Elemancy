package be.nerosro.elemancy.client;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.phys.Vec3;

/**
 * Client render data for a five-point ritual sigil.
 */
public final class RitualSigilRenderState extends EntityRenderState {
    public final Vec3[] vertices = new Vec3[5];
    public int color = 0xFFFFFFFF;
    public float drawProgress;
    public float opacity;
}