package be.nerosro.elemancy.items.tools.earth;

import be.nerosro.elemancy.network.EarthExcavationTogglePayload;
import be.nerosro.soulmark.skilltree.ContextualKeyActions;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

/**
 * Registers Earth-tool mode switching on Soulmark's existing contextual action key.
 */
public final class EarthExcavationKeyAction {
    private EarthExcavationKeyAction() {
    }

    public static void register() {
        ContextualKeyActions.register(
            player -> EarthTools.isEarthTool(player.getMainHandItem()),
            _ -> ClientPacketDistributor.sendToServer(new EarthExcavationTogglePayload())
        );
    }
}