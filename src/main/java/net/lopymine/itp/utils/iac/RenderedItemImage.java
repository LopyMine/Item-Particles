package net.lopymine.itp.utils.iac;

import java.util.*;
import lombok.*;
import net.minecraft.resources.Identifier;

@Getter
@Setter
@AllArgsConstructor
public class RenderedItemImage {

	private ArrayList<Pixel> colors;

	public int getColor(int anotherColor) {
		return anotherColor;
	}

	public record Pixel(Identifier texture, int x, int y, int color) {

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof Pixel(Identifier texture1, int x1, int y1, int color1))) return false;
			return x() == x1 && y() == y1 && color() == color1 && Objects.equals(texture(), texture1);
		}

		@Override
		public int hashCode() {
			return Objects.hash(texture(), x(), y(), color());
		}
	}

}
