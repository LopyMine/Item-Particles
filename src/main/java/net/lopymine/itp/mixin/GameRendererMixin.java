package net.lopymine.itp.mixin;

import net.lopymine.itp.manager.*;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

	@Inject(at = @At("TAIL"), method = "tick")
	private void inject(CallbackInfo ci) {
		ItemParticleManager.getInstance().tick();
		FirstPersonParticleRenderer.getInstance().tick();
	}

	//? if >=26.1 {
	/*@Inject(at = @At(value = "INVOKE", shift = Shift.AFTER, target = "Lnet/minecraft/client/renderer/GameRenderer;renderItemInHand(Lnet/minecraft/client/renderer/state/level/CameraRenderState;FLorg/joml/Matrix4fc;)V"), method = "renderLevel")
	*///?} else {
	@Inject(at = @At(value = "INVOKE", shift = Shift.AFTER, target = "Lnet/minecraft/client/renderer/GameRenderer;renderItemInHand(FZLorg/joml/Matrix4f;)V"), method = "renderLevel")
	 //?}
	private void renderFirstPersonParticles(DeltaTracker deltaTracker, CallbackInfo ci) {
		FirstPersonParticleRenderer.getInstance().render(deltaTracker.getGameTimeDeltaPartialTick(false));
	}
}
