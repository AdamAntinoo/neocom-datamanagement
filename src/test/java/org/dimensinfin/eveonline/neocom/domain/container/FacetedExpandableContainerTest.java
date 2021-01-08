package org.dimensinfin.eveonline.neocom.domain.container;

import java.io.Serializable;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.core.interfaces.ICollaboration;
import org.dimensinfin.core.interfaces.IExpandable;
import org.dimensinfin.core.interfaces.IJsonAngular;
import org.dimensinfin.eveonline.neocom.database.entities.NeoAsset;
import org.dimensinfin.eveonline.neocom.domain.NeoComNode;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceRegion;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceRegionImplementation;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceSystem;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceSystemImplementation;
import org.dimensinfin.eveonline.neocom.domain.space.StationImplementation;
import org.dimensinfin.eveonline.neocom.domain.space.Structure;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRegionsRegionIdOk;

class FacetedExpandableContainerTest {
	@Test
	void buildComplete() {
		final TestFacet facet = new TestFacet( "-TEST-" );
		final FacetedExpandableContainer container = new FacetedExpandableContainer.Builder<TestFacet, NeoAsset>()
				.withFacet( facet )
				.build();
		Assertions.assertNotNull( container );
	}

	@Test
	void buildFailure() {
		final TestFacet facet = new TestFacet( "-TEST-" );

		final NullPointerException thrown = Assertions.assertThrows( NullPointerException.class,
				() -> new FacetedExpandableContainer.Builder<TestFacet, NeoAsset>()
						.withFacet( null )
						.build(),
				"Expected FacetedExpandableContainer.Builder() to throw null verification, but it didn't." );
		Assertions.assertNotNull( thrown );
	}

	@Test
	void getFacet() {
		final String expected = "-CHECK-VALUE-";
		final TestFacet facet = new TestFacet( expected );
		final FacetedExpandableContainer container = new FacetedExpandableContainer.Builder<TestFacet, NeoAsset>()
				.withFacet( facet )
				.build();
		Assertions.assertNotNull( container );
		Assertions.assertTrue( container instanceof IExpandable );
		Assertions.assertTrue( container instanceof ICollaboration );
		Assertions.assertTrue( container instanceof IJsonAngular );
		Assertions.assertTrue( container instanceof Serializable );
		Assertions.assertTrue( container.getFacet() instanceof NeoComNode );
		Assertions.assertTrue( container.getFacet() instanceof ICollaboration );
	}

	@Test
	void spaceRegionUseCase() {
		final GetUniverseRegionsRegionIdOk region = Mockito.mock( GetUniverseRegionsRegionIdOk.class );
		final FacetedExpandableContainer regionSlot = new FacetedExpandableContainer
				.Builder<SpaceRegion, SpaceSystem>()
				.withFacet( new SpaceRegionImplementation.Builder().withRegion( region ).build() )
				.build();
		final NeoAsset asset = Mockito.mock( NeoAsset.class );
		regionSlot.addContent( asset );

		Assertions.assertNotNull( regionSlot );
		Assertions.assertTrue( regionSlot.getFacet() instanceof SpaceRegion );
		Assertions.assertTrue( regionSlot.getFacet() instanceof ICollaboration );
		final List collaborations = regionSlot.collaborate2Model( "-TEST-VARIANT-" );
		Assertions.assertTrue( collaborations.size() > 0 );
		Assertions.assertEquals( 1, collaborations.size() );

		final SpaceSystemImplementation space = Mockito.mock( SpaceSystemImplementation.class );
		final StationImplementation station = Mockito.mock( StationImplementation.class );
		final Structure structure = Mockito.mock( Structure.class );
		regionSlot.addContent( space );
		regionSlot.addContent( station );
		regionSlot.addContent( structure );
		Assertions.assertEquals( 4, regionSlot.collaborate2Model( "-TEST-VARIANT-" ).size() );
	}
}

final class TestFacet extends NeoComNode {
	private final String expected;

	public TestFacet( final String expected ) {
		this.expected = expected;
	}
}
