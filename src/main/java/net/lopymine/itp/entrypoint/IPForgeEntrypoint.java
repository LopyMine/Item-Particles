package net.lopymine.itp.entrypoint;

//? if forge {
/*import net.lopymine.ip.ItemParticles;
import net.lopymine.mossylib.MossyLib;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

@Mod(ItemParticles.MOD_ID)
public class IPForgeEntrypoint {

	public IPForgeEntrypoint() {
		ItemParticles.onInitialize();
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> IPForgeClientEntrypoint::onInitializeClient);
	}

}

*///?}
