//~ client_fabric_commands

package net.lopymine.itp.client.command;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.lopymine.itp.ItemParticles;
import net.lopymine.itp.client.command.tags.TagsCommand;
import static net.lopymine.mossylib.utils.CommandUtils.literal;

public class ItemParticlesCommandManager {

	public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
		dispatcher.register(literal(ItemParticles.MOD_ID.replace("_", "-"))
				.then(TagsCommand.get()));
	}

}
