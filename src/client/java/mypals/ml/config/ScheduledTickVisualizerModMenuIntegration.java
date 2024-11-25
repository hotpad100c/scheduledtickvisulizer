package mypals.ml.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.api.controller.FloatSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.minecraft.text.Text;

import java.awt.*;

import static mypals.ml.ScheduledTickVisulizerClient.UpadteSettings;

public class ScheduledTickVisualizerModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        var instance = ScheduledTickVisualizerConfig.CONFIG_HANDLER;
        return screen -> YetAnotherConfigLib.createBuilder()
                .title(Text.translatable("config.scheduledtickvisualizer.title"))
                .category(ConfigCategory.createBuilder()
                        .name(Text.translatable("config.scheduledtickvisualizer.settings"))
                        .tooltip(Text.translatable("config.scheduledtickvisualizer.settings.tooltip"))
                        .group(OptionGroup.createBuilder()
                                .name(Text.translatable("config.scheduledtickvisualizer.render"))
                                .description(OptionDescription.of(Text.translatable("config.scheduledtickvisualizer.render.description")))
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.translatable("config.scheduledtickvisualizer.main_render"))
                                        .description(OptionDescription.of(Text.translatable("config.scheduledtickvisualizer.main_render.description")))
                                        .binding(true, () -> instance.instance().showInfo, newVal -> instance.instance().showInfo = newVal)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.translatable("config.scheduledtickvisualizer.tick_type_render"))
                                        .description(OptionDescription.of(Text.translatable("config.scheduledtickvisualizer.tick_type_render.description")))
                                        .binding(true, () -> instance.instance().showTickTypeInfo, newVal -> instance.instance().showTickTypeInfo = newVal)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.translatable("config.scheduledtickvisualizer.sub_order_render"))
                                        .description(OptionDescription.of(Text.translatable("config.scheduledtickvisualizer.sub_order_render.description")))
                                        .binding(false, () -> instance.instance().showSubOrderInfo, newVal -> instance.instance().showSubOrderInfo = newVal)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.translatable("config.scheduledtickvisualizer.sub_order_sort"))
                                        .description(OptionDescription.of(Text.translatable("config.scheduledtickvisualizer.sub_order_sort.description")))
                                        .binding(true, () -> instance.instance().sortSubOrderInfo, newVal -> instance.instance().sortSubOrderInfo = newVal)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.translatable("config.scheduledtickvisualizer.trigger_tick_render"))
                                        .description(OptionDescription.of(Text.translatable("config.scheduledtickvisualizer.trigger_tick_render.description")))
                                        .binding(true, () -> instance.instance().showTriggerInfo, newVal -> instance.instance().showTriggerInfo = newVal)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.translatable("config.scheduledtickvisualizer.tick_priority_render"))
                                        .description(OptionDescription.of(Text.translatable("config.scheduledtickvisualizer.tick_priority_render.description")))
                                        .binding(true, () -> instance.instance().showPriorityInfo, newVal -> instance.instance().showPriorityInfo = newVal)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<Float>createBuilder()
                                        .name(Text.translatable("config.scheduledtickvisualizer.font_size"))
                                        .description(OptionDescription.of(Text.translatable("config.scheduledtickvisualizer.font_size.description")))
                                        .binding(0.015F, () -> instance.instance().textSize, newVal -> instance.instance().textSize = newVal)
                                        .controller(opt -> FloatSliderControllerBuilder.create(opt)
                                                .range(0.01F, 0.02F)
                                                .step(0.001F)
                                                .formatValue(val -> Text.literal(val + "")))
                                        .build())
                                .option(Option.<Color>createBuilder()
                                        .name(Text.translatable("config.scheduledtickvisualizer.block_tick_color"))
                                        .binding(Color.magenta, () -> instance.instance().blockTickColor, newVal -> instance.instance().blockTickColor = newVal)
                                        .controller(opt -> ColorControllerBuilder.create(opt)
                                                .allowAlpha(false))
                                        .build())
                                .option(Option.<Color>createBuilder()
                                        .name(Text.translatable("config.scheduledtickvisualizer.fluid_tick_color"))
                                        .binding(Color.magenta, () -> instance.instance().fluidTickColor, newVal -> instance.instance().fluidTickColor = newVal)
                                        .controller(opt -> ColorControllerBuilder.create(opt)
                                                .allowAlpha(false))
                                        .build())
                                .option(Option.<Color>createBuilder()
                                        .name(Text.translatable("config.scheduledtickvisualizer.sub_order_color"))
                                        .binding(Color.red, () -> instance.instance().subOrderColor, newVal -> instance.instance().subOrderColor = newVal)
                                        .controller(opt -> ColorControllerBuilder.create(opt)
                                                .allowAlpha(false))
                                        .build())
                                .option(Option.<Color>createBuilder()
                                        .name(Text.translatable("config.scheduledtickvisualizer.trigger_time_color"))
                                        .binding(Color.green, () -> instance.instance().triggerColor, newVal -> instance.instance().triggerColor = newVal)
                                        .controller(opt -> ColorControllerBuilder.create(opt)
                                                .allowAlpha(false))
                                        .build())
                                .option(Option.<Color>createBuilder()
                                        .name(Text.translatable("config.scheduledtickvisualizer.tick_priority_color"))
                                        .binding(Color.CYAN, () -> instance.instance().priorityColor, newVal -> instance.instance().priorityColor = newVal)
                                        .controller(opt -> ColorControllerBuilder.create(opt)
                                                .allowAlpha(false))
                                        .build())
                                .build())
                        .build())
                .save(() -> {
                    instance.save();
                    UpadteSettings();
                })
                .build()
                .generateScreen(screen);
    }
}
