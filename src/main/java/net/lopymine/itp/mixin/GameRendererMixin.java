package net.lopymine.itp.mixin;

import net.lopymine.itp.manager.*;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
//? if >=26.3 {
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import net.minecraft.client.Minecraft;
//?}
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

	//? if >=26.3 {
	@Inject(at = @At(value = "INVOKE", shift = Shift.AFTER, target = "Lnet/minecraft/client/renderer/GameRenderer;renderItemInHand(Lnet/minecraft/client/renderer/state/level/CameraRenderState;Lnet/minecraft/client/renderer/state/level/PlayerRenderState;Lcom/mojang/renderpearl/api/textures/GpuTextureView;)V"), method = "render3dHud")
	private void renderFirstPersonParticles(CallbackInfo ci, @Local GpuTextureView depthTextureView) {
		FirstPersonParticleRenderer.getInstance().render(Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false), depthTextureView);
	}
	//?} else {
	/*//? if >=26.1 {
	@Inject(at = @At(value = "INVOKE", shift = Shift.AFTER, target = "Lnet/minecraft/client/renderer/GameRenderer;renderItemInHand(Lnet/minecraft/client/renderer/state/level/CameraRenderState;FLorg/joml/Matrix4fc;)V"), method = "renderLevel")
	//?} else {
	/^@Inject(at = @At(value = "INVOKE", shift = Shift.AFTER, target = "Lnet/minecraft/client/renderer/GameRenderer;renderItemInHand(FZLorg/joml/Matrix4f;)V"), method = "renderLevel")
	 ^///?}
	private void renderFirstPersonParticles(DeltaTracker deltaTracker, CallbackInfo ci) {
		FirstPersonParticleRenderer.getInstance().render(deltaTracker.getGameTimeDeltaPartialTick(false));
	}
	*///?}
}
