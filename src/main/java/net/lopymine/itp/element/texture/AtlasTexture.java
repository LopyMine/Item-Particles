package net.lopymine.itp.element.texture;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.*;
import net.lopymine.itp.ItemParticles;
import net.lopymine.itp.atlas.*;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.*;
import static net.lopymine.mossylib.utils.CodecUtils.option;

@Getter
@Setter
public class AtlasTexture implements ITexture {

	public static final Identifier NO_SPRITE = ItemParticles.id("no_sprite");

	public static final Codec<AtlasTexture> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
			option("sprite", NO_SPRITE, Identifier.CODEC, AtlasTexture::getSpriteNotNull),
			option("atlas", ItemParticlesAtlasManager.ATLAS_ID, Identifier.CODEC, AtlasTexture::getAtlas)
	).apply(instance, AtlasTexture::new));

	@Nullable
	private Identifier sprite;
	@NotNull
	private Identifier atlas;

	@Nullable
	private TextureAtlasSprite atlasSprite;
	private boolean missingSprite;

	public AtlasTexture(@Nullable TextureAtlasSprite sprite) {
		this(null, null);
		this.atlasSprite   = sprite;
		this.missingSprite = MissingSpritesReporter.isMissing(sprite);
	}

	public AtlasTexture(@Nullable Identifier sprite, @Nullable Identifier atlas) {
		this.sprite = sprite;
		this.atlas  = atlas == null ? ItemParticlesAtlasManager.ATLAS_ID : atlas;
	}

	@Override
	public Identifier getId() {
		return this.getSpriteNotNull();
	}

	@Override
	public void initialize() {
		ItemParticlesAtlasManager manager = ItemParticlesAtlasManager.getInstance();
		this.atlas = manager.resolveAtlasId(this.sprite, this.atlas);
		this.atlasSprite = manager.getSprite(this.sprite, this.atlas);
		this.missingSprite = MissingSpritesReporter.isMissing(this.atlasSprite);
	}

	@Override
	public void clear() {
		this.atlasSprite   = null;
		this.missingSprite = false;
	}

	@NotNull
	public Identifier getSpriteNotNull() {
		return this.sprite == null ? NO_SPRITE : this.sprite;
	}
}
