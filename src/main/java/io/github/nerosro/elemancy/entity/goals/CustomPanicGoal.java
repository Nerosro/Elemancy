package io.github.nerosro.elemancy.entity.goals;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.PanicGoal;

/**
 * Created by Nerosro on 13/11/2023.
 */
public class CustomPanicGoal extends PanicGoal {
    public CustomPanicGoal(PathfinderMob pMob, double pSpeedModifier) {
        super(pMob, pSpeedModifier);
    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    protected boolean shouldPanic() {
        return true;
    }
}
