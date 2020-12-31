package org.dimensinfin.eveonline.neocom.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ExpandableContainerTest {
	private static final String DEFAULT_VARIANT = "-DEFAULT-VARIANT-";

	@Test
	public void clear() {
		final ExpandableContainer<TestNeoComExpandableNode> expandableContainer = new ExpandableContainer();
		Assertions.assertNotNull( expandableContainer );
		Assertions.assertEquals( 0, expandableContainer.getContents().size() );
		expandableContainer.addContent( new TestNeoComExpandableNode() );
		expandableContainer.addContent( new TestNeoComExpandableNode() );
		expandableContainer.addContent( new TestNeoComExpandableNode() );
		expandableContainer.addContent( new TestNeoComExpandableNode() );
		Assertions.assertEquals( 4, expandableContainer.getContents().size() );
		expandableContainer.clear();
		Assertions.assertEquals( 0, expandableContainer.getContents().size() );
	}

	@Test
	public void collaborate2Model() {
		final ExpandableContainer<TestNeoComExpandableNode> expandableContainer = new ExpandableContainer();
		Assertions.assertNotNull( expandableContainer );
		Assertions.assertEquals( 0, expandableContainer.getContents().size() );
		Assertions.assertEquals( 0, expandableContainer.collaborate2Model( DEFAULT_VARIANT ).size() );
		expandableContainer.addContent( new TestNeoComExpandableNode() );
		expandableContainer.addContent( new TestNeoComExpandableNode() );
		expandableContainer.addContent( new TestNeoComExpandableNode() );
		expandableContainer.addContent( new TestNeoComExpandableNode() );
		Assertions.assertEquals( 4, expandableContainer.getContents().size() );
		Assertions.assertEquals( 4, expandableContainer.collaborate2Model( DEFAULT_VARIANT ).size() );
	}

	@Test
	public void getContentCount() {
		final ExpandableContainer<TestNeoComExpandableNode> expandableContainer = new ExpandableContainer();
		Assertions.assertNotNull( expandableContainer );
		Assertions.assertEquals( 0, expandableContainer.getContentCount() );
		expandableContainer.addContent( new TestNeoComExpandableNode() );
		expandableContainer.addContent( new TestNeoComExpandableNode() );
		expandableContainer.addContent( new TestNeoComExpandableNode() );
		expandableContainer.addContent( new TestNeoComExpandableNode() );
		Assertions.assertEquals( 4, expandableContainer.getContentCount() );
	}

	@Test
	public void getContents() {
		final ExpandableContainer<TestNeoComExpandableNode> expandableContainer = new ExpandableContainer();
		Assertions.assertNotNull( expandableContainer );
		Assertions.assertEquals( 0, expandableContainer.getContents().size() );
		expandableContainer.addContent( new TestNeoComExpandableNode() );
		expandableContainer.addContent( new TestNeoComExpandableNode() );
		expandableContainer.addContent( new TestNeoComExpandableNode() );
		expandableContainer.addContent( new TestNeoComExpandableNode() );
		Assertions.assertEquals( 4, expandableContainer.getContents().size() );
	}

	@Test
	public void isEmpty() {
		final ExpandableContainer<TestNeoComExpandableNode> expandableContainer = new ExpandableContainer();
		Assertions.assertNotNull( expandableContainer );
		Assertions.assertTrue( expandableContainer.isEmpty() );
		expandableContainer.addContent( new TestNeoComExpandableNode() );
		Assertions.assertFalse( expandableContainer.isEmpty() );
	}
}
