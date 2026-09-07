package net.lopymine.itp.element.controller.speed;

import lombok.*;
import net.lopymine.itp.config.particle.ParticlePhysics.RotationSpeedPhysics.RotationConfig;
import net.lopymine.itp.element.base.*;
import net.minecraft.util.RandomSource;

@Getter
@Setter
public class MovementRotationSpeedController<T extends IRotatableElement&IMovableElement> extends AbstractRotationSpeedController<MovementRotationSpeedController<T>, T> {

	private double azimuth;

	public MovementRotationSpeedController(RotationConfig config, RandomSource random) {
		super(config, random);
	}

	@Override
	protected void updateRotation(T element, double deltaX, double deltaY, double deltaZ) {
		double horizontal = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
		this.setRotation(Math.toDegrees(Math.atan2(horizontal, deltaY)));
		if (horizontal == 0.0D) {
			return;
		}
		this.azimuth = Math.toDegrees(Math.atan2(deltaZ, deltaX));
	}

	@Override
	protected MovementRotationSpeedController<T> getController() {
		return this;
	}

}
