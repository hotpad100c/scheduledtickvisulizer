package mypals.ml;

import mypals.ml.command.CommandRegister;
import mypals.ml.config.ScheduledTickVisualizerConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static mypals.ml.ScheduledTickVisulizer.TICK_PACKET_ID;
import static mypals.ml.config.ScheduledTickVisualizerConfig.sortSubOrderInfo;

public class ScheduledTickVisulizerClient implements ClientModInitializer {
	public static float textSize = 0.012f;
	@Override
	public void onInitializeClient() {
		UpadteSettings();
		ClientCommandRegistrationCallback.EVENT.register((commandDispatcher, commandRegistryAccess) -> {
			CommandRegister.register(commandDispatcher);
		});

		PayloadTypeRegistry.playS2C().register(ScheduledTickDataPayload.ID,ScheduledTickDataPayload.CODEC);
		ClientPlayNetworking.registerGlobalReceiver(ScheduledTickDataPayload.ID, (payload, context) -> {
			if(Objects.equals(payload.type(), "Block")){
				if(sortSubOrderInfo){
					List<SchedulTickObject> l = payload.ticks();
					l.sort(Comparator.comparingLong(t -> t.subTickOrder));
					for (int i = 0; i < l.size(); i++) {
						l.get(i).subTickOrder = i + 1;
					}
					InfoRender.setScheduledTicksBlock(l);
				}else{
					InfoRender.setScheduledTicksBlock(payload.ticks());
				}
			}else if(Objects.equals(payload.type(), "Fluid")){
				if(sortSubOrderInfo){
					List<SchedulTickObject> l = payload.ticks();
					l.sort(Comparator.comparingLong(t -> t.subTickOrder));
					for (int i = 0; i < l.size(); i++) {
						l.get(i).subTickOrder = i + 1;
					}
					InfoRender.setScheduledTicksFluid(l);
				}else{
					InfoRender.setScheduledTicksFluid(payload.ticks());
				}
			}
		});
	}
	public static void UpadteSettings()
	{
		var instance = ScheduledTickVisualizerConfig.CONFIG_HANDLER;
		instance.load();
	}
}