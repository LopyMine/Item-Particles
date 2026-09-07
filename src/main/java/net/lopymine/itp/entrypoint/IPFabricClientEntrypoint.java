package net.lopymine.itp.entrypoint;

//? if fabric {

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.lopymine.itp.atlas.ItemParticlesAtlasManager;
import net.lopymine.itp.client.ItemParticlesClient;
import net.lopymine.itp.client.command.ItemParticlesCommandManager;
import net.lopymine.itp.resourcepack.manager.ParticlesConfigsManager;
import net.lopymine.itp.resourcepack.reload.ItemParticlesClientReloadListener;
import net.lopymine.mossylib.loader.MossyLoader;

public class IPFabricClientEntrypoint implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ItemParticlesClient.onInitializeClient();
		ClientLifecycleEvents.CLIENT_STOPPING.register((client) -> {
			ItemParticlesAtlasManager.getInstance().close();
		});

		MossyLoader.registerReloadListener(new ItemParticlesClientReloadListener());
		MossyLoader.registerCommands(ItemParticlesCommandManager::register);

		ClientPlayConnectionEvents.JOIN.register((aa, bb, vv) -> {
			ParticlesConfigsManager.updateCombinedMap();
		});

	}
}

//?}
