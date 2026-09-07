package net.lopymine.itp.element.spawner;

import java.util.List;
import net.lopymine.itp.manager.ItemParticleManager.ItemParticleRequest;

public interface IParticleSpawner {

	List<ItemParticleRequest> tickAndSpawn(SpawnContext context);

	List<ItemParticleRequest> spawn(SpawnContext context);

}
