package me.szumielxd.conditionsz.elements;

import java.util.function.UnaryOperator;

import org.jetbrains.annotations.NotNull;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ConditionsGroup implements Calculatable {
	
	private final @NotNull LogicalOperation operation;
	private final @NotNull Calculatable leftCond;
	private final @NotNull Calculatable rightCond;
	
	@Override
	public boolean test(UnaryOperator<String> replacer) {
		return operation.apply(leftCond, rightCond).test(replacer);
	}
	
	public @NotNull String toString() {
		String left = leftCond.toString();
		String right = rightCond.toString();
		if (rightCond instanceof ConditionsGroup) {
			right = "(" + right + ")";
			if (leftCond instanceof ConditionsGroup) {
				left = "(" + left + ")";
			}
		}
		return left + " " + operation.getOperator() + " " + right;
	}

}
