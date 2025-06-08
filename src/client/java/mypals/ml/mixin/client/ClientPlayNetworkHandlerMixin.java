package mypals.ml.mixin.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static mypals.ml.ScheduledTickVisualizer.HELLO_PACKET_ID;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Inject(method = "onGameJoin", at = @At(value = "RETURN"))
    private void onGameJoined(GameJoinS2CPacket packet, CallbackInfo ci) {
        ClientPlayNetworking.send(HELLO_PACKET_ID, PacketByteBufs.create().writeString("hi!"));
    }
}
