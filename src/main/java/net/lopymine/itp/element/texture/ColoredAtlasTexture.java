package net.lopymine.itp.element.texture;

import java.util.function.Function;
import lombok.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
public class ColoredAtlasTexture extends AtlasTexture {

	private Function<Integer, Integer> color;

	public ColoredAtlasTexture(@Nullable Identifier sprite, @Nullable Identifier atlas, Function<Integer, Integer> color) {
		super(sprite, atlas);
		this.color = color;
	}

}
