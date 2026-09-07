package net.lopymine.itp.family;

import com.mojang.serialization.Codec;
import java.util.*;
import lombok.Getter;
import net.lopymine.itp.ItemParticles;
import net.lopymine.itp.client.ItemParticlesClient;
import net.lopymine.itp.resourcepack.manager.AbstractConfigsManager;
import net.lopymine.mossylib.logger.MossyLogger;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

@Getter
public class FamilyParticlesConfigManager extends AbstractConfigsManager<FamilyParticleConfig> {

	public static final Set<Identifier> FALLBACK_CONFIG_IDS = Set.of(
			ItemParticles.id("ifamilies/fallback/standard.json"),
			ItemParticles.id("ifamilies/fallback/standard.json5")
	);
	private static final FamilyParticlesConfigManager INSTANCE = new FamilyParticlesConfigManager();
	private final Map<Identifier, FamilyParticleConfig> registeredConfigsMap = new HashMap<>();
	private final List<FamilyParticleConfig> registeredConfigs = new ArrayList<>();

	public static FamilyParticlesConfigManager getInstance() {
		return INSTANCE;
	}

	@Override
	protected String getFolderName() {
		return "ifamilies";
	}

	@Override
	protected Codec<FamilyParticleConfig> getCodec() {
		return FamilyParticleConfig.CODEC;
	}

	@Override
	protected String getConfigName() {
		return "particles family";
	}

	@Override
	protected MossyLogger getLogger() {
		return ItemParticlesClient.LOGGER;
	}

	@Override
	protected String getModId() {
		return ItemParticles.MOD_ID;
	}

	@Override
	protected void registerConfig(FamilyParticleConfig config, Identifier id) {
		config.setLocation(id);
		this.registeredConfigsMap.computeIfAbsent(id, (key) -> config);
		this.registeredConfigs.add(config);
	}

	@Override
	public void reload() {
		this.registeredConfigsMap.clear();
		this.registeredConfigs.clear();
		super.reload();
		this.getFallbackConfig();
		this.registeredConfigs.sort(Comparator.comparingInt(FamilyParticleConfig::getPriority).reversed());
	}

	@NotNull
	public FamilyParticleConfig getFallbackConfig() {
		for (Identifier id : FALLBACK_CONFIG_IDS) {
			FamilyParticleConfig fallbacks = this.registeredConfigsMap.get(id);
			if (fallbacks == null) {
				continue;
			}
			return fallbacks;
		}
		throw new IllegalArgumentException("Failed to find fallback family config for Item Particles!");
	}

}
