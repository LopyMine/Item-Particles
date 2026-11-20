package net.lopymine.itp.atlas;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import net.lopymine.itp.client.ItemParticlesClient;
import net.lopymine.itp.config.ItemParticlesConfig;
import net.lopymine.itp.element.texture.*;
import net.lopymine.itp.family.atlas.LockableAtlasTexture;
import net.lopymine.itp.family.atlas.manager.FamilyParticlesAtlasManager;
import net.lopymine.itp.mixin.TextureAtlasAccessor;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

public class MissingSpritesReporter {

	private static final AtomicBoolean DUMPED = new AtomicBoolean();
	private static volatile boolean active;

	public static void reset() {
		active = false;
	}

	public static void activate() {
		DUMPED.set(false);
		active = true;
	}

	public static boolean isMissing(@Nullable TextureAtlasSprite sprite) {
		return sprite != null && MissingTextureAtlasSprite.getLocation().equals(sprite.contents().name());
	}

	public static boolean isDebug() {
		return active && ItemParticlesConfig.getInstance().getMainConfig().isDebugModeEnabled();
	}

	public static void reportMissing(AtlasTexture texture) {
		if (!active) {
			return;
		}
		dumpAtlasesOnce();
		ItemParticlesClient.LOGGER.warn("Failed to render a particle with texture \"{}\" from atlas \"{}\". See the Item Particles dynamic atlases dump.", texture.getSpriteNotNull(), texture.getAtlas());
	}

	public static void reportTexture(ITexture texture) {
		if (!active) {
			return;
		}
		dumpAtlasesOnce();
		if (texture instanceof AtlasTexture atlasTexture) {
			ItemParticlesClient.LOGGER.info("Spawned a particle with texture \"{}\" from atlas \"{}\"", atlasTexture.getSpriteNotNull(), atlasTexture.getAtlas());
			return;
		}
		ItemParticlesClient.LOGGER.info("Spawned a particle with {} \"{}\"", texture.getClass().getSimpleName(), texture.getId());
	}

	private static void dumpAtlasesOnce() {
		if (DUMPED.compareAndSet(false, true)) {
			dumpAtlases();
		}
	}

	private static void dumpAtlases() {
		StringBuilder builder = new StringBuilder("============= ITEM PARTICLES DYNAMIC ATLASES DUMP ================\n");
		ItemParticlesAtlasManager atlasManager = ItemParticlesAtlasManager.getInstance();
		appendAtlas(builder, ItemParticlesAtlasManager.ATLAS_ID, atlasManager.getAtlas());
		appendAtlas(builder, ItemParticlesAtlasManager.LEGACY_ATLAS_ID, atlasManager.getLegacyAtlas());
		for (String key : new TreeSet<>(FamilyParticlesAtlasManager.keys())) {
			FamilyParticlesAtlasManager manager = FamilyParticlesAtlasManager.get(key);
			if (manager == null) {
				continue;
			}
			LockableAtlasTexture atlasTexture = manager.getAtlasTexture();
			appendAtlas(builder, manager.getAtlasId(), atlasTexture == null ? null : atlasTexture.getAtlas());
		}
		builder.append("====================================================================");
		ItemParticlesClient.LOGGER.info("\n{}", builder);
	}

	private static void appendAtlas(StringBuilder builder, Identifier atlasId, @Nullable TextureAtlas atlas) {
		builder.append(atlasId).append(' ');
		if (atlas == null) {
			builder.append("<not stitched>\n");
			return;
		}
		List<String> sprites = ((TextureAtlasAccessor) atlas).getTexturesByName()
				.keySet()
				.stream()
				.map((id) -> "\"" + id + "\"")
				.sorted()
				.toList();
		builder.append('[').append(String.join(", ", sprites)).append("]\n");
	}

}
