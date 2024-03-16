package me.szumielxd.conditionsz.parser.section;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EscapeSectionParser implements SectionParser {

	private static final SectionParseResult SUCCESS = new SectionParseResult(2, null, false, false, false, false, false);
	
	@Override
	public @Nullable SectionParseResult tryParseEscape(@NotNull SectionParseState state) {
		return state.currentChar() == '\\' ? success(SUCCESS) : null;
	}
	
	private SectionParseResult success(SectionParseResult result) {
		return result;
	}

}
