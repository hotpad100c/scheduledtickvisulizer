package mypals.ml;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import mypals.ml.LogsManager.LogManager;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ScheduledTickVisualizer implements ModInitializer {
	public static final String MOD_ID = "scheduledtickvisualizer";
	public static MinecraftServer server = null;
	//public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final Identifier TICK_PACKET_ID = Identifier.of(MOD_ID, "tick_data_packet");
	public static final Identifier HELLO_PACKET_ID = Identifier.of(MOD_ID, "hello");
	public static List<ServerPlayerEntity> players = new ArrayList<>();
	public static LogManager logManager;
	public static final GameRules.Key<GameRules.IntRule> SCHEDULED_TICK_PACK_RANGE = GameRuleRegistry.register(
			"scheduledTickInformationRange",
			GameRules.Category.MISC,
			GameRuleFactory.createIntRule(20)
	);

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
	public void onInitialize() {
		ServerTickEvents.END_WORLD_TICK.register(this::OnServerTick);
		ServerLifecycleEvents.SERVER_STARTED.register(s -> server = s);
		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
			players.remove(handler.player);
		});
		ServerPlayNetworking.registerGlobalReceiver(HELLO_PACKET_ID, (server, player, handler, buf, responseSender) -> {
			players.add(player);
		});
		CommandRegistrationCallback.EVENT.register((dispatcher, e,registryAccess) -> {
			dispatcher.register(
					CommandManager.literal("scheduledTickVisualizerServer")
							.then(CommandManager.literal("server")
									.then(CommandManager.literal("log")
											.then(CommandManager.argument("ticks", IntegerArgumentType.integer())
													.executes(this::executeCommand)))));
		});
	}
	private int executeCommand(CommandContext<ServerCommandSource> context) {
		int ticks = IntegerArgumentType.getInteger(context, "ticks");
		ServerCommandSource source = context.getSource();
		source.sendFeedback(()->Text.literal("Started log for["+ticks+"]ticks..."), false);
		LocalDateTime now = LocalDateTime.now();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
		String formattedDate = now.format(formatter);
		String name = "scheduledTickLog_" + formattedDate;
		if(logManager != null && logManager.fileName.equals(name)){
			logManager.ticks = ticks;
			return 1;
		}
		logManager = new LogManager(name,ticks);
		return 1;
	}

	private void OnServerTick(ServerWorld serverWorld) {
		if(logManager != null && logManager.ticks > 0)
			logManager.ticks--;
		else
			logManager = null;
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