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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.core.util.Chrono;
import org.dimensinfin.eveonline.neocom.core.NeocomRuntimeException;
import org.dimensinfin.eveonline.neocom.database.entity.Colony;
import org.dimensinfin.eveonline.neocom.database.entity.Credential;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdFittings200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdIndustryJobs200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdPlanets200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdPlanetsPlanetIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdPlanetsPlanetIdOkPins;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniversePlanetsPlanetIdOk;
import org.dimensinfin.eveonline.neocom.industry.Fitting;
import org.dimensinfin.eveonline.neocom.industry.Job;
import org.dimensinfin.eveonline.neocom.planetary.ColonyStructure;

/**
 * @author Adam Antinoo
 */
// - CLASS IMPLEMENTATION ...................................................................................
public class GlobalDataManagerNetwork extends GlobalDataManagerConfiguration {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger("GlobalDataManagerNetwork");

	// --- M A P P E R S   &   T R A N S F O R M E R S   S E C T I O N
	/**
	 * Instance for the mapping of OK instances to the MVC compatible classes.
	 */
	private static final ModelMapper modelMapper = new ModelMapper();

	static {
		modelMapper.getConfiguration()
				.setFieldMatchingEnabled(true)
				.setMethodAccessLevel(Configuration.AccessLevel.PRIVATE);
	}

	/**
	 * Jackson mapper to use for object json serialization.
	 */
	private static final ObjectMapper jsonMapper = new ObjectMapper();

	static {
		jsonMapper.enable(SerializationFeature.INDENT_OUTPUT);
		jsonMapper.registerModule(new JodaModule());
		// Add our own serializers.
		SimpleModule neocomSerializerModule = new SimpleModule();
//		neocomSerializerModule.addSerializer(Ship.class, new ShipSerializer());
		neocomSerializerModule.addSerializer(Credential.class, new CredentialSerializer());
		jsonMapper.registerModule(neocomSerializerModule);
	}

	// --- N E T W O R K    D O W N L O A D   I N T E R F A C E
	public static List<Job> downloadIndustryJobs4Credential( final Credential credential ) {
		logger.info(">> [GlobalDataManagerNetwork.downloadIndustryJobs4Credential]");
		List<Job> results = new ArrayList<>();
		try {
			// Get to the Network and download the data from the ESI api.
			final List<GetCharactersCharacterIdIndustryJobs200Ok> industryJobs = ESINetworkManager.getCharactersCharacterIdIndustryJobs(credential.getAccountId()
					, credential.getRefreshToken()
					, SERVER_DATASOURCE);
			if (null != industryJobs) {
				// Process the data and convert it to structures compatible with MVC.
				for (GetCharactersCharacterIdIndustryJobs200Ok job : industryJobs) {
					final Job newjob = modelMapper.map(job, Job.class);
					newjob.store();
					results.add(newjob);
				}
			}
			return results;
		} catch (NeocomRuntimeException nrex) {
			logger.info("EX [GlobalDataManager.downloadIndustryJobs4Credential]> Credential not found in the list. Exception: {}", nrex
					.getMessage());
			return new ArrayList<>();
		} catch (RuntimeException ntex) {
			logger.info("EX [GlobalDataManager.downloadIndustryJobs4Credential]> Mapping error - {}", ntex
					.getMessage());
			return new ArrayList<>();
		} finally {
			logger.info("<< [GlobalDataManagerNetwork.downloadIndustryJobs4Credential]");
		}
	}

	public static List<Colony> downloadColonies4Credential( final Credential credential ) {
		// Optimize the access to the Colony data.
		//		if(colonies.size()<1) {
		final Chrono accessFullTime = new Chrono();
		List<Colony> colonies = new ArrayList<>();
		try {
			// Create a request to the ESI api downloader to get the list of Planets of the current Character.
			final int identifier = credential.getAccountId();
			final List<GetCharactersCharacterIdPlanets200Ok> colonyInstances = ESINetworkManager.getCharactersCharacterIdPlanets(identifier, credential.getRefreshToken(), SERVER_DATASOURCE);
			// Transform the received OK instance into a NeoCom compatible model instance.
			for (GetCharactersCharacterIdPlanets200Ok colonyOK : colonyInstances) {
				try {
					Colony col = modelMapper.map(colonyOK, Colony.class);
					// Block to add additional data not downloaded on this call.
					// To set more information about this particular planet we should call the Universe database.
					final GetUniversePlanetsPlanetIdOk planetData = ESINetworkManager.getUniversePlanetsPlanetId(col.getPlanetId(), credential.getRefreshToken(), SERVER_DATASOURCE);
					if (null != planetData) col.setPlanetData(planetData);

					try {
						// During this first phase download all the rest of the information.
						// Get to the Network and download the data from the ESI api.
						final GetCharactersCharacterIdPlanetsPlanetIdOk colonyStructures = ESINetworkManager.getCharactersCharacterIdPlanetsPlanetId(credential.getAccountId(), col.getPlanetId(), credential.getRefreshToken(), SERVER_DATASOURCE);
						if (null != colonyStructures) {
							// Add the original data to the colony if we need some more information later.
							col.setStructuresData(colonyStructures);
							List<ColonyStructure> results = new ArrayList<>();

							// Process the structures converting the pin to the Colony structures compatible with MVC.
							final List<GetCharactersCharacterIdPlanetsPlanetIdOkPins> pinList = colonyStructures.getPins();
							for (GetCharactersCharacterIdPlanetsPlanetIdOkPins structureOK : pinList) {
								ColonyStructure newstruct = modelMapper.map(structureOK, ColonyStructure.class);
								// TODO Convert the structure to a serialized Json string and store it into the database for fast access.
								try {
									final String serialized = jsonMapper.writeValueAsString(newstruct);
									final String storageIdentifier = constructPlanetStorageIdentifier(credential.getAccountId()
											, col.getPlanetId());
									// TODO Removed until the compilation is complete. This is something we should review before adding
									// it back.
//									final ColonyStorage storage = new ColonyStorage(newstruct.getPinId())
//											.setPlanetIdentifier(storageIdentifier)
//											.setColonySerialization(serialized)
//											.store();
								} catch (JsonProcessingException jpe) {
									jpe.printStackTrace();
								}
								// missing code
								results.add(newstruct);
							}
							col.setStructures(results);
						}
					} catch (RuntimeException rtex) {
						rtex.printStackTrace();
					}
					col.store();
					colonies.add(col);
				} catch (RuntimeException rtex) {
					rtex.printStackTrace();
				}
			}
		} catch (RuntimeException rtex) {
			rtex.printStackTrace();
		}
		return colonies;
	}

	// TODO Review with the use of session
	public List<ColonyStructure> downloadStructures4Colony( final int characterid, final int planetid ) {
		logger.info(">> [GlobalDataManager.accessStructures4Colony]");
		List<ColonyStructure> results = new ArrayList<>();
//		// Get the Credential that matched the received identifier.
//		Credential credential = DataManagementModelStore.getCredential4Id(characterid);
//		if (null != credential) {
//			// Get to the Network and download the data from the ESI api.
//			final GetCharactersCharacterIdPlanetsPlanetIdOk colonyStructures = ESINetworkManager.getCharactersCharacterIdPlanetsPlanetId(credential.getAccountId(), planetid, credential.getRefreshToken(), SERVER_DATASOURCE);
//			if (null != colonyStructures) {
//				// Process the structures converting the pin to the Colony structures compatible with MVC.
//				final List<GetCharactersCharacterIdPlanetsPlanetIdOkPins> pinList = colonyStructures.getPins();
//				for (GetCharactersCharacterIdPlanetsPlanetIdOkPins structureOK : pinList) {
//					ColonyStructure newstruct = modelMapper.map(structureOK, ColonyStructure.class);
//					// TODO Convert the structure to a serialized Json string and store it into the database for fast access.
//					try {
//						final String serialized = jsonMapper.writeValueAsString(newstruct);
//						final String storageIdentifier = constructPlanetStorageIdentifier(credential.getAccountId(), planetid);
//						final ColonyStorage storage = new ColonyStorage(newstruct.getPinId())
//								.setPlanetIdentifier(storageIdentifier)
//								.setColonySerialization(serialized)
//								.store();
//					} catch (JsonProcessingException jpe) {
//						jpe.printStackTrace();
//					}
//					results.add(newstruct);
//				}
//			}
//		} else {
//			// TODO. It will not return null. The miss searching for a credential will generate an exception.
//			// Possible that because the application has been previously removed from memory that data is not reloaded.
//			// Call the reloading mechanism and have a second opportunity.
//			DataManagementModelStore.accessCredentialList();
//			credential = DataManagementModelStore.getCredential4Id(characterid);
//			if (null == credential) return new ArrayList<>();
//			else return GlobalDataManager.downloadStructures4Colony(characterid, planetid);
//		}
		return results;
	}

	public static List<Fitting> downloadFittings4Credential( final Credential credential ) {
		logger.info(">> [GlobalDataManager.downloadFittings4Credential]");
		List<Fitting> results = new ArrayList<>();
		try {
			// Get to the Network and download the data from the ESI api.
			final List<GetCharactersCharacterIdFittings200Ok> fittings = ESINetworkManager.getCharactersCharacterIdFittings
					(credential.getAccountId()
							, credential.getRefreshToken()
							, SERVER_DATASOURCE);
			if (null != fittings) {
				// Process the fittings processing them and converting the data to structures compatible with MVC.
				for (GetCharactersCharacterIdFittings200Ok fit : fittings) {
					final Fitting newfitting = modelMapper.map(fit, Fitting.class);
					results.add(newfitting);
				}
			}
			return results;
		} catch (NeocomRuntimeException nrex) {
			logger.info("EX [GlobalDataManager.downloadFittings4Credential]> Credential not found in the list. Exception: {}", nrex
					.getMessage());
			return new ArrayList<>();
		} catch (RuntimeException ntex) {
			logger.info("EX [GlobalDataManager.downloadFittings4Credential]> Mapping error - {}", ntex
					.getMessage());
			return new ArrayList<>();
		} finally {
			logger.info("<< [GlobalDataManager.downloadFittings4Credential]");
		}
	}

	// - CLASS IMPLEMENTATION ...................................................................................
	public static class CredentialSerializer extends JsonSerializer<Credential> {
		// - F I E L D - S E C T I O N ............................................................................

		// - M E T H O D - S E C T I O N ..........................................................................
		@Override
		public void serialize( final Credential value, final JsonGenerator jgen, final SerializerProvider provider )
				throws IOException, JsonProcessingException {
			jgen.writeStartObject();
			jgen.writeStringField("jsonClass", value.getJsonClass());
			jgen.writeNumberField("accountId", value.getAccountId());
			jgen.writeStringField("accountName", value.getAccountName());
			jgen.writeStringField("tokenType", value.getTokenType());
			jgen.writeBooleanField("isESI", value.isESICompatible());
			jgen.writeEndObject();
		}
	}
	// ........................................................................................................
}

// - UNUSED CODE ............................................................................................
//[01]