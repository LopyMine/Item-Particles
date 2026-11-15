package net.lopymine.itp.element.base;

import lombok.*;

@Getter
@Setter
public class TickElement implements ITickElement {

	protected int ticks;

	public void tickElement() {
		this.ticks++;
		if (this.ticks == Integer.MAX_VALUE) {
			this.ticks = 0;
		}
	}

}
