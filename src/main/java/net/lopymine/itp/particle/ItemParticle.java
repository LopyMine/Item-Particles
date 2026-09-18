package net.lopymine.itp.particle;

import java.util.*;
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
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
//? if >=1.21.9 {
/*import net.minecraft.client.renderer.RenderPipelines;
*///?} else {
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
//?}
import net.minecraft.core.Direction.Axis;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.*;

public class ItemParticle extends AbstractItemParticle<ItemParticle> {

	//? if >=1.21.9 {
	/*private static final Map<ResourceLocation, Layer> LAYERS_BY_ATLAS = new ConcurrentHashMap<>();
	*///?} else {
	private static final Map<ResourceLocation, ParticleRenderType> RENDER_TYPES_BY_ATLAS = new ConcurrentHashMap<>();
	//?}

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

	private ResourceLocation getAtlasId() {
		return this.getElementTexture() instanceof AtlasTexture atlasTexture
				? atlasTexture.getAtlas()
				: ItemParticlesAtlasManager.ATLAS_ID;
	}

	//? if >=1.21.9 {
	/*@Override
	@NotNull
	protected Layer getLayer() {
		return LAYERS_BY_ATLAS.computeIfAbsent(this.getAtlasId(), (id) -> new Layer(true, id, RenderPipelines.TRANSLUCENT_PARTICLE));
	}
	*///?} else {
	@Override
	@NotNull
	public ParticleRenderType getRenderType() {
		return RENDER_TYPES_BY_ATLAS.computeIfAbsent(this.getAtlasId(), ItemParticle::createRenderType);
	}

	private static ParticleRenderType createRenderType(ResourceLocation atlasId) {
		ParticleRenderType type = new ParticleRenderType() {

			@Override
			public BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
				RenderSystem.depthMask(true);
				RenderSystem.setShaderTexture(0, atlasId);
				RenderSystem.enableBlend();
				RenderSystem.defaultBlendFunc();
				return tesselator.begin(Mode.QUADS, DefaultVertexFormat.PARTICLE);
			}

			@Override
			public String toString() {
				return atlasId.toString();
			}
		};

		ArrayList<ParticleRenderType> list = new ArrayList<>(ParticleEngine.RENDER_ORDER);
		int index = list.indexOf(ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT);
		list.add(index >= 0 ? index + 1 : list.size() - 1, type);
		ParticleEngine.RENDER_ORDER = list;

		return type;
	}
	//?}
}
