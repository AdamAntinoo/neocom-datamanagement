package org.dimensinfin.eveonline.neocom.steps.multisteps;

import org.dimensinfin.eveonline.neocom.database.ISDEDatabaseAdapter;
import org.dimensinfin.eveonline.neocom.support.adapters.NeoComComponentFactory;
import org.dimensinfin.eveonline.neocom.support.location.LocationWorld;
import org.junit.Assert;

import java.util.List;
import java.util.Map;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class DM01InitializeLocationCatalog {
	private static final String COUNTER_CLASS = "counterClass";
	private static final String COUNTER = "count";

	private LocationWorld locationWorld;
	private Map<String, Integer> counters;

	public DM01InitializeLocationCatalog( final LocationWorld locationWorld ) {
		this.locationWorld = locationWorld;
	}

	@Given("a new empty Location Catalog store and repository")
	public void aNewEmptyLocationCatalogStoreAndRepository() {
		final ISDEDatabaseAdapter sdeAdapter = NeoComComponentFactory.getSingleton().getSDEDatabaseAdapter();
		Assert.assertNotNull(sdeAdapter);
		this.locationWorld.setSdeDatabaseManager(sdeAdapter);
		this.locationWorld.getLocationRepository().deleteAll(); // Clear the records on the sde repository
		this.locationWorld.setLocationCatalogService(NeoComComponentFactory.getSingleton().getLocationCatalogService());
	}

	@When("the Location Catalog is checked")
	public void theLocationCatalogIsChecked() {
		this.counters = this.locationWorld.getLocationCatalogService().getLocationTypeCounters();
	}

	@Then("the number of records on the LocationCache table is")
	public void theNumberOfRecordsOnTheLocationCacheTableIs( final List<Map<String, String>> cucumberTable ) {
		for (Map<String, String> row : cucumberTable) {
			final Integer obtained = this.counters.get(row.get(COUNTER_CLASS));
			Assert.assertEquals(Integer.parseInt(row.get(COUNTER)), obtained.intValue());
		}
	}
}