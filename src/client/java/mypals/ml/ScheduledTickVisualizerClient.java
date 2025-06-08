package mypals.ml;

import mypals.ml.command.CommandRegister;
import mypals.ml.config.ScheduledTickVisualizerConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWScrollCallback;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.ArrayList;
import static mypals.ml.config.ScheduledTickVisualizerConfig.sortSubOrderInfo;

public class ScheduledTickVisualizerClient implements ClientModInitializer {
	public static float textSize = 0.012f;
	private KeyBinding viewOrderKeyBindingUp;
	private KeyBinding viewOrderKeyBindingDown;
	private GLFWScrollCallback scrollCallback;
	public static int orderViewerIndex = 0;
	private static boolean viewOrderKeyPressed = false;


	@Override
	public void onInitializeClient() {
		UpadteSettings();

		viewOrderKeyBindingUp = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.scheduledTickVisualizer.up",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_UP,
				"category.scheduledTickVisualizer.keys"
		));
		viewOrderKeyBindingDown = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.scheduledTickVisualizer.down",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_DOWN,
				"category.scheduledTickVisualizer.keys"
		));
		ClientCommandRegistrationCallback.EVENT.register((commandDispatcher, commandRegistryAccess) -> {
			CommandRegister.register(commandDispatcher);
		});
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.currentScreen == null && viewOrderKeyBindingDown.wasPressed()) {
				orderViewerIndex--;
			}
			if (client.currentScreen == null && viewOrderKeyBindingUp.wasPressed()) {
				orderViewerIndex++;
			}
		});
		ClientPlayNetworking.registerGlobalReceiver(ScheduledTickVisualizer.TICK_PACKET_ID, (client, handler, buf, responseSender)->{
			String type=buf.readString();
			List<SchedulTickObject> ticks=buf.readCollection(ArrayList::new,
					packetByteBuf -> new SchedulTickObject(
							packetByteBuf.readBlockPos(),
							packetByteBuf.readLong(),
							packetByteBuf.readInt(),
							packetByteBuf.readLong(),
							packetByteBuf.readString()
					));
			MinecraftClient.getInstance().execute(()->{
				if(Objects.equals(type, "Block")){
					if(sortSubOrderInfo){
						ticks.sort(Comparator.comparingLong(t -> t.subTick));
						for (int i = 0; i < ticks.size(); i++) {
							ticks.get(i).subTick = i + 1;
						}
						InfoRender.setScheduledTicksBlock(ticks);
					}else{
						InfoRender.setScheduledTicksBlock(ticks);
					}
				}else if(Objects.equals(type, "Fluid")){
					if(sortSubOrderInfo){
						ticks.sort(Comparator.comparingLong(t -> t.subTick));
						for (int i = 0; i < ticks.size(); i++) {
							ticks.get(i).subTick = i + 1;
						}
						InfoRender.setScheduledTicksFluid(ticks);
					}else{
						InfoRender.setScheduledTicksFluid(ticks);
					}
				}
			});


		});
	}
	public static void UpadteSettings()
	{
		var instance = ScheduledTickVisualizerConfig.CONFIG_HANDLER;
		instance.load();
	}
}
