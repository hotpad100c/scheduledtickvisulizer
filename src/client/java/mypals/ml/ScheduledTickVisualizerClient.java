package mypals.ml;

import mypals.ml.command.ScheduledTickVisualizerCommandRegister;
import mypals.ml.config.ScheduledTickVisualizerConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWScrollCallback;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static mypals.ml.config.ScheduledTickVisualizerConfig.sortSubOrderInfo;

public class ScheduledTickVisualizerClient implements ClientModInitializer {
	public static float textSize = 0.012f;
	private KeyBinding viewOrderKeyBindingUp;
	private KeyBinding viewOrderKeyBindingDown;
	public static int orderViewerIndex = 0;
	private static boolean viewOrderKeyPressed = false;
	private static final Comparator<SchedulTickObject> TICK_COMPARATOR =
			Comparator.comparingLong(t -> t.subTickOrder);

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
			ScheduledTickVisualizerCommandRegister.register(commandDispatcher);
		});
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.currentScreen == null && viewOrderKeyBindingDown.wasPressed()) {
				orderViewerIndex--;
			}
			if (client.currentScreen == null && viewOrderKeyBindingUp.wasPressed()) {
				orderViewerIndex++;
			}
		});

		ClientPlayNetworking.registerGlobalReceiver(ScheduledTickDataPayload.ID, (payload, context) -> {
			List<SchedulTickObject> ticks = payload.ticks();
			if (sortSubOrderInfo && !isSorted(ticks)) {
				ticks.sort(TICK_COMPARATOR);
				int[] index = {1};
				ticks.forEach(t -> t.subTickOrder = index[0]++);
			}
			if (Objects.equals(payload.type(), "Block")) {
				InfoRender.setScheduledTicksBlock(ticks);
			} else if (Objects.equals(payload.type(), "Fluid")) {
				InfoRender.setScheduledTicksFluid(ticks);
			}
		});
	}
	private static boolean isSorted(List<SchedulTickObject> ticks) {
		for (int i = 1; i < ticks.size(); i++) {
			if (ticks.get(i - 1).subTickOrder > ticks.get(i).subTickOrder) {
				return false;
			}
		}
		return true;
	}
	public static void UpadteSettings()
	{
		var instance = ScheduledTickVisualizerConfig.CONFIG_HANDLER;
		instance.load();
	}
}
