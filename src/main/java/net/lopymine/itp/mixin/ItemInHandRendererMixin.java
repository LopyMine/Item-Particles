package net.lopymine.itp.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.lopymine.itp.manager.ItemParticleManager;
import net.minecraft.client.renderer.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//? if >=1.21.4 {
/*import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.item.ItemStackRenderState;
*///?} else {
import net.minecraft.world.entity.HumanoidArm;
//?}

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {

	//? if >=1.21.4 {
	/*@Inject(at = @At(value = "INVOKE", shift = Shift.AFTER, target = "Lnet/minecraft/client/renderer/item/ItemStackRenderState;submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;III)V"), method = "renderItem")
	private void acceptFirstPersonItemParticleRequests(LivingEntity entity, ItemStack itemStack, ItemDisplayContext displayContext, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, CallbackInfo ci, @Local ItemStackRenderState state) {
		ItemParticleManager.getInstance().acceptFirstPersonItemParticleRequests(state, poseStack, displayContext, entity);
	}
	*///?} else {
	@Inject(at = @At("TAIL"), method = "renderItem")
	private void acceptItemParticleRequests(LivingEntity entity, ItemStack itemStack, ItemDisplayContext displayContext, boolean leftHand, PoseStack poseStack, MultiBufferSource multiBufferSource, int lightCoords, CallbackInfo ci) {
		ItemParticleManager manager = ItemParticleManager.getInstance();

		if (ItemParticleManager.isFirstPersonCameraEntity(entity)) {
			manager.acceptFirstPersonItemParticleRequests(itemStack, poseStack, displayContext, entity);
			return;
		}

		switch (displayContext) {
			case THIRD_PERSON_RIGHT_HAND -> manager.acceptArmedItemParticleRequests(itemStack, poseStack, HumanoidArm.RIGHT, entity);
			case THIRD_PERSON_LEFT_HAND -> manager.acceptArmedItemParticleRequests(itemStack, poseStack, HumanoidArm.LEFT, entity);
			default -> { }
		}
	}
	//?}
}
