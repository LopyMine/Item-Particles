package net.lopymine.itp.element.controller.modifier;

import net.lopymine.itp.element.controller.IController;

@SuppressWarnings("unused")
public interface IControllerModifier<C extends IController<E>, E> {

	void modify(C controller, E element);

	void tick(E element);

}
