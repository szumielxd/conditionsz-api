package me.szumielxd.conditionsz.parser.section;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.szumielxd.conditionsz.elements.LogicalOperation;

public class OperatorSectionParser implements SectionParser {

	@Override
	public @Nullable SectionParseResult tryParseEscape(@NotNull SectionParseState state) {
		var str = state.getString(2);
		var operation = str != null ? LogicalOperation.getByOperator(str) : null;
		return operation != null ? success(new SectionParseResult(2, operation, true, true, false, false, false)) : null;
	}
	
	private SectionParseResult success(SectionParseResult result) {
		return result;
	}

}
