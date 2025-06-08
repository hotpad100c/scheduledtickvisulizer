package mypals.ml.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import mypals.ml.LogsManager.ScheduledTickVisualizerLogger;
import mypals.ml.ScheduledTickVisualizer;
import net.minecraft.world.tick.ChunkTickScheduler;
import net.minecraft.world.tick.OrderedTick;
import net.minecraft.world.tick.QueryableTickScheduler;
import net.minecraft.world.tick.WorldTickScheduler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Queue;
import java.util.function.LongPredicate;

@Mixin(WorldTickScheduler.class)
public abstract class WorldTickSchedulerMixin<T> implements QueryableTickScheduler<T> {

    @Shadow @Final public Long2ObjectMap<ChunkTickScheduler<T>> chunkTickSchedulers;

    @Shadow @Final private Long2LongMap nextTriggerTickByChunkPos;

    @Shadow @Final private LongPredicate tickingFutureReadyPredicate;

    @Shadow @Final private Queue<ChunkTickScheduler<T>> tickableChunkTickSchedulers;

    @WrapMethod(method = "collectTickableChunkTickSchedulers(J)V")
    public void tickBlockTickSchedulerStart(long time, Operation<Void> original) {
        ObjectIterator<Long2LongMap.Entry> objectIterator = Long2LongMaps.fastIterator(this.nextTriggerTickByChunkPos);
        if(ScheduledTickVisualizer.logManager != null && ScheduledTickVisualizer.logManager.ticks > 0){
            ScheduledTickVisualizerLogger.writeLogFile(ScheduledTickVisualizer.logManager.fileName,
                    "WorldTickScheduler:Started collecting tickable ChunkTickSchedulers.");
        }
        while (objectIterator.hasNext()) {
            Long2LongMap.Entry entry = (Long2LongMap.Entry)objectIterator.next();
            long l = entry.getLongKey();
            long m = entry.getLongValue();
            if(ScheduledTickVisualizer.logManager != null && ScheduledTickVisualizer.logManager.ticks > 0){
                ScheduledTickVisualizerLogger.writeLogFile(ScheduledTickVisualizer.logManager.fileName,
                        "WorldTickScheduler:Got a ChunkTickScheduler:" +
                                "pos[" + l+"],trigger time sample[" + m +"] and current time is<" + time +">.");
            }
            if (m <= time) {
                ChunkTickScheduler<T> chunkTickScheduler = this.chunkTickSchedulers.get(l);
                if (chunkTickScheduler == null) {
                    if(ScheduledTickVisualizer.logManager != null && ScheduledTickVisualizer.logManager.ticks > 0){
                        ScheduledTickVisualizerLogger.writeLogFile(ScheduledTickVisualizer.logManager.fileName,
                                "WorldTickScheduler:ChunkTickScheduler is null, skipping..");
                    }
                    objectIterator.remove();
                } else {
                    if(ScheduledTickVisualizer.logManager != null && ScheduledTickVisualizer.logManager.ticks > 0){
                        ScheduledTickVisualizerLogger.writeLogFile(ScheduledTickVisualizer.logManager.fileName,
                                "WorldTickScheduler:Got a ChunkTickScheduler contains executable scheduled tick:" + chunkTickScheduler);
                    }
                    OrderedTick<T> orderedTick = chunkTickScheduler.peekNextTick();
                    if (orderedTick == null) {
                        objectIterator.remove();
                    } else if (orderedTick.triggerTick() > time) {
                        if(ScheduledTickVisualizer.logManager != null && ScheduledTickVisualizer.logManager.ticks > 0){
                            ScheduledTickVisualizerLogger.writeLogFile(ScheduledTickVisualizer.logManager.fileName,
                                    "WorldTickScheduler:Got sample OrderedTick from current ChunkTickScheduler:" + orderedTick);
                        }
                        entry.setValue(orderedTick.triggerTick());
                    } else if (this.tickingFutureReadyPredicate.test(l)) {
                        if(ScheduledTickVisualizer.logManager != null && ScheduledTickVisualizer.logManager.ticks > 0){
                            ScheduledTickVisualizerLogger.writeLogFile(ScheduledTickVisualizer.logManager.fileName,
                                    "WorldTickScheduler:this ChunkTickScheduler wasn't ready,skipping...:");
                        }
                        objectIterator.remove();
                        this.tickableChunkTickSchedulers.add(chunkTickScheduler);
                    }
                }
            }
        }
        if(ScheduledTickVisualizer.logManager != null && ScheduledTickVisualizer.logManager.ticks > 0){
            ScheduledTickVisualizerLogger.writeLogFile(ScheduledTickVisualizer.logManager.fileName,
                    "WorldTickScheduler:Finished collecting tickable ChunkTickSchedulers.");
        }
    }

}
