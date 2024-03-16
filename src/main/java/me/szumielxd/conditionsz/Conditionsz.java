package me.szumielxd.conditionsz;

import java.util.Collections;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

import me.szumielxd.conditionsz.elements.Calculatable;
import me.szumielxd.conditionsz.parser.ConditionBuilder;
import me.szumielxd.conditionsz.parser.SectionBuilder;
import me.szumielxd.conditionsz.parser.compare.BiggerCondition;
import me.szumielxd.conditionsz.parser.compare.BiggerEqualCondition;
import me.szumielxd.conditionsz.parser.compare.ConditionOperation;
import me.szumielxd.conditionsz.parser.compare.ContainsCondition;
import me.szumielxd.conditionsz.parser.compare.EqualCondition;
import me.szumielxd.conditionsz.parser.compare.EqualIgnoreCaseCondition;
import me.szumielxd.conditionsz.parser.compare.EqualNumberCondition;
import me.szumielxd.conditionsz.parser.compare.NotContainsCondition;
import me.szumielxd.conditionsz.parser.compare.NotEqualCondition;
import me.szumielxd.conditionsz.parser.compare.NotEqualIgnoreCaseCondition;
import me.szumielxd.conditionsz.parser.compare.NotEqualNumberCondition;
import me.szumielxd.conditionsz.parser.compare.SmallerCondition;
import me.szumielxd.conditionsz.parser.compare.SmallerEqualCondition;
import me.szumielxd.conditionsz.parser.section.BlindSectionParser;
import me.szumielxd.conditionsz.parser.section.ClosingBracketSectionParser;
import me.szumielxd.conditionsz.parser.section.EscapeSectionParser;
import me.szumielxd.conditionsz.parser.section.OpeningBracketSectionParser;
import me.szumielxd.conditionsz.parser.section.OperatorSectionParser;
import me.szumielxd.conditionsz.parser.section.SectionParser;
import me.szumielxd.conditionsz.parser.section.StringSectionParser;

public class Conditionsz {
	
	/*
	 * `(` - move to child nodes
	 * `)` - append previous text to current node level
	 * #operator# - append previous text to current node level AND set current operator
	 * `\` - remove current character increase pointer and append next character to text
	 * `"` - enter/exit string sequence
	 */

	private static final @NotNull List<SectionParser> DEFAULT_SECTION_PARSERS = List.of(
			new StringSectionParser(),
			new EscapeSectionParser(),
			new BlindSectionParser(),
			new OpeningBracketSectionParser(),
			new ClosingBracketSectionParser(),
			new OperatorSectionParser());
	
	private static final List<ConditionOperation> DEFAULT_CONDITIONS = List.of(
			new BiggerCondition(),
			new BiggerEqualCondition(),
			new ContainsCondition(),
			new EqualCondition(),
			new EqualIgnoreCaseCondition(),
			new EqualNumberCondition(),
			new NotContainsCondition(),
			new NotEqualCondition(),
			new NotEqualIgnoreCaseCondition(),
			new NotEqualNumberCondition(),
			new SmallerCondition(),
			new SmallerEqualCondition());
	
	private static final @NotNull Conditionsz INSTANCE = builder().defaultConfiguration().build();
	
	private final @NotNull Map<Integer, Map<String, ConditionOperation>> mappedConditions; // (operatorLength, operatorName) -> condition
	private final @NotNull List<SectionParser> sectionParsers;
	
	private Conditionsz(List<ConditionOperation> conditions, @NotNull List<SectionParser> sectionParsers) {
		this.mappedConditions = conditions.stream()
				.map(c -> Map.entry(c.getOperator(), c))
				.sorted((e1, e2) -> -Integer.compare(e1.getKey().length(), e2.getKey().length()))
				.collect(Collectors.groupingBy(e -> e.getKey().length(), LinkedHashMap::new,
						Collectors.toMap(Entry::getKey, Entry::getValue, (a, b) -> a)));
		this.sectionParsers = sectionParsers;
	}
	
	public @NotNull Calculatable parse(String text) {
		ConditionBuilder conditionBuilder = new ConditionBuilder(this.mappedConditions);
		Deque<SectionBuilder> sectionStack = new LinkedList<>();
		sectionStack.push(new SectionBuilder());
		int i = 0;
		while (i < text.length()) {
			var state = new SectionParser.SectionParseState(text, i, text.charAt(i), conditionBuilder.isAppendSpace());
			SectionParser.SectionParseResult result = this.sectionParsers.stream()
					.map(s -> s.tryParseEscape(state))
					.filter(Objects::nonNull)
					.findFirst()
					.orElse(SectionParser.SectionParseResult.DEFAULT);
			if (!result.preventDefault()) {
				conditionBuilder.append(state.getString(result.moveChars()));
			}
			if (result.buildCondition() && !conditionBuilder.isEmpty()) {
				var cond = conditionBuilder.build();
				sectionStack.peek().append(cond);
				conditionBuilder = new ConditionBuilder(this.mappedConditions);
			}
			if (result.operation() != null) {
				checkConditionBuilderEmpty(conditionBuilder, "cannot append operator");
				sectionStack.peek().append(result.operation());
			}
			if (result.ascentSection()) {
				checkConditionBuilderEmpty(conditionBuilder, "cannot ascend section");
				sectionStack.push(new SectionBuilder());
			}
			if (result.descendSection()) {
				checkConditionBuilderEmpty(conditionBuilder, "cannot descend section");
				var section = sectionStack.pop().build();
				sectionStack.peek().append(section);
			}
			if (result.toggleString()) {
				conditionBuilder.toggleAppendSpace();
			}
			i += result.moveChars();
		}
		return this.finalizeParse(conditionBuilder, sectionStack);
	}
	
	private @NotNull Calculatable finalizeParse(@NotNull ConditionBuilder conditionBuilder, @NotNull Deque<SectionBuilder> sectionStack) {
		if (sectionStack.size() > 1) {
			throw new IllegalStateException("Missing closing bracket");
		}
		if (!conditionBuilder.isEmpty()) {
			sectionStack.peek().append(conditionBuilder.build());
		}
		return sectionStack.pop().build();
	}
	
	private void checkConditionBuilderEmpty(ConditionBuilder conditionBuilder, String message) {
		if (!conditionBuilder.isEmpty()) {
			throw new IllegalStateException(message + " when current condition builder is not empty");
		}
	}
	
	public static final @NotNull Conditionsz get() {
		return INSTANCE;
	}
	
	public static final @NotNull ConditionszBuilder builder() {
		return new ConditionszBuilder();
	}
	
	
	public static class ConditionszBuilder {
		
		private final @NotNull List<SectionParser> sections = new LinkedList<>();
		private final @NotNull List<ConditionOperation> conditions = new LinkedList<>();
		
		private ConditionszBuilder() {
			// private
		}
		
		public @NotNull ConditionszBuilder addSections(@NotNull List<SectionParser> sections) {
			this.sections.addAll(sections);
			return this;
		}
		
		public @NotNull ConditionszBuilder addConditions(@NotNull List<ConditionOperation> conditions) {
			this.conditions.addAll(conditions);
			return this;
		}
		
		public @NotNull ConditionszBuilder defaultSections() {
			return addSections(DEFAULT_SECTION_PARSERS);
		}
		
		public @NotNull ConditionszBuilder defaultConditions() {
			return addConditions(DEFAULT_CONDITIONS);
		}
		
		public @NotNull ConditionszBuilder defaultConfiguration() {
			return defaultSections()
					.defaultConditions();
		}
		
		public @NotNull List<SectionParser> sections() {
			return Collections.unmodifiableList(this.sections);
		}
		
		public @NotNull List<ConditionOperation> conditions() {
			return Collections.unmodifiableList(this.conditions);
		}
		
		public @NotNull Conditionsz build() {
			return new Conditionsz(this.conditions, this.sections);
		}
		
	}
	

}
