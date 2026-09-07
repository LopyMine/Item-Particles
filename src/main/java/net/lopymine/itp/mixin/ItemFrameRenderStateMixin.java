package net.lopymine.itp.mixin;

import java.util.UUID;
import net.lopymine.itp.utils.mixin.RenderStateWithUUID;
import net.minecraft.client.renderer.entity.state.ItemFrameRenderState;
import org.spongepowered.asm.mixin.*;

@Mixin(ItemFrameRenderState.class)
public class ItemFrameRenderStateMixin implements RenderStateWithUUID {

	@Unique
	private UUID itemParticles$uuid;

	@Override
	public UUID itemParticles$getUUID() {
		return this.itemParticles$uuid;
	}

	@Override
	public void itemParticles$setUUID(UUID uuid) {
		this.itemParticles$uuid = uuid;
	}
}
