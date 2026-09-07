package net.lopymine.itp.element.controller.modifier.speed;

import lombok.*;
import net.lopymine.itp.config.speed.SpeedConfig;
import net.lopymine.itp.element.base.*;
import net.lopymine.itp.element.controller.speed.SpeedController;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.RandomSource;

@Getter
@Setter
public class SpeedInAngleDirectionControllerModifier<E extends IMovableElement&IRotatableElement> implements ISpeedControllerModifier<SpeedController<E>, E> {

	private SpeedConfig config;
	private RandomSource random;
	private Axis axis;

	public SpeedInAngleDirectionControllerModifier(SpeedConfig config, RandomSource random, Axis axis) {
		this.config = config;
		this.random = random;
		this.axis   = axis;
	}

	@Override
	public double getImpulse(E element) {
		return this.calc(this.config.getImpulseBidirectional(this.random), element);
	}

	@Override
	public double getAcceleration(E element) {
		return this.calc(this.config.getAccelerationBidirectional(this.random), element);
	}

	@Override
	public double getBraking(E element) {
		return Math.abs(this.calc(this.config.getBraking(), element));
	}

	@Override
	public double getTurbulence(E element) {
		return this.calc(this.config.getTurbulence().getRandom(this.random), element);
	}

	public double calc(double multiplier, E element) {
		if (multiplier == 0.0D) {
			return 0.0D;
		}

		double angle = Math.toRadians(element.getAngle());

		if (this.axis == Axis.Y) {
			return -Math.cos(angle) * multiplier;
		}

		double horizontal = Math.sin(angle) * multiplier;
		double azimuth = Math.toRadians(element.getAzimuth());

		return (this.axis == Axis.X ? Math.cos(azimuth) : Math.sin(azimuth)) * horizontal;
	}
}
