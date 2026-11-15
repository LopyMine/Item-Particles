package net.lopymine.itp.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.lopymine.itp.manager.ItemParticleManager;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
//? if >=1.21.4 {
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.ItemEntityRenderState;
//?} else {
/*import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.item.ItemEntity;
*///?}
//? if >=26.1 {
import net.minecraft.client.renderer.state.level.CameraRenderState;
//?} elif >=1.21.4 {
/*import net.minecraft.client.renderer.state.CameraRenderState;
*///?}

@Mixin(ItemEntityRenderer.class)
public class ItemEntityRendererMixin {

	//? if >=26.1 {
	@WrapMethod(method = "submit(Lnet/minecraft/client/renderer/entity/state/ItemEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V")
	//?} elif >=1.21.4 {
	/*@WrapMethod(method = "submit(Lnet/minecraft/client/renderer/entity/state/ItemEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V")
	*///?}
	//? if >=1.21.4 {
	private void trackDroppedItem(ItemEntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraState, Operation<Void> original) {
		ItemParticleManager manager = ItemParticleManager.getInstance();
		manager.setCurrentDroppedRenderState(state);
		try {
			original.call(state, poseStack, submitNodeCollector, cameraState);
		} finally {
			manager.setCurrentDroppedRenderState(null);
		}
	}
	//?} else {
	/*@WrapMethod(method = "render(Lnet/minecraft/world/entity/item/ItemEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V")
	private void trackDroppedItem(ItemEntity itemEntity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource, int lightCoords, Operation<Void> original) {
		ItemParticleManager manager = ItemParticleManager.getInstance();
		manager.setCurrentDroppedItemEntity(itemEntity);
		try {
			original.call(itemEntity, entityYaw, partialTicks, poseStack, multiBufferSource, lightCoords);
		} finally {
			manager.setCurrentDroppedItemEntity(null);
		}
	}
	*///?}

}
