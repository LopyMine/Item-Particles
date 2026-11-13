package net.lopymine.itp.entrypoint;

//? if neoforge {
/*import net.lopymine.itp.ItemParticles;

import net.lopymine.itp.client.*;
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
//? if >=1.21.11 {
/^import net.neoforged.neoforge.client.event.ExtractLevelRenderStateEvent;
^///?} else {
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
//?}

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

		//? if >=1.21.11 {
		/^NeoForge.EVENT_BUS.addListener(ExtractLevelRenderStateEvent.class, (event) -> ItemParticlesDebugRenderer.emitGizmos());
		^///?} else {
		NeoForge.EVENT_BUS.addListener(RenderLevelStageEvent.class, (event) -> {
			if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) {
				return;
			}
			BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
			ItemParticlesDebugRenderer.render(event.getPoseStack(), bufferSource);
			bufferSource.endBatch(RenderType.debugFilledBox());
		});
		//?}
	}

	public static class LevelJoinEvent extends Event { }

}

*///?}

