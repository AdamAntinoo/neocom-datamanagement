package org.dimensinfin.eveonline.neocom.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

class NeoComNodeTest {

	@Test
	public void capitalizeFirstLetter() {
		final String expected = "Capitalized";
		final String obtained = NeoComNode.capitalizeFirstLetter( "capitalized" );
		Assertions.assertEquals( expected, obtained );
	}

	@Test
	public void capitalizeFirstLetterEmpty() {
		final String expected = "";
		final String obtained = NeoComNode.capitalizeFirstLetter( "" );
		Assertions.assertEquals( expected, obtained );
	}

	@Test
	public void capitalizeFirstLetterFailure() {
		NullPointerException thrown = Assertions.assertThrows( NullPointerException.class,
				() -> NeoComNode.capitalizeFirstLetter( null ),
				"Expected NeoComNode.capitalizeFirstLetter() to throw null verification, but it didn't." );
	}

	@Test
	public void compareContract() {
		final NeoComNode one = new TestNeoComNode();
		final NeoComNode two = new TestNeoComNode();
		int oneCode = one.hashCode();
		Assertions.assertEquals( 0, one.compareTo( two ), "Nodes should compare to be equal" );
		Assertions.assertEquals( 0, two.compareTo( one ), "Nodes should compare to be equal" );
	}

	@Test
	public void equalsContract() {
		EqualsVerifier.forClass( NeoComNode.class )
				.usingGetClass().verify();
	}

	@Test
	public void getJsonClass() {
		final TestNeoComNode node = new TestNeoComNode();
		Assertions.assertNotNull( node );
		Assertions.assertEquals( "TestNeoComNode", node.getJsonClass() );
	}

	@Test
	public void hashCodeContract() {
		final NeoComNode one = new TestNeoComNode();
		final NeoComNode two = new TestNeoComNode();
		int oneCode = one.hashCode();
		Assertions.assertEquals( one.hashCode(), two.hashCode(), "HashCodes should be equal" );
		Assertions.assertEquals( oneCode, one.hashCode(), "HashCode should not change" );
	}

	@Test
	public void collaborate2Model() {
		final TestNeoComNode node = new TestNeoComNode();
		Assertions.assertNotNull( node );
		Assertions.assertTrue( node.collaborate2Model( "-TEST-VARIANT-" ).isEmpty() );
	}

	private class TestNeoComNode extends NeoComNode {
	}
}
