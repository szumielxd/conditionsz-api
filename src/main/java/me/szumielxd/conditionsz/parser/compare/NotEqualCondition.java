package me.szumielxd.conditionsz.parser.compare;

public class NotEqualCondition extends ConditionOperation {

	public NotEqualCondition() {
		super("!=");
	}
	
	@Override
	public boolean test(String left, String right) {
		return !left.equals(right);
	}

}
