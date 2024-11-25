package mypals.ml.mixin;

import mypals.ml.SchedulTickObject;
import mypals.ml.ScheduledTickDataPayload;
import mypals.ml.ScheduledTickVisulizer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
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
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import static mypals.ml.ScheduledTickVisulizer.SCHEDULED_TICK_PACK_RANGE;
import static mypals.ml.ScheduledTickVisulizer.getPlayersNearBy;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World implements StructureWorldAccess, AttachmentTarget {
    @Shadow @Final private WorldTickScheduler<Block> blockTickScheduler;
    @Shadow @Final private WorldTickScheduler<Fluid> fluidTickScheduler;

    protected ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long biomeAccess, int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, profiler, isClient, debugWorld, biomeAccess, maxChainedNeighborUpdates);
    }

    @Inject(method = "Lnet/minecraft/server/world/ServerWorld;tick(Ljava/util/function/BooleanSupplier;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/tick/TickManager;shouldTick()Z",
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
                                    "BlockTickOrdered"));
                }
            });
        });
        fluidTickScheduler.chunkTickSchedulers.values().forEach(chunkTickScheduler -> {
            chunkTickScheduler.getQueueAsStream().forEach(orderedTick ->{
                if(orderedTick.triggerTick() - time > 0 && !getPlayersNearBy(orderedTick.pos(),this.getGameRules().getInt(SCHEDULED_TICK_PACK_RANGE)).isEmpty()){
                    players.addAll(getPlayersNearBy(orderedTick.pos(),this.getGameRules().getInt(SCHEDULED_TICK_PACK_RANGE)));
                    orderedFluidTicks.add(
                            new SchedulTickObject(orderedTick.pos(),
                                    orderedTick.triggerTick() - time,
                                    orderedTick.priority().getIndex(), orderedTick.subTickOrder(),
                                    "FluidTickOrdered"));
                }
            });
        });
        if(ScheduledTickVisulizer.server != null){
            for(ServerPlayerEntity player : players){
                ServerPlayNetworking.send(player,new ScheduledTickDataPayload(orderedBlockTicks,"Block"));
                ServerPlayNetworking.send(player,new ScheduledTickDataPayload(orderedFluidTicks,"Fluid"));
            }
        }
    }
}
