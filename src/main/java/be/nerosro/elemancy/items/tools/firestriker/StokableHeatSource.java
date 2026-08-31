package be.nerosro.elemancy.items.tools.firestriker;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

public interface StokableHeatSource {
    boolean elemancy$tryStoke(ServerLevel level, Player player);
}