package net.lopymine.itp.element.controller.modifier.speed;

import net.lopymine.itp.element.base.IMovableElement;
import net.lopymine.itp.element.controller.modifier.IControllerModifier;
import net.lopymine.itp.element.controller.speed.AbstractSpeedController;

public interface ISpeedControllerModifier<C extends AbstractSpeedController<C, E>, E extends IMovableElement> extends IControllerModifier<C, E> {

	double getImpulse(E element);

	double getAcceleration(E element);

	double getBraking(E element);

	double getTurbulence(E element);

	@Override
	default void modify(C controller, E element) {
	}

	@Override
	default void tick(E element) {
	}
}
