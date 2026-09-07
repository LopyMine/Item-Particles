package net.lopymine.itp.config.sub;

import com.mojang.serialization.Codec;
import java.util.Locale;
import java.util.function.Supplier;
import lombok.*;
import net.lopymine.itp.ItemParticles;
import net.lopymine.mossylib.utils.CodecUtils;
import net.lopymine.mossylib.yacl.utils.EnumWithText;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import static com.mojang.serialization.codecs.RecordCodecBuilder.create;
import static net.lopymine.mossylib.utils.CodecUtils.option;

@Getter
@Setter
@AllArgsConstructor
public class ItemParticlesCacheConfig {

	public static final Codec<ItemParticlesCacheConfig> CODEC = create((instance) -> instance.group(
			option("invalidate_mode", CacheInvalidateMode.MANUAL_INVALIDATE, CacheInvalidateMode.CODEC, ItemParticlesCacheConfig::getInvalidateMode)
	).apply(instance, ItemParticlesCacheConfig::new));

	private CacheInvalidateMode invalidateMode;

	public static Supplier<ItemParticlesCacheConfig> getNewInstance() {
		return () -> CodecUtils.parseNewInstanceHacky(CODEC);
	}

	public enum CacheInvalidateMode implements StringRepresentable, EnumWithText {

		AFTER_RESOURCE_RELOADING,
		AFTER_GAME_LAUNCH,
		MANUAL_INVALIDATE;

		public static final Codec<CacheInvalidateMode> CODEC = StringRepresentable.fromEnum(CacheInvalidateMode::values);

		@Override
		public String getSerializedName() {
			return this.name().toLowerCase(Locale.ROOT);
		}

		@Override
		public Component getText() {
			return ItemParticles.text("modmenu.option.invalidate_mode." + this.getSerializedName());
		}
	}
}
