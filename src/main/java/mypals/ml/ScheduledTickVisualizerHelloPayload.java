package mypals.ml;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

import java.util.UUID;

public record ScheduledTickVisualizerHelloPayload(UUID player) implements CustomPayload{
    public static final CustomPayload.Id<ScheduledTickVisualizerHelloPayload> ID = new CustomPayload.Id<>(ScheduledTickVisualizer.GREETING_PACKET_ID);
    public UUID getPlayer(){
        return player;
    }

   static final PacketCodec<PacketByteBuf, ScheduledTickVisualizerHelloPayload> CODEC = PacketCodec.of(
            (value, buf) -> {
                buf.writeUuid(value.getPlayer());
            },
            buf -> new ScheduledTickVisualizerHelloPayload(
                buf.readUuid()
            )
    );
    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
