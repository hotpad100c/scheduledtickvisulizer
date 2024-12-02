package mypals.ml;

import com.jcraft.jorbis.Block;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TickOrderResolver {
    public static List<BlockPos> resolveTickOrder(List<SchedulTickObject> scheduledTicks) {

        scheduledTicks.sort(Comparator
                .comparingLong((SchedulTickObject d) -> d.triggerTick)
                .thenComparingInt(d -> d.priority)
                .thenComparingLong(d -> d.subTickOrder)
        );
        List<BlockPos> ordered= new ArrayList<>();
        for (SchedulTickObject tick : scheduledTicks) {
            ordered.add(new BlockPos(tick.getPos()));
        }
        return ordered;
    }
}
