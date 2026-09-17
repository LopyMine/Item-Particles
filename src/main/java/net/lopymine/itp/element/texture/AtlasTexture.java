package net.lopymine.itp.element.texture;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.*;
import net.lopymine.itp.ItemParticles;
import net.lopymine.itp.atlas.ItemParticlesAtlasManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.*;
import static net.lopymine.mossylib.utils.CodecUtils.option;

@Getter
@Setter
public class AtlasTexture implements ITexture {

	public static final ResourceLocation NO_SPRITE = ItemParticles.id("no_sprite");

	public static final Codec<AtlasTexture> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
			option("sprite", NO_SPRITE, ResourceLocation.CODEC, AtlasTexture::getSpriteNotNull),
			option("atlas", ItemParticlesAtlasManager.ATLAS_ID, ResourceLocation.CODEC, AtlasTexture::getAtlas)
	).apply(instance, AtlasTexture::new));

	@Nullable
	private ResourceLocation sprite;
	@NotNull
	private ResourceLocation atlas;

	@Nullable
	private TextureAtlasSprite atlasSprite;

	public AtlasTexture(@Nullable TextureAtlasSprite sprite) {
		this(null, null);
		this.atlasSprite = sprite;
	}

	public AtlasTexture(@Nullable ResourceLocation sprite, @Nullable ResourceLocation atlas) {
		this.sprite = sprite;
		this.atlas  = atlas == null ? ItemParticlesAtlasManager.ATLAS_ID : atlas;
	}

	@Override
	public ResourceLocation getId() {
		return this.getSpriteNotNull();
	}

	@Override
	public void initialize() {
		this.atlasSprite = ItemParticlesAtlasManager.getInstance().getSprite(this.sprite, this.atlas);
	}

	@Override
	public void clear() {
		this.atlasSprite = null;
	}

	@NotNull
	public ResourceLocation getSpriteNotNull() {
		return this.sprite == null ? NO_SPRITE : this.sprite;
	}
}
