package mypals.ml;

import net.minecraft.util.math.BlockPos;

import java.util.Objects;

public final class SchedulTickObject{
    public BlockPos pos;
    public long time;
    public int priority;
    public long subTick;
    public String name;

    public SchedulTickObject(BlockPos pos, long time, int priority, long subTick, String name) {
        this.pos=pos;
        this.time=time;
        this.priority=priority;
        this.subTick=subTick;
        this.name=name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SchedulTickObject obj = (SchedulTickObject) o;
        return pos==obj.pos && time==obj.time && priority==obj.priority && subTick==obj.subTick && name==obj.name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pos, time, priority, subTick, name);
    }
}