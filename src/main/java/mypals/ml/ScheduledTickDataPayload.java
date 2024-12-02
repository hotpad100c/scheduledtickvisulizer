package mypals.ml;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

import java.util.List;

public record ScheduledTickDataPayload(List<SchedulTickObject> ticks, String type) implements CustomPayload {
    public static final Id<ScheduledTickDataPayload> ID = new CustomPayload.Id<>(ScheduledTickVisualizer.TICK_PACKET_ID);
    public static final PacketCodec<PacketByteBuf, ScheduledTickDataPayload> CODEC = PacketCodec.of(
            (value, buf) -> {
                buf.writeCollection(value.ticks(), SchedulTickObject.CODEC);
                buf.writeString(value.type);
            },
            buf -> new ScheduledTickDataPayload(
                    buf.readList(SchedulTickObject.CODEC),
                    buf.readString()
            )
    );
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
