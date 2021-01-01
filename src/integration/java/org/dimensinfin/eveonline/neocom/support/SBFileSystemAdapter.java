package org.dimensinfin.eveonline.neocom.support;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;

import javax.validation.constraints.NotNull;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.dimensinfin.eveonline.neocom.provider.IFileSystem;
import org.dimensinfin.logging.LogWrapper;

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
	private static final String DIRECTORY_SEPARATOR = "/";
	protected String applicationDirectory = "./NeoCom.Infinity";

	// - C O N S T R U C T O R S
	@Inject
	public SBFileSystemAdapter( final @NotNull @Named("ApplicationDirectory") String applicationDirectory ) {
		this.applicationDirectory = applicationDirectory;
	}

	@Override
	public InputStream openResource4Input( final String filePath ) throws IOException {
		return new FileInputStream( new File( applicationDirectory + DIRECTORY_SEPARATOR + filePath ) );
	}

	@Override
	public OutputStream openResource4Output( final String filePath ) throws IOException {
		return new FileOutputStream( new File( applicationDirectory + DIRECTORY_SEPARATOR + filePath ) );
	}

	@Override
	public InputStream openAsset4Input( final String filePath ) throws IOException {
		URI propertyURI = null;
		try {
			final String executionDirectory = new java.io.File( "." ).getCanonicalPath() + "/build/resources/main/";
			propertyURI = new URI( executionDirectory + filePath );
			LogWrapper.info( MessageFormat.format( "Processing file: {0}", propertyURI.toString() ) );
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
			LogWrapper.info( MessageFormat.format( "Processing file: {0}", propertyURI.toString() ) );
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
		return accessResource4Path( filePath );
	}

	@Override
	public void copyFromAssets( final String sourceFileName, final String destinationDirectory ) {
		// TODO - Implement the copy fom one place to another
	}
	// - C O R E

	@Override
	public String toString() {
		return new ToStringBuilder( this, ToStringStyle.JSON_STYLE )
				.append( "applicationDirectory", this.applicationDirectory )
				.toString();
	}

//	// - B U I L D E R
//	public static class Builder {
//		private SBFileSystemAdapter onConstruction;
//
//// - C O N S T R U C T O R S
//		public Builder() {
//			this.onConstruction = new SBFileSystemAdapter();
//		}
//
//		public SBFileSystemAdapter build() {
//			return this.onConstruction;
//		}
//
//		public SBFileSystemAdapter.Builder optionalApplicationDirectory( final String applicationDirectory ) {
//			if (null != applicationDirectory) this.onConstruction.applicationDirectory = applicationDirectory;
//			return this;
//		}
//	}
}
