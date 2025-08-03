package mypals.ml.mixin;

import mypals.ml.LogsManager.ScheduledTickVisualizerLogger;
import mypals.ml.SchedulTickObject;
import mypals.ml.ScheduledTickDataPayload;
import mypals.ml.ScheduledTickVisualizer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerTickManager;
import net.minecraft.server.command.TickCommand;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.GameRules;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.tick.TickManager;
import net.minecraft.world.tick.TickScheduler;
import net.minecraft.world.tick.WorldTickScheduler;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import static mypals.ml.ScheduledTickVisualizer.*;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World implements StructureWorldAccess{
    @Shadow @Final private WorldTickScheduler<Block> blockTickScheduler;
    @Shadow @Final private WorldTickScheduler<Fluid> fluidTickScheduler;

    protected ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry, boolean isClient, boolean debugWorld, long seed, int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, isClient, debugWorld, seed, maxChainedNeighborUpdates);
    }

    @Shadow public abstract GameRules getGameRules();


    @Shadow @NotNull public abstract MinecraftServer getServer();

    @Shadow public abstract TickManager getTickManager();

    @Inject(method = "Lnet/minecraft/server/world/ServerWorld;tick(Ljava/util/function/BooleanSupplier;)V",
            at = @At("HEAD"))
    public void tick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {

        if(!((ServerTickManager) this.getTickManager()).isSprinting()){
            List<SchedulTickObject> orderedFluidTicks = new ArrayList<>();
            List<SchedulTickObject> orderedBlockTicks = new ArrayList<>();
            Set<ServerPlayerEntity> players = new HashSet<>();
            long time = this.getTime();

            // Logging
            if (ScheduledTickVisualizer.logManager != null && ScheduledTickVisualizer.logManager.ticks > 0) {
                ScheduledTickVisualizerLogger.writeLogFile(ScheduledTickVisualizer.logManager.fileName,
                        "====================================");
                ScheduledTickVisualizerLogger.writeLogFile(ScheduledTickVisualizer.logManager.fileName,
                        "|ServerWorld:Started a tick, current tick is: tick" + time);
            }

            Map<Object, String> typeNameCache = new HashMap<>();

            processTicks(blockTickScheduler, orderedBlockTicks, "Block", time, players, typeNameCache);
            processTicks(fluidTickScheduler, orderedFluidTicks, "Fluid", time, players, typeNameCache);

            if (ScheduledTickVisualizer.server != null) {
                for (ServerPlayerEntity player : players) {
                    ServerPlayNetworking.send(player, new ScheduledTickDataPayload(orderedBlockTicks, "Block"));
                    ServerPlayNetworking.send(player, new ScheduledTickDataPayload(orderedFluidTicks, "Fluid"));
                }
            }
        }
    }
    @Unique
    private void processTicks(WorldTickScheduler<?> scheduler, List<SchedulTickObject> targetList, String typeKey,
                              long time, Set<ServerPlayerEntity> players, Map<Object, String> typeNameCache) {
        List<SchedulTickObject> tempList = new ArrayList<>();
        scheduler.chunkTickSchedulers.values().forEach(chunkTickScheduler -> {
            chunkTickScheduler.getQueueAsStream().forEach(orderedTick -> {
                if (orderedTick.triggerTick() - time > 0) {
                    List<ServerPlayerEntity> nearbyPlayers = getPlayersNearBy(
                            orderedTick.pos(), this.getGameRules().getInt(SCHEDULED_TICK_PACK_RANGE));
                    if (!nearbyPlayers.isEmpty()) {
                        players.addAll(nearbyPlayers);
                        String typeName = typeNameCache.computeIfAbsent(orderedTick.type(), key ->
                                typeKey.equals("Block")
                                        ? Text.translatable(((Block)orderedTick.type()).getTranslationKey()).getString()
                                        : Text.translatable(((Fluid)orderedTick.type()).getStateManager().getDefaultState().getBlockState().getBlock().getTranslationKey()).getString()
                        );
                        tempList.add(new SchedulTickObject(
                                orderedTick.pos(),
                                orderedTick.triggerTick() - time,
                                orderedTick.priority().getIndex(),
                                orderedTick.subTickOrder(),
                                typeName
                        ));
                    }
                }
            });
        });
        if(this.getServer().getGameRules().getBoolean(SORT_SUBORDER)) {
            tempList.sort(Comparator.comparingLong(SchedulTickObject::getTickOrder));
            for (int i = 0; i < tempList.size(); i++) {
                tempList.get(i).subTickOrder = i + 1;
            }
        }
        targetList.addAll(tempList);
    }
    @Inject(method = "Lnet/minecraft/server/world/ServerWorld;tick(Ljava/util/function/BooleanSupplier;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/tick/WorldTickScheduler;tick(JILjava/util/function/BiConsumer;)V",
                    shift = At.Shift.BEFORE,ordinal = 0))
    public void tickBlockTickSchedulerStart(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        if(ScheduledTickVisualizer.logManager != null && ScheduledTickVisualizer.logManager.ticks > 0){
            ScheduledTickVisualizerLogger.writeLogFile(ScheduledTickVisualizer.logManager.fileName,"|--ServerWorld:Ticking BlockTickScheduler..");
        }
    }
    @Inject(method = "Lnet/minecraft/server/world/ServerWorld;tick(Ljava/util/function/BooleanSupplier;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/tick/WorldTickScheduler;tick(JILjava/util/function/BiConsumer;)V",
                    shift = At.Shift.AFTER,ordinal = 0))
    public void tickBlockTickSchedulerEnd(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        if(ScheduledTickVisualizer.logManager != null && ScheduledTickVisualizer.logManager.ticks > 0){
            ScheduledTickVisualizerLogger.writeLogFile(ScheduledTickVisualizer.logManager.fileName,"L_ServerWorld:Finished ticking BlockTickScheduler..");
        }
    }
    @Inject(method = "Lnet/minecraft/server/world/ServerWorld;tick(Ljava/util/function/BooleanSupplier;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/tick/WorldTickScheduler;tick(JILjava/util/function/BiConsumer;)V",
                    shift = At.Shift.BEFORE,ordinal = 1))
    public void tickFluidTickSchedulerStart(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        if(ScheduledTickVisualizer.logManager != null && ScheduledTickVisualizer.logManager.ticks > 0){
            ScheduledTickVisualizerLogger.writeLogFile(ScheduledTickVisualizer.logManager.fileName,"|--ServerWorld:Ticking FluidTickScheduler...");
        }
    }
    @Inject(method = "Lnet/minecraft/server/world/ServerWorld;tick(Ljava/util/function/BooleanSupplier;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/tick/WorldTickScheduler;tick(JILjava/util/function/BiConsumer;)V",
                    shift = At.Shift.AFTER,ordinal = 1))
    public void tickFluidTickSchedulerEnd(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        if(ScheduledTickVisualizer.logManager != null && ScheduledTickVisualizer.logManager.ticks > 0){
            ScheduledTickVisualizerLogger.writeLogFile(ScheduledTickVisualizer.logManager.fileName,"L_ServerWorld:Finished ticking FluidTickScheduler..");
        }
    }

}
