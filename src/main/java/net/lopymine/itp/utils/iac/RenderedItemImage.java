package net.lopymine.itp.utils.iac;

import java.util.*;
import lombok.*;
import net.minecraft.resources.ResourceLocation;

@Getter
@Setter
@AllArgsConstructor
public class RenderedItemImage {

	private ArrayList<Pixel> colors;

	public int getColor(int anotherColor) {
		return anotherColor;
	}

	public record Pixel(ResourceLocation texture, int x, int y, int color) {

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof Pixel pixel)) return false;
			return x() == pixel.x() && y() == pixel.y() && color() == pixel.color() && Objects.equals(texture(), pixel.texture());
		}

		@Override
		public int hashCode() {
			return Objects.hash(texture(), x(), y(), color());
		}
	}

}
