package net.lopymine.itp.element.texture;

import lombok.*;
import net.lopymine.itp.config.misc.CachedItem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.*;

@Getter
@Setter
public class ItemTexture implements ITexture {

	private CachedItem cachedItem;

	@Nullable
	private ItemStack stack;

	public ItemTexture(@NotNull CachedItem cachedItem) {
		this.cachedItem = cachedItem;
	}

	@Override
	public void initialize() {
		this.stack = this.cachedItem.getItem().getDefaultInstance();
	}

	@Override
	public void clear() {
		this.stack = null;
	}

	@Override
	public Identifier getId() {
		return this.cachedItem.getId();
	}

	@Override
	public TextureAtlasSprite getAtlasSprite() {
		return null; // todo
	}
}
