package net.lopymine.itp.element.color.advanced;

import lombok.*;
import net.lopymine.itp.element.base.TickElement;

@Getter
@Setter
public abstract class AbstractAdvancedColorProviderWithPeriod extends TickElement implements IAdvancedColorProvider {

	protected float speed;
	protected float changeColorTick;

	public AbstractAdvancedColorProviderWithPeriod(float speed) {
		this.speed = speed;
	}

	public void updateChangeColorTick() {
		this.changeColorTick = this.ticks + this.speed;
	}

}
