package me.szumielxd.conditionsz;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class DebugTest {
	
	@Test
	void complexConditionParseJustRight() {
		var result = Conditionsz.get().parse("(\"ASD = ASD\" =\"\") || (XXX=\"D{ad\\}\\\"f} D\")");
		String expected = "\"ASD = ASD\" = \"\" || XXX = \"D{ad\\}\\\"f} D\"";
		assertEquals(expected, result.toString());
	}
	
	@ParameterizedTest
	@ValueSource(strings = {
			"7.0000000000001 >= 7",
			"7.0000000000001 > 7",
			"\"A S D\" ^ A",
			"A\\nSD = \"A\\nSD\"",
			"A\\nSD ~ \"a\\nSD\"",
			"10 >< 10.0",
			"meh !^ \\n",
			"A\\nSD != \"ASD\"",
			"not !~ similar",
			"10.0 <> 10.0001",
			"21 < 37",
			"911 <= 911"})
	void constantComparationReturnsTrue(String arg) {
		assertTrue(Conditionsz.get().parse(arg).test());
	}
	
	@ParameterizedTest
	@ValueSource(strings = {
			"ASD <= ASDD",
			"7.0 >= 7.0000000000001",
			"yyy ^ yes",
			"A\\nSD = \"ASD\"",
			"A\\nSD ~ \"a \\nSD\"",
			"10 >< 10.1",
			"meh !^ eh",
			"kayak != kayak", // palindrome check hehe...
			"qwerty !~ QwErTy",
			"0.1 <> 0.1",
			"911 < 911",
			"37 <= 21"})
	void constantComparationReturnsFalse(String arg) {
		assertFalse(Conditionsz.get().parse(arg).test());
	}
	
	@Test
	void placeholderComparationReturnsTrue() {
		var cond = Conditionsz.get().parse("AS\\nD = {te\\}xt}");
		Map<String, String> replacements = Map.of("te}xt", "AS\nD");
		assertTrue(cond.test(replacements::get));
	}
	
	@Test
	void placeholderComparationReturnsFalse() {
		var cond = Conditionsz.get().parse("AS\\nD = {text}");
		Map<String, String> replacements = Map.of("text", "AS\\nD");
		assertFalse(cond.test(replacements::get));
	}

}
