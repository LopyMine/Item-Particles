package net.lopymine.itp.entrypoint;

//? if fabric {

import net.fabricmc.api.ModInitializer;
import net.lopymine.itp.ItemParticles;

public class IPFabricEntrypoint implements ModInitializer {

	@Override
	public void onInitialize() {
		ItemParticles.onInitialize();
	}
}

//?}
