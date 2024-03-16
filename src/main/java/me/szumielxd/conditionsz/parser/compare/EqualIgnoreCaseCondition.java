package me.szumielxd.conditionsz.parser.compare;

public class EqualIgnoreCaseCondition extends ConditionOperation {

	public EqualIgnoreCaseCondition() {
		super("~");
	}
	
	@Override
	public boolean test(String left, String right) {
		return left.equalsIgnoreCase(right);
	}

}
