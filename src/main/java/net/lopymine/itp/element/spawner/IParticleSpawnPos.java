package net.lopymine.itp.element.spawner;

import java.util.Map;
import net.lopymine.itp.manager.ItemParticleManager.ItemParticleRequest;
import net.minecraft.resources.Identifier;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

public interface IParticleSpawnPos {

	int x();

	int y();

	@Nullable
	default Identifier texture() {
		return null;
	}

	@Nullable
	default Vector3fc[] get(ItemParticleRequest request, Map<PixelPos, Vector3fc[]> map) {
		return map.get(new PixelPos(this.x(), this.y()));
	}
}
