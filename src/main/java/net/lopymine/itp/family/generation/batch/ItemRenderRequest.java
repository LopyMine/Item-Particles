package net.lopymine.itp.family.generation.batch;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import org.jetbrains.annotations.Nullable;

public record ItemRenderRequest(ResourceLocation itemId, Item item, @Nullable BucketItem bucket) {

	public boolean isFluid() {
		return this.bucket != null;
	}
}
