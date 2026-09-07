package net.lopymine.itp.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.File;
import java.util.concurrent.CompletableFuture;
import lombok.*;
import net.lopymine.itp.ItemParticles;
import net.lopymine.itp.config.sub.*;
import net.lopymine.mossylib.loader.MossyLoader;
import net.lopymine.mossylib.utils.*;
import org.slf4j.*;
import static net.lopymine.mossylib.utils.CodecUtils.option;

@Getter
@Setter
@AllArgsConstructor
public class ItemParticlesConfig {

	public static final Codec<ItemParticlesConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			option("sent_notice_message", false, Codec.BOOL, ItemParticlesConfig::isSentNoticeMessage),
			option("main", ItemParticlesMainConfig.getNewInstance(), ItemParticlesMainConfig.CODEC, ItemParticlesConfig::getMainConfig),
			option("particle", ItemParticleConfig.getNewInstance(), ItemParticleConfig.CODEC, ItemParticlesConfig::getParticleConfig),
			option("coefficients", ItemParticlesCoefficientsConfig.getNewInstance(), ItemParticlesCoefficientsConfig.CODEC, ItemParticlesConfig::getCoefficientsConfig),
			option("whitelists", ItemParticlesItemWhitelistsConfig.getNewInstance(), ItemParticlesItemWhitelistsConfig.CODEC, ItemParticlesConfig::getWhitelistsConfig),
			option("family_generation", ItemParticlesFamilyGenerationConfig.getNewInstance(), ItemParticlesFamilyGenerationConfig.CODEC, ItemParticlesConfig::getFamilyGenerationConfig),
			option("cache", ItemParticlesCacheConfig.getNewInstance(), ItemParticlesCacheConfig.CODEC, ItemParticlesConfig::getCacheConfig)
	).apply(instance, ItemParticlesConfig::new));

	private static final File CONFIG_FILE = MossyLoader.getConfigDir().resolve(ItemParticles.MOD_ID + ".json5").toFile();
	private static final Logger LOGGER = LoggerFactory.getLogger(ItemParticles.MOD_NAME + "/Config");
	private static ItemParticlesConfig INSTANCE;

	private boolean sentNoticeMessage;
	private ItemParticlesMainConfig mainConfig;
	private ItemParticleConfig particleConfig;
	private ItemParticlesCoefficientsConfig coefficientsConfig;
	private ItemParticlesItemWhitelistsConfig whitelistsConfig;
	private ItemParticlesFamilyGenerationConfig familyGenerationConfig;
	private ItemParticlesCacheConfig cacheConfig;

	private ItemParticlesConfig() {
		throw new IllegalArgumentException();
	}

	public static ItemParticlesConfig getInstance() {
		return INSTANCE == null ? reload() : INSTANCE;
	}

	public static ItemParticlesConfig reload() {
		return INSTANCE = read();
	}

	public static ItemParticlesConfig getNewInstance() {
		return CodecUtils.parseNewInstanceHacky(CODEC);
	}

	private static ItemParticlesConfig read() {
		return ConfigUtils.readConfig(CODEC, CONFIG_FILE, LOGGER);
	}

	public void saveAsync() {
		CompletableFuture.runAsync(this::save);
	}

	public void save() {
		ConfigUtils.saveConfig(this, CODEC, CONFIG_FILE, LOGGER);
	}
}
