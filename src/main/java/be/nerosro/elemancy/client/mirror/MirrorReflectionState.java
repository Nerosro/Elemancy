package be.nerosro.elemancy.client.mirror;

import org.jspecify.annotations.Nullable;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;

/**
 * Client render data for an active Mirror reflection.
 */
public final class MirrorReflectionState extends BlockEntityRenderState {

    public Direction facing = Direction.NORTH;
    public boolean active;
    public float animationTime;
    public Identifier skinTexture;
    public boolean slimModel;
    public float transitionProgress;
    public MirrorTransitionStyle transitionStyle = MirrorTransitionStyle.CENTER_EXPANSION;
    public boolean transitionEntering;
    public @Nullable FlickerAppearance flickerAppearance;
    public @Nullable FlickerWindow flickerWindow;
}