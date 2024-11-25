package mypals.ml;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.FormattingTuple;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public class ScheduledTickVisulizer implements ModInitializer {
	public static final String MOD_ID = "scheduledtickvisulizer";
	public static MinecraftServer server;
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static List<SchedulTickObject> schedulTickObjectList = new CopyOnWriteArrayList<>();
	public static final Identifier TICK_PACKET_ID = Identifier.of(MOD_ID, "tick_data_packet");
	public static final GameRules.Key<GameRules.IntRule> SCHEDULED_TICK_PACK_RANGE = GameRuleRegistry.register(
			"scheduledTickInformationRange",
			GameRules.Category.MISC,
			GameRuleFactory.createIntRule(20)
	);

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
	public void onInitialize() {
		ServerTickEvents.START_WORLD_TICK.register(this::OnServerTick);
		ServerLifecycleEvents.SERVER_STARTED.register(s -> server = s);
	}

	private void OnServerTick(ServerWorld serverWorld) {
		schedulTickObjectList.clear();
	}
	public static List<ServerPlayerEntity> getPlayersNearBy(BlockPos blockPos,float distance){
		List<ServerPlayerEntity> pList = new ArrayList<>();
		for(ServerPlayerEntity player : server.getPlayerManager().getPlayerList()){
			double playerX = player.getX();
			double playerY = player.getY();
			double playerZ = player.getZ();

			double blockX = blockPos.getX() + 0.5;
			double blockY = blockPos.getY() + 0.5;
			double blockZ = blockPos.getZ() + 0.5;

			double distanceSquared = Math.pow(blockX - playerX, 2)
					+ Math.pow(blockY - playerY, 2)
					+ Math.pow(blockZ - playerZ, 2);

			if(distanceSquared < distance*distance){
				pList.add(player);
			}
		}
		return pList;
	}

}