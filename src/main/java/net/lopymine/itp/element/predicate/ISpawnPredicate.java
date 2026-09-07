package net.lopymine.itp.element.predicate;

import net.minecraft.world.item.ItemStack;

public interface ISpawnPredicate {

	boolean test(ItemStack stack);

}
