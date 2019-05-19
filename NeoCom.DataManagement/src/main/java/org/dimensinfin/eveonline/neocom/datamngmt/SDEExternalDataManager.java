//  PROJECT:     NeoCom.DataManagement(NEOC.DTM)
//  AUTHORS:     Adam Antinoo - adamantinoo.git@gmail.com
//  COPYRIGHT:   (c) 2013-2018 by Dimensinfin Industries, all rights reserved.
//  ENVIRONMENT: Java 1.8 Library.
//  DESCRIPTION: NeoCom project library that comes from the old Models package but that includes much more
//               functionality than the model definitions for the Eve Online NeoCom application.
//               If now defines the pure java code for all the repositories, caches and managers that do
//               not have an specific Android implementation serving as a code base for generic platform
//               development. The architecture model has also changed to a better singleton/static
//               implementation that reduces dependencies and allows separate use of the modules. Still
//               there should be some initialization/configuration code to connect the new library to the
//               runtime implementation provided by the Application.
package org.dimensinfin.eveonline.neocom.datamngmt;

import com.annimon.stream.Stream;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseAncestries200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseBloodlines200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRaces200Ok;
import org.dimensinfin.eveonline.neocom.industry.InventoryFlag;
import org.dimensinfin.eveonline.neocom.interfaces.IConfigurationProvider;
import org.dimensinfin.eveonline.neocom.interfaces.IFileSystem;
import org.dimensinfin.eveonline.neocom.model.GetUniverseAncestries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

/**
 * This static class centralizes all the functionality to access data. It will provide a consistent api to the rest
 * of the application and will hide the internals of how that data is obtained, managed and stored.
 * All they now are direct database access or cache access or even Model list accesses will be hidden under an api
 * that will decide at any point from where to get the information and if there are more jobs to do to keep
 * that information available and up to date.
 *
 * The initial release will start transferring the ModelFactory functionality.
 *
 * @author Adam Antinoo
 */
public class SDEExternalDataManager {
	protected static Logger logger = LoggerFactory.getLogger("GlobalDataManager");

	// - C O M P O N E N T S
	protected IConfigurationProvider configurationProvider;
	protected IFileSystem fileSystemAdapter;
	protected ESIGlobalAdapter esiAdapter;

	public static void initialize () {
		// Load SDE data files and create the caches.
		// YAML Location Flags.
		final String inventoryFlagFileLocation = GlobalDataManager.getResourceString("R.sde.external.yaml.locationpath")
				+ GlobalDataManager.getResourceString("R.sde.external.yaml.inventoryFlag");
		try {
			//			final File sourceFile = new File(inventoryFlagFileLocation);
			final List<InventoryFlag> flagList = yamlMapper.readValue(GlobalDataManager.openAsset4Input(inventoryFlagFileLocation)
					, new TypeReference<List<InventoryFlag>>() {
					});
			flagStore.clear();
			// Transform the list into map to simply the search for the elements.
			Stream.of(flagList)
			      .forEach((flag) -> {
				      flagStore.put(flag.getFlagID(), flag);
			      });
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (RuntimeException rtex) {
			rtex.printStackTrace();
		}

		// JSON Ancestries
//		readAncestries();
//		// JSON Bloodlines
//		readBloodLines();
//		// JSON Races
//		readRaces();
	}

	// - Y A M L - L O C A T I O N   F L A G S
	private static ObjectMapper yamlMapper = null;

	static {
		final YAMLFactory yamlFactory = new YAMLFactory();
		yamlFactory.disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER);

		yamlMapper = new ObjectMapper(yamlFactory);
		yamlMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		yamlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	private static final HashMap<Integer, InventoryFlag> flagStore = new HashMap();

//	public static void loadSDEData() {
//		data =
//		ancestriesCache.
//	}

	//	private static final String inventoryFlagFileLocation = GlobalDataManager.getResourceString("R.sde.external.yaml.locationpath")
	//			+ GlobalDataManager.getResourceString("R.sde.external.yaml.inventoryFlag");
	//	private static final HashMap<Integer, InventoryFlag> flagStore = new HashMap();

	//	static {
	//		// Loading YAML data during initialization.
	//		try {
	//			final File sourceFile = new File(inventoryFlagFileLocation);
	//			final List<InventoryFlag> flagList = yamlMapper.readValue(sourceFile, new TypeReference<List<InventoryFlag>>() {
	//			});
	//			flagStore.clear();
	//			// Transform the list into map to simply the search for the elements.
	//			Stream.of(flagList)
	//					.forEach(( flag ) -> {
	//						flagStore.put(flag.getFlagID(), flag);
	//					});
	//		} catch (IOException ioe) {
	//			ioe.printStackTrace();
	//		} catch (RuntimeException rtex) {
	//			rtex.printStackTrace();
	//		}
	//
	//	}

	public InventoryFlag searchFlag4Id (final int identifier) {
		return flagStore.get(identifier);
	}

	//--- J S O N - A N C E S T R I E S
	/**
	 * Jackson mapper to use for object json serialization.
	 */
	private static final ObjectMapper jsonMapper = new ObjectMapper();
	protected static Hashtable<Integer, GetUniverseAncestries200Ok> ancestriesCache = new Hashtable<>();
	protected static Hashtable<Integer, GetUniverseBloodlines200Ok> bloodLinesCache = new Hashtable<>();
	protected static Hashtable<Integer, GetUniverseRaces200Ok> racesCache = new Hashtable<>();

	static {
		jsonMapper.enable(SerializationFeature.INDENT_OUTPUT);
		jsonMapper.registerModule(new JodaModule());
		// Add our own serializers.
		SimpleModule neocomSerializerModule = new SimpleModule();
		//		neocomSerializerModule.addSerializer(Ship.class, new ShipSerializer());
		//		neocomSerializerModule.addSerializer(Credential.class, new CredentialSerializer());
		//		jsonMapper.registerModule(neocomSerializerModule);
	}

//	public static void readAncestries () {
//		logger.info(">> [SDEExternalDataManager.readAncestries]");
//		try {
//			// Get the file location to process.
//			final String source = GlobalDataManager.getResourceString("R.sde.external.json.locationpath")
//					+ GlobalDataManager.getResourceString("R.sde.external.json.universe.ancestries");
//			final List<GetUniverseAncestries200Ok> ancestries = jsonMapper.readValue(GlobalDataManager.openAsset4Input(source)
//					, new TypeReference<List<GetUniverseAncestries200Ok>>() {
//					});
//			ancestriesCache.clear();
//			for (GetUniverseAncestries200Ok line : ancestries) {
//				ancestriesCache.put(line.getId(), line);
//			}
//		} catch (IOException ioe) {
//			ioe.printStackTrace();
//		}
//		logger.info("<< [SDEExternalDataManager.readAncestries]");
//	}
//
//	public static GetUniverseAncestries200Ok searchSDEAncestry (final int identifier) {
//		GetUniverseAncestries200Ok hit = ancestriesCache.get(identifier);
//		if ( null == hit ) hit = new GetUniverseAncestries200Ok();
//		return hit;
//	}

	//--- J S O N - B L O O D L I N E S
//	public static void readBloodLines () {
//		logger.info(">> [SDEExternalDataManager.readBloodLines]");
//		try {
//			// Get the file location to process.
//			final String source = GlobalDataManager.getResourceString("R.sde.external.json.locationpath")
//					+ GlobalDataManager.getResourceString("R.sde.external.json.universe.bloodlines");
//			final List<GetUniverseBloodlines200Ok> bloodLines = jsonMapper.readValue(GlobalDataManager.openAsset4Input(source)
//					, new TypeReference<List<GetUniverseBloodlines200Ok>>() {
//					});
//			bloodLinesCache.clear();
//			for (GetUniverseBloodlines200Ok line : bloodLines) {
//				bloodLinesCache.put(line.getBloodlineId(), line);
//			}
//		} catch (IOException ioe) {
//			ioe.printStackTrace();
//		}
//		logger.info("<< [SDEExternalDataManager.readBloodLines]");
//	}
//
//	public static GetUniverseBloodlines200Ok searchSDEBloodline (final int identifier) {
//		GetUniverseBloodlines200Ok hit = bloodLinesCache.get(identifier);
//		if ( null == hit ) hit = new GetUniverseBloodlines200Ok();
//		return hit;
//	}

	//--- J S O N - R A C E S
//	public static void readRaces () {
//		logger.info(">> [SDEExternalDataManager.readRaces]");
//		try {
//			// Get the file location to process.
//			final String source = GlobalDataManager.getResourceString("R.sde.external.json.locationpath")
//					+ GlobalDataManager.getResourceString("R.sde.external.json.universe.races");
//			final List<GetUniverseRaces200Ok> races = jsonMapper.readValue(GlobalDataManager.openAsset4Input(source)
//					, new TypeReference<List<GetUniverseRaces200Ok>>() {
//					});
//			racesCache.clear();
//			for (GetUniverseRaces200Ok line : races) {
//				racesCache.put(line.getRaceId(), line);
//			}
//		} catch (IOException ioe) {
//			ioe.printStackTrace();
//		}
//		logger.info("<< [SDEExternalDataManager.readRaces]");
//	}
//
//	public static GetUniverseRaces200Ok searchSDERace (final int identifier) {
//		GetUniverseRaces200Ok hit = racesCache.get(identifier);
//		if ( null == hit ) hit = new GetUniverseRaces200Ok();
//		return hit;
//	}
}
