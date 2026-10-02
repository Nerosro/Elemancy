package com.nerosro.elemancy.mixin.client;

import be.nerosro.elemancy.entity.LightShieldDecoyEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.entity.ClientAvatarState;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.entity.player.PlayerSkin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.jspecify.annotations.Nullable;

/** Supplies client-only avatar data for the common Light Shield decoy entity. */
@Mixin(LightShieldDecoyEntity.class)
public abstract class LightShieldDecoyClientAvatarMixin implements ClientAvatarEntity {

    @Unique
    private final ClientAvatarState elemancy$avatarState = new ClientAvatarState();

    @Override
    public ClientAvatarState avatarState() {
        return elemancy$avatarState;
    }

    @Override
    public PlayerSkin getSkin() {
        LightShieldDecoyEntity decoy = (LightShieldDecoyEntity) (Object) this;
        if (Minecraft.getInstance().getConnection() == null || decoy.getOwnerId() == null) {
            return DefaultPlayerSkin.getDefaultSkin();
        }
        PlayerInfo owner = Minecraft.getInstance().getConnection().getPlayerInfo(decoy.getOwnerId());
        return owner == null ? DefaultPlayerSkin.getDefaultSkin() : owner.getSkin();
    }

    @Override
    public Parrot.@Nullable Variant getParrotVariantOnShoulder(boolean left) {
        return null;
    }

    @Override
    public boolean showExtraEars() {
        return false;
    }
}