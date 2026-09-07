package net.lopymine.itp.t2o;

import net.lopymine.itp.utils.ArgbUtils2;

public interface Texture2ObjectPixelFilter {

	Texture2ObjectPixelFilter NOT_TRANSPARENT = () -> (x, y, imageWidth, imageHeight, color) -> ArgbUtils2.getAlpha(color) != 0;

	Texture2Object<Boolean> getFilter();

}
