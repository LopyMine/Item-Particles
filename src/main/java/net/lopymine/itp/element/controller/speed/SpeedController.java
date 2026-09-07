package net.lopymine.itp.element.controller.speed;

import net.lopymine.itp.config.speed.SpeedConfig;
import net.lopymine.itp.element.base.IMovableElement;
import net.minecraft.util.RandomSource;

public class SpeedController<E extends IMovableElement> extends AbstractSpeedController<SpeedController<E>, E> {

	public SpeedController(SpeedConfig config, RandomSource random, double impulse) {
		super(config, random, impulse * config.getImpulseInheritCoefficient());
	}

	@Override
	protected SpeedController<E> getController() {
		return this;
	}
}
