package mypals.ml.config;

import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import mypals.ml.ScheduledTickVisualizer;
import net.fabricmc.loader.api.FabricLoader;

import java.awt.*;

public class ScheduledTickVisualizerConfig {
    public static ConfigClassHandler<ScheduledTickVisualizerConfig> CONFIG_HANDLER = ConfigClassHandler.createBuilder(ScheduledTickVisualizerConfig.class)
            .id(ScheduledTickVisualizer.id("config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("ScheduledTickVisualizer.json5"))
                    .appendGsonBuilder(GsonBuilder::setPrettyPrinting)
                    .setJson5(true)
                    .build())
            .build();
    @SerialEntry
    public static boolean simplify = false;
    @SerialEntry
    public static boolean showInfo = false;
    @SerialEntry
    public static boolean showInfoBox = false;
    @SerialEntry
    public static boolean shadow = true;
    @SerialEntry
    public static boolean background = false;
    @SerialEntry
    public static boolean showAccurateBlockType = false;
    @SerialEntry
    public static boolean showTickTypeInfo = true;
    @SerialEntry
    public static boolean showSubOrderInfo = false;
    @SerialEntry
    public static boolean sortSubOrderInfo = true;
    @SerialEntry
    public static boolean showTriggerInfo = true;
    @SerialEntry
    public static boolean showPriorityInfo = true;
    @SerialEntry
    public static float textSize = 0.012F;
    @SerialEntry
    public static int timeOutDelay = 30;
    @SerialEntry
    public static Color blockTickColor = Color.magenta;

    @SerialEntry
    public static Color fluidTickColor = Color.green;
    @SerialEntry
    public static Color backgroundColor = Color.darkGray;
    @SerialEntry
    public static Color subOrderColor = Color.red;
    @SerialEntry
    public static Color triggerColor = Color.green;
    @SerialEntry
    public static Color priorityColor = Color.CYAN;
    @SerialEntry
    public static float boxAlpha = 0.12F;

}
