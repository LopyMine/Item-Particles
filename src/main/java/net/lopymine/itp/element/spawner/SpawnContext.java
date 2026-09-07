package net.lopymine.itp.element.spawner;

import net.minecraft.world.item.ItemStack;

public record SpawnContext(ItemStack stack, SpawnCategory category, double impulseX, double impulseY, double impulseZ) {

}
