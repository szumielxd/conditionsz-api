package me.szumielxd.conditionsz.parser.compare;

import java.util.function.BiPredicate;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class ConditionOperation implements BiPredicate<String, String> {
	
	@Getter private final @NotNull String operator;
	
	protected @Nullable Number parseNumber(@NotNull String text) {
		try {
			return Long.parseLong(text);
		} catch (NumberFormatException e) {
			// ignore
		}
		try {
			return Double.parseDouble(text);
		} catch (NumberFormatException e) {
			// ignore
		}
		return null;
	}
	
	/**
	 * @implNote Both numbers are of the same type
	 * 
	 * @param left
	 * @param right
	 * @return
	 */
	protected final @Nullable Pair<Number> parseNumberPair(@NotNull String left, @NotNull String right) {
		try {
			return new Pair<>(Long.parseLong(left), Long.parseLong(right)); // both numbers are integer
		} catch (NumberFormatException e) {
			// ignore
		}
		try {
			return new Pair<>(Double.parseDouble(left), Double.parseDouble(right)); // both numbers are small enough to be double
		} catch (NumberFormatException e) {
			// ignore
		}
		return null;
	}
	
	protected record Pair<T>(@NotNull T left, @NotNull T right) {}

}
