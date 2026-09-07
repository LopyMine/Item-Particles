package net.lopymine.itp.element.spawner;

import java.util.*;
import net.lopymine.itp.manager.ItemParticleManager.ItemParticleRequest;
import net.minecraft.resources.Identifier;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

public record AdvancedSpawnPos(@Nullable Identifier texture, int x, int y) implements IParticleSpawnPos {

	public static final IParticleSpawnPos FULL_POS = new IParticleSpawnPos() {
		@Override
		public int x() {
			return 0;
		}

		@Override
		public int y() {
			return 0;
		}

		@Override
		public @Nullable Vector3fc[] get(ItemParticleRequest request, Map<PixelPos, Vector3fc[]> map) {
			ArrayList<PixelPos> list = new ArrayList<>(map.keySet());
			return map.get(list.get(request.getRandom().nextInt(0, list.size())));
		}
	};

}
