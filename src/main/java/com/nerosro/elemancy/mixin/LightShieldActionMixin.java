package com.nerosro.elemancy.mixin;

import be.nerosro.elemancy.items.tools.lightshield.LightShieldConcealment;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class LightShieldActionMixin {

    @Shadow public ServerPlayer player;

    @Inject(method = "handleAnimate", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/server/level/ServerLevel;)V", shift = At.Shift.AFTER))
    private void elemancy$onSwing(ServerboundSwingPacket packet, CallbackInfo callback) {
        LightShieldConcealment.endOnAction(player);
    }

    @Inject(method = "handleUseItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/server/level/ServerLevel;)V", shift = At.Shift.AFTER))
    private void elemancy$onUseItem(ServerboundUseItemPacket packet, CallbackInfo callback) {
        LightShieldConcealment.endOnAction(player);
    }

    @Inject(method = "handleUseItemOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/server/level/ServerLevel;)V", shift = At.Shift.AFTER))
    private void elemancy$onUseItemOn(ServerboundUseItemOnPacket packet, CallbackInfo callback) {
        LightShieldConcealment.endOnAction(player);
    }

    @Inject(method = "handleInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/server/level/ServerLevel;)V", shift = At.Shift.AFTER))
    private void elemancy$onInteract(ServerboundInteractPacket packet, CallbackInfo callback) {
        LightShieldConcealment.endOnAction(player);
    }

    @Inject(method = "handlePlayerAction", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/server/level/ServerLevel;)V", shift = At.Shift.AFTER))
    private void elemancy$onPlayerAction(ServerboundPlayerActionPacket packet, CallbackInfo callback) {
        if (packet.getAction() != ServerboundPlayerActionPacket.Action.RELEASE_USE_ITEM) {
            LightShieldConcealment.endOnAction(player);
        }
    }

    @Inject(method = "handleSetCarriedItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/server/level/ServerLevel;)V", shift = At.Shift.AFTER))
    private void elemancy$onHotbarSelect(ServerboundSetCarriedItemPacket packet, CallbackInfo callback) {
        LightShieldConcealment.endOnAction(player);
    }
}