package me.szumielxd.conditionsz.parser.compare;

public class ContainsCondition extends ConditionOperation {

	public ContainsCondition() {
		super("^");
	}
	
	@Override
	public boolean test(String left, String right) {
		return !right.isEmpty() && left.contains(right);
	}

}
