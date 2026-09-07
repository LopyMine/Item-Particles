package net.lopymine.itp.config.sub;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Supplier;
import lombok.*;
import net.lopymine.mossylib.utils.CodecUtils;
import static net.lopymine.mossylib.utils.CodecUtils.option;

@Getter
@Setter
@AllArgsConstructor
public class ItemParticlesMainConfig {

	public static final Codec<ItemParticlesMainConfig> CODEC = RecordCodecBuilder.create(inst -> inst.group(
			option("mod_enabled", true, Codec.BOOL, ItemParticlesMainConfig::isModEnabled),
			option("debug_mode_enabled", false, Codec.BOOL, ItemParticlesMainConfig::isDebugModeEnabled),
			option("nbt_debug_mode_enabled", false, Codec.BOOL, ItemParticlesMainConfig::isNbtDebugModeEnabled)
	).apply(inst, ItemParticlesMainConfig::new));

	private boolean modEnabled;
	private boolean debugModeEnabled;
	private boolean nbtDebugModeEnabled;

	public static Supplier<ItemParticlesMainConfig> getNewInstance() {
		return () -> CodecUtils.parseNewInstanceHacky(CODEC);
	}
}
