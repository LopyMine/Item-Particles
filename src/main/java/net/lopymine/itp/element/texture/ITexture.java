package net.lopymine.itp.element.texture;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

public interface ITexture {

	void clear();

	ResourceLocation getId();

	TextureAtlasSprite getAtlasSprite();

	void initialize();

}
