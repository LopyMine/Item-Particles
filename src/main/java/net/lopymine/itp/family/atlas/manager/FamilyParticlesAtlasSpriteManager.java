package net.lopymine.itp.family.atlas.manager;

import com.mojang.blaze3d.platform.NativeImage;
import java.util.*;
import java.util.Map.Entry;
import net.lopymine.itp.family.atlas.AtlasSprite;
import net.lopymine.itp.family.cache.FamilyParticlesAtlasCacheManager;
import net.lopymine.itp.utils.MissingSpriteUtils;
import net.minecraft.resources.ResourceLocation;

public class FamilyParticlesAtlasSpriteManager {

	private static final AtlasSprite MISSING_SPRITE = AtlasSprite.of(MissingSpriteUtils.getMissingParticle());

	static {
		MISSING_SPRITE.setClosable(false);
	}

	public static Map<String, Set<AtlasSprite>> createSpritesFromGeneratedTextures() {
		Map<String, Set<AtlasSprite>> map = new HashMap<>();

		for (Entry<String, Map<ResourceLocation, Map<ResourceLocation, NativeImage>>> e : FamilyParticlesAtlasCacheManager.getNamespaceTextures().entrySet()) {
			String atlasId = e.getKey();
			Set<AtlasSprite> set = new HashSet<>();
			for (Entry<ResourceLocation, Map<ResourceLocation, NativeImage>> ee : e.getValue().entrySet()) {
				for (Entry<ResourceLocation, NativeImage> entry : ee.getValue().entrySet()) {
					AtlasSprite sprite = AtlasSprite.of(unwrapIdForAtlasSprite(entry.getKey()), entry.getValue());
					set.add(sprite);
				}
			}
			set.add(MISSING_SPRITE);
			map.put(atlasId, set);
		}

		return map;
	}

	public static ResourceLocation unwrapIdForAtlasSprite(ResourceLocation id) {
		if (id.getPath().endsWith(".png")) {
			id = id.withPath((path) -> path.substring(0, path.length() - 4));
			if (id.getPath().startsWith("textures/")) {
				id = id.withPath((path) -> path.substring(9));
			}
		}
		return id;
	}

}
