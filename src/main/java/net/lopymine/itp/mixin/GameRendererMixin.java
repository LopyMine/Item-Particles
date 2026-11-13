package net.lopymine.itp.mixin;

import net.lopymine.itp.manager.*;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//? if >=1.21 {
import net.minecraft.client.DeltaTracker;
//?} else {
/*import com.mojang.blaze3d.vertex.PoseStack;
*///?}
//? if >=26.3 {
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.state.OptionsRenderState;
import net.minecraft.client.renderer.state.level.*;
//?}

@Mixin(GameRenderer.class)
public class GameRendererMixin {

	@Inject(at = @At("TAIL"), method = "tick")
	private void inject(CallbackInfo ci) {
		ItemParticleManager.getInstance().tick();
		FirstPersonParticleRenderer.getInstance().tick();
	}

	//? if >=26.3 {
	@Inject(at = @At(value = "INVOKE", shift = Shift.AFTER, target = "Lnet/minecraft/client/renderer/GameRenderer;renderItemInHand(Lnet/minecraft/client/renderer/state/level/CameraRenderState;Lnet/minecraft/client/renderer/state/level/PlayerRenderState;Lcom/mojang/renderpearl/api/textures/GpuTextureView;)V"), method = "render3dHud")
	//?} elif >=26.1 {
	/*@Inject(at = @At(value = "INVOKE", shift = Shift.AFTER, target = "Lnet/minecraft/client/renderer/GameRenderer;renderItemInHand(Lnet/minecraft/client/renderer/state/level/CameraRenderState;FLorg/joml/Matrix4fc;)V"), method = "renderLevel")
	*///?} elif >=1.21.11 {
	/*@Inject(at = @At(value = "INVOKE", shift = Shift.AFTER, target = "Lnet/minecraft/client/renderer/GameRenderer;renderItemInHand(FZLorg/joml/Matrix4f;)V"), method = "renderLevel")
	*///?} elif >=1.21 {
	/*@Inject(at = @At(value = "INVOKE", shift = Shift.AFTER, target = "Lnet/minecraft/client/renderer/GameRenderer;renderItemInHand(Lnet/minecraft/client/Camera;FLorg/joml/Matrix4f;)V"), method = "renderLevel")
	*///?} else {
	/*@Inject(at = @At(value = "INVOKE", shift = Shift.AFTER, target = "Lnet/minecraft/client/renderer/GameRenderer;renderItemInHand(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/Camera;F)V"), method = "renderLevel")
	*///?}
	//? if >=26.3 {
	private void renderFirstPersonParticles(CameraRenderState cameraState, PlayerRenderState playerState, OptionsRenderState optionsState, boolean consistentDepthRequired, CallbackInfo ci, @Local GpuTextureView depthTextureView) {
		float tickProgress = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false);
	//?} elif >=1.21 {
	/*private void renderFirstPersonParticles(DeltaTracker deltaTracker, CallbackInfo ci) {
		float tickProgress = deltaTracker.getGameTimeDeltaPartialTick(false);
	*///?} else {
	/*private void renderFirstPersonParticles(float tickProgress, long finishTimeNano, PoseStack poseStack, CallbackInfo ci) {
	*///?}
		//? if <1.21.4 {
		/*ItemParticleManager.getInstance().acceptPendingFirstPersonItemParticleRequests();
		*///?}
		FirstPersonParticleRenderer.getInstance().render(tickProgress/*? if >=26.3 {*/, depthTextureView/*?}*/);
	}
}
