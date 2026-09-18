package net.lopymine.itp.entrypoint;

//? if fabric {

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
//? if >=26.1 {
/*import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
*///?} elif >=1.21.11 {
/*import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
*///?} else {
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
//?}
import net.lopymine.itp.atlas.ItemParticlesAtlasManager;
import net.lopymine.itp.client.*;
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

		//? if >=26.1 {
		/*LevelRenderEvents.COLLECT_SUBMITS.register((context) -> ItemParticlesDebugRenderer.emitGizmos());
		*///?} elif >=1.21.11 {
		/*WorldRenderEvents.END_EXTRACTION.register((context) -> ItemParticlesDebugRenderer.emitGizmos());
		*///?} else {
		WorldRenderEvents.BEFORE_DEBUG_RENDER.register((context) -> ItemParticlesDebugRenderer.render(context.matrixStack(), context.consumers()));
		//?}
	}
}

//?}
