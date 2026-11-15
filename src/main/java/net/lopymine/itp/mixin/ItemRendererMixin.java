package net.lopymine.itp.mixin;

//? if <1.21.4 {
/*import com.mojang.blaze3d.vertex.PoseStack;
import net.lopymine.itp.manager.ItemParticleManager;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

	@Inject(at = @At("TAIL"), method = "render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V")
	private void acceptDroppedItemParticleRequests(ItemStack stack, ItemDisplayContext displayContext, boolean leftHand, PoseStack poseStack, MultiBufferSource multiBufferSource, int lightCoords, int overlay, BakedModel model, CallbackInfo ci) {
		ItemParticleManager manager = ItemParticleManager.getInstance();
		ItemEntity itemEntity = manager.getCurrentDroppedItemEntity();
		if (itemEntity == null) {
			return;
		}
		manager.acceptDroppedItemParticleRequests(stack, displayContext, poseStack, itemEntity);
	}

}
*///?}
