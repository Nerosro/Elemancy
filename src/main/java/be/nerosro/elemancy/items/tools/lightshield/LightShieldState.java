package be.nerosro.elemancy.items.tools.lightshield;

public final class LightShieldState {

    private static final int INVISIBILITY_COOLDOWN_TICKS = 200;

    private boolean effectsUsedThisRaise;
    private int invisibilityCooldownEndsAtTick;

    void resetRaise() {
        effectsUsedThisRaise = false;
    }

    boolean tryUseRaiseEffects() {
        if (effectsUsedThisRaise) {
            return false;
        }
        effectsUsedThisRaise = true;
        return true;
    }

    int invisibilityCooldownTicksLeft(int currentTick) {
        return Math.max(0, invisibilityCooldownEndsAtTick - currentTick);
    }

    boolean tryStartInvisibilityCooldown(int currentTick) {
        if (invisibilityCooldownTicksLeft(currentTick) > 0) {
            return false;
        }
        invisibilityCooldownEndsAtTick = currentTick + INVISIBILITY_COOLDOWN_TICKS;
        return true;
    }
}