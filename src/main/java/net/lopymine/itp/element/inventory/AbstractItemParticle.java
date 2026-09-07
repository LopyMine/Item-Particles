package net.lopymine.itp.element.inventory;

import java.lang.Math;
import lombok.*;
import net.lopymine.itp.config.ItemParticlesConfig;
import net.lopymine.itp.element.base.*;
import net.lopymine.itp.element.controller.IController;
import net.lopymine.itp.element.controller.color.ColorController;
import net.lopymine.itp.element.controller.size.DynamicSizeController;
import net.lopymine.itp.element.controller.speed.*;
import net.lopymine.itp.element.size.StaticSize;
import net.lopymine.itp.element.texture.*;
import net.lopymine.itp.element.texture.provider.ITextureProvider;
import net.lopymine.itp.manager.FirstPersonParticleRenderer;
import net.lopymine.itp.utils.ArgbUtils2;
import net.lopymine.mossylib.utils.ArgbUtils;
import net.minecraft.client.*;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
//? if >=26.1 {
/*import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
*///?} else {
import net.minecraft.client.renderer.state.QuadParticleRenderState;
//?}
import net.minecraft.util.*;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.*;
import org.joml.*;

@Setter
@Getter
public abstract class AbstractItemParticle<E extends AbstractItemParticle<E>> extends SingleQuadParticle implements IElement, IRotatableElement, IRepaintable, IRandomizable, IResizableElement, IBillboardElement {

	public static float SIZE_SCALE = 1.0F / 64.0F;
	public static float SPEED_SCALE = 1.0F / 24.0F;
	public static double HAND_DEPTH_BIAS = 0.005D;
	public static double FIRST_PERSON_FACING_OFFSET = 0.0D;

	protected boolean initialized = false;
	private double standardParticleAngle;
	private double standardTextureAngle;
	private ITextureProvider textureProvider;
	private ColorController<E> colorController;
	private DynamicSizeController<E> dynamicSizeController;
	private SpeedController<E> xSpeedController;
	private SpeedController<E> ySpeedController;
	private SpeedController<E> zSpeedController;
	private MovementRotationSpeedController<E> movementRotationSpeedController;
	private TextureRotationSpeedController<E> textureRotationSpeedController;
	private IController<E>[] controllers;
	private ITexture elementTexture;

	private boolean spawnedInHand;
	private double handDepth;

	private double lastWidth = StaticSize.STANDARD_SIZE.getWidth();
	private double lastHeight = StaticSize.STANDARD_SIZE.getHeight();
	private double width = StaticSize.STANDARD_SIZE.getWidth();
	private double height = StaticSize.STANDARD_SIZE.getHeight();

	private double lastParticleAngle;
	private double particleAngle;
	private double standardParticleAzimuth;
	private double particleAzimuth;
	private double lastTextureAngle;
	private double textureAngle;

	protected AbstractItemParticle(ClientLevel level, double x, double y, double z, TextureAtlasSprite sprite) {
		super(level, x, y, z, sprite);
		this.hasPhysics = true;
		this.gravity    = 0.0F;
		this.friction   = 1.0F;
	}

	protected abstract E getElement();

	@Override
	public void tick() {
		if (!this.isInitialized()) {
			return;
		}

		if (this.isDead()) {
			return;
		}

		this.age++;

		this.textureProvider.tick();
		this.setElementTexture(this.textureProvider.getTexture(this.random));

		if (this.textureProvider.isShouldDead() || this.age > this.getLifeTimeTicks()) {
			this.setDead(true);
			return;
		}

		if (this.colorController != null) {
			this.colorController.tick(this.getElement());
		}

		this.dynamicSizeController.tick(this.getElement());

		if (!this.onGround) {
			this.movementRotationSpeedController.tick(this.getElement());
			this.lastParticleAngle = this.particleAngle;
			if (this.movementRotationSpeedController.isRotateInMovementDirection()) {
				this.particleAngle   = this.movementRotationSpeedController.getRotation();
				this.particleAzimuth = this.movementRotationSpeedController.getAzimuth();
			} else {
				this.particleAngle = (this.particleAngle + this.movementRotationSpeedController.getSpeed()) % 360F;
			}

			this.textureRotationSpeedController.tick(this.getElement());
			this.lastTextureAngle = this.textureAngle;
			if (this.textureRotationSpeedController.isRotateInMovementDirection()) {
				this.textureAngle = this.textureRotationSpeedController.getRotation();
			} else {
				this.textureAngle = (this.textureAngle + this.textureRotationSpeedController.getSpeed()) % 360F;
			}

			this.oRoll = (float) Math.toRadians(this.lastTextureAngle);
			this.roll  = this.oRoll + (float) Math.toRadians(Mth.wrapDegrees(this.textureAngle - this.lastTextureAngle));
		} else {
			this.lastParticleAngle = this.particleAngle;
			this.lastTextureAngle  = this.textureAngle;
			this.oRoll             = this.roll;
		}

		this.xSpeedController.tick(this.getElement());
		this.ySpeedController.tick(this.getElement());
		this.zSpeedController.tick(this.getElement());

		this.processCustomControllers();

		this.xd = this.xSpeedController.getSpeed() * SPEED_SCALE;
		this.yd = this.ySpeedController.getSpeed() * -SPEED_SCALE;
		this.zd = this.zSpeedController.getSpeed() * SPEED_SCALE;

		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;

		this.move(this.xd, this.yd, this.zd);

		this.applyConfigTransparency();
	}

	protected void processCustomControllers() { }

	@Override
	@SuppressWarnings("NullableProblems")
	public void extract(QuadParticleRenderState state, Camera camera, float tickProgress) {
		if (this.isInFrontOfHand(camera, tickProgress)) {
			return;
		}

		if (!this.spawnedInHand) {
			super.extract(state, camera, tickProgress);
			return;
		}

		Quaternionf rotation = this.getBillboardRotation(camera, tickProgress, new Quaternionf());
		if (this.roll != 0.0F) {
			rotation.rotateZ(Mth.lerp(tickProgress, this.oRoll, this.roll));
		}
		this.extractRotatedQuad(state, camera, rotation, tickProgress);
	}

	public Quaternionf getBillboardRotation(Camera camera, float tickProgress, Quaternionf dest) {
		if (!this.spawnedInHand) {
			return dest.set(camera.rotation());
		}

		Vec3 cameraPos = camera.position();
		Vector3fc forward = camera.forwardVector();

		Vector3f normal = new Vector3f(
				(float) (Mth.lerp(tickProgress, this.xo, this.x) - cameraPos.x() - forward.x() * FIRST_PERSON_FACING_OFFSET),
				(float) (Mth.lerp(tickProgress, this.yo, this.y) - cameraPos.y() - forward.y() * FIRST_PERSON_FACING_OFFSET),
				(float) (Mth.lerp(tickProgress, this.zo, this.z) - cameraPos.z() - forward.z() * FIRST_PERSON_FACING_OFFSET)
		);

		if (normal.lengthSquared() < 1.0E-8F) {
			return dest.set(camera.rotation());
		}

		normal.normalize();
		if (normal.dot(forward) > 0.0F) {
			normal.negate();
		}
		return dest.rotationTo(new Vector3f(forward).negate(), normal).mul(camera.rotation());
	}

	public boolean isInFrontOfHand(Camera camera, float tickProgress) {
		if (!this.spawnedInHand || !Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
			return false;
		}

		double depth = FirstPersonParticleRenderer.getViewDepth(
				camera.position(),
				Mth.lerp(tickProgress, this.xo, this.x),
				Mth.lerp(tickProgress, this.yo, this.y),
				Mth.lerp(tickProgress, this.zo, this.z)
		);

		return depth < this.handDepth - HAND_DEPTH_BIAS;
	}

	public void extractInHandSpace(QuadParticleRenderState particleTypeRenderState, Camera camera, float tickProgress, Matrix4f levelToHand, float sizeScale) {
		Quaternionf rotation = this.getBillboardRotation(camera, tickProgress, new Quaternionf());

		if (this.roll != 0.0F) {
			rotation.rotateZ(Mth.lerp(tickProgress, this.oRoll, this.roll));
		}

		Vec3 cameraPos = camera.position();
		Vector3f position = levelToHand.transformPosition(new Vector3f(
				(float) (Mth.lerp(tickProgress, this.xo, this.x) - cameraPos.x()),
				(float) (Mth.lerp(tickProgress, this.yo, this.y) - cameraPos.y()),
				(float) (Mth.lerp(tickProgress, this.zo, this.z) - cameraPos.z())
		));

		particleTypeRenderState.add(
				this.getLayer(),
				position.x(),
				position.y(),
				position.z(),
				rotation.x,
				rotation.y,
				rotation.z,
				rotation.w,
				this.getQuadSize(tickProgress) * sizeScale,
				this.getU0(),
				this.getU1(),
				this.getV0(),
				this.getV1(),
				ARGB.colorFromFloat(this.alpha, this.rCol, this.gCol, this.bCol),
				//? if >=26.1 {
				/*this.getLightCoords(tickProgress)
				*///?} else {
				this.getLightColor(tickProgress)
				//?}
		);
	}

	@Override
	@Nullable
	public Quaternionf getFacingRotation(Quaternionf quaternion) {
		//? if >=26.2 {
		/*Camera camera = Minecraft.getInstance().gameRenderer.mainCamera();
		*///?} else {
		Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
		 //?}

		if (!camera.isInitialized()) {
			return null;
		}

		return this.getBillboardRotation(camera, 1.0F, quaternion);
	}

	@Override
	public float getQuadSize(float tickProgress) {
		return (float) Mth.lerp(tickProgress, this.lastWidth, this.width) * SIZE_SCALE;
	}

	public void setElementTexture(@NotNull ITexture texture) {
		this.elementTexture = texture;
		if (texture instanceof AtlasTexture atlasTexture && atlasTexture.getAtlasSprite() != null) {
			this.setSprite(atlasTexture.getAtlasSprite());
		}
	}

	protected void applyConfigTransparency() {
		int color = this.getRenderColor();
		this.setAlpha(ArgbUtils2.getAlpha(color) / 255.0F);
	}

	@SuppressWarnings("unused")
	protected int getRenderColor() {
		if (!this.isInitialized()) {
			return -1;
		}
		int color = this.getColor();
		int alpha = ArgbUtils.getAlpha(color);
		int configAlpha = (int) (ItemParticlesConfig.getInstance().getParticleConfig().getParticleTransparency() * 255F);
		if (alpha <= configAlpha) {
			return color;
		}
		return ArgbUtils2.getArgb(configAlpha, ArgbUtils2.getRed(color), ArgbUtils2.getGreen(color), ArgbUtils2.getBlue(color));
	}

	public double getAngle() {
		return this.standardParticleAngle + this.particleAngle;
	}

	@Override
	public void setAngle(double degrees) {
		this.particleAngle = degrees;
	}

	@Override
	public double getAzimuth() {
		return this.standardParticleAzimuth + this.particleAzimuth;
	}

	@Override
	public void setAzimuth(double degrees) {
		this.particleAzimuth = degrees;
	}

	@Override
	public void setWidth(double width) {
		this.lastWidth = this.width;
		this.width     = width;
		this.quadSize  = (float) width;
	}

	@Override
	public void setHeight(double height) {
		this.lastHeight = this.height;
		this.height     = height;
	}

	@Override
	public RandomSource getRandom() {
		return this.random;
	}

	@Override
	public int getTicks() {
		return this.age;
	}

	public int getLifeTimeTicks() {
		return this.getLifetime();
	}

	public void setLifeTimeTicks(int lifeTimeTicks) {
		this.setLifetime(lifeTimeTicks);
	}

	@Override
	public boolean isDead() {
		return this.removed;
	}

	public void setDead(boolean dead) {
		if (dead) {
			this.remove();
		}
	}

	@Override
	public double getX() {
		return this.x;
	}

	@Override
	public void setX(double x) {
		this.setPos(x, this.y, this.z);
	}

	@Override
	public double getY() {
		return this.y;
	}

	@Override
	public void setY(double y) {
		this.setPos(this.x, y, this.z);
	}

	public double getZ() {
		return this.z;
	}

	public void setZ(double z) {
		this.setPos(this.x, this.y, z);
	}

	@Override
	public double getLastX() {
		return this.xo;
	}

	@Override
	public void setLastX(double x) {
		this.xo = x;
	}

	@Override
	public double getLastY() {
		return this.yo;
	}

	@Override
	public void setLastY(double y) {
		this.yo = y;
	}

	public double getLastZ() {
		return this.zo;
	}

	@Override
	public void setLastZ(double z) {
		this.zo = z;
	}

	@Override
	public double getSpeedX() {
		return this.xd;
	}

	@Override
	public void setSpeedX(double speedX) {
		this.xd = speedX;
	}

	@Override
	public double getSpeedY() {
		return this.yd;
	}

	@Override
	public void setSpeedY(double speedY) {
		this.yd = speedY;
	}

	public double getSpeedZ() {
		return this.zd;
	}

	public void setSpeedZ(double speedZ) {
		this.zd = speedZ;
	}

	@Override
	public int getColor() {
		return ArgbUtils2.getArgb(
				Math.round(this.alpha * 255.0F),
				Math.round(this.rCol * 255.0F),
				Math.round(this.gCol * 255.0F),
				Math.round(this.bCol * 255.0F)
		);
	}

	@Override
	public void setColor(int color) {
		this.setColor(
				ArgbUtils2.getRed(color) / 255.0F,
				ArgbUtils2.getGreen(color) / 255.0F,
				ArgbUtils2.getBlue(color) / 255.0F
		);
		this.setAlpha(ArgbUtils2.getAlpha(color) / 255.0F);
	}
}
