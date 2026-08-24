package be.nerosro.elemancy.ritual.shared;

/**
 * Tunable timing constants shared by ritual cutscenes and ritual presentation entities.
 */
public final class Timings {
    private Timings() {
    }

    /**
     * Ticks for a particle stream to travel from its source to its target (used both for the
     * center->pillar streams and the pillar->standing-tile finale streams). 60 ticks = 3 seconds.
     */
    public static final int STREAM_TRAVEL_TICKS = 60;

    /**
     * Ticks of silence between one pillar's stream finishing and the next pillar's stream
     * starting, during the sequential pillar-streaming phase. 20 ticks = 1 second.
     */
    public static final int STREAM_PAUSE_TICKS = 20;

    /**
     * Ticks between each of the finale's 4 rapid strikes on the player.
     */
    public static final int FINALE_STRIKE_INTERVAL_TICKS = 4;

    /**
     * Ticks between each pillar's strike during the pillar-striking phase (applies before the
     * first strike too - i.e. the pause after all 4 streams finish, then between each strike).
     */
    public static final int PILLAR_STRIKE_INTERVAL_TICKS = 40;

    /**
     * Total sigil duration before capstone lightning begins: 1 second drawing plus 2 seconds held.
     */
    public static final int SIGIL_HOLD_TICKS = 60;

    /**
     * Ticks for the pentagram to draw from its first activated capstone.
     */
    public static final int SIGIL_DRAW_TICKS = 20;

    /**
     * Ticks for the pentagram to fade after the lightning phase starts.
     */
    public static final int SIGIL_FADE_TICKS = 15;

    /**
     * Ticks to wait after the pillar phase's last strike before starting the finale streams.
     */
    public static final int PRE_FINALE_DELAY_TICKS = 20;
}
