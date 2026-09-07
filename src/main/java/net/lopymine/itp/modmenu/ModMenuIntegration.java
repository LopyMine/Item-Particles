package net.lopymine.itp.modmenu;

import net.lopymine.itp.ItemParticles;
import net.lopymine.itp.yacl.YACLConfigurationScreen;
import net.lopymine.mossylib.modmenu.AbstractModMenuIntegration;
import net.minecraft.client.gui.screens.Screen;

public class ModMenuIntegration extends AbstractModMenuIntegration {

	@Override
	protected String getModId() {
		return ItemParticles.MOD_ID;
	}

	@Override
	protected Screen createConfigScreen(Screen screen) {
		return YACLConfigurationScreen.createScreen(screen);
	}
}
