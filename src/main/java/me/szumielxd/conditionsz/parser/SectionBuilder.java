package me.szumielxd.conditionsz.parser;

import java.util.LinkedList;
import java.util.Queue;

import org.jetbrains.annotations.NotNull;

import lombok.RequiredArgsConstructor;
import me.szumielxd.conditionsz.elements.Calculatable;
import me.szumielxd.conditionsz.elements.ConditionsGroup;
import me.szumielxd.conditionsz.elements.LogicalOperation;
import me.szumielxd.conditionsz.parser.exception.ConditionszParseException;

@RequiredArgsConstructor
public final class SectionBuilder {
	
	private final @NotNull Queue<Object> stack = new LinkedList<>();
	
	public @NotNull SectionBuilder append(@NotNull LogicalOperation operation) {
		if (this.stack.size() % 2 == 0) {
			throw new ConditionszParseException("cannot append operation now, calculatable expected");
		}
		this.stack.offer(operation);
		return this;
	}
	
	public @NotNull SectionBuilder append(@NotNull Calculatable calculatable) {
		if (this.stack.size() % 2 == 1) {
			throw new ConditionszParseException("cannot append calculatable now, operation expected (%s)".formatted(this.stack));
		}
		this.stack.offer(calculatable);
		return this;
	}
	
	public @NotNull Calculatable build() {
		if (this.stack.size() % 2 == 0) {
			throw new ConditionszParseException("last appended element wasn't calculatable (%d)".formatted(this.stack.size()));
		}
		var leftCalc = (Calculatable) this.stack.poll();
		while (!this.stack.isEmpty()) {
			var operation = (LogicalOperation) this.stack.poll();
			var rightCalc = (Calculatable) this.stack.poll();
			leftCalc = new ConditionsGroup(operation, leftCalc, rightCalc);
		}
		return leftCalc;
	}
	
	

}
