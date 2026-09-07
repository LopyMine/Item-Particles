package net.lopymine.itp.element.texture.provider;

import java.util.*;

import net.lopymine.itp.element.base.ITickElement;
import net.lopymine.itp.element.texture.*;
import net.minecraft.util.RandomSource;

public interface ITextureProvider extends ITickElement {

	Map<Object, List<ITexture>> CACHED_SPRITES = new HashMap<>();

	static void clear() {
		for (List<ITexture> list : CACHED_SPRITES.values()) {
			for (ITexture texture : list) {
				texture.clear();
			}
		}
		CACHED_SPRITES.clear();
	}

	static ITextureProvider getTextureProvider(Object cacheKey, List<ITexture> textures, double animationSpeed, int lifeTime, TextureAnimationType type) {
		List<ITexture> sprites = CACHED_SPRITES.computeIfAbsent(cacheKey, (key) -> {
			for (ITexture texture : textures) {
				texture.initialize();
			}
			return textures;
		});
		return switch (type) {
			case STRETCH -> new StretchTextureProvider(sprites, animationSpeed, lifeTime);
			case ONETIME -> new OneTimeTextureProvider(sprites, animationSpeed, lifeTime);
			case LOOP -> new LoopTextureProvider(sprites, animationSpeed, lifeTime);
			case RANDOM -> new RandomTextureProvider(sprites, animationSpeed, lifeTime);
			case RANDOM_STATIC -> new RandomStaticTextureProvider(sprites, animationSpeed, lifeTime);
		};
	}

	ITexture getInitializationTexture(RandomSource random);

	ITexture getTexture(RandomSource random);

	boolean isShouldDead();
}
