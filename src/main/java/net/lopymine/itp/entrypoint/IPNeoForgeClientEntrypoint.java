package net.lopymine.itp.entrypoint;

//? if neoforge {
/*import net.lopymine.itp.ItemParticles;

import net.lopymine.itp.client.ItemParticlesClient;
import net.lopymine.itp.client.command.ItemParticlesCommandManager;
import net.lopymine.itp.modmenu.ModMenuIntegration;
import net.lopymine.itp.resourcepack.manager.ParticlesConfigsManager;
import net.lopymine.itp.resourcepack.reload.ItemParticlesClientReloadListener;
import net.lopymine.mossylib.loader.MossyLoader;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.*;
import net.neoforged.bus.api.*;
import net.neoforged.fml.*;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = ItemParticles.MOD_ID, dist = Dist.CLIENT)
public class IPNeoForgeClientEntrypoint {

	public IPNeoForgeClientEntrypoint(IEventBus bus, ModContainer container) {
		ItemParticlesClient.onInitializeClient();
		ModMenuIntegration integration = new ModMenuIntegration();
		integration.register(container);

		MossyLoader.registerReloadListener(new ItemParticlesClientReloadListener());
		MossyLoader.registerCommands(ItemParticlesCommandManager::register);

		NeoForge.EVENT_BUS.addListener(LevelJoinEvent.class, (event) -> {
			ParticlesConfigsManager.updateCombinedMap();
		});

	}

	public static class LevelJoinEvent extends Event { }

}

*///?}

