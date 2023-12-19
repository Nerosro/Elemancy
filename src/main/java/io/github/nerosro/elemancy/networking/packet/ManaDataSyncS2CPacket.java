package io.github.nerosro.elemancy.networking.packet;

import io.github.nerosro.elemancy.client.ClientManaData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Created by Nerosro on 18/07/2023.
 */
public class ManaDataSyncS2CPacket {
    private final int mana;
    private final double regen;
    private final String element;
    private final int currentMana;

    public ManaDataSyncS2CPacket(int mana, double regen, String element, int currentMana) {
        this.mana = mana;
        this.regen = regen;
        this.element = element;
        this.currentMana = currentMana;
    }

    public ManaDataSyncS2CPacket(FriendlyByteBuf buf){
        this.mana = buf.readInt();
        this.regen = buf.readDouble();
        this.element = buf.readUtf();
        this.currentMana = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf){
        buf.writeInt(mana);
        buf.writeDouble(regen);
        buf.writeUtf(element);
        buf.writeInt(currentMana);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier){
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
           //Here we are on the Client

            ClientManaData.set(mana, regen, element);
            ClientManaData.setCurrentMana(currentMana);
        });
        return true;
    }
}
