package me.szumielxd.conditionsz.parser.section;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.szumielxd.conditionsz.elements.LogicalOperation;

public interface SectionParser {
	
	public @Nullable SectionParseResult tryParseEscape(@NotNull SectionParseState state);

	
	public record SectionParseResult(int moveChars, @Nullable LogicalOperation operation, boolean preventDefault, boolean buildCondition, boolean ascentSection, boolean descendSection, boolean toggleString) {
		public static final @NotNull SectionParseResult DEFAULT = new SectionParseResult(1, null, false, false, false, false, false);
	}
	
	public record SectionParseState(@NotNull String full, int currentIndex, char currentChar, boolean stringMode) {
		public @Nullable String getString(int length) {
			int endIndex = currentIndex + length;
			if (full.length() >= endIndex) {
				return full.substring(currentIndex, endIndex);
			}
			return null;
		}
	}

}
