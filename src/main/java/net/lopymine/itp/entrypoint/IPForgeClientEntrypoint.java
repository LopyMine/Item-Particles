package net.lopymine.itp.entrypoint;

//? if forge {

/*import net.lopymine.ip.client.ItemParticlesClient;
import net.lopymine.mossylib.loader.MossyLoader;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModLoadingContext;

public class IPForgeClientEntrypoint {

	public static void onInitializeClient() {
		ItemParticlesClient.onInitializeClient();
		ModMenuIntegration integration = new ModMenuIntegration();
		integration.register(ModLoadingContext.get().getActiveContainer());

		MossyLoader.registerReloadListener(new ItemParticlesClientReloadListener());
		MossyLoader.registerCommands(ItemParticlesCommandManager::register);

		MinecraftForge.EVENT_BUS.<LevelJoinEvent>addListener((event) -> {
			ParticlesConfigsManager.updateCombinedMap();
		});
	}

	public static class LevelJoinEvent extends Event { }

}

*///?}

