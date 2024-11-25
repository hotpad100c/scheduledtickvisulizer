package mypals.ml;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.tick.TickPriority;
import org.jetbrains.annotations.Nullable;

public class SchedulTickObject {
    public  BlockPos pos;
    public long triggerTick;
    public int priority;
    public long subTickOrder;

    public final String type;
    public SchedulTickObject(BlockPos pos, long triggerTick, int priority, long subTickOrder, String type) {
        this.pos = pos;
        this.triggerTick = triggerTick;
        this.priority = priority;
        this.subTickOrder = subTickOrder;
        this.type = type;
    }
    public BlockPos getPos(){
        return pos;
    }
    public long getTriggerTick(){
        return triggerTick;
    }
    public int getPriority(){
        return priority;
    }
    public long getTickOrder(){
        return subTickOrder;
    }
    public String getType(){
        return type;
    }
    public static final PacketCodec<PacketByteBuf,SchedulTickObject> CODEC = PacketCodec.of(
            (value, buf) -> {
                buf.writeBlockPos(value.getPos());
                buf.writeLong(value.getTriggerTick());
                buf.writeInt(value.getPriority());
                buf.writeLong(value.getTickOrder());
                buf.writeString(value.getType());
            },
            buf -> new SchedulTickObject(
                    buf.readBlockPos(),
                    buf.readLong(),
                    buf.readInt(),
                    buf.readLong(),
                    buf.readString()
            )
    );
}
