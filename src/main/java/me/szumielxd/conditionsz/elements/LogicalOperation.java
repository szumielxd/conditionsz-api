package me.szumielxd.conditionsz.elements;

import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum LogicalOperation implements BiFunction<Calculatable, Calculatable, Calculatable> {
	
	OR("||", (t, u) -> (r -> t.test(r) || u.test(r))),
	AND("&&", (t, u) -> (r -> t.test(r) && u.test(r))),
	XOR("^^", (t, u) -> (r -> t.test(r) != u.test(r))),
	NOR("~|", (t, u) -> (r -> !t.test(r) && !u.test(r))),
	NAND("~&", (t, u) -> (r -> !t.test(r) || !u.test(r)));
	
	@Getter private final @NotNull String operator;
	private final @NotNull BiFunction<Calculatable, Calculatable, Calculatable> resultCalculator;

	@Override
	public @NotNull Calculatable apply(@NotNull Calculatable t, @NotNull Calculatable u) {
		return resultCalculator.apply(t, u);
	}
	
	private static final Map<String, LogicalOperation> OPERATIONS_BY_OPERATOR = Stream.of(values())
			.collect(Collectors.toMap(LogicalOperation::getOperator, Function.identity()));
	
	public static @Nullable LogicalOperation getByOperator(@NotNull String operator) {
		return OPERATIONS_BY_OPERATOR.get(Objects.requireNonNull(operator, "operator"));
	}

}
