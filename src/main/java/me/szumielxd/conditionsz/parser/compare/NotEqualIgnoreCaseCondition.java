package me.szumielxd.conditionsz.parser.compare;

public class NotEqualIgnoreCaseCondition extends ConditionOperation {

	public NotEqualIgnoreCaseCondition() {
		super("!~");
	}
	
	@Override
	public boolean test(String left, String right) {
		return !left.equalsIgnoreCase(right);
	}

}
