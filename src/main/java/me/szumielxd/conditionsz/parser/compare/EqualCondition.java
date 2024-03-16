package me.szumielxd.conditionsz.parser.compare;

public class EqualCondition extends ConditionOperation {

	public EqualCondition() {
		super("=");
	}
	
	@Override
	public boolean test(String left, String right) {
		return left.equals(right);
	}

}
