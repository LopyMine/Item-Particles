package net.lopymine.itp.mixin;

import java.util.UUID;
import net.lopymine.itp.utils.mixin.RenderStateWithUUID;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmedEntityRenderState.class)
public class ArmedEntityRenderStateMixin implements RenderStateWithUUID {

	@Unique
	private UUID uuid;

	@Inject(at = @At("HEAD"), method = "extractArmedEntityRenderState")
	private static void extractWithUUID(LivingEntity entity, ArmedEntityRenderState state, ItemModelResolver itemModelResolver, float partialTicks, CallbackInfo ci) {
		((RenderStateWithUUID) state).itemParticles$setUUID(entity.getUUID());
	}

	@Override
	public UUID itemParticles$getUUID() {
		return this.uuid;
	}

	@Override
	public void itemParticles$setUUID(UUID uuid) {
		this.uuid = uuid;
	}
}
