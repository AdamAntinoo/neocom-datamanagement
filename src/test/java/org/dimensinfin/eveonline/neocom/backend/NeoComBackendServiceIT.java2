package org.dimensinfin.eveonline.neocom.backend;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.backend.rest.v1.CredentialStoreResponse;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.database.entities.MiningExtractionEntity;
import org.dimensinfin.eveonline.neocom.provider.RetrofitFactory;
import org.dimensinfin.eveonline.neocom.support.IntegrationEnvironmentDefinitionTCLocal;

public class NeoComBackendServiceIT extends IntegrationEnvironmentDefinitionTCLocal {
	@Test
	public void accessTodayMiningExtractions4Pilot() {
		// Given
		final NeoComBackendService neoComBackendService = new NeoComBackendService.Builder()
				.withRetrofitFactory( this.itRetrofitFactory )
				.build();
		final String token = "eyJhbGciOiJSUzI1NiIsImtpZCI6IkpXVC1TaWduYXR1cmUtS2V5IiwidHlwIjoiSldUIn0" +
				".eyJzY3AiOlsicHVibGljRGF0YSIsImVzaS1sb2NhdGlvbi5yZWFkX2xvY2F0aW9uLnYxIiwiZXNpLWxvY2F0aW9uLnJlYWRfc2hpcF90eXBlLnYxIiwiZXNpLW1haWwucmVhZF9tYWlsLnYxIiwiZXNpLXNraWxscy5yZWFkX3NraWxscy52MSIsImVzaS1za2lsbHMucmVhZF9za2lsbHF1ZXVlLnYxIiwiZXNpLXdhbGxldC5yZWFkX2NoYXJhY3Rlcl93YWxsZXQudjEiLCJlc2ktd2FsbGV0LnJlYWRfY29ycG9yYXRpb25fd2FsbGV0LnYxIiwiZXNpLXNlYXJjaC5zZWFyY2hfc3RydWN0dXJlcy52MSIsImVzaS1jbG9uZXMucmVhZF9jbG9uZXMudjEiLCJlc2ktdW5pdmVyc2UucmVhZF9zdHJ1Y3R1cmVzLnYxIiwiZXNpLWFzc2V0cy5yZWFkX2Fzc2V0cy52MSIsImVzaS1wbGFuZXRzLm1hbmFnZV9wbGFuZXRzLnYxIiwiZXNpLWZpdHRpbmdzLnJlYWRfZml0dGluZ3MudjEiLCJlc2ktaW5kdXN0cnkucmVhZF9jaGFyYWN0ZXJfam9icy52MSIsImVzaS1tYXJrZXRzLnJlYWRfY2hhcmFjdGVyX29yZGVycy52MSIsImVzaS1jaGFyYWN0ZXJzLnJlYWRfYmx1ZXByaW50cy52MSIsImVzaS1jb250cmFjdHMucmVhZF9jaGFyYWN0ZXJfY29udHJhY3RzLnYxIiwiZXNpLWNsb25lcy5yZWFkX2ltcGxhbnRzLnYxIiwiZXNpLXdhbGxldC5yZWFkX2NvcnBvcmF0aW9uX3dhbGxldHMudjEiLCJlc2ktY2hhcmFjdGVycy5yZWFkX25vdGlmaWNhdGlvbnMudjEiLCJlc2ktY29ycG9yYXRpb25zLnJlYWRfZGl2aXNpb25zLnYxIiwiZXNpLWFzc2V0cy5yZWFkX2NvcnBvcmF0aW9uX2Fzc2V0cy52MSIsImVzaS1jb3Jwb3JhdGlvbnMucmVhZF9ibHVlcHJpbnRzLnYxIiwiZXNpLWNvbnRyYWN0cy5yZWFkX2NvcnBvcmF0aW9uX2NvbnRyYWN0cy52MSIsImVzaS1pbmR1c3RyeS5yZWFkX2NvcnBvcmF0aW9uX2pvYnMudjEiLCJlc2ktbWFya2V0cy5yZWFkX2NvcnBvcmF0aW9uX29yZGVycy52MSIsImVzaS1pbmR1c3RyeS5yZWFkX2NoYXJhY3Rlcl9taW5pbmcudjEiLCJlc2ktaW5kdXN0cnkucmVhZF9jb3Jwb3JhdGlvbl9taW5pbmcudjEiXSwianRpIjoiOGM3NjkxMTctNjNhNC00ODc4LWFmNjAtMzVhYTVkN2FkNDk5Iiwia2lkIjoiSldULVNpZ25hdHVyZS1LZXkiLCJzdWIiOiJDSEFSQUNURVI6RVZFOjIxMTMxOTc0NzAiLCJhenAiOiJlYWNhYTljZDM2NTk0MTg5ODc3NTQ0ZDg1MTc1MzczNCIsIm5hbWUiOiJUaXAgVG9waGFuZSIsIm93bmVyIjoiWCtSZFNGTGtlVyt3YURrclhzVnRGV1F2UlpZPSIsImV4cCI6MTU3MzI5ODE1NSwiaXNzIjoibG9naW4uZXZlb25saW5lLmNvbSJ9.TQXxxIw1PQevWJ8V8rrKONDCHF_H7g5keCoGigwbqXnhwLfladBALMNVY3xCiphgERztVhMi5Bn5naEk8hCs1MlcluCZeWIY1xHNBQuGgSj7AHJ3KUMALxKTHr-8X4tdNH6V9LM-McghzXqSkN4CG6mMTfnEg7wBFegWzF0Djt0JTy9WalwUNEp1NxGzvL3oR4oH_qi6ZQtR4sLbfKokOlPJgRHPbs-LnyBUBArxbRGS002huVhhxgnlgDv72jmx48sjQg_c6vRcQvz3kmpHxqwU1Vb_e6BnWh9zZhAuJdVnMwbjL4UhFS8KvZX4NqrYu2NIfTqyCLXtPo_OH3aaeg";
		final String scope = "publicData esi-location.read_location.v1 esi-location.read_ship_type.v1 esi-mail.read_mail.v1 esi-skills.read_skills.v1 " +
				"esi-skills.read_skillqueue.v1 esi-wallet.read_character_wallet.v1 esi-wallet.read_corporation_wallet.v1 esi-search.search_structures.v1 esi-clones.read_clones.v1 esi-universe.read_structures.v1 esi-assets.read_assets.v1 esi-planets.manage_planets.v1 esi-fittings.read_fittings.v1 esi-industry.read_character_jobs.v1 esi-markets.read_character_orders.v1 esi-characters.read_blueprints.v1 esi-contracts.read_character_contracts.v1 esi-clones.read_implants.v1 esi-wallet.read_corporation_wallets.v1 esi-characters.read_notifications.v1 esi-corporations.read_divisions.v1 esi-assets.read_corporation_assets.v1 esi-corporations.read_blueprints.v1 esi-contracts.read_corporation_contracts.v1 esi-industry.read_corporation_jobs.v1 esi-markets.read_corporation_orders.v1 esi-industry.read_character_mining.v1 esi-industry.read_corporation_mining.v1";
		final Credential credential = new Credential.Builder( 2113197470 )
				.withAccountName( "Tip Tophane" )
				.withAccessToken( token )
				.withRefreshToken( "/IOJfA0020usbI9dZ5yebQ==" )
				.withDataSource( "Tranquility" )
				.withScope( scope )
				.build();
		// Test
		final List<MiningExtractionEntity> obtained = neoComBackendService.accessTodayMiningExtractions4Pilot( credential );
		// Assertions
		Assertions.assertNotNull( obtained );
		Assertions.assertEquals( 1, obtained.size() );
		Assertions.assertEquals( 2113197470, obtained.get( 0 ).getOwnerId() );
		Assertions.assertEquals( "2020-03-08", obtained.get( 0 ).getExtractionDateName() );
		Assertions.assertEquals( 24, obtained.get( 0 ).getExtractionHour() );
	}

//	@Test
	public void accessTodayMiningExtractions4PilotException() {
		// Given
		final NeoComBackendService neoComBackendService = new NeoComBackendService.Builder()
				.withRetrofitFactory( this.itRetrofitFactory )
				.build();
		final String token = "eyJhbGciOiJSUzI1NiIsImtpZCI6IkpXVC1TaWduYXR1cmUtS2V5IiwidHlwIjoiSldUIn0" +
				".eyJzY3AiOlsicHVibGljRGF0YSIsImVzaS1sb2NhdGlvbi5yZWFkX2xvY2F0aW9uLnYxIiwiZXNpLWxvY2F0aW9uLnJlYWRfc2hpcF90eXBlLnYxIiwiZXNpLW1haWwucmVhZF9tYWlsLnYxIiwiZXNpLXNraWxscy5yZWFkX3NraWxscy52MSIsImVzaS1za2lsbHMucmVhZF9za2lsbHF1ZXVlLnYxIiwiZXNpLXdhbGxldC5yZWFkX2NoYXJhY3Rlcl93YWxsZXQudjEiLCJlc2ktd2FsbGV0LnJlYWRfY29ycG9yYXRpb25fd2FsbGV0LnYxIiwiZXNpLXNlYXJjaC5zZWFyY2hfc3RydWN0dXJlcy52MSIsImVzaS1jbG9uZXMucmVhZF9jbG9uZXMudjEiLCJlc2ktdW5pdmVyc2UucmVhZF9zdHJ1Y3R1cmVzLnYxIiwiZXNpLWFzc2V0cy5yZWFkX2Fzc2V0cy52MSIsImVzaS1wbGFuZXRzLm1hbmFnZV9wbGFuZXRzLnYxIiwiZXNpLWZpdHRpbmdzLnJlYWRfZml0dGluZ3MudjEiLCJlc2ktaW5kdXN0cnkucmVhZF9jaGFyYWN0ZXJfam9icy52MSIsImVzaS1tYXJrZXRzLnJlYWRfY2hhcmFjdGVyX29yZGVycy52MSIsImVzaS1jaGFyYWN0ZXJzLnJlYWRfYmx1ZXByaW50cy52MSIsImVzaS1jb250cmFjdHMucmVhZF9jaGFyYWN0ZXJfY29udHJhY3RzLnYxIiwiZXNpLWNsb25lcy5yZWFkX2ltcGxhbnRzLnYxIiwiZXNpLXdhbGxldC5yZWFkX2NvcnBvcmF0aW9uX3dhbGxldHMudjEiLCJlc2ktY2hhcmFjdGVycy5yZWFkX25vdGlmaWNhdGlvbnMudjEiLCJlc2ktY29ycG9yYXRpb25zLnJlYWRfZGl2aXNpb25zLnYxIiwiZXNpLWFzc2V0cy5yZWFkX2NvcnBvcmF0aW9uX2Fzc2V0cy52MSIsImVzaS1jb3Jwb3JhdGlvbnMucmVhZF9ibHVlcHJpbnRzLnYxIiwiZXNpLWNvbnRyYWN0cy5yZWFkX2NvcnBvcmF0aW9uX2NvbnRyYWN0cy52MSIsImVzaS1pbmR1c3RyeS5yZWFkX2NvcnBvcmF0aW9uX2pvYnMudjEiLCJlc2ktbWFya2V0cy5yZWFkX2NvcnBvcmF0aW9uX29yZGVycy52MSIsImVzaS1pbmR1c3RyeS5yZWFkX2NoYXJhY3Rlcl9taW5pbmcudjEiLCJlc2ktaW5kdXN0cnkucmVhZF9jb3Jwb3JhdGlvbl9taW5pbmcudjEiXSwianRpIjoiOGM3NjkxMTctNjNhNC00ODc4LWFmNjAtMzVhYTVkN2FkNDk5Iiwia2lkIjoiSldULVNpZ25hdHVyZS1LZXkiLCJzdWIiOiJDSEFSQUNURVI6RVZFOjIxMTMxOTc0NzAiLCJhenAiOiJlYWNhYTljZDM2NTk0MTg5ODc3NTQ0ZDg1MTc1MzczNCIsIm5hbWUiOiJUaXAgVG9waGFuZSIsIm93bmVyIjoiWCtSZFNGTGtlVyt3YURrclhzVnRGV1F2UlpZPSIsImV4cCI6MTU3MzI5ODE1NSwiaXNzIjoibG9naW4uZXZlb25saW5lLmNvbSJ9.TQXxxIw1PQevWJ8V8rrKONDCHF_H7g5keCoGigwbqXnhwLfladBALMNVY3xCiphgERztVhMi5Bn5naEk8hCs1MlcluCZeWIY1xHNBQuGgSj7AHJ3KUMALxKTHr-8X4tdNH6V9LM-McghzXqSkN4CG6mMTfnEg7wBFegWzF0Djt0JTy9WalwUNEp1NxGzvL3oR4oH_qi6ZQtR4sLbfKokOlPJgRHPbs-LnyBUBArxbRGS002huVhhxgnlgDv72jmx48sjQg_c6vRcQvz3kmpHxqwU1Vb_e6BnWh9zZhAuJdVnMwbjL4UhFS8KvZX4NqrYu2NIfTqyCLXtPo_OH3aaeg";
		final String scope = "publicData esi-location.read_location.v1 esi-location.read_ship_type.v1 esi-mail.read_mail.v1 esi-skills.read_skills.v1 " +
				"esi-skills.read_skillqueue.v1 esi-wallet.read_character_wallet.v1 esi-wallet.read_corporation_wallet.v1 esi-search.search_structures.v1 esi-clones.read_clones.v1 esi-universe.read_structures.v1 esi-assets.read_assets.v1 esi-planets.manage_planets.v1 esi-fittings.read_fittings.v1 esi-industry.read_character_jobs.v1 esi-markets.read_character_orders.v1 esi-characters.read_blueprints.v1 esi-contracts.read_character_contracts.v1 esi-clones.read_implants.v1 esi-wallet.read_corporation_wallets.v1 esi-characters.read_notifications.v1 esi-corporations.read_divisions.v1 esi-assets.read_corporation_assets.v1 esi-corporations.read_blueprints.v1 esi-contracts.read_corporation_contracts.v1 esi-industry.read_corporation_jobs.v1 esi-markets.read_corporation_orders.v1 esi-industry.read_character_mining.v1 esi-industry.read_corporation_mining.v1";
		final Credential credential = new Credential.Builder( 2113197470 )
				.withAccountName( "Tip Tophane" )
				.withAccessToken( token )
				.withRefreshToken( "/IOJfA0020usbI9dZ5yebQ==" )
				.withDataSource( "Tranquility" )
				.withScope( scope )
				.build();
		// When
		Mockito.doThrow( RuntimeException.class ).when( this.itRetrofitFactory ).accessBackendConnector();
		// Test
		final List<MiningExtractionEntity> obtained = neoComBackendService.accessTodayMiningExtractions4Pilot( credential );
		// Assertions
		Assertions.assertNotNull( obtained );
		Assertions.assertEquals( 0, obtained.size() );
	}

	@Test
	public void buildComplete() {
		final RetrofitFactory retrofitFactory = Mockito.mock( RetrofitFactory.class );
		final NeoComBackendService neoComBackendService = new NeoComBackendService.Builder()
				.withRetrofitFactory( retrofitFactory )
				.build();
		Assertions.assertNotNull( neoComBackendService );
	}

	@Test
	public void buildFailure() {
		final RetrofitFactory retrofitFactory = Mockito.mock( RetrofitFactory.class );
		Assertions.assertThrows( NullPointerException.class, () -> {
			final NeoComBackendService neoComBackendService = new NeoComBackendService.Builder()
					.withRetrofitFactory( null )
					.build();
		} );
		Assertions.assertThrows( NullPointerException.class, () -> {
			final NeoComBackendService neoComBackendService = new NeoComBackendService.Builder()
					.build();
		} );
	}

	@Test
	public void storeCredential() {
		// Given
		final NeoComBackendService neoComBackendService = new NeoComBackendService.Builder()
				.withRetrofitFactory( this.itRetrofitFactory )
				.build();
		final String token = "eyJhbGciOiJSUzI1NiIsImtpZCI6IkpXVC1TaWduYXR1cmUtS2V5IiwidHlwIjoiSldUIn0" +
				".eyJzY3AiOlsicHVibGljRGF0YSIsImVzaS1sb2NhdGlvbi5yZWFkX2xvY2F0aW9uLnYxIiwiZXNpLWxvY2F0aW9uLnJlYWRfc2hpcF90eXBlLnYxIiwiZXNpLW1haWwucmVhZF9tYWlsLnYxIiwiZXNpLXNraWxscy5yZWFkX3NraWxscy52MSIsImVzaS1za2lsbHMucmVhZF9za2lsbHF1ZXVlLnYxIiwiZXNpLXdhbGxldC5yZWFkX2NoYXJhY3Rlcl93YWxsZXQudjEiLCJlc2ktd2FsbGV0LnJlYWRfY29ycG9yYXRpb25fd2FsbGV0LnYxIiwiZXNpLXNlYXJjaC5zZWFyY2hfc3RydWN0dXJlcy52MSIsImVzaS1jbG9uZXMucmVhZF9jbG9uZXMudjEiLCJlc2ktdW5pdmVyc2UucmVhZF9zdHJ1Y3R1cmVzLnYxIiwiZXNpLWFzc2V0cy5yZWFkX2Fzc2V0cy52MSIsImVzaS1wbGFuZXRzLm1hbmFnZV9wbGFuZXRzLnYxIiwiZXNpLWZpdHRpbmdzLnJlYWRfZml0dGluZ3MudjEiLCJlc2ktaW5kdXN0cnkucmVhZF9jaGFyYWN0ZXJfam9icy52MSIsImVzaS1tYXJrZXRzLnJlYWRfY2hhcmFjdGVyX29yZGVycy52MSIsImVzaS1jaGFyYWN0ZXJzLnJlYWRfYmx1ZXByaW50cy52MSIsImVzaS1jb250cmFjdHMucmVhZF9jaGFyYWN0ZXJfY29udHJhY3RzLnYxIiwiZXNpLWNsb25lcy5yZWFkX2ltcGxhbnRzLnYxIiwiZXNpLXdhbGxldC5yZWFkX2NvcnBvcmF0aW9uX3dhbGxldHMudjEiLCJlc2ktY2hhcmFjdGVycy5yZWFkX25vdGlmaWNhdGlvbnMudjEiLCJlc2ktY29ycG9yYXRpb25zLnJlYWRfZGl2aXNpb25zLnYxIiwiZXNpLWFzc2V0cy5yZWFkX2NvcnBvcmF0aW9uX2Fzc2V0cy52MSIsImVzaS1jb3Jwb3JhdGlvbnMucmVhZF9ibHVlcHJpbnRzLnYxIiwiZXNpLWNvbnRyYWN0cy5yZWFkX2NvcnBvcmF0aW9uX2NvbnRyYWN0cy52MSIsImVzaS1pbmR1c3RyeS5yZWFkX2NvcnBvcmF0aW9uX2pvYnMudjEiLCJlc2ktbWFya2V0cy5yZWFkX2NvcnBvcmF0aW9uX29yZGVycy52MSIsImVzaS1pbmR1c3RyeS5yZWFkX2NoYXJhY3Rlcl9taW5pbmcudjEiLCJlc2ktaW5kdXN0cnkucmVhZF9jb3Jwb3JhdGlvbl9taW5pbmcudjEiXSwianRpIjoiOGM3NjkxMTctNjNhNC00ODc4LWFmNjAtMzVhYTVkN2FkNDk5Iiwia2lkIjoiSldULVNpZ25hdHVyZS1LZXkiLCJzdWIiOiJDSEFSQUNURVI6RVZFOjIxMTMxOTc0NzAiLCJhenAiOiJlYWNhYTljZDM2NTk0MTg5ODc3NTQ0ZDg1MTc1MzczNCIsIm5hbWUiOiJUaXAgVG9waGFuZSIsIm93bmVyIjoiWCtSZFNGTGtlVyt3YURrclhzVnRGV1F2UlpZPSIsImV4cCI6MTU3MzI5ODE1NSwiaXNzIjoibG9naW4uZXZlb25saW5lLmNvbSJ9.TQXxxIw1PQevWJ8V8rrKONDCHF_H7g5keCoGigwbqXnhwLfladBALMNVY3xCiphgERztVhMi5Bn5naEk8hCs1MlcluCZeWIY1xHNBQuGgSj7AHJ3KUMALxKTHr-8X4tdNH6V9LM-McghzXqSkN4CG6mMTfnEg7wBFegWzF0Djt0JTy9WalwUNEp1NxGzvL3oR4oH_qi6ZQtR4sLbfKokOlPJgRHPbs-LnyBUBArxbRGS002huVhhxgnlgDv72jmx48sjQg_c6vRcQvz3kmpHxqwU1Vb_e6BnWh9zZhAuJdVnMwbjL4UhFS8KvZX4NqrYu2NIfTqyCLXtPo_OH3aaeg";
		final String scope = "publicData esi-location.read_location.v1 esi-location.read_ship_type.v1 esi-mail.read_mail.v1 esi-skills.read_skills.v1 " +
				"esi-skills.read_skillqueue.v1 esi-wallet.read_character_wallet.v1 esi-wallet.read_corporation_wallet.v1 esi-search.search_structures.v1 esi-clones.read_clones.v1 esi-universe.read_structures.v1 esi-assets.read_assets.v1 esi-planets.manage_planets.v1 esi-fittings.read_fittings.v1 esi-industry.read_character_jobs.v1 esi-markets.read_character_orders.v1 esi-characters.read_blueprints.v1 esi-contracts.read_character_contracts.v1 esi-clones.read_implants.v1 esi-wallet.read_corporation_wallets.v1 esi-characters.read_notifications.v1 esi-corporations.read_divisions.v1 esi-assets.read_corporation_assets.v1 esi-corporations.read_blueprints.v1 esi-contracts.read_corporation_contracts.v1 esi-industry.read_corporation_jobs.v1 esi-markets.read_corporation_orders.v1 esi-industry.read_character_mining.v1 esi-industry.read_corporation_mining.v1";
		final Credential credential = new Credential.Builder( 2113197470 )
				.withAccountName( "Tip Tophane" )
				.withAccessToken( token )
				.withRefreshToken( "/IOJfA0020usbI9dZ5yebQ==" )
				.withDataSource( "Tranquility" )
				.withScope( scope )
				.build();

		// Test
		final String expected = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9" +
				".eyJzdWIiOiJFU0kgT0F1dGgyIEF1dGhlbnRpY2F0aW9uIiwiY29ycG9yYXRpb25JZCI6OTgzODQ3MjYsImFjY291bnROYW1lIjoiVGVzdGluZyBDaGFyYWN0ZXIgQWNjb3VudCIsImlzcyI6Ik5lb0NvbS5JbmZpbml0eS5CYWNrZW5kIiwidW5pcXVlSWQiOiJ0cmFucXVpbGl0eS85MzgxMzMxMCIsInBpbG90SWQiOjkzODEzMzEwfQ.eJvBC2144s7sKv5rxSUVEjNbP2BpQJlJhmTOu4AJ9eJj9so_WcrAthbvwgYM4BqyBSNZAjw7bVegieWqx8IX8Q";
		final CredentialStoreResponse obtained = neoComBackendService.storeCredential( credential );
		Assertions.assertEquals( expected, obtained.getJwtToken() );
	}
}
