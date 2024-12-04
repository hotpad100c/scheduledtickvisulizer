package mypals.ml;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Colors;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

import java.awt.*;
import java.util.ArrayList;

import static mypals.ml.config.ScheduledTickVisualizerConfig.*;

public class StringRenderer {
    public static double lastTickPosX = 0;
    public static double lastTickPosY = 0;
    public static double lastTickPosZ = 0;
    public static void renderTextList(MatrixStack matrixStack, BlockPos pos, float tickDelta, float line, ArrayList<String> texts, ArrayList<Integer> colors, float size) {
        drawStringList(matrixStack, pos, tickDelta,  line, texts, colors, size) ;

        }
    private static VertexConsumerProvider.Immediate getVertexConsumer() {
        return MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
    }
    public static void drawStringList(MatrixStack matrices, BlockPos textPos, float tickDelta, float line, ArrayList<String> texts, ArrayList<Integer> colors, float size) {
        MinecraftClient client = MinecraftClient.getInstance();
        Camera camera = client.gameRenderer.getCamera();
        //modelViewMatrixStack modelViewMatrix = new modelViewMatrixStack(1);
        //modelViewMatrix.identity();

        if (camera.isReady() && client.getEntityRenderDispatcher().gameOptions != null && client.player != null) {
            matrices.push();
            float x = (float) (textPos.toCenterPos().getX() - MathHelper.lerp(tickDelta, lastTickPosX, camera.getPos().getX()));
            float y = (float) (textPos.toCenterPos().getY() - MathHelper.lerp(tickDelta, lastTickPosY, camera.getPos().getY()));
            float z = (float) (textPos.toCenterPos().getZ() - MathHelper.lerp(tickDelta, lastTickPosZ, camera.getPos().getZ()));
            lastTickPosX = camera.getPos().getX();
            lastTickPosY = camera.getPos().getY();
            lastTickPosZ = camera.getPos().getZ();

            matrices.translate(x, y, z);
            matrices.multiply(MinecraftClient.getInstance().gameRenderer.getCamera().getRotation());
            matrices.scale(size, -size, 1);
            Matrix4f modelViewMatrix = matrices.peek().getPositionMatrix();

            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

            RenderSystem.disableDepthTest();

            float totalHeight = 0.0F;
            for (String text : texts) {
                totalHeight += textRenderer.getWrappedLinesHeight(text, Integer.MAX_VALUE) * 1.25F;
            }

            float renderYBase = -totalHeight / 2.0F; // 起始位置，从底部开始
            for (int i = 0; i < texts.size(); i++) {
                float renderX = -textRenderer.getWidth(texts.get(i)) * 0.5F; // 居中
                float renderY = renderYBase + textRenderer.getWrappedLinesHeight(texts.get(i), Integer.MAX_VALUE) * 1.25F * i;
                VertexConsumerProvider.Immediate immediate = getVertexConsumer();
                textRenderer.draw(
                        texts.get(i), renderX, renderY, colors.get(i) != null? colors.get(i) : Color.white.getRGB(), shadow,
                        modelViewMatrix, immediate, TextRenderer.TextLayerType.SEE_THROUGH, background?backgroundColor.getRGB():0,
                        0xF000F0
                );
                immediate.draw();
            }
            matrices.pop();

            // 恢复矩阵状态
            RenderSystem.enableDepthTest();
        }
    }
    public static void drawCube(MatrixStack matrices, BlockPos pos, float size, float tickDelta, Color color,float alpha) {
        MinecraftClient client = MinecraftClient.getInstance();
        Camera camera = client.gameRenderer.getCamera();
        if (camera.isReady() && client.getEntityRenderDispatcher().gameOptions != null && client.player != null) {
            matrices.push();
            float x = (float) (pos.getX() - MathHelper.lerp(tickDelta, lastTickPosX, camera.getPos().getX()));
            float y = (float) (pos.getY() - MathHelper.lerp(tickDelta, lastTickPosY, camera.getPos().getY()));
            float z = (float) (pos.getZ() - MathHelper.lerp(tickDelta, lastTickPosZ, camera.getPos().getZ()));
            lastTickPosX = camera.getPos().getX();
            lastTickPosY = camera.getPos().getY();
            lastTickPosZ = camera.getPos().getZ();

            matrices.translate(x, y, z);
            //matrices.scale(size, size, size);
            Matrix4f modelViewMatrix = matrices.peek().getPositionMatrix();
            RenderSystem.disableDepthTest();

            VertexConsumerProvider.Immediate immediate = getVertexConsumer();
            VertexConsumer vertexConsumer = immediate.getBuffer(RenderLayer.getDebugQuads());

            float minOffset = -0.001F - size;
            float maxOffset = 1.001F + size;

            float red = ((color.getRGB() >> 16) & 0xFF) / 255.0f;
            float green = ((color.getRGB() >> 8) & 0xFF) / 255.0f;
            float blue = (color.getRGB() & 0xFF) / 255.0f;

            vertexConsumer.vertex(modelViewMatrix, minOffset, maxOffset, minOffset).color(red, green, blue, alpha);
            vertexConsumer.vertex(modelViewMatrix, maxOffset, maxOffset, minOffset).color(red, green, blue, alpha);
            vertexConsumer.vertex(modelViewMatrix, maxOffset, maxOffset, maxOffset).color(red, green, blue, alpha);
            vertexConsumer.vertex(modelViewMatrix, minOffset, maxOffset, maxOffset).color(red, green, blue, alpha);

            vertexConsumer.vertex(modelViewMatrix, minOffset, minOffset, maxOffset).color(red, green, blue, alpha);
            vertexConsumer.vertex(modelViewMatrix, maxOffset, minOffset, maxOffset).color(red, green, blue, alpha);
            vertexConsumer.vertex(modelViewMatrix, maxOffset, minOffset, minOffset).color(red, green, blue, alpha);
            vertexConsumer.vertex(modelViewMatrix, minOffset, minOffset, minOffset).color(red, green, blue, alpha);

            vertexConsumer.vertex(modelViewMatrix, minOffset, maxOffset, minOffset).color(red, green, blue, alpha);
            vertexConsumer.vertex(modelViewMatrix, minOffset, maxOffset, maxOffset).color(red, green, blue, alpha);
            vertexConsumer.vertex(modelViewMatrix, minOffset, minOffset, maxOffset).color(red, green, blue, alpha);
            vertexConsumer.vertex(modelViewMatrix, minOffset, minOffset, minOffset).color(red, green, blue, alpha);

            vertexConsumer.vertex(modelViewMatrix, maxOffset, minOffset, minOffset).color(red, green, blue, alpha);
            vertexConsumer.vertex(modelViewMatrix, maxOffset, minOffset, maxOffset).color(red, green, blue, alpha);
            vertexConsumer.vertex(modelViewMatrix, maxOffset, maxOffset, maxOffset).color(red, green, blue, alpha);
            vertexConsumer.vertex(modelViewMatrix, maxOffset, maxOffset, minOffset).color(red, green, blue, alpha);

            vertexConsumer.vertex(modelViewMatrix, minOffset, minOffset, minOffset).color(red, green, blue, alpha);
            vertexConsumer.vertex(modelViewMatrix, maxOffset, minOffset, minOffset).color(red, green, blue, alpha);
            vertexConsumer.vertex(modelViewMatrix, maxOffset, maxOffset, minOffset).color(red, green, blue, alpha);
            vertexConsumer.vertex(modelViewMatrix, minOffset, maxOffset, minOffset).color(red, green, blue, alpha);

            vertexConsumer.vertex(modelViewMatrix, minOffset, maxOffset, maxOffset).color(red, green, blue, alpha);
            vertexConsumer.vertex(modelViewMatrix, maxOffset, maxOffset, maxOffset).color(red, green, blue, alpha);
            vertexConsumer.vertex(modelViewMatrix, maxOffset, minOffset, maxOffset).color(red, green, blue, alpha);
            vertexConsumer.vertex(modelViewMatrix, minOffset, minOffset, maxOffset).color(red, green, blue, alpha);


            immediate.draw();
            matrices.pop();

            // 恢复矩阵状态
            RenderSystem.enableDepthTest();
        }
    }

    public static void drawString(MatrixStack matrixStack,float tickDelta, Camera camera, Vec3d textPos, String text, int color, float SIZE, boolean seeThrow) {

        Matrix4fStack modelViewMatrix = new Matrix4fStack(1);
        modelViewMatrix.identity();

        float x = (float) (textPos.x - MathHelper.lerp(tickDelta, lastTickPosX, camera.getPos().getX()));
        float y = (float) (textPos.y - MathHelper.lerp(tickDelta, lastTickPosY, camera.getPos().getY()));
        float z = (float) (textPos.z - MathHelper.lerp(tickDelta, lastTickPosZ, camera.getPos().getZ()));
        lastTickPosX = camera.getPos().getX();
        lastTickPosY = camera.getPos().getY();
        lastTickPosZ = camera.getPos().getZ();
        modelViewMatrix.translate(x, y, z);
        modelViewMatrix.rotate(MinecraftClient.getInstance().gameRenderer.getCamera().getRotation());
        modelViewMatrix.scale(SIZE, -SIZE, 1);

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        float totalWidth = textRenderer.getWidth(text);
        float writtenWidth = 1;
        float renderX = -totalWidth * 0.5F + writtenWidth;

        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        RenderSystem.disableDepthTest();

        if(seeThrow)
            textRenderer.draw(text, renderX, 0, color, false, modelViewMatrix
                    , immediate, TextRenderer.TextLayerType.SEE_THROUGH, 0, 0xF000F0);
        else
            textRenderer.draw(text, renderX, 0, color, false, modelViewMatrix
                    , immediate, TextRenderer.TextLayerType.NORMAL, 0, 0xF000F0);
        immediate.draw();
        RenderSystem.enableDepthTest();

    }

}