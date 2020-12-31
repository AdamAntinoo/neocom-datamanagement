package org.dimensinfin.eveonline.neocom.support;

import java.util.ArrayList;
import java.util.List;

import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.database.repositories.PlanetaryRepository;
import org.dimensinfin.eveonline.neocom.planetary.Schematics;

public class PlanetaryRepositoryMock {
	private int schematicsId = 92;
	private int schematics4Output = 92;

	private PlanetaryRepositoryMock() {}

	public PlanetaryRepository generatePlanetaryRepositoryMock() {
		final PlanetaryRepository mockPlanetaryRepository = Mockito.mock(PlanetaryRepository.class);
		Mockito.when(mockPlanetaryRepository.searchSchematics4Id(this.schematicsId))
		       .thenReturn(this.generateSchematics4Id());
		Mockito.when(mockPlanetaryRepository.searchSchematics4Output(this.schematics4Output))
		       .thenReturn(this.generateSchematics4Output());
		return mockPlanetaryRepository;
	}

	protected List<Schematics> generateSchematics4Id() {
		final List<Schematics> schematics = new ArrayList<>();
		schematics.add(new Schematics.Builder()
				               .withSchematicId(92)
				               .withSchematicName("Synthetic Synapses")
				               .withCycleTime(3600)
				               .withResourceTypeId(2312)
				               .withQuantity(10)
				               .withDirection((1 == 1) ? true : false)
				               .build());
		schematics.add(new Schematics.Builder()
				               .withSchematicId(92)
				               .withSchematicName("Synthetic Synapses")
				               .withCycleTime(3600)
				               .withResourceTypeId(2319)
				               .withQuantity(10)
				               .withDirection((1 == 1) ? true : false)
				               .build());
		schematics.add(new Schematics.Builder()
				               .withSchematicId(92)
				               .withSchematicName("Synthetic Synapses")
				               .withCycleTime(3600)
				               .withResourceTypeId(2346)
				               .withQuantity(3)
				               .withDirection((0 == 1) ? true : false)
				               .build());
		return schematics;
	}

	protected List<Schematics> generateSchematics4Output() {
		final List<Schematics> schematics = new ArrayList<>();
		schematics.add(new Schematics.Builder()
				               .withSchematicId(92)
				               .withSchematicName("Synthetic Synapses")
				               .withCycleTime(3600)
				               .withResourceTypeId(2312)
				               .withQuantity(10)
				               .withDirection((1 == 1) ? true : false)
				               .build());
		schematics.add(new Schematics.Builder()
				               .withSchematicId(92)
				               .withSchematicName("Synthetic Synapses")
				               .withCycleTime(3600)
				               .withResourceTypeId(2319)
				               .withQuantity(10)
				               .withDirection((1 == 1) ? true : false)
				               .build());
		schematics.add(new Schematics.Builder()
				               .withSchematicId(92)
				               .withSchematicName("Synthetic Synapses")
				               .withCycleTime(3600)
				               .withResourceTypeId(2346)
				               .withQuantity(3)
				               .withDirection((0 == 1) ? true : false)
				               .build());
		return schematics;
	}

	// - B U I L D E R
	public static class Builder {
		private PlanetaryRepositoryMock onConstruction;

		public Builder() {
			this.onConstruction = new PlanetaryRepositoryMock();
		}

		public PlanetaryRepositoryMock.Builder withSchematics4Id( final Integer schematicsId ) {
			if (null != schematicsId) this.onConstruction.schematicsId = schematicsId;
			return this;
		}

		public PlanetaryRepositoryMock.Builder withSchematics4Output( final Integer schematics4Output ) {
			if (null != schematics4Output) this.onConstruction.schematics4Output = schematics4Output;
			return this;
		}

		public PlanetaryRepositoryMock build() {
			return this.onConstruction;
		}
	}
}