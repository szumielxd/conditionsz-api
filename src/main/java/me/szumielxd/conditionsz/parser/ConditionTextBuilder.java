package me.szumielxd.conditionsz.parser;

import java.util.Map;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lombok.Getter;
import me.szumielxd.conditionsz.parser.DummyStringBuilder.ParseableText;
import me.szumielxd.conditionsz.parser.compare.ConditionOperation;
import me.szumielxd.conditionsz.parser.exception.ConditionszParseException;
import me.szumielxd.conditionsz.util.LimitedStringSequenceBuffer;

final class ConditionTextBuilder {
	
	public static final char STRING_CHAR = '"';
	
	private final @NotNull Map<Integer, Map<String, ConditionOperation>> operations;
	@Getter private boolean appendSpace = false;
	private final @NotNull DummyStringBuilder textBuilder = new DummyStringBuilder();
	@Getter private boolean markedForDone = false;
	private @NotNull LimitedStringSequenceBuffer operatorBuffer;
	
	public ConditionTextBuilder(@NotNull Map<Integer, Map<String, ConditionOperation>> operations, int maxOperatorLength) {
		this.operatorBuffer = new LimitedStringSequenceBuffer(maxOperatorLength);
		this.operations = operations;
	}
	
	public @NotNull boolean append(@NotNull String str) {
		if (this.appendSpace || !str.equals(" ")) {
			String toAppend = this.appendSpace ? str : this.operatorBuffer.append(str);
			if (toAppend != null) {
				if (isMarkedForDone()) {
					throwDoneException();
				}
				this.textBuilder.append(toAppend);
			}
			return true;
		}
		return false;
	}
	
	public @Nullable String peekOperatorChars(int length, boolean force) {
		return this.operatorBuffer.first(length, force);
	}
	
	public @Nullable ConditionOperation tryFetchOperation() {
		return this.tryFetchOperation(false);
	}
	
	public @Nullable ConditionOperation tryFetchOperation(boolean search) {
		for (var e : this.operations.entrySet()) {
			var operation = tryFetchOperationWithLength(e.getKey(), e.getValue(), search);
			if (operation != null) {
				return operation;
			}
		}
		return null;
	}
	
	private @Nullable ConditionOperation tryFetchOperationWithLength(int size, Map<String, ConditionOperation> operations, boolean search) {
		int maxOffset = search ? this.operatorBuffer.length() - size : 0;
		for (int i = 0; i <= maxOffset; i++) {
			String operatorString = this.peekOperatorChars(size + i, search);
			if (operatorString != null) {
				operatorString = operatorString.substring(i);
				var operation = operations.get(operatorString);
				if (operation != null) {
					Stream.of(this.operatorBuffer.subarray(0, i)).forEachOrdered(this.textBuilder::append);
					this.operatorBuffer.skip(size + i);
					return operation;
				}
			}
		}
		return null;
	}
	
	public @NotNull String[] getAndClearBufferLeft() {
		var array = this.operatorBuffer.array();
		this.operatorBuffer.reset();
		return array;
	}
	
	public void toggleAppendSpace() {
		if (this.isMarkedForDone()) {
			throwDoneException();
		}
		if (!this.appendSpace) {
			if (!this.isEmpty()) {
				throw new ConditionszParseException("Cannot toggle on space appendance after appending other character");
			}
			this.appendSpace = true;
		} else {
			this.appendSpace = false;
			this.markedForDone = true;
		}
	}
	
	public boolean isEmpty() {
		return this.textBuilder.isEmpty()
				&& this.operatorBuffer.isEmpty();
	}
	
	private void throwDoneException() {
		throw new ConditionszParseException("Cannot perform any modificational operations after string close bracket");
	}
	
	public @NotNull DummyString build() {
		if (this.appendSpace) {
			throw new ConditionszParseException("Cannot build condition with unclosed space appendance");
		}
		Stream.of(this.operatorBuffer.array()).forEachOrdered(this.textBuilder::append);
		if (!this.isMarkedForDone() && this.textBuilder.isEmpty()) {
			throw new ConditionszParseException("Detected empty string as condition side. If you wish to compare empty text, put it between `%s` parenthesis"
					.formatted(ConditionBuilder.STRING_CHAR));
		}
		return new DummyString(this.textBuilder.build(), this.isMarkedForDone());
	}
	
	record DummyString(@NotNull ParseableText text, boolean quote) {
		@Override
		public String toString() {
			return quote ? '"' + text().toString() + '"' : text().toString();
		}
	}

}
