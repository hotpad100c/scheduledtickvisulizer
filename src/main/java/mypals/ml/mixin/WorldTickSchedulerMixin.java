package mypals.ml.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import mypals.ml.LogsManager.ScheduledTickVisualizerLogger;
import mypals.ml.SchedulTickObject;
import mypals.ml.ScheduledTickDataPayload;
import mypals.ml.ScheduledTickVisualizer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.tick.ChunkTickScheduler;
import net.minecraft.world.tick.OrderedTick;
import net.minecraft.world.tick.QueryableTickScheduler;
import net.minecraft.world.tick.WorldTickScheduler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.function.BooleanSupplier;
import java.util.function.LongPredicate;
import java.util.function.Supplier;

import static mypals.ml.ScheduledTickVisualizer.SCHEDULED_TICK_PACK_RANGE;
import static mypals.ml.ScheduledTickVisualizer.getPlayersNearBy;

@Mixin(WorldTickScheduler.class)
public abstract class WorldTickSchedulerMixin<T> implements QueryableTickScheduler<T> {

    @Shadow @Final public Long2ObjectMap<ChunkTickScheduler<T>> chunkTickSchedulers;

    @Shadow @Final private Long2LongMap nextTriggerTickByChunkPos;

    @Shadow @Final private LongPredicate tickingFutureReadyPredicate;

    @Shadow @Final private Queue<ChunkTickScheduler<T>> tickableChunkTickSchedulers;

    @Shadow protected abstract boolean isTickableTicksCountUnder(int maxTicks);

    @Shadow protected abstract void addTickableTick(OrderedTick<T> tick);

    @Shadow protected abstract void addTickableTicks(Queue<ChunkTickScheduler<T>> tickableChunkTickSchedulers, ChunkTickScheduler<T> chunkTickScheduler, long tick, int maxTicks);

    @Shadow protected abstract void schedule(OrderedTick<T> tick);

    @Shadow @Final public Queue<OrderedTick<T>> tickableTicks;

    @Shadow @Final private List<OrderedTick<T>> tickedTicks;


    @WrapMethod(method = "addTickableTicks(JI)V")
    public void addTickableTicks(long time, int maxTicks, Operation<Void> original) {
        ChunkTickScheduler chunkTickScheduler;
        if(ScheduledTickVisualizer.logManager != null && ScheduledTickVisualizer.logManager.ticks > 0){
            ScheduledTickVisualizerLogger.writeLogFile(ScheduledTickVisualizer.logManager.fileName,
                    "|   |--WorldTickScheduler:Started adding tickable ScheduledTicks.");
        }
        while(this.isTickableTicksCountUnder(maxTicks) && (chunkTickScheduler = (ChunkTickScheduler)this.tickableChunkTickSchedulers.poll()) != null) {
            OrderedTick<T> orderedTick = chunkTickScheduler.pollNextTick();
            if(ScheduledTickVisualizer.logManager != null && ScheduledTickVisualizer.logManager.ticks > 0){
                ScheduledTickVisualizerLogger.writeLogFile(ScheduledTickVisualizer.logManager.fileName,
                        "|   |   |--WorldTickScheduler:Got a ScheduledTick:\n" +
                                "|   |   |--Type["+orderedTick.type().toString()+"]\n" +
                                "|   |   |--SubtickOrder["+orderedTick.subTickOrder()+"]\n" +
                                "|   |   |--Priority["+orderedTick.priority()+"]\n" +
                                "|   |   |--Pos["+orderedTick.pos().toString()+"]\n" +
                                "|   |   |--Trigger time [" + orderedTick.triggerTick() +"]\n" +
                                "|   |   L_Current time <" + time +">.");
            }
            this.addTickableTick(orderedTick);
            if(ScheduledTickVisualizer.logManager != null && ScheduledTickVisualizer.logManager.ticks > 0){
                ScheduledTickVisualizerLogger.writeLogFile(ScheduledTickVisualizer.logManager.fileName,
                        "|   |   |--WorldTickScheduler:Added a ScheduledTicks to TickableTicks list("+tickedTicks.size() + "/" + maxTicks + ")");
            }
            this.addTickableTicks(this.tickableChunkTickSchedulers, chunkTickScheduler, time, maxTicks);
            OrderedTick<T> orderedTick2 = chunkTickScheduler.peekNextTick();
            if (orderedTick2 != null) {
                if(ScheduledTickVisualizer.logManager != null && ScheduledTickVisualizer.logManager.ticks > 0){
                    ScheduledTickVisualizerLogger.writeLogFile(ScheduledTickVisualizer.logManager.fileName,
                            "|   |   |--WorldTickScheduler:Got another ScheduledTick for comparing:\n" +
                                    "|   |   |--Type["+orderedTick2.type().toString()+"]\n" +
                                    "|   |   |--SubtickOrder["+orderedTick2.subTickOrder()+"]\n" +
                                    "|   |   |--Priority["+orderedTick2.priority()+"]\n" +
                                    "|   |   |--Pos["+orderedTick2.pos().toString()+"]\n" +
                                    "|   |   |--Trigger time [" + orderedTick2.triggerTick() +"]\n" +
                                    "|   |   L_Current time <" + time +">.");
                }
                if (orderedTick2.triggerTick() <= time && this.isTickableTicksCountUnder(maxTicks)) {
                    this.tickableChunkTickSchedulers.add(chunkTickScheduler);
                    if(ScheduledTickVisualizer.logManager != null && ScheduledTickVisualizer.logManager.ticks > 0){
                        ScheduledTickVisualizerLogger.writeLogFile(ScheduledTickVisualizer.logManager.fileName,
                                "|   |   |--WorldTickScheduler:This ChunkTickScheduler has other tickable ScheduledTicks,continue checking...");
                    }
                } else {
                    if(ScheduledTickVisualizer.logManager != null && ScheduledTickVisualizer.logManager.ticks > 0){
                        ScheduledTickVisualizerLogger.writeLogFile(ScheduledTickVisualizer.logManager.fileName,
                                "|   |   L_WorldTickScheduler:This ChunkTickScheduler don't have any other tickable ScheduledTicks,skip...");
                    }
                    this.schedule(orderedTick2);
                }
            }else{
                if(ScheduledTickVisualizer.logManager != null && ScheduledTickVisualizer.logManager.ticks > 0){
                    ScheduledTickVisualizerLogger.writeLogFile(ScheduledTickVisualizer.logManager.fileName,
                            "|   |   L_WorldTickScheduler:This ChunkTickScheduler don't have any other tickable ScheduledTicks,skip...");
                }
            }
        }
    }
    @WrapMethod(method = "collectTickableChunkTickSchedulers(J)V")
    public void tickBlockTickSchedulerStart(long time, Operation<Void> original) {
        ObjectIterator<Long2LongMap.Entry> objectIterator = Long2LongMaps.fastIterator(this.nextTriggerTickByChunkPos);
        if(ScheduledTickVisualizer.logManager != null && ScheduledTickVisualizer.logManager.ticks > 0){
            ScheduledTickVisualizerLogger.writeLogFile(ScheduledTickVisualizer.logManager.fileName,
                    "|   |--WorldTickScheduler:Started collecting tickable ChunkTickSchedulers.");
        }
        while (objectIterator.hasNext()) {
            Long2LongMap.Entry entry = (Long2LongMap.Entry)objectIterator.next();
            long l = entry.getLongKey();
            long m = entry.getLongValue();
            int x = (int) (l & 0xFFFFFFFFL);        // 提取低 32 位
            int z = (int) ((l >>> 32) & 0xFFFFFFFFL); // 提取高 32 位
            if(ScheduledTickVisualizer.logManager != null && ScheduledTickVisualizer.logManager.ticks > 0){
                ScheduledTickVisualizerLogger.writeLogFile(ScheduledTickVisualizer.logManager.fileName,
                        "|   |   |--WorldTickScheduler:Got a ChunkTickScheduler:\n" +
                                "|   |   |   |--pos[" + x + ", " + z +"]\n" +
                                "|   |   |   |--trigger time sample [" + m +"] \n" +
                                "|   |   |   L_current time <" + time +">.");
            }
            if (m <= time) {
                ChunkTickScheduler<T> chunkTickScheduler = this.chunkTickSchedulers.get(l);
                if (chunkTickScheduler == null) {
                    if(ScheduledTickVisualizer.logManager != null && ScheduledTickVisualizer.logManager.ticks > 0){
                        ScheduledTickVisualizerLogger.writeLogFile(ScheduledTickVisualizer.logManager.fileName,
                                "|   |   L_WorldTickScheduler:ChunkTickScheduler is null, skipping..");
                    }
                    objectIterator.remove();
                } else {
                    if(ScheduledTickVisualizer.logManager != null && ScheduledTickVisualizer.logManager.ticks > 0){
                        ScheduledTickVisualizerLogger.writeLogFile(ScheduledTickVisualizer.logManager.fileName,
                                "|   |   L_WorldTickScheduler:Got a ChunkTickScheduler contains executable scheduled tick:" + chunkTickScheduler);
                    }
                    OrderedTick<T> orderedTick = chunkTickScheduler.peekNextTick();
                    if (orderedTick == null) {
                        objectIterator.remove();
                    } else if (orderedTick.triggerTick() > time) {
                        if(ScheduledTickVisualizer.logManager != null && ScheduledTickVisualizer.logManager.ticks > 0){
                            ScheduledTickVisualizerLogger.writeLogFile(ScheduledTickVisualizer.logManager.fileName,
                                    "|   |   L_WorldTickScheduler:Got sample OrderedTick from current ChunkTickScheduler:" + orderedTick);
                        }
                        entry.setValue(orderedTick.triggerTick());
                    } else if (this.tickingFutureReadyPredicate.test(l)) {
                        if(ScheduledTickVisualizer.logManager != null && ScheduledTickVisualizer.logManager.ticks > 0){
                            ScheduledTickVisualizerLogger.writeLogFile(ScheduledTickVisualizer.logManager.fileName,
                                    "|   |   L_WorldTickScheduler:This ChunkTickScheduler is ready,add to tickableChunkTickSchedulers...:");
                        }
                        objectIterator.remove();
                        this.tickableChunkTickSchedulers.add(chunkTickScheduler);
                    }
                }
            }else{
                if(ScheduledTickVisualizer.logManager != null && ScheduledTickVisualizer.logManager.ticks > 0){
                    ScheduledTickVisualizerLogger.writeLogFile(ScheduledTickVisualizer.logManager.fileName,
                            "|   |   L_WorldTickScheduler:This ChunkTickScheduler is not ready,skipping...:");
                }
            }
        }
        if(ScheduledTickVisualizer.logManager != null && ScheduledTickVisualizer.logManager.ticks > 0){
            ScheduledTickVisualizerLogger.writeLogFile(ScheduledTickVisualizer.logManager.fileName,
                    "|   L_WorldTickScheduler:Finished collecting tickable ChunkTickSchedulers.");
        }
    }

}
