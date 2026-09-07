package net.lopymine.itp.element.texture;

import com.mojang.serialization.Codec;
import java.util.Locale;
import net.minecraft.util.StringRepresentable;

public enum TextureAnimationType implements StringRepresentable {

	STRETCH,
	ONETIME,
	LOOP,
	RANDOM,
	RANDOM_STATIC;

	public static final Codec<TextureAnimationType> CODEC = StringRepresentable.fromEnum(TextureAnimationType::values);

	@Override
	public String getSerializedName() {
		return this.name().toLowerCase(Locale.ROOT);
	}
}
