package mypals.ml;

import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static mypals.ml.ScheduledTickVisulizerClient.textSize;
import static mypals.ml.StringRenderer.*;
import static mypals.ml.config.ScheduledTickVisualizerConfig.*;

public class InfoRender {

    public static List<SchedulTickObject> scheduledTicksFluid = new ArrayList<>();
    public static List<SchedulTickObject> scheduledTicksBlock = new ArrayList<>();
    public static int blockTickDataClearTimer = timeOutDelay;
    public static int fluidTickDataClearTimer = timeOutDelay;
    public static void setScheduledTicksBlock(List<SchedulTickObject> scheduledTicks){
        scheduledTicksBlock = scheduledTicks;
        blockTickDataClearTimer = timeOutDelay;
    }
    public static void setScheduledTicksFluid(List<SchedulTickObject> scheduledTicks){
        scheduledTicksFluid = scheduledTicks;
        fluidTickDataClearTimer = timeOutDelay;
    }
    @SuppressWarnings("ConstantConditions")
    public static void render(MatrixStack matrixStack, RenderTickCounter counter) {
        if(showInfo) {
            for (SchedulTickObject tick : scheduledTicksBlock) {
                ArrayList<Integer> colors = new ArrayList<>();
                ArrayList<String> text = new ArrayList<>();
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
                renderTextList(matrixStack, tick.pos, counter.getTickDelta(true), 5, text, colors, textSize);
                if(showInfoBox)
                    drawCube(matrixStack,tick.pos,0.05f,counter.getTickDelta(true),
                        new Color(blockTickColor.getRed(),blockTickColor.getGreen(),blockTickColor.getBlue()),boxAlpha);
            }
            for (SchedulTickObject tick : scheduledTicksFluid) {
                ArrayList<Integer> colors = new ArrayList<>();
                ArrayList<String> text = new ArrayList<>();
                if(showTickTypeInfo){
                    if(showAccurateBlockType)
                        text.add(tick.type);
                    else
                        text.add(Text.translatable("text.scheduledtick.fluid").getString());
                    colors.add(fluidTickColor.getRGB());
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
                renderTextList(matrixStack, tick.pos,  counter.getTickDelta(true), 5, text, colors, textSize);
                if(showInfoBox)
                    drawCube(matrixStack,tick.pos,0.05f,counter.getTickDelta(true),
                        new Color(fluidTickColor.getRed(),fluidTickColor.getGreen(),fluidTickColor.getBlue()),boxAlpha);
            }
            if(fluidTickDataClearTimer>0)
                fluidTickDataClearTimer--;
            if(blockTickDataClearTimer>0)
                blockTickDataClearTimer--;
            if(blockTickDataClearTimer<=0)
                scheduledTicksBlock.clear();
            if(fluidTickDataClearTimer<=0)
                scheduledTicksFluid.clear();
        }
    }
}
