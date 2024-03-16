package me.szumielxd.conditionsz.parser.section;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.szumielxd.conditionsz.parser.ConditionBuilder;

public class StringSectionParser implements SectionParser {
	
	private static final SectionParseResult SUCCESS = new SectionParseResult(1, null, true, false, false, false, true);
	
	@Override
	public @Nullable SectionParseResult tryParseEscape(@NotNull SectionParseState state) {
		return state.currentChar() == ConditionBuilder.STRING_CHAR ? success(SUCCESS) : null;
	}
	
	private SectionParseResult success(SectionParseResult result) {
		return result;
	}

}
