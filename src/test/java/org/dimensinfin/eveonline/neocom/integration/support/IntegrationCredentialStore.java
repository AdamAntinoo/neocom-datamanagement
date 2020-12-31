package org.dimensinfin.eveonline.neocom.integration.support;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;

import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.provider.IFileSystem;

public class IntegrationCredentialStore {
	private static final ObjectMapper mapper = new ObjectMapper();

	private Credential credential;
	private IFileSystem fileSystemAdapter;

	private IntegrationCredentialStore() {}

	public Credential readCredential() throws IOException {
		this.credential = this.mapper.readValue( FileUtils.readFileToString(
				new File( this.fileSystemAdapter.accessResource4Path( "/TestData/integrationCredential.json" ) ),
				"utf-8" ), Credential.class );
		return this.credential;
	}

	public void writeCredential( final Credential credential )  {
		try {
			this.credential = credential;
			mapper.writeValue(
					new File(this.fileSystemAdapter.accessResource4Path( "TestData/integrationCredential.json" ) ),
					this.credential);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// - B U I L D E R
	public static class Builder {
		private IntegrationCredentialStore onConstruction;

		public Builder() {
			this.onConstruction = new IntegrationCredentialStore();
		}

		public IntegrationCredentialStore.Builder withFileSystemAdapter( final IFileSystem fileSystemAdapter ) {
			Objects.requireNonNull( fileSystemAdapter );
			this.onConstruction.fileSystemAdapter = fileSystemAdapter;
			return this;
		}

		public IntegrationCredentialStore build() {
			return this.onConstruction;
		}
	}
}
