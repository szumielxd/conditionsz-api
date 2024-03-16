package me.szumielxd.conditionsz.parser.section;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlindSectionParser implements SectionParser {
	
	@Override
	public @Nullable SectionParseResult tryParseEscape(@NotNull SectionParseState state) {
		return state.stringMode() ? success(SectionParseResult.DEFAULT) : null;
	}
	
	private SectionParseResult success(SectionParseResult result) {
		return result;
	}

}
