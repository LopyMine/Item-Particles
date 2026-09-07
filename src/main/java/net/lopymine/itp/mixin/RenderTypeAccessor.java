package net.lopymine.itp.mixin;

import net.minecraft.client.renderer.rendertype.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderType.class)
public interface RenderTypeAccessor {

	@Accessor("state")
	RenderSetup ItemParticles$getState();

}
