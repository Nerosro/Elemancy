package be.nerosro.elemancy.client.mirror;

import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;

/**
 * Rare figure profiles rendered in a single entry flicker window.
 */
public enum FlickerAppearance {
    ALLAY("textures/entity/allay/allay.png", BodyProfile.ALLAY, 32, 32),
    CHICKEN("textures/entity/chicken/chicken_temperate.png", BodyProfile.CHICKEN, 64, 32),
    CREEPER("textures/entity/creeper/creeper.png", BodyProfile.CREEPER, 64, 32),
    ZOMBIE("textures/entity/zombie/zombie.png", BodyProfile.HUMANOID, 64, 64);

    private static final FlickerAppearance[] ENABLED_VALUES = {
        //ALLAY,
        CHICKEN,
        //CREEPER,
        //ZOMBIE
    };

    private final Identifier texture;
    private final BodyProfile profile;
    private final int textureWidth;
    private final int textureHeight;

    FlickerAppearance(String texture, BodyProfile profile, int textureWidth,
                      int textureHeight) {
        this.texture = Identifier.withDefaultNamespace(texture);
        this.profile = profile;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    public Identifier texture() {
        return texture;
    }


    public BodyProfile profile() {
        return profile;
    }

    public int textureWidth() {
        return textureWidth;
    }

    public int textureHeight() {
        return textureHeight;
    }

    public static FlickerAppearance random(RandomSource random) {
        return ENABLED_VALUES[random.nextInt(ENABLED_VALUES.length)];
    }

    public enum BodyProfile {
        ALLAY,
        CHICKEN,
        CREEPER,
        HUMANOID,
    }
}