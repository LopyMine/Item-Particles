package net.lopymine.itp.config.sub;

import com.mojang.serialization.Codec;
import java.util.*;
import java.util.function.Supplier;
import lombok.*;
import net.lopymine.itp.ItemParticles;
import net.lopymine.itp.client.ItemParticlesClient;
import net.lopymine.mossylib.utils.CodecUtils;
import net.lopymine.mossylib.yacl.utils.EnumWithText;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;
import static com.mojang.serialization.codecs.RecordCodecBuilder.create;
import static net.lopymine.mossylib.utils.CodecUtils.option;

@Getter
@Setter
@AllArgsConstructor
public class ItemParticlesFamilyGenerationConfig {

	public static final Codec<ItemParticlesFamilyGenerationConfig> CODEC = create((instance) -> instance.group(
			option("mods_mode", Mode.BLACKLIST, Mode.CODEC, ItemParticlesFamilyGenerationConfig::getModsMode),
			option("mods", "", Codec.STRING, ItemParticlesFamilyGenerationConfig::getMods),
			option("items_mode", Mode.BLACKLIST, Mode.CODEC, ItemParticlesFamilyGenerationConfig::getItemsMode),
			option("items", "", Codec.STRING, ItemParticlesFamilyGenerationConfig::getItems)
	).apply(instance, ItemParticlesFamilyGenerationConfig::new));

	private Mode modsMode;
	private String mods;
	private Mode itemsMode;
	private String items;
	private Set<String> compiledMods;
	private Set<Integer> compiledItems;

	public ItemParticlesFamilyGenerationConfig(Mode modsMode, String mods, Mode itemsMode, String items) {
		this.modsMode  = modsMode;
		this.mods      = mods;
		this.itemsMode = itemsMode;
		this.items     = items;
	}

	public static Supplier<ItemParticlesFamilyGenerationConfig> getNewInstance() {
		return () -> CodecUtils.parseNewInstanceHacky(CODEC);
	}

	public void recompileAll() {
		this.getCompiledMods(true);
		this.getCompiledItems(true);
	}

	public Set<String> getCompiledMods() {
		return this.getCompiledMods(false);
	}

	public Set<String> getCompiledMods(boolean recompile) {
		if (this.compiledMods != null && !recompile) {
			return this.compiledMods;
		}

		Set<String> namespaces = new HashSet<>();
		if (!this.mods.isEmpty()) {
			for (String mod : this.mods.split(" ")) {
				if (!mod.isEmpty()) {
					namespaces.add(mod.trim());
				}
			}
		}

		return this.compiledMods = namespaces;
	}

	public Set<Integer> getCompiledItems() {
		return this.getCompiledItems(false);
	}

	public Set<Integer> getCompiledItems(boolean recompile) {
		if (this.compiledItems != null && !recompile) {
			return this.compiledItems;
		}

		Set<Integer> ids = new HashSet<>();
		if (this.items.isEmpty()) {
			return this.compiledItems = ids;
		}

		for (String item : this.items.split(" ")) {
			if (item.isEmpty()) {
				continue;
			}
			try {
				Identifier itemId = ItemParticles.parseId(item.trim());
				//? if >=1.21.2 {
				Optional<Reference<Item>> optional = BuiltInRegistries.ITEM.get(itemId);
				if (optional.isEmpty()) {
					ItemParticlesClient.LOGGER.warn("Invalid item in family generation list: " + item);
					continue;
				}
				int id = BuiltInRegistries.ITEM.getId(optional.get().value());
				//?} else {
				/*int id = BuiltInRegistries.ITEM.getId(BuiltInRegistries.ITEM.get(itemId));
				 *///?}
				if (id == -1) {
					ItemParticlesClient.LOGGER.warn("Failed to find item in family generation list: " + item);
					continue;
				}
				ids.add(id);
			} catch (Exception e) {
				ItemParticlesClient.LOGGER.warn("Invalid item in family generation list: " + item);
			}
		}

		return this.compiledItems = ids;
	}

	public boolean canGenerateFor(Item item) {
		Identifier itemId = BuiltInRegistries.ITEM.getKey(item);

		boolean modListed = this.getCompiledMods().contains(itemId.getNamespace());
		if ((this.modsMode == Mode.WHITELIST) != modListed) {
			return false;
		}

		boolean itemListed = this.getCompiledItems().contains(BuiltInRegistries.ITEM.getId(item));
		return (this.itemsMode == Mode.WHITELIST) == itemListed;
	}

	public enum Mode implements StringRepresentable, EnumWithText {

		BLACKLIST,
		WHITELIST;

		public static final Codec<Mode> CODEC = StringRepresentable.fromEnum(Mode::values);

		@Override
		public @NotNull String getSerializedName() {
			return this.name().toLowerCase(Locale.ROOT);
		}

		@Override
		public Component getText() {
			return ItemParticles.text("modmenu.option.family_generation_mode." + this.getSerializedName());
		}
	}
}
