package net.lopymine.itp.element.spawner;

import com.mojang.serialization.Codec;
import java.util.Locale;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum SpawnCategory implements StringRepresentable {

	FIRST_PERSON,
	THIRD_PERSON,
	DROPPED_ITEM,
	ITEM_FRAME,
	SHELF;

	public static final Codec<SpawnCategory> CODEC = StringRepresentable.fromEnum(SpawnCategory::values);

	@Override
	public @NotNull String getSerializedName() {
		return this.name().toLowerCase(Locale.ROOT);
	}
}
