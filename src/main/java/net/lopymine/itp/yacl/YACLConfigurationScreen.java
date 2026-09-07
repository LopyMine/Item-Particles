package net.lopymine.itp.yacl;

import lombok.experimental.ExtensionMethod;
import net.lopymine.itp.ItemParticles;
import net.lopymine.itp.config.ItemParticlesConfig;
import net.lopymine.itp.yacl.category.*;
import net.lopymine.mossylib.yacl.api.SimpleYACLScreen;
import net.lopymine.mossylib.yacl.extension.SimpleOptionExtension;
import net.minecraft.client.gui.screens.Screen;

@ExtensionMethod(SimpleOptionExtension.class)
public class YACLConfigurationScreen {

	private YACLConfigurationScreen() {
		throw new IllegalStateException("Screen class");
	}

	public static Screen createScreen(Screen parent) {
		ItemParticlesConfig defConfig = ItemParticlesConfig.getNewInstance();
		ItemParticlesConfig config = ItemParticlesConfig.getInstance();

		Runnable onSave = () -> {
			config.getWhitelistsConfig().recompileAll();
			config.getFamilyGenerationConfig().recompileAll();
			config.saveAsync();
		};
		return SimpleYACLScreen.startBuilder(ItemParticles.MOD_ID, parent, onSave)
				.categories(GeneralCategory.get(defConfig, config))
				.categories(ParticlesSpawnCategory.get(defConfig, config))
				.build();
	}

}
