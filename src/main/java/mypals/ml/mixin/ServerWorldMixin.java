package mypals.ml.mixin;

import mypals.ml.LogsManager.ScheduledTickVisualizerLogger;
import mypals.ml.SchedulTickObject;
import mypals.ml.ScheduledTickVisualizer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.entity.ai.brain.ScheduleRule;
import net.minecraft.fluid.Fluid;
import net.minecraft.network.PacketByteBuf;
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
import net.minecraft.world.tick.WorldTickScheduler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import static mypals.ml.ScheduledTickVisualizer.SCHEDULED_TICK_PACK_RANGE;
import static mypals.ml.ScheduledTickVisualizer.getPlayersNearBy;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World implements StructureWorldAccess, AttachmentTarget {
    @Shadow @Final private WorldTickScheduler<Block> blockTickScheduler;
    @Shadow @Final private WorldTickScheduler<Fluid> fluidTickScheduler;
    protected ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long biomeAccess, int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, profiler, isClient, debugWorld, biomeAccess, maxChainedNeighborUpdates);
    }
    @Inject(method = "Lnet/minecraft/server/world/ServerWorld;tick(Ljava/util/function/BooleanSupplier;)V",
            at = @At(value = "HEAD",
            shift = At.Shift.AFTER))
    public void tick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        List<SchedulTickObject> orderedFluidTicks = new ArrayList<>();
        List<SchedulTickObject> orderedBlockTicks = new ArrayList<>();
        List<ServerPlayerEntity> players = new ArrayList<>();
        long time = this.getTime();
        blockTickScheduler.chunkTickSchedulers.values().forEach(chunkTickScheduler -> {
            chunkTickScheduler.getQueueAsStream().forEach(orderedTick ->{
                if(orderedTick.triggerTick() - time > 0 && !getPlayersNearBy(orderedTick.pos(),this.getGameRules().getInt(SCHEDULED_TICK_PACK_RANGE)).isEmpty()){
                    players.addAll(getPlayersNearBy(orderedTick.pos(),this.getGameRules().getInt(SCHEDULED_TICK_PACK_RANGE)));
                    orderedBlockTicks.add(
                            new SchedulTickObject(orderedTick.pos(),
                                    orderedTick.triggerTick() - time,
                                    orderedTick.priority().getIndex(), orderedTick.subTickOrder(),
                                    Text.translatable(orderedTick.type().getTranslationKey()).getString()));
                }
            });
        });
        fluidTickScheduler.chunkTickSchedulers.values().forEach(chunkTickScheduler ->{
            chunkTickScheduler.getQueueAsStream().forEach(orderedTick ->{
                if(orderedTick.triggerTick() - time > 0 && !getPlayersNearBy(orderedTick.pos(),this.getGameRules().getInt(SCHEDULED_TICK_PACK_RANGE)).isEmpty()){
                    players.addAll(getPlayersNearBy(orderedTick.pos(),this.getGameRules().getInt(SCHEDULED_TICK_PACK_RANGE)));
                    orderedFluidTicks.add(
                            new SchedulTickObject(orderedTick.pos(),
                                    orderedTick.triggerTick() - time,
                                    orderedTick.priority().getIndex(), orderedTick.subTickOrder(),
                                    Text.translatable(orderedTick.type().getStateManager().getDefaultState().getBlockState().getBlock().getTranslationKey()).getString()));
                }
            });
        });
        if(ScheduledTickVisualizer.server != null){
            for(ServerPlayerEntity player : players){
                ServerPlayNetworking.send(player,ScheduledTickVisualizer.TICK_PACKET_ID,ScheduledTickData(orderedBlockTicks,"Block"));
                ServerPlayNetworking.send(player,ScheduledTickVisualizer.TICK_PACKET_ID,ScheduledTickData(orderedFluidTicks,"Fluid"));
            }
        }
    }
    protected PacketByteBuf ScheduledTickData(List<SchedulTickObject> ticks,String type) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(type);
        buf.writeCollection(ticks, (packetByteBuf, schedulTickObject) -> {
            packetByteBuf.writeBlockPos(schedulTickObject.pos);
            packetByteBuf.writeLong(schedulTickObject.time);
            packetByteBuf.writeInt(schedulTickObject.priority);
            packetByteBuf.writeLong(schedulTickObject.subTick);
            packetByteBuf.writeString(schedulTickObject.name);
        });
        return buf;
    }
    @Inject(method = "Lnet/minecraft/server/world/ServerWorld;tick(Ljava/util/function/BooleanSupplier;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/tick/WorldTickScheduler;tick(JILjava/util/function/BiConsumer;)V",
                    shift = At.Shift.BEFORE,ordinal = 0))
    public void tickBlockTickSchedulerStart(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        if(ScheduledTickVisualizer.logManager != null && ScheduledTickVisualizer.logManager.ticks > 0){
            ScheduledTickVisualizerLogger.writeLogFile(ScheduledTickVisualizer.logManager.fileName,"-----------------------------------");
            ScheduledTickVisualizerLogger.writeLogFile(ScheduledTickVisualizer.logManager.fileName,"ServerWorld:Ticking BlockTickScheduler..");
        }
    }
    @Inject(method = "Lnet/minecraft/server/world/ServerWorld;tick(Ljava/util/function/BooleanSupplier;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/tick/WorldTickScheduler;tick(JILjava/util/function/BiConsumer;)V",
                    shift = At.Shift.AFTER,ordinal = 0))
    public void tickBlockTickSchedulerEnd(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        if(ScheduledTickVisualizer.logManager != null && ScheduledTickVisualizer.logManager.ticks > 0){
            ScheduledTickVisualizerLogger.writeLogFile(ScheduledTickVisualizer.logManager.fileName,"ServerWorld:Finished ticking BlockTickScheduler..");
        }
    }
    @Inject(method = "Lnet/minecraft/server/world/ServerWorld;tick(Ljava/util/function/BooleanSupplier;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/tick/WorldTickScheduler;tick(JILjava/util/function/BiConsumer;)V",
                    shift = At.Shift.BEFORE,ordinal = 1))
    public void tickFluidTickSchedulerStart(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        if(ScheduledTickVisualizer.logManager != null && ScheduledTickVisualizer.logManager.ticks > 0){
            ScheduledTickVisualizerLogger.writeLogFile(ScheduledTickVisualizer.logManager.fileName,"ServerWorld:Ticking FluidTickScheduler...");
        }
    }
    @Inject(method = "Lnet/minecraft/server/world/ServerWorld;tick(Ljava/util/function/BooleanSupplier;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/tick/WorldTickScheduler;tick(JILjava/util/function/BiConsumer;)V",
                    shift = At.Shift.AFTER,ordinal = 1))
    public void tickFluidTickSchedulerEnd(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        if(ScheduledTickVisualizer.logManager != null && ScheduledTickVisualizer.logManager.ticks > 0){
            ScheduledTickVisualizerLogger.writeLogFile(ScheduledTickVisualizer.logManager.fileName,"ServerWorld:Finished ticking FluidTickScheduler..");
            ScheduledTickVisualizerLogger.writeLogFile(ScheduledTickVisualizer.logManager.fileName,"-----------------------------------");
        }
    }

}
