package org.dimensinfin.eveonline.neocom.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NeoComExpandableNodeTest {
	@Test
	public void equalsContract() {
		final TestNeoComExpandableNode iExpandableA = new TestNeoComExpandableNode();
		final TestNeoComExpandableNode iExpandableB = new TestNeoComExpandableNode();
		Assertions.assertTrue( iExpandableA.equals( iExpandableB ) );
	}

	@Test
	public void iExpandableInterfaceContract() {
		final TestNeoComExpandableNode iExpandable = new TestNeoComExpandableNode();

		Assertions.assertNotNull( iExpandable );
		Assertions.assertFalse( iExpandable.isExpanded() );
		Assertions.assertTrue( iExpandable.expand() );
		Assertions.assertTrue( iExpandable.isExpanded() );
		Assertions.assertFalse( iExpandable.collapse() );
		Assertions.assertFalse( iExpandable.isExpanded() );
		Assertions.assertTrue( iExpandable.toggleExpand() );
		Assertions.assertTrue( iExpandable.isExpanded() );
		Assertions.assertFalse( iExpandable.toggleExpand() );
		Assertions.assertFalse( iExpandable.isExpanded() );
		Assertions.assertTrue( iExpandable.isEmpty() );
	}

	@Test
	void hashCodeContract() {
		final TestNeoComExpandableNode iExpandableA = new TestNeoComExpandableNode();
		final TestNeoComExpandableNode iExpandableB = new TestNeoComExpandableNode();
		Assertions.assertEquals( iExpandableA.hashCode(), iExpandableB.hashCode() );
	}
}

final class TestNeoComExpandableNode extends NeoComExpandableNode {

	@Override
	public boolean isEmpty() {
		return true;
	}
}
