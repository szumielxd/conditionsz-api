package me.szumielxd.conditionsz.parser;

import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

import lombok.RequiredArgsConstructor;
import me.szumielxd.conditionsz.parser.exception.ConditionszParseException;

final class DummyStringBuilder {
	
	private static final char STRING_CHARACTER = '\"';
	private static final char ESCAPE_CHARACTER = '\\';
	private static final char SPACE_CHARACTER = ' ';
	private static final char NEWLINE_CHARACTER = '\n';
	private static final char PLACEHOLDER_OPEN_CHARACTER = '{';
	private static final char PLACEHOLDER_CLOSE_CHARACTER = '}';
	private static final @NotNull String ESCAPED_CHARS = "" + STRING_CHARACTER + ESCAPE_CHARACTER + PLACEHOLDER_OPEN_CHARACTER;
	private static final @NotNull String FULL_ESCAPED_CHARS = ESCAPED_CHARS + PLACEHOLDER_CLOSE_CHARACTER;
	private static final @NotNull String NEWLINE_ESCAPE = "\\n";
	
	private StringBuilder stringBuilder = new StringBuilder();
	private Deque<ParseableText> texts = new LinkedList<>();
	private boolean placeholderMode = false;
	
	public void append(@NotNull String str) {
		if (str.length() == 1) {
			switch (str.charAt(0)) {
				case PLACEHOLDER_OPEN_CHARACTER -> {
					this.buildText();
					this.placeholderMode = true;
					return;
				}
				case PLACEHOLDER_CLOSE_CHARACTER -> {
					this.buildText();
					this.placeholderMode = false;
					return;
				}
				case SPACE_CHARACTER -> this.ensureConstantText();
			}
		}
		if (str.length() > 0 && str.charAt(0) == ESCAPE_CHARACTER) {
			if (str.startsWith(NEWLINE_ESCAPE)) {
				str = str.substring(2) + NEWLINE_CHARACTER;
			} else {
				str = str.substring(1);
			}
			if (str.isEmpty()) {
				throw new ConditionszParseException("Found unescaped `%s` character".formatted(ESCAPE_CHARACTER));
			}
		}
		this.stringBuilder.append(str);
	}
	
	public ParseableText build() {
		this.ensureConstantText();
		this.buildText();
		return switch (this.texts.size()) {
			case 0 -> new ConstantText("");
			case 1 -> this.texts.peek();
			default -> new ComboText(this.texts);
		};
	}
	
	public boolean isEmpty() {
		return this.stringBuilder.isEmpty() && this.texts.isEmpty();
	}
	
	
	private void buildText() {
		if (this.placeholderMode) {
			this.texts.add(new PlaceholderText(this.stringBuilder.toString()));
		} else {
			if (!stringBuilder.isEmpty()) {
				this.texts.add(new ConstantText(this.stringBuilder.toString()));
			}
		}
		this.stringBuilder = new StringBuilder();
	}
	
	private void ensureConstantText() {
		if (this.placeholderMode) {
			this.placeholderMode = false;
			this.stringBuilder.insert(0, PLACEHOLDER_OPEN_CHARACTER); // someone probably forgot to close placeholder brackets. Treat as plain text
		}
	}
	
	
	private static @NotNull String escapeString(@NotNull String str) {
		return escapeString(str, false);
	}
	
	private static @NotNull String escapeString(@NotNull String str, boolean full) {
		StringBuilder sb = new StringBuilder();
		char[] chars = str.toCharArray();
		String escaped = full ? FULL_ESCAPED_CHARS : ESCAPED_CHARS;
		for (int i = 0; i < chars.length; i++) {
			if (escaped.indexOf(chars[i]) != -1) {
				sb.append(ESCAPE_CHARACTER);
			}
			if (chars[i] == NEWLINE_CHARACTER) {
				sb.append(NEWLINE_ESCAPE);
			} else {
				sb.append(chars[i]);
			}
		}
		return sb.toString();
	}
	
	private static @NotNull String toPlaceholder(@NotNull String str) {
		return PLACEHOLDER_OPEN_CHARACTER + str + PLACEHOLDER_CLOSE_CHARACTER;
	}
	
	
	public interface ParseableText {
		
		public @NotNull String parse(@NotNull UnaryOperator<String> replacer);
		
	}
	
	@RequiredArgsConstructor
	public class ConstantText implements ParseableText {

		private final @NotNull String text;
		
		@Override
		public @NotNull String parse(@NotNull UnaryOperator<String> replacer) {
			return this.text;
		}
		
		@Override
		public String toString() {
			return escapeString(this.text);
		}
		
	}
	
	@RequiredArgsConstructor
	public class PlaceholderText implements ParseableText {

		private final @NotNull String placeholder;
		
		@Override
		public @NotNull String parse(@NotNull UnaryOperator<String> replacer) {
			var result = replacer.apply(this.placeholder);
			return result != null ? result : toPlaceholder(this.placeholder);
		}
		
		@Override
		public String toString() {
			return toPlaceholder(escapeString(this.placeholder, true));
		}
		
	}
	
	@RequiredArgsConstructor
	public class ComboText implements ParseableText {
		
		private final @NotNull Collection<ParseableText> texts;
		
		@Override
		public @NotNull String parse(@NotNull UnaryOperator<String> replacer) {
			return this.texts.stream()
					.map(t -> t.parse(replacer))
					.collect(Collectors.joining());
		}
		
		@Override
		public String toString() {
			return texts.stream()
					.map(Object::toString)
					.collect(Collectors.joining());
		}
		
	}
	

}
