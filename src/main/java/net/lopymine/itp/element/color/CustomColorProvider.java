package net.lopymine.itp.element.color;

import lombok.*;
import net.lopymine.itp.client.ItemParticlesClient;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

@Getter
@Setter
public class CustomColorProvider implements IColorProvider {

	private final String original;
	private int color = -1;

	public CustomColorProvider(String original) {
		this.original = original;
	}

	@Override
	public IColorProvider copy() {
		return new CustomColorProvider(this.original);
	}

	@Override
	public int tick(RandomSource random) {
		return this.color;
	}

	@Override
	public void compile(ItemStack stack, RandomSource random) {
		try {
			String color = this.original.substring(1);
			if (color.length() == 6) {
				this.color = 0xFF000000 | Integer.parseInt(color, 16);
			} else if (color.length() == 8) {
				this.color = (int) Long.parseLong(color, 16);
			}
		} catch (Exception e) {
			ItemParticlesClient.LOGGER.error("Failed to parse custom color from \"{}\"! Reason:", this.original, e);
		}
	}

	@Override
	public String asString() {
		return "custom";
	}

	@Override
	public String toString() {
		return this.getString(this.color);
	}
}
