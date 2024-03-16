package me.szumielxd.conditionsz.parser.compare;

public class NotContainsCondition extends ConditionOperation {

	public NotContainsCondition() {
		super("!^");
	}
	
	@Override
	public boolean test(String left, String right) {
		return !right.isEmpty() && !left.contains(right);
	}

}
