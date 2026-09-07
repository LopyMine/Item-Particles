package net.lopymine.itp.element.texture;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;

public interface ITexture {

	void clear();

	Identifier getId();

	TextureAtlasSprite getAtlasSprite();

	void initialize();

}
