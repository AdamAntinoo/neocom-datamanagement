package org.dimensinfin.eveonline.neocom.support;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.eveonline.neocom.provider.IFileSystem;

public class SupportFileSystem implements IFileSystem {
	private static final Logger logger = LoggerFactory.getLogger( SupportFileSystem.class );
	private static final String DEFAULT_APPLICATION_FOLDER = "./src/test/NeoCom.UnitTest";

	private String applicationDirectory = DEFAULT_APPLICATION_FOLDER;

	// - C O N S T R U C T O R S
	private SupportFileSystem() {}

	@Override
	public boolean checkWritable( final String filePath ) {
		return true;
	}

	@Override
	public InputStream openResource4Input( final String filePath ) throws IOException {
		return new FileInputStream( new File( applicationDirectory + "/" + filePath ) );
	}

	@Override
	public OutputStream openResource4Output( final String filePath ) throws IOException {
		return new FileOutputStream( new File( applicationDirectory + "/" + filePath ) );
	}

	@Override
	public InputStream openAsset4Input( final String filePath ) throws IOException {
		URI propertyURI = null;
		try {
			final String executionDirectory = new java.io.File( "." ).getCanonicalPath() + "/build/resources/main/";
			propertyURI = new URI( executionDirectory + filePath );
			logger.info( "DD [FileSystemSBImplementation.openAsset4Input]> Processing file: {}", propertyURI );
		} catch (URISyntaxException use) {
		}
		return new FileInputStream( propertyURI.getPath() );
	}

	@Override
	public String accessAsset4Path( final String filePath ) throws IOException {
		URI propertyURI = null;
		try {
			final String executionDirectory = new java.io.File( "." ).getCanonicalPath() + "/build/resources/main/";
			propertyURI = new URI( executionDirectory + filePath );
			logger.info( "DD [FileSystemSBImplementation.accessAsset4Path]> Processing file: {}", propertyURI );
		} catch (URISyntaxException e) {
		}
		return propertyURI.getPath();
	}

	@Override
	public String accessResource4Path( final String filePath ) {
		return applicationDirectory + "/" + filePath;
	}

	@Override
	public String accessPublicResource4Path( final String filePath ) throws IOException {
		return this.accessResource4Path( filePath );
	}

	@Override
	public void copyFromAssets( final String sourceFileName, final String destinationDirectory ) {
	}

	// - B U I L D E R
	public static class Builder {
		private SupportFileSystem onConstruction;

		public Builder() {
			this.onConstruction = new SupportFileSystem();
		}

		public SupportFileSystem.Builder optionalApplicationDirectory( final String applicationDirectory ) {
			Objects.requireNonNull( applicationDirectory );
			this.onConstruction.applicationDirectory = applicationDirectory;
			return this;
		}

		public SupportFileSystem build() {
			return this.onConstruction;
		}
	}
}
