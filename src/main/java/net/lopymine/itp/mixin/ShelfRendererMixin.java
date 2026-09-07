package net.lopymine.itp.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.lopymine.itp.manager.ItemParticleManager;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.ShelfRenderer;
import net.minecraft.client.renderer.blockentity.state.ShelfRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.level.block.entity.ShelfBlockEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ShelfRenderer.class)
public class ShelfRendererMixin {

	@Inject(at = @At("TAIL"), method = "extractRenderState(Lnet/minecraft/world/level/block/entity/ShelfBlockEntity;Lnet/minecraft/client/renderer/blockentity/state/ShelfRenderState;FLnet/minecraft/world/phys/Vec3;Lnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V")
	private void trackShelfItems(ShelfBlockEntity blockEntity, ShelfRenderState state, float partialTicks, Vec3 cameraPosition, CrumblingOverlay breakProgress, CallbackInfo ci) {
		ItemParticleManager.getInstance().trackShelfItems(blockEntity.getBlockPos(), blockEntity.getItems());
	}

	@Inject(at = @At(value = "INVOKE", shift = Shift.AFTER, target = "Lnet/minecraft/client/renderer/item/ItemStackRenderState;submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;III)V"), method = "submitItem(Lnet/minecraft/client/renderer/blockentity/state/ShelfRenderState;Lnet/minecraft/client/renderer/item/ItemStackRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;IF)V")
	private void acceptItemParticleRequests(ShelfRenderState state, ItemStackRenderState itemStackRenderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int slot, float yRot, CallbackInfo ci) {
		ItemParticleManager.getInstance().acceptShelfItemParticleRequests(itemStackRenderState, poseStack, state.blockPos, slot);
	}
}
