package com.keremc.achilles.util;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class SimpleFuture<V> {
	private V value;
	@NonNull private Callable<V> callable;

	public void start() {
		try {
			value = callable.call();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public V get() {
		while (value == null) {}

		return value;
	}
}
