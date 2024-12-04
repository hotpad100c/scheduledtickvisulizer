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
