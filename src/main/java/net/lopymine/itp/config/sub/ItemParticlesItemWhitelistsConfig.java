package net.lopymine.itp.config.sub;

import com.mojang.serialization.Codec;
import java.util.*;
import java.util.function.Supplier;
import lombok.*;
import net.lopymine.itp.ItemParticles;
import net.lopymine.itp.client.ItemParticlesClient;
import net.lopymine.itp.element.spawner.SpawnCategory;
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
public class ItemParticlesItemWhitelistsConfig {

	public static final Codec<ItemParticlesItemWhitelistsConfig> CODEC = create((instance) -> instance.group(
			option("first_person_config", ParticlesItemWhitelistConfig.getNewInstance(), ParticlesItemWhitelistConfig.CODEC, ItemParticlesItemWhitelistsConfig::getFirstPersonConfig),
			option("third_person_config", ParticlesItemWhitelistConfig.getNewInstance(), ParticlesItemWhitelistConfig.CODEC, ItemParticlesItemWhitelistsConfig::getThirdPersonConfig),
			option("dropped_item_config", ParticlesItemWhitelistConfig.getNewInstance(), ParticlesItemWhitelistConfig.CODEC, ItemParticlesItemWhitelistsConfig::getDroppedItemConfig),
			option("item_frame_config", ParticlesItemWhitelistConfig.getNewInstance(), ParticlesItemWhitelistConfig.CODEC, ItemParticlesItemWhitelistsConfig::getItemFrameConfig),
			option("shelf_config", ParticlesItemWhitelistConfig.getNewInstance(), ParticlesItemWhitelistConfig.CODEC, ItemParticlesItemWhitelistsConfig::getShelfConfig)
	).apply(instance, ItemParticlesItemWhitelistsConfig::new));

	private ParticlesItemWhitelistConfig firstPersonConfig;
	private ParticlesItemWhitelistConfig thirdPersonConfig;
	private ParticlesItemWhitelistConfig droppedItemConfig;
	private ParticlesItemWhitelistConfig itemFrameConfig;
	private ParticlesItemWhitelistConfig shelfConfig;

	public static Supplier<ItemParticlesItemWhitelistsConfig> getNewInstance() {
		return () -> CodecUtils.parseNewInstanceHacky(CODEC);
	}

	public ParticlesItemWhitelistConfig getConfig(SpawnCategory category) {
		return switch (category) {
			case FIRST_PERSON -> this.firstPersonConfig;
			case THIRD_PERSON -> this.thirdPersonConfig;
			case DROPPED_ITEM -> this.droppedItemConfig;
			case ITEM_FRAME -> this.itemFrameConfig;
			case SHELF -> this.shelfConfig;
		};
	}

	public void recompileAll() {
		for (SpawnCategory category : SpawnCategory.values()) {
			this.getConfig(category).getCompiledItems(true);
		}
	}

	public enum Mode implements StringRepresentable, EnumWithText {

		DISABLED,
		WHITELIST,
		BLACKLIST;

		public static final Codec<Mode> CODEC = StringRepresentable.fromEnum(Mode::values);

		@Override
		public @NotNull String getSerializedName() {
			return this.name().toLowerCase(Locale.ROOT);
		}

		@Override
		public Component getText() {
			return ItemParticles.text("modmenu.option.whitelist_mode." + this.getSerializedName());
		}
	}

	@Getter
	@Setter
	@AllArgsConstructor
	public static class ParticlesItemWhitelistConfig {

		public static final Codec<ParticlesItemWhitelistConfig> CODEC = create((instance) -> instance.group(
				option("mode", Mode.DISABLED, Mode.CODEC, ParticlesItemWhitelistConfig::getMode),
				option("items", "", Codec.STRING, ParticlesItemWhitelistConfig::getItems)
		).apply(instance, ParticlesItemWhitelistConfig::new));

		private Mode mode;
		private String items;
		private int[] compiledItems;

		public ParticlesItemWhitelistConfig(Mode mode, String items) {
			this.mode  = mode;
			this.items = items;
		}

		public static Supplier<ParticlesItemWhitelistConfig> getNewInstance() {
			return () -> CodecUtils.parseNewInstanceHacky(CODEC);
		}

		public int[] getCompiledItems() {
			return this.getCompiledItems(false);
		}

		public int[] getCompiledItems(boolean recompile) {
			if (this.compiledItems != null && !recompile) {
				return this.compiledItems;
			}

			List<Integer> items = new ArrayList<>();

			if (this.items.isEmpty()) {
				return this.compiledItems = new int[0];
			}

			for (String item : this.items.split(" ")) {
				try {
					Identifier itemId = ItemParticles.parseId(item);
					//? if >=1.21.2 {
					Optional<Reference<Item>> optional = BuiltInRegistries.ITEM.get(itemId);
					if (optional.isEmpty()) {
						ItemParticlesClient.LOGGER.warn("Invalid item in whitelist: " + item);
						continue;
					}
					int id = BuiltInRegistries.ITEM.getId(optional.get().value());
					//?} else {
					/*int id = BuiltInRegistries.ITEM.getId(BuiltInRegistries.ITEM.get(itemId));
					 *///?}
					if (id == -1) {
						ItemParticlesClient.LOGGER.warn("Failed to find item in whitelist: " + item);
						continue;
					}
					items.add(id);
				} catch (Exception e) {
					ItemParticlesClient.LOGGER.warn("Invalid item in whitelist: " + item);
				}
			}

			return this.compiledItems = items.stream().mapToInt(Integer::intValue).toArray();
		}

		public boolean cannotProcess(Item item) {
			if (this.mode == Mode.DISABLED) {
				return false;
			}

			int itemId = BuiltInRegistries.ITEM.getId(item);

			for (int compiledItem : this.getCompiledItems()) {
				if (compiledItem == itemId) {
					return this.mode != Mode.WHITELIST;
				}
			}

			return this.mode != Mode.BLACKLIST;
		}
	}

}
