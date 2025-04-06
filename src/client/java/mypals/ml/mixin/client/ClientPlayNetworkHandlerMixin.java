package mypals.ml.mixin.client;

import mypals.ml.ScheduledTickVisualizerHelloPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Inject(method = "onGameJoin", at = @At(value = "RETURN"))
    private void onGameJoined(GameJoinS2CPacket packet, CallbackInfo ci) {
        ClientPlayNetworking.send(new ScheduledTickVisualizerHelloPayload(MinecraftClient.getInstance().player.getUuid()));
    }
}
