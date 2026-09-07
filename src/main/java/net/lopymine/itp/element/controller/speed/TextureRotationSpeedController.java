package net.lopymine.itp.element.controller.speed;

import java.lang.Math;
import lombok.*;
import net.lopymine.itp.config.particle.ParticlePhysics.RotationSpeedPhysics.RotationConfig;
import net.lopymine.itp.element.base.*;
import net.minecraft.util.RandomSource;
import org.joml.*;

@Getter
@Setter
public class TextureRotationSpeedController<T extends IRotatableElement&IMovableElement&IBillboardElement> extends AbstractRotationSpeedController<TextureRotationSpeedController<T>, T> {

	public TextureRotationSpeedController(RotationConfig config, RandomSource random) {
		super(config, random);
	}

	@Override
	protected void updateRotation(T element, double deltaX, double deltaY, double deltaZ) {
		Quaternionf facing = element.getFacingRotation(new Quaternionf());

		if (facing == null) {
			return;
		}

		Vector3f movement = facing.conjugate().transform((float) deltaX, (float) deltaY, (float) deltaZ, new Vector3f());

		if (movement.x() == 0.0F && movement.y() == 0.0F) {
			return;
		}

		this.setRotation(Math.toDegrees(Math.atan2(movement.y(), movement.x())) - 90.0D);
	}

	@Override
	protected TextureRotationSpeedController<T> getController() {
		return this;
	}

}
