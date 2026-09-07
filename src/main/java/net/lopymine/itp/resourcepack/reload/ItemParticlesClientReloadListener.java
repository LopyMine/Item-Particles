package net.lopymine.itp.resourcepack.reload;

import java.util.concurrent.Executor;
import net.lopymine.itp.ItemParticles;
import net.lopymine.itp.atlas.ItemParticlesAtlasManager;
import net.lopymine.itp.config.ItemParticlesConfig;
import net.lopymine.itp.config.sub.ItemParticlesCacheConfig.CacheInvalidateMode;
import net.lopymine.itp.element.texture.provider.ITextureProvider;
import net.lopymine.itp.family.FamilyParticlesConfigManager;
import net.lopymine.itp.family.atlas.manager.FamilyParticlesAtlasManager;
import net.lopymine.itp.family.cache.*;
import net.lopymine.itp.resourcepack.manager.ParticlesConfigsManager;
import net.lopymine.mossylib.reload.AbstractResourceReloadListener;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ResourceManager;

public class ItemParticlesClientReloadListener extends AbstractResourceReloadListener {

	private boolean first = true;

	@Override
	public String getModId() {
		return ItemParticles.MOD_ID;
	}

	@Override
	protected void reloadStuff(PreparationBarrier barrier, ResourceManager manager, Executor prepareExecutor, Executor applyExecutor) {
		if (this.first && ItemParticlesConfig.getInstance().getCacheConfig().getInvalidateMode() == CacheInvalidateMode.AFTER_GAME_LAUNCH) {
			FamilyParticlesCacheManager.deleteSilence();
		}
		if (!this.first && ItemParticlesConfig.getInstance().getCacheConfig().getInvalidateMode() == CacheInvalidateMode.AFTER_RESOURCE_RELOADING) {
			FamilyParticlesCacheManager.deleteSilence();
		}
		this.first = false;

		ITextureProvider.clear(); // clear cached sprites
		FamilyParticlesAtlasManager.closeAll();
		FamilyParticlesAtlasCacheManager.clear(); // release images
		FamilyParticlesSpawnAreasCacheManager.clear(); // clear spawn areas
		ItemParticlesAtlasManager.getInstance().reload(barrier, manager, prepareExecutor, applyExecutor); // reload mod atlas
		ParticlesConfigsManager.getInstance().reload(); // reload configs
		FamilyParticlesConfigManager.getInstance().reload(); // reload family configs
		if (Minecraft.getInstance().level != null) {
			ParticlesConfigsManager.updateCombinedMap(); // final combine
		}
	}
}
