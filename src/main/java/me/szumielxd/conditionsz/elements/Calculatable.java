package me.szumielxd.conditionsz.elements;

import java.util.Map;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public interface Calculatable extends Predicate<UnaryOperator<String>> {
	
	default boolean test() {
		return test(UnaryOperator.identity());
	}
	
	default boolean test(Map<String, String> replacements) {
		return test(replacements::get);
	}

}
