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

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.eveonline.neocom.provider.IFileSystem;

/**
 * Spring boot implementation for the File System isolation interface. We can get access to the application generated data
 * files stored on the private application folder or to the application assets deployed with the compiled code.
 *
 * The Assets api will access the readonly application deployed files while the Resource api will deal with the temporary
 * application storage files like cache or running stored local data.
 *
 * @author Adam Antinoo
 */
public class SBFileSystemAdapter implements IFileSystem {
	private static Logger logger = LoggerFactory.getLogger( SBFileSystemAdapter.class );

	protected String applicationDirectory = "./NeoCom.Infinity";

	// - C O N S T R U C T O R S
	protected SBFileSystemAdapter() {}

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
			final String executionDirectory = new File( "." ).getCanonicalPath() + "/build/resources/main/";
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
			final String executionDirectory = new File( "." ).getCanonicalPath() + "/build/resources/main/";
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
		// TODO - Implement the copy fom one place to another
	}

	// - C O R E
	@Override
	public String toString() {
		return new ToStringBuilder( this, ToStringStyle.JSON_STYLE )
				.append( "applicationFolder", applicationDirectory )
				.toString();
	}

	// - B U I L D E R
	public static class Builder {
		private SBFileSystemAdapter onConstruction;

		public Builder() {
			this.onConstruction = new SBFileSystemAdapter();
		}

		public SBFileSystemAdapter.Builder optionalApplicationDirectory( final String applicationDirectory ) {
			Objects.requireNonNull( applicationDirectory );
			this.onConstruction.applicationDirectory = applicationDirectory;
			return this;
		}

		public SBFileSystemAdapter build() {
			return this.onConstruction;
		}
	}
}
