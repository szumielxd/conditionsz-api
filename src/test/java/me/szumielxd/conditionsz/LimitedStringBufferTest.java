package me.szumielxd.conditionsz;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import me.szumielxd.conditionsz.util.LimitedStringSequenceBuffer;

class LimitedStringBufferTest {

	@Test
	void appendASDfirst2ReturnsAS() {
		var buffer = new LimitedStringSequenceBuffer(3);
		buffer.append("A", "S", "D");
		Assertions.assertEquals("AS", buffer.first(2));
	}
	
	@Test
	void appendASDfirst4ReturnsNULL() {
		var buffer = new LimitedStringSequenceBuffer(4);
		buffer.append("A", "S", "D");
		Assertions.assertNull(buffer.first(4));
	}
	
	@Test
	void appendASDfirst2ReturnsNULL() {
		var buffer = new LimitedStringSequenceBuffer(4);
		buffer.append("A", "S", "D");
		Assertions.assertNull(buffer.first(2));
	}
	
	@Test
	void appendASDfirst2ForcedReturnsAS() {
		var buffer = new LimitedStringSequenceBuffer(4);
		buffer.append("A", "S", "D");
		Assertions.assertEquals("AS", buffer.first(2, true));
	}
	
}
