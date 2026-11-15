package net.lopymine.itp.resourcepack.manager;

import com.google.gson.JsonParser;
import com.mojang.serialization.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import net.lopymine.mossylib.logger.MossyLogger;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.*;

public abstract class AbstractConfigsManager<C> {

	protected abstract String getFolderName();

	protected abstract String getLegacyFolderName();

	protected abstract Codec<C> getCodec();

	protected abstract String getModId();

	protected abstract String getConfigName();

	protected abstract MossyLogger getLogger();

	protected abstract void registerConfig(C config, Identifier id);

	public String getFolderPath() {
		return "%s/%s".formatted(this.getModId(), this.getFolderName());
	}

	public void reload() {
		this.getLogger().info("Started registration {} from resources...", this.getConfigName().toUpperCase(Locale.ROOT));
		ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();

		AtomicInteger foundConfigs = new AtomicInteger();
		AtomicInteger registeredConfigs = new AtomicInteger();

		Map<Identifier, Resource> resources = new LinkedHashMap<>();
		Set<String> legacyLayoutPacks = new LinkedHashSet<>();

		resourceManager.listResources(this.getLegacyFolderName(), (id) -> isConfig(id) && id.getNamespace().equals(this.getModId()))
				.forEach((id, resource) -> {
					resources.put(this.normalizeLegacyId(id), resource);
					legacyLayoutPacks.add(resource.sourcePackId());
				});

		resourceManager.listResources(this.getFolderPath(), AbstractConfigsManager::isConfig)
				.forEach(resources::put);

		this.warnAboutLegacyLayout(legacyLayoutPacks);

		resources.forEach((id, resource) -> {
			foundConfigs.getAndIncrement();

			try (InputStream inputStream = resource.open();
			     BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
				C config = this.getCodec().decode(JsonOps.INSTANCE, JsonParser.parseReader(reader))/*? if >=1.20.5 {*/.getOrThrow()/*?} else {*//*.getOrThrow(false, this.getLogger()::error)*//*?}*/.getFirst();
				this.registerConfig(config, id);
				this.getLogger().debug("Registered {} at \"{}\"", this.getConfigName(), id);
				registeredConfigs.getAndIncrement();
			} catch (Exception e) {
				this.getLogger().error("Failed to parse {} from \"{}\"! Reason:", this.getConfigName(), id, e);
			}
		});

		this.getLogger().info("Registration finished, found: {}, registered: {}", foundConfigs.get(), registeredConfigs.get());
	}

	private void warnAboutLegacyLayout(Set<String> packs) {
		if (packs.isEmpty()) {
			return;
		}
		this.getLogger().warn(
				"Found {} in the legacy assets layout (\"{}\"), move them to \"{}\". Packs: {}",
				this.getConfigName(),
				this.getLegacyFolderName(),
				this.getFolderPath(),
				String.join(", ", packs)
		);
	}

	private Identifier normalizeLegacyId(Identifier id) {
		return id.withPath("%s/%s".formatted(this.getFolderPath(), id.getPath().substring(this.getLegacyFolderName().length() + 1)));
	}

	private static boolean isConfig(Identifier id) {
		return id.getPath().endsWith(".json5") || id.getPath().endsWith(".json");
	}
}
