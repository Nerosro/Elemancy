package be.nerosro.elemancy.block;

import org.jspecify.annotations.Nullable;

import be.nerosro.elemancy.client.mirror.FlickerAppearance;
import be.nerosro.elemancy.client.mirror.FlickerWindow;
import be.nerosro.elemancy.client.mirror.MirrorTransitionStyle;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Client render anchor for the lower half of a placed Mirror.
 */
public final class MirrorBlockEntity extends BlockEntity {

    private static final float TRANSITION_TICKS = 12.0f;
    private static final float FLICKER_TRANSITION_TICKS = 21.0f;
    private static final int RARE_APPEARANCE_CHANCE = 100;

    private boolean transitionTarget;
    private float transitionProgress;
    private long lastTransitionTick = Long.MIN_VALUE;
    private MirrorTransitionStyle transitionStyle = MirrorTransitionStyle.CENTER_EXPANSION;
    private long entrySequence;
    private @Nullable FlickerAppearance flickerAppearance;
    private @Nullable FlickerWindow flickerWindow;

    public MirrorBlockEntity(BlockPos pos, BlockState state) {
        super(ElemancyBlockEntities.MIRROR.get(), pos, state);
    }

    /**
     * Advances this client's transient reflection transition without persisting gameplay state.
     */
    public TransitionState updateTransition(boolean active) {
        if (level == null) {
            return new TransitionState(transitionProgress, transitionStyle, transitionTarget, flickerAppearance,
                flickerWindow);
        }

        long gameTime = level.getGameTime();
        if (active != transitionTarget) {
            transitionTarget = active;
            if (active) {
                transitionProgress = 0.0f;
                selectEntryAppearance(gameTime);
            } else {
                flickerAppearance = null;
                flickerWindow = null;
            }
        }

        if (lastTransitionTick != gameTime) {
            long elapsed = lastTransitionTick == Long.MIN_VALUE ? 1L : Math.max(1L, gameTime - lastTransitionTick);
            float transitionDuration = transitionStyle.isFlicker() ? FLICKER_TRANSITION_TICKS : TRANSITION_TICKS;
            float delta = elapsed / transitionDuration;
            transitionProgress = Math.clamp(transitionProgress + (transitionTarget ? delta : -delta), 0.0f, 1.0f);
            lastTransitionTick = gameTime;
        }
        return new TransitionState(transitionProgress, transitionStyle, transitionTarget, flickerAppearance, flickerWindow);
    }

    /**
     * Selects one stable, client-local transition outcome for the current activation entry.
     */
    private void selectEntryAppearance(long gameTime) {
        entrySequence++;
        long entrySeed = worldPosition.asLong() ^ gameTime ^ entrySequence;
        RandomSource randomSeed = RandomSource.create(entrySeed);

        transitionStyle = MirrorTransitionStyle.random(randomSeed);
        //transitionStyle = MirrorTransitionStyle.FLICKER; //Set hardcoded value here for testing.
        flickerAppearance = null;
        flickerWindow = null;
        if (!transitionStyle.isFlicker()) return;
        if (randomSeed.nextInt(RARE_APPEARANCE_CHANCE) != 0) return;

        flickerAppearance = FlickerAppearance.random(randomSeed);
        flickerWindow = FlickerWindow.random(randomSeed);
    }

    public record TransitionState(float progress, MirrorTransitionStyle style, boolean entering,
                                  @Nullable FlickerAppearance flickerAppearance,
                                  @Nullable FlickerWindow flickerWindow) {
    }
}