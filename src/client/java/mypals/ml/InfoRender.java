package mypals.ml;

import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static mypals.ml.ScheduledTickVisualizerClient.textSize;
import static mypals.ml.StringRenderer.*;
import static mypals.ml.config.ScheduledTickVisualizerConfig.*;

public class InfoRender {

    public static List<SchedulTickObject> scheduledTicksFluid = new ArrayList<>();
    public static List<SchedulTickObject> scheduledTicksBlock = new ArrayList<>();
    public static int blockTickDataClearTimer = timeOutDelay;
    public static int fluidTickDataClearTimer = timeOutDelay;
    public static List<BlockPos> reorderedBlockTicks = new ArrayList<>();
    public static List<BlockPos> reorderedFluidTicks = new ArrayList<>();
    public static void setScheduledTicksBlock(List<SchedulTickObject> scheduledTicks){
        scheduledTicksBlock = scheduledTicks;
        blockTickDataClearTimer = timeOutDelay;
        reorderedBlockTicks = TickOrderResolver.resolveTickOrder(scheduledTicks);
    }
    public static void setScheduledTicksFluid(List<SchedulTickObject> scheduledTicks){
        scheduledTicksFluid = scheduledTicks;
        fluidTickDataClearTimer = timeOutDelay;
        reorderedFluidTicks = TickOrderResolver.resolveTickOrder(scheduledTicks);
    }
    @SuppressWarnings("ConstantConditions")
    public static void render(MatrixStack matrixStack, RenderTickCounter counter) {
        if(showInfo) {
            if (!reorderedBlockTicks.isEmpty() || !reorderedFluidTicks.isEmpty()) {
                int totalSize = reorderedBlockTicks.size() + reorderedFluidTicks.size();

                if (ScheduledTickVisualizerClient.orderViewerIndex < 0 || ScheduledTickVisualizerClient.orderViewerIndex >= totalSize) {
                    ScheduledTickVisualizerClient.orderViewerIndex = 0;
                }

                BlockPos pos;
                if (ScheduledTickVisualizerClient.orderViewerIndex < reorderedBlockTicks.size()) {
                    pos = reorderedBlockTicks.get(ScheduledTickVisualizerClient.orderViewerIndex);
                } else {
                    int fluidIndex = ScheduledTickVisualizerClient.orderViewerIndex - reorderedBlockTicks.size();
                    pos = reorderedFluidTicks.get(fluidIndex);
                }

                drawCube(matrixStack, pos, 0.01f, counter.getTickDelta(true),
                        new Color(225, 225, 225), boxAlpha);
            }

            for (SchedulTickObject tick : scheduledTicksBlock) {
                ArrayList<Integer> colors = new ArrayList<>();
                ArrayList<String> text = new ArrayList<>();
                if(simplify){
                    text.add(
                            tick.subTickOrder + "@" +
                            tick.triggerTick +
                            (tick.priority != 0 ? ("[" + tick.priority +"]"):"")
                    );
                    colors.add(blockTickColor.getRGB());
                }
                else{
                    if(showTickTypeInfo){
                        if(showAccurateBlockType)
                            text.add(tick.type);
                        else
                            text.add(Text.translatable("text.scheduledtick.block").getString());
                        colors.add(blockTickColor.getRGB());
                    }
                    if(showSubOrderInfo){
                        text.add( Text.translatable("text.scheduledtick.sub_order").getString() + ": " + tick.subTickOrder);
                        colors.add(subOrderColor.getRGB());
                    }
                    if(showTriggerInfo){
                        text.add(  Text.translatable("text.scheduledtick.trigger").getString() + ": " + tick.triggerTick);
                        colors.add(triggerColor.getRGB());
                    }
                    if(showPriorityInfo){
                        text.add( Text.translatable("text.scheduledtick.priority").getString() + ": " + tick.priority);
                        colors.add(priorityColor.getRGB());
                    }
                }
                renderTextList(matrixStack, tick.pos, counter.getTickDelta(true), 5, text, colors, textSize);
                if(showInfoBox)
                    drawCube(matrixStack,tick.pos,0f,counter.getTickDelta(true),
                            new Color(blockTickColor.getRed(),blockTickColor.getGreen(),blockTickColor.getBlue()),boxAlpha);

            }
            for (SchedulTickObject tick : scheduledTicksFluid) {
                ArrayList<Integer> colors = new ArrayList<>();
                ArrayList<String> text = new ArrayList<>();
                if(simplify){
                    text.add(
                            tick.subTickOrder + "@" +
                            tick.triggerTick +
                            (tick.priority != 0 ? ("[" + tick.priority +"]"):"")
                    );
                    colors.add(fluidTickColor.getRGB());
                }
                else {
                    if (showTickTypeInfo) {
                        if (showAccurateBlockType)
                            text.add(tick.type);
                        else
                            text.add(Text.translatable("text.scheduledtick.fluid").getString());
                        colors.add(fluidTickColor.getRGB());
                    }
                    if (showSubOrderInfo) {
                        text.add(Text.translatable("text.scheduledtick.sub_order").getString() + ": " + tick.subTickOrder);
                        colors.add(subOrderColor.getRGB());
                    }
                    if (showTriggerInfo) {
                        text.add(Text.translatable("text.scheduledtick.trigger").getString() + ": " + tick.triggerTick);
                        colors.add(triggerColor.getRGB());
                    }
                    if (showPriorityInfo) {
                        text.add(Text.translatable("text.scheduledtick.priority").getString() + ": " + tick.priority);
                        colors.add(priorityColor.getRGB());
                    }
                }
                renderTextList(matrixStack, tick.pos, counter.getTickDelta(true), 5, text, colors, textSize);
                if (showInfoBox)
                    drawCube(matrixStack, tick.pos, 0f, counter.getTickDelta(true),
                            new Color(fluidTickColor.getRed(), fluidTickColor.getGreen(), fluidTickColor.getBlue()), boxAlpha);
            }
            if(fluidTickDataClearTimer>0)
                fluidTickDataClearTimer--;
            if(blockTickDataClearTimer>0)
                blockTickDataClearTimer--;
            if(blockTickDataClearTimer<=0){
                reorderedBlockTicks.clear();
                scheduledTicksBlock.clear();
            }
            if(fluidTickDataClearTimer<=0){
                reorderedFluidTicks.clear();
                scheduledTicksFluid.clear();
            }
        }
    }
}
