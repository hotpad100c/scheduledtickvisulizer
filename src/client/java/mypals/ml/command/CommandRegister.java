package mypals.ml.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static mypals.ml.config.ScheduledTickVisualizerConfig.*;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class CommandRegister {
    private static String mod_name = Text.translatable("command.scheduledtickvisualizer.stv.name").getString();
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {

        dispatcher.register(

                literal("scheduledTickVisualizer")
                        .executes(context -> {
                            FabricClientCommandSource source = context.getSource();

                            source.sendFeedback(Text.literal(mod_name).
                                    formatted(Formatting.BOLD).formatted(Formatting.GOLD));
                            source.sendFeedback(Text.literal( Text.translatable("command.scheduledtickvisualizer.render_main").getString() + showInfo).
                                    formatted(getEnabledOrDisabledColor(showInfo)));
                            source.sendFeedback(Text.literal( Text.translatable("command.scheduledtickvisualizer.render_box").getString() + showInfoBox).
                                    formatted(getEnabledOrDisabledColor(showInfoBox)));
                            source.sendFeedback(Text.literal(Text.translatable("command.scheduledtickvisualizer.shadow").getString() + shadow).
                                    formatted(getEnabledOrDisabledColor(shadow)));
                            source.sendFeedback(Text.literal(Text.translatable("command.scheduledtickvisualizer.background").getString() + background).
                                    formatted(getEnabledOrDisabledColor(background)));

                            source.sendFeedback(Text.literal( Text.translatable("command.scheduledtickvisualizer.tick_type").getString() + showTickTypeInfo).
                                    formatted(getEnabledOrDisabledColor(showTickTypeInfo)));
                            source.sendFeedback(Text.literal(Text.translatable("command.scheduledtickvisualizer.tick_type_accurate").getString() + showAccurateBlockType).
                                    formatted(getEnabledOrDisabledColor(showAccurateBlockType)));

                            source.sendFeedback(Text.literal(Text.translatable("command.scheduledtickvisualizer.order").getString() + showSubOrderInfo).
                                    formatted(getEnabledOrDisabledColor(showSubOrderInfo)));
                            source.sendFeedback(Text.literal(Text.translatable("command.scheduledtickvisualizer.sort_order").getString() + sortSubOrderInfo).
                                    formatted(getEnabledOrDisabledColor(sortSubOrderInfo)));

                            source.sendFeedback(Text.literal(Text.translatable("command.scheduledtickvisualizer.trigger_time").getString() + showTriggerInfo).
                                    formatted(getEnabledOrDisabledColor(showTriggerInfo)));

                            source.sendFeedback(Text.literal(Text.translatable("command.scheduledtickvisualizer.priority").getString() + showPriorityInfo).
                                    formatted(getEnabledOrDisabledColor(showPriorityInfo)));

                            source.sendFeedback(Text.literal(Text.translatable("command.scheduledtickvisualizer.text_size").getString() + textSize).
                                    formatted(Formatting.GREEN));
                            source.sendFeedback(Text.literal(Text.translatable("command.scheduledtickvisualizer.time_out_delay").getString() + timeOutDelay).
                                    formatted(Formatting.GREEN));
                            return 1;
                        })
                        .then(literal("mainRender")
                            .then(argument("enable", BoolArgumentType.bool())
                                    .executes(context -> {
                                        boolean enable = BoolArgumentType.getBool(context, "enable");
                                        FabricClientCommandSource source = context.getSource();
                                        source.sendFeedback(Text.literal(mod_name + Text.translatable("command.scheduledtickvisualizer.render_main").getString() + enable).
                                                formatted(getEnabledOrDisabledColor(enable)));
                                        CommandManager.setStaticBooleanField("showInfo",enable);
                                        return 1;
                                    })
                            )
                        )
                        .then(literal("renderBox")
                                .then(argument("enable", BoolArgumentType.bool())
                                        .executes(context -> {
                                            boolean enable = BoolArgumentType.getBool(context, "enable");
                                            FabricClientCommandSource source = context.getSource();
                                            source.sendFeedback(Text.literal(mod_name + Text.translatable("command.scheduledtickvisualizer.render_box").getString() + enable).
                                                    formatted(getEnabledOrDisabledColor(enable)));
                                            CommandManager.setStaticBooleanField("showInfoBox",enable);
                                            return 1;
                                        })
                                )
                        )
                        .then(literal("enableTextShadow")
                                .then(argument("enable", BoolArgumentType.bool())
                                        .executes(context -> {
                                            boolean enable = BoolArgumentType.getBool(context, "enable");
                                            FabricClientCommandSource source = context.getSource();
                                            source.sendFeedback(Text.literal(mod_name + Text.translatable("command.scheduledtickvisualizer.shadow").getString() + enable).
                                                    formatted(getEnabledOrDisabledColor(enable)));
                                            CommandManager.setStaticBooleanField("shadow",enable);
                                            return 1;
                                        })
                                )
                        )
                        .then(literal("enableTextBackground")
                                .then(argument("enable", BoolArgumentType.bool())
                                        .executes(context -> {
                                            boolean enable = BoolArgumentType.getBool(context, "enable");
                                            FabricClientCommandSource source = context.getSource();
                                            source.sendFeedback(Text.literal(mod_name + Text.translatable("command.scheduledtickvisualizer.background").getString() + enable).
                                                    formatted(getEnabledOrDisabledColor(enable)));
                                            CommandManager.setStaticBooleanField("background",enable);
                                            return 1;
                                        })
                                )
                        )
                        .then(literal("enableRenderAccurateBlockType")
                                .then(argument("enable", BoolArgumentType.bool())
                                        .executes(context -> {
                                            boolean enable = BoolArgumentType.getBool(context, "enable");
                                            FabricClientCommandSource source = context.getSource();
                                            source.sendFeedback(Text.literal(mod_name + Text.translatable("command.scheduledtickvisualizer.tick_type_accurate").getString() + enable).
                                                    formatted(getEnabledOrDisabledColor(enable)));
                                            CommandManager.setStaticBooleanField("showAccurateBlockType",enable);
                                            return 1;
                                        })
                                )
                        )
                        .then(literal("enableRenderTickType")
                                .then(argument("enable", BoolArgumentType.bool())
                                        .executes(context -> {
                                            boolean enable = BoolArgumentType.getBool(context, "enable");
                                            FabricClientCommandSource source = context.getSource();
                                            source.sendFeedback(Text.literal(mod_name + Text.translatable("command.scheduledtickvisualizer.tick_type").getString() + enable).
                                                    formatted(getEnabledOrDisabledColor(enable)));
                                            CommandManager.setStaticBooleanField("showTickTypeInfo",enable);
                                            return 1;
                                        })
                                )
                        )
                        .then(literal("enableRenderSubTickOrder")
                                .then(argument("enable", BoolArgumentType.bool())
                                        .executes(context -> {
                                            boolean enable = BoolArgumentType.getBool(context, "enable");
                                            FabricClientCommandSource source = context.getSource();
                                            source.sendFeedback(Text.literal(mod_name + Text.translatable("command.scheduledtickvisualizer.order").getString() + enable).
                                                    formatted(getEnabledOrDisabledColor(enable)));
                                            CommandManager.setStaticBooleanField("showSubOrderInfo",enable);
                                            return 1;
                                        })
                                )
                        )
                        .then(literal("enableSortRenderSubTickOrder")
                                .then(argument("enable", BoolArgumentType.bool())
                                        .executes(context -> {
                                            boolean enable = BoolArgumentType.getBool(context, "enable");
                                            FabricClientCommandSource source = context.getSource();
                                            source.sendFeedback(Text.literal(mod_name + Text.translatable("command.scheduledtickvisualizer.sort_order").getString() + enable).
                                                    formatted(getEnabledOrDisabledColor(enable)));
                                            CommandManager.setStaticBooleanField("sortSubOrderInfo",enable);
                                            return 1;
                                        })
                                )
                        )
                        .then(literal("enableRenderTriggerTime")
                                .then(argument("enable", BoolArgumentType.bool())
                                        .executes(context -> {
                                            boolean enable = BoolArgumentType.getBool(context, "enable");
                                            FabricClientCommandSource source = context.getSource();
                                            source.sendFeedback(Text.literal(mod_name + Text.translatable("command.scheduledtickvisualizer.trigger_time").getString() + enable).
                                                    formatted(getEnabledOrDisabledColor(enable)));
                                            CommandManager.setStaticBooleanField("showTriggerInfo",enable);
                                            return 1;
                                        })
                                )
                        )
                        .then(literal("enableRenderTickPriority")
                                .then(argument("enable", BoolArgumentType.bool())
                                        .executes(context -> {
                                            boolean enable = BoolArgumentType.getBool(context, "enable");
                                            FabricClientCommandSource source = context.getSource();
                                            source.sendFeedback(Text.literal(mod_name + Text.translatable("command.scheduledtickvisualizer.priority").getString() + enable).
                                                    formatted(getEnabledOrDisabledColor(enable)));
                                            CommandManager.setStaticBooleanField("showPriorityInfo",enable);
                                            return 1;
                                        })
                                )
                        )
                        .then(literal("setTextSize")
                                .then(argument("value", FloatArgumentType.floatArg())
                                        .executes(context -> {
                                            float v = FloatArgumentType.getFloat(context, "value");
                                            FabricClientCommandSource source = context.getSource();
                                            source.sendFeedback(Text.literal(mod_name + Text.translatable("command.scheduledtickvisualizer.text_size").getString() + v).
                                                    formatted(getEnabledOrDisabledColor(true)));
                                            CommandManager.setStaticFloatField("textSize",v);
                                            return 1;
                                        })
                                )
                        )
                        .then(literal("setTimeOutDelay")
                                .then(argument("value", IntegerArgumentType.integer())
                                        .executes(context -> {
                                            int v = IntegerArgumentType.getInteger(context, "value");
                                            FabricClientCommandSource source = context.getSource();
                                            source.sendFeedback(Text.literal(mod_name + Text.translatable("command.scheduledtickvisualizer.time_out_delay").getString() + v).
                                                    formatted(getEnabledOrDisabledColor(true)));
                                            CommandManager.setStaticIntField("timeOutDelay",v);
                                            return 1;
                                        })
                                )
                        )
        );
    }
    private static Formatting getEnabledOrDisabledColor(boolean enable){
        return enable?Formatting.GREEN:Formatting.GRAY;
    }
}
