package net.lopymine.itp.family.generation.batch;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

@Getter
public class BatchResult<T> {

	@Nullable
	private volatile T value;
	private volatile boolean ready;

	public void complete(@Nullable T value) {
		this.value = value;
		this.ready = true;
	}
}
