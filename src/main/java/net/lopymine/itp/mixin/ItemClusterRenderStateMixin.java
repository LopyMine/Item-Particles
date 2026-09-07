package net.lopymine.itp.mixin;

import java.util.UUID;
import net.lopymine.itp.utils.mixin.RenderStateWithUUID;
import net.minecraft.client.renderer.entity.state.ItemClusterRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemClusterRenderState.class)
public class ItemClusterRenderStateMixin implements RenderStateWithUUID {

	@Unique
	private UUID itemParticles$uuid;

	@Inject(at = @At("HEAD"), method = "extractItemGroupRenderState")
	private void extractWithUUID(Entity entity, ItemStack stack, ItemModelResolver itemModelResolver, CallbackInfo ci) {
		this.itemParticles$setUUID(entity.getUUID());
	}

	@Override
	public UUID itemParticles$getUUID() {
		return this.itemParticles$uuid;
	}

	@Override
	public void itemParticles$setUUID(UUID uuid) {
		this.itemParticles$uuid = uuid;
	}
}
