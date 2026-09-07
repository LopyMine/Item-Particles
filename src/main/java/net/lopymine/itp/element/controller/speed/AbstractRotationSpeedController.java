package net.lopymine.itp.element.controller.speed;

import lombok.*;
import net.lopymine.itp.config.particle.ParticlePhysics.RotationSpeedPhysics.RotationConfig;
import net.lopymine.itp.element.base.*;
import net.minecraft.util.RandomSource;

@Getter
@Setter
public abstract class AbstractRotationSpeedController<C extends AbstractRotationSpeedController<C, T>, T extends IRotatableElement&IMovableElement> extends AbstractSpeedController<C, T> {

	private boolean rotateInMovementDirection;
	private double rotation;

	public AbstractRotationSpeedController(RotationConfig config, RandomSource random) {
		super(config.getSpeedConfig(), random, 0.0D);
		this.rotateInMovementDirection = config.isRotateInMovementDirection();
	}

	@Override
	public void tick(T element) {
		super.tick(element);

		if (!this.rotateInMovementDirection) {
			return;
		}

		double deltaX = element.getX() - element.getLastX();
		double deltaY = element.getY() - element.getLastY();
		double deltaZ = element.getZ() - element.getLastZ();

		if (deltaX == 0.0D && deltaY == 0.0D && deltaZ == 0.0D) {
			return;
		}

		this.updateRotation(element, deltaX, deltaY, deltaZ);
	}

	protected abstract void updateRotation(T element, double deltaX, double deltaY, double deltaZ);

}
