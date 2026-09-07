package net.lopymine.itp.particle;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.lopymine.itp.atlas.ItemParticlesAtlasManager;
import net.lopymine.itp.config.particle.*;
import net.lopymine.itp.config.particle.ParticlePhysics.*;
import net.lopymine.itp.element.controller.modifier.speed.SpeedInAngleDirectionControllerModifier;
import net.lopymine.itp.element.controller.size.DynamicSizeController;
import net.lopymine.itp.element.controller.speed.*;
import net.lopymine.itp.element.inventory.AbstractItemParticle;
import net.lopymine.itp.element.spawner.SpawnContext;
import net.lopymine.itp.element.texture.AtlasTexture;
import net.lopymine.itp.element.texture.provider.ITextureProvider;
import net.lopymine.itp.manager.ItemParticleManager.ItemParticleRequest;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction.Axis;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.*;

public class ItemParticle extends AbstractItemParticle<ItemParticle> {

	private static final Map<Identifier, Layer> LAYERS_BY_ATLAS = new ConcurrentHashMap<>();

	protected ItemParticle(ClientLevel level, double x, double y, double z, TextureAtlasSprite sprite) {
		super(level, x, y, z, sprite);
	}

	@Nullable
	public static ItemParticle create(ItemParticleRequest request, double x, double y, double z) {
		ParticleConfig config = request.getConfig();
		SpawnContext context = request.getContext();
		if (config == null || context == null) {
			return null;
		}

		ItemParticle particle = new ItemParticle(Minecraft.getInstance().level, 0D, 0D, 0D, null);
		RandomSource random = particle.getRandom();

		int lifeTimeTicks = config.getLifeTimeTicks();
		particle.setLifeTimeTicks(lifeTimeTicks);
		ITextureProvider textureProvider = ITextureProvider.getTextureProvider(config, config.getTextures(), config.getAnimationSpeed(), config.getLifeTimeTicks(), config.getAnimationType());
		particle.setTextureProvider(textureProvider);
		particle.setElementTexture(textureProvider.getInitializationTexture(random));

		DynamicSizeController<ItemParticle> dynamicSizeController = new DynamicSizeController<>(config.getSize(), particle);
		particle.setDynamicSizeController(dynamicSizeController);

		particle.setX(x);
		particle.setLastX(x);

		particle.setY(y);
		particle.setLastY(y);

		particle.setZ(z);
		particle.setLastZ(z);

		ParticlePhysics physics = config.getPhysics();
		BasePhysics base = physics.getBase();
		RotationSpeedPhysics rotation = physics.getRotation();

		double standardParticleAngle = rotation.getParticleRotationConfig().getSpawnAngle().getRandom(random);
		particle.setStandardParticleAngle(standardParticleAngle);
		double standardParticleAzimuth = rotation.getParticleRotationConfig().getSpawnAzimuth().getRandom(random);
		particle.setStandardParticleAzimuth(standardParticleAzimuth);
		double standardTextureAngle = rotation.getTextureRotationConfig().getSpawnAngle().getRandom(random);
		particle.setStandardTextureAngle(standardTextureAngle);

		SpeedController<ItemParticle> xSpeedController = new SpeedController<>(base.getXzSpeed(), random, context.impulseX());
		particle.setXSpeedController(xSpeedController);
		xSpeedController.registerModifier(new SpeedInAngleDirectionControllerModifier<>(base.getAngleSpeed(), random, Axis.X), true, particle);

		SpeedController<ItemParticle> zSpeedController = new SpeedController<>(base.getXzSpeed(), random, context.impulseZ());
		particle.setZSpeedController(zSpeedController);
		zSpeedController.registerModifier(new SpeedInAngleDirectionControllerModifier<>(base.getAngleSpeed(), random, Axis.Z), true, particle);

		SpeedController<ItemParticle> ySpeedController = new SpeedController<>(base.getYSpeed(), random, context.impulseY());
		particle.setYSpeedController(ySpeedController);
		ySpeedController.registerModifier(new SpeedInAngleDirectionControllerModifier<>(base.getAngleSpeed(), random, Axis.Y), true, particle);

		MovementRotationSpeedController<ItemParticle> particleRotationSpeedController = new MovementRotationSpeedController<>(rotation.getParticleRotationConfig(), random);
		particle.setMovementRotationSpeedController(particleRotationSpeedController);
		TextureRotationSpeedController<ItemParticle> textureRotationSpeedController = new TextureRotationSpeedController<>(rotation.getTextureRotationConfig(), random);
		particle.setTextureRotationSpeedController(textureRotationSpeedController);

		particle.setColorController(request.getColorController());

		particle.setInitialized(true);
		return particle;
	}

	@Override
	protected ItemParticle getElement() {
		return this;
	}

	@Override
	@NotNull
	protected Layer getLayer() {
		Identifier atlasId = this.getElementTexture() instanceof AtlasTexture atlasTexture
				? atlasTexture.getAtlas()
				: ItemParticlesAtlasManager.ATLAS_ID;
		return LAYERS_BY_ATLAS.computeIfAbsent(atlasId, (id) -> new Layer(true, id, RenderPipelines.TRANSLUCENT_PARTICLE));
	}
}
