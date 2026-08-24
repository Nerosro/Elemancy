package be.nerosro.elemancy.infusion;

import be.nerosro.elemancy.ElemancyColors;

/**
 * Identifies which infusion spell a recipe belongs to.
 * Each spell has its own particle color and future unlock requirements.
 */
public enum InfusionSpell {
    ELEMENTIZE(ElemancyColors.MANA.rgb()),
    TRANSMUTE(ElemancyColors.TRANSMUTE.rgb());

    private final int particleColor;

    InfusionSpell(int particleColor) {
        this.particleColor = particleColor;
    }

    public int particleColor() {
        return particleColor;
    }
}

