package net.lopymine.itp.mixin;

//? if >=1.21.4 {
import com.mojang.blaze3d.vertex.PoseStack;
import net.lopymine.itp.manager.ItemParticleManager;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.ItemClusterRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStackRenderState.class)
public class ItemStackRenderStateMixin {

	@Inject(at = @At("TAIL"), method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;III)V")
	private void acceptDroppedItemParticleRequests(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, int overlay, int outlineColor, CallbackInfo ci) {
		ItemParticleManager manager = ItemParticleManager.getInstance();
		ItemClusterRenderState renderState = manager.getCurrentDroppedRenderState();
		if (renderState == null) {
			return;
		}
		manager.acceptDroppedItemParticleRequests((ItemStackRenderState) (Object) this, poseStack, renderState);
	}

}
//?}
