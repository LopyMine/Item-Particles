package net.lopymine.itp.entrypoint;

//? if forge {

/*import net.lopymine.itp.client.*;
import net.lopymine.itp.client.command.ItemParticlesCommandManager;
import net.lopymine.itp.modmenu.ModMenuIntegration;
import net.lopymine.itp.resourcepack.manager.ParticlesConfigsManager;
import net.lopymine.itp.resourcepack.reload.ItemParticlesClientReloadListener;
import net.lopymine.mossylib.loader.MossyLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.*;
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

		MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, RenderLevelStageEvent.class, (event) -> {
			if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) {
				return;
			}
			BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
			ItemParticlesDebugRenderer.render(event.getPoseStack(), bufferSource);
			bufferSource.endBatch(RenderType.debugFilledBox());
		});
	}

	public static class LevelJoinEvent extends Event { }

}

*///?}
