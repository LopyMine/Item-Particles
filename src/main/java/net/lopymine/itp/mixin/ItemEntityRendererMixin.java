package net.lopymine.itp.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.lopymine.itp.manager.ItemParticleManager;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//? if >=1.21.4 {
/*import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.entity.state.ItemClusterRenderState;
*///?} else {
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.item.ItemEntity;
//?}

@Mixin(ItemEntityRenderer.class)
public class ItemEntityRendererMixin {

	//? if >=1.21.4 {
	/*@Inject(at = @At(value = "INVOKE", shift = Shift.AFTER, target = "Lnet/minecraft/client/renderer/item/ItemStackRenderState;submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;III)V", ordinal = 0), method = "submitMultipleFromCount(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/ItemClusterRenderState;Lnet/minecraft/util/RandomSource;Lnet/minecraft/world/phys/AABB;)V")
	private static void acceptItemParticleRequests1(CallbackInfo ci, @Local(argsOnly = true) ItemClusterRenderState state, @Local(argsOnly = true) PoseStack poseStack) {
		ItemParticleManager.getInstance().acceptDroppedItemParticleRequests(state.item, poseStack, state);
	}

	@Inject(at = @At(value = "INVOKE", shift = Shift.AFTER, target = "Lnet/minecraft/client/renderer/item/ItemStackRenderState;submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;III)V", ordinal = 2), method = "submitMultipleFromCount(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/ItemClusterRenderState;Lnet/minecraft/util/RandomSource;Lnet/minecraft/world/phys/AABB;)V")
	private static void acceptItemParticleRequests2(CallbackInfo ci, @Local(argsOnly = true) ItemClusterRenderState state, @Local(argsOnly = true) PoseStack poseStack) {
		ItemParticleManager.getInstance().acceptDroppedItemParticleRequests(state.item, poseStack, state);
	}
	*///?} else {
	//? if >=1.21 {
	/*@Inject(at = @At(value = "INVOKE", shift = Shift.AFTER, target = "Lnet/minecraft/client/renderer/entity/ItemEntityRenderer;renderMultipleFromCount(Lnet/minecraft/client/renderer/entity/ItemRenderer;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/item/ItemStack;Lnet/minecraft/client/resources/model/BakedModel;ZLnet/minecraft/util/RandomSource;)V"), method = "render(Lnet/minecraft/world/entity/item/ItemEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V")
	*///?} else {
	@Inject(at = @At(value = "INVOKE", shift = Shift.AFTER, target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V"), method = "render(Lnet/minecraft/world/entity/item/ItemEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V")
	//?}
	private void acceptItemParticleRequests(ItemEntity itemEntity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource, int lightCoords, CallbackInfo ci) {
		ItemParticleManager.getInstance().acceptDroppedItemParticleRequests(itemEntity.getItem(), poseStack, itemEntity);
	}
	//?}

}
