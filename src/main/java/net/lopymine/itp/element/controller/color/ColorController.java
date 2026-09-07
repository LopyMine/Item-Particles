package net.lopymine.itp.element.controller.color;

import lombok.*;
import net.lopymine.itp.element.base.*;
import net.lopymine.itp.element.color.IColorProvider;
import net.lopymine.itp.element.controller.IController;

@Getter
@Setter
public class ColorController<I extends IRepaintable&IRandomizable> implements IController<I> {

	private IColorProvider colorType;

	public ColorController(IColorProvider colorType) {
		this.colorType = colorType;
	}

	@Override
	public void tick(I element) {
		element.setColor(this.colorType.tick(element.getRandom()));
	}
}
