package net.lopymine.itp.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.lopymine.itp.manager.ItemParticleManager;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import net.minecraft.world.entity.decoration.ItemFrame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//? if >=1.21.4 {
/*import net.lopymine.itp.utils.mixin.RenderStateWithUUID;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.ItemFrameRenderState;
*///?} else {
import net.minecraft.client.renderer.MultiBufferSource;
//?}
//? if >=26.1 {
/*import net.minecraft.client.renderer.state.level.CameraRenderState;
*///?} elif >=1.21.4 {
/*import net.minecraft.client.renderer.state.CameraRenderState;
*///?}

@Mixin(ItemFrameRenderer.class)
public class ItemFrameRendererMixin {

	//? if >=1.21.4 {
	/*@Inject(at = @At("TAIL"), method = "extractRenderState(Lnet/minecraft/world/entity/decoration/ItemFrame;Lnet/minecraft/client/renderer/entity/state/ItemFrameRenderState;F)V")
	private void extractWithUUID(ItemFrame entity, ItemFrameRenderState state, float partialTicks, CallbackInfo ci) {
		((RenderStateWithUUID) state).itemParticles$setUUID(entity.getUUID());
	}

	//? if >=26.1 {
	/^@Inject(at = @At(value = "INVOKE", shift = Shift.AFTER, target = "Lnet/minecraft/client/renderer/item/ItemStackRenderState;submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;III)V"), method = "submit(Lnet/minecraft/client/renderer/entity/state/ItemFrameRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V")
	^///?} else {
	@Inject(at = @At(value = "INVOKE", shift = Shift.AFTER, target = "Lnet/minecraft/client/renderer/item/ItemStackRenderState;submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;III)V"), method = "submit(Lnet/minecraft/client/renderer/entity/state/ItemFrameRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V")
	//?}
	private void acceptItemParticleRequests(ItemFrameRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera, CallbackInfo ci) {
		ItemParticleManager.getInstance().acceptItemFrameParticleRequests(state.item, poseStack, state);
	}
	*///?} else {
	@Inject(at = @At(value = "INVOKE", shift = Shift.AFTER, target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderStatic(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;IILcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/level/Level;I)V"), method = "render(Lnet/minecraft/world/entity/decoration/ItemFrame;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V")
	private void acceptItemParticleRequests(ItemFrame itemFrame, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource, int lightCoords, CallbackInfo ci) {
		ItemParticleManager.getInstance().acceptItemFrameParticleRequests(itemFrame.getItem(), poseStack, itemFrame);
	}
	//?}
}
