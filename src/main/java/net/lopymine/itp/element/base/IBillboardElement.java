package net.lopymine.itp.element.base;

import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

public interface IBillboardElement {

	@Nullable
	Quaternionf getFacingRotation(Quaternionf quaternion);

}
