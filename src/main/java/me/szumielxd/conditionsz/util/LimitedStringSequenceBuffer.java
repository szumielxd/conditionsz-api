package me.szumielxd.conditionsz.util;

import java.util.Optional;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class LimitedStringSequenceBuffer {
	
	private final String[] chars;
	
	private int length = 0;
	private int nextIndex = 0;
	
	public LimitedStringSequenceBuffer(int maxLength) {
		this.chars = new String[maxLength];
	}
	
	public @Nullable String append(@NotNull String ch) {
		if (this.length < chars.length) {
			this.length++;
		}
		int index = nextIndex++ % chars.length;
		String old = this.chars[index];
		this.chars[index] = ch;
		return old;
	}
	
	public void append(@NotNull String... arr) {
		Stream.of(arr)
				.skip(Math.max(arr.length - chars.length, 0))
				.forEach(this::append);
	}
	
	public int length() {
		return this.length;
	}
	
	public boolean isEmpty() {
		return this.length == 0;
	}
	
	public int maxLength() {
		return this.chars.length;
	}
	
	public void reset() {
		this.length = 0;
	}
	
	public void skip(int skip) {
		if (this.length < skip) {
			throw new IllegalArgumentException("`skip` cannot be bigger than length");
		}
		this.length -= skip;
	}
	
	public @Nullable String last(int charCount) {
		if (charCount > this.length) {
			return null;
		}
		String[] result = new String[charCount];
		int startLimit = chars.length + nextIndex;
		for (int i = 1; i <= charCount; i++) {
			result[charCount - i] = chars[(startLimit - i) % chars.length];
		}
		return String.join("", result);
	}
	
	public @NotNull String[] subarray(int start) {
		return subarray(start, this.length);
	}
	
	public @NotNull String[] subarray(int start, int end) {
		if (end > this.length) {
			throw new IllegalArgumentException("`end` cannot be bigger than length");
		}
		if (end < start) {
			throw new IllegalArgumentException("`end` cannot be smaller than `start`");
		}
		int charCount = end - start;
		int startLimit = chars.length + nextIndex - (this.length - end);
		if (charCount < 1) {
			return new String[0];
		}
		String[] result = new String[charCount];
		for (int i = 1; i <= charCount; i++) {
			result[charCount - i] =
					chars[(startLimit - i) % chars.length];
		}
		return result;
	}
	
	public @NotNull String[] array() {
		return this.subarray(0);
	}
	
	public @Nullable String first(int charCount) {
		return first(charCount, false);
	}
	
	public @Nullable String first(int charCount, boolean ignoreLength) {
		if (charCount > this.length) {
			return null;
		}
		if (!ignoreLength && this.length < this.chars.length) {
			return null;
		}
		String[] result = new String[charCount];
		int firstIndex = (this.nextIndex - this.length) % chars.length;
		for (int i = 0; i < charCount; i++) {
			result[i] = chars[(firstIndex + i) % chars.length];
		}
		return String.join("", result);
	}
	
	@Override
	public String toString() {
		return Optional.ofNullable(last(this.length)).orElse("");
	}

}
