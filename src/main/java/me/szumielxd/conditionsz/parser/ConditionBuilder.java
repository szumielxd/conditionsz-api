package me.szumielxd.conditionsz.parser;

import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lombok.AllArgsConstructor;
import me.szumielxd.conditionsz.elements.Calculatable;
import me.szumielxd.conditionsz.parser.ConditionTextBuilder.DummyString;
import me.szumielxd.conditionsz.parser.compare.ConditionOperation;
import me.szumielxd.conditionsz.parser.exception.ConditionszParseException;

public final class ConditionBuilder {
	
	public static final char STRING_CHAR = '"';
	
	private final @NotNull Map<Integer, Map<String, ConditionOperation>> operations; // (operatorLength, operatorName) -> condition
	private @Nullable DummyString leftText = null;
	private final int maxOperatorLength;
	
	private @NotNull ConditionTextBuilder currentTextBuilder;
	private @Nullable ConditionOperation operator;
	
	public ConditionBuilder(@NotNull Map<Integer, Map<String, ConditionOperation>> operations) {
		this.operations = operations;
		this.maxOperatorLength = operations.keySet().stream()
				.mapToInt(Integer::intValue)
				.max()
				.orElse(0);
		this.currentTextBuilder = new ConditionTextBuilder(this.operations, this.maxOperatorLength);
	}
	
	public @NotNull ConditionBuilder append(@NotNull String str) {
		if (this.currentTextBuilder.append(str)) {
			this.checkForOperator(false);
		}
		return this;
	}
	
	private void checkForOperator(boolean search) {
		var operation = this.currentTextBuilder.tryFetchOperation(search);
		if (operation != null) {
			this.setOperator(operation);
		}
	}
	
	public void toggleAppendSpace() {
		if (!this.currentTextBuilder.isEmpty() && !this.isAppendSpace()) {
			this.checkForOperator(true);
		}
		this.currentTextBuilder.toggleAppendSpace();
	}
	
	public boolean isAppendSpace() {
		return this.currentTextBuilder.isAppendSpace();
	}
	
	public boolean isEmpty() {
		return this.leftText == null && this.currentTextBuilder.isEmpty();
	}
	
	public @NotNull Calculatable build() {
		if (this.operator == null) {
			this.checkForOperator(true);
		}
		var left = this.leftText;
		var right = this.currentTextBuilder.build();
		if (left == null) {
			throw new ConditionszParseException("left side of condition cannot be empty");
		}
		return new DummyCondition(left, right, this.operator);
	}
	
	private void setOperator(@NotNull ConditionOperation operator) {
		if (this.operator != null) {
			throw new ConditionszParseException("Single condition must have one operator, no more. (given: `%s`, current: `%s`)".formatted(operator, this.operator));
		}
		this.operator = operator;
		var toAppend = this.currentTextBuilder.getAndClearBufferLeft();
		this.leftText = this.currentTextBuilder.build();
		this.currentTextBuilder = new ConditionTextBuilder(this.operations, this.maxOperatorLength);
		Stream.of(toAppend).forEachOrdered(this.currentTextBuilder::append);
	}
	
	@AllArgsConstructor
	private class DummyCondition implements Calculatable {

		private final @NotNull DummyString left;
		private final @NotNull DummyString right;
		private final @NotNull ConditionOperation operation;
		
		@Override
		public boolean test(UnaryOperator<String> replacer) {
			return this.operation.test(
					left.text().parse(replacer),
					right.text().parse(replacer));
		}
		
		@Override
		public String toString() {
			return this.left + " " + operation.getOperator() + " " + this.right;
		}
		
	}

}
