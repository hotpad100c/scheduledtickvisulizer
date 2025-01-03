package mypals.ml.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import mypals.ml.InfoRender;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4fStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRenderMixin {
	@Inject(
			method = "method_62212",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/debug/DebugRenderer;renderLate(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;DDD)V")
	)private void render(CallbackInfo ci,
						 @Local MatrixStack stack
	) {
		InfoRender.render(new MatrixStack());
	}
}