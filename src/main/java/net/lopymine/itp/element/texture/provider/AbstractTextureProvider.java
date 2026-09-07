package net.lopymine.itp.element.texture.provider;

import java.util.List;
import lombok.*;
import net.lopymine.itp.atlas.ItemParticlesAtlasManager;
import net.lopymine.itp.element.base.TickElement;
import net.lopymine.itp.element.texture.*;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;

@Setter
@Getter
@AllArgsConstructor
public abstract class AbstractTextureProvider extends TickElement implements ITextureProvider {

	protected List<ITexture> textures;
	protected double animationSpeed;
	protected int lifeTime;
	protected boolean shouldDead;

	@Nullable
	protected ITexture missingTexture;

	public AbstractTextureProvider(List<ITexture> textures, double animationSpeed, int lifeTime, boolean shouldDead) {
		this.textures       = textures;
		this.animationSpeed = animationSpeed;
		this.lifeTime       = lifeTime;
		this.shouldDead     = shouldDead;
	}

	public AbstractTextureProvider(List<ITexture> textures, double animationSpeed, int lifeTime) {
		this(textures, animationSpeed, lifeTime, false);
	}

	@Override
	public ITexture getInitializationTexture(RandomSource random) {
		if (this.textures.isEmpty()) {
			return this.getMissingSprite();
		}
		return this.getInitializationTextureFromNotEmptyTextures(random);
	}

	private ITexture getMissingSprite() {
		if (this.missingTexture == null) {
			this.missingTexture = new AtlasTexture(ItemParticlesAtlasManager.getInstance().getMissingSprite());
		}
		return this.missingTexture;
	}

	protected abstract ITexture getInitializationTextureFromNotEmptyTextures(RandomSource random);

	@Override
	public ITexture getTexture(RandomSource random) {
		if (this.textures.isEmpty()) {
			return this.getMissingSprite();
		}
		return this.getTextureFromNotEmptyTextures(random);
	}

	protected abstract ITexture getTextureFromNotEmptyTextures(RandomSource random);

	protected void markDead() {
		this.shouldDead = true;
	}
}
