package me.szumielxd.conditionsz.parser.compare;

public class SmallerCondition extends ConditionOperation {

	public SmallerCondition() {
		super("<");
	}

	@Override
	public boolean test(String left, String right) {
		var parsed = this.parseNumberPair(left, right);
		if (parsed != null) {
			Number l = parsed.left();
			Number r = parsed.right();
			if (l instanceof Long) {
				return l.longValue() < r.longValue();
			} else {
				return l.doubleValue() < r.doubleValue();
			}
		}
		return false;
	}

}
