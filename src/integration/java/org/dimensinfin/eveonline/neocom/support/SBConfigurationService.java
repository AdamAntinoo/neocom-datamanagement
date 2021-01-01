package org.dimensinfin.eveonline.neocom.support;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import javax.validation.constraints.NotNull;

import com.annimon.stream.Stream;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import org.dimensinfin.eveonline.neocom.provider.AConfigurationService;
import org.dimensinfin.logging.LogWrapper;

public class SBConfigurationService extends AConfigurationService {
	// - C O N S T R U C T O R S
	@Inject
	public SBConfigurationService( final @NotNull @Named("PropertiesDirectory") String propertiesDirectory ) {
		this.configuredPropertiesDirectory = propertiesDirectory;
		this.readAllProperties();
	}

	public void setProperty( final String propertyName, final String value ) {
		this.configurationProperties.setProperty( propertyName, value );
	}

	/**
	 * Reads all the files found on the parameter directory path. Because the directory is read as a stream the method to read
	 * the directory does not use the file system isolation.
	 */
	protected List<String> getResourceFiles( final String initialPath ) throws IOException {
		final File rootFolder = new File( System.getProperty( "user.dir" ) + initialPath );
		LogWrapper.info( MessageFormat.format( "User directory: {0}", System.getProperty( "user.dir" ) ) );
		LogWrapper.info( MessageFormat.format( "Configured path: {0}", initialPath ) );
		LogWrapper.info( MessageFormat.format( "Root directory: {0}", rootFolder.toString() ) );
		return this.listFilesForFolder( rootFolder );
	}

	public void readAllProperties() {
		LogWrapper.enter();
		// Read all .properties files under the predefined path on the /resources folder.
		List<String> propertyFiles;
		try {
			propertyFiles = this.getResourceFiles( this.getResourceLocation() );
		} catch (final IOException ioe) {
			LogWrapper.error( ioe );
			propertyFiles = new ArrayList<>();
		}
		Stream.of( propertyFiles )
				.sorted()
				.forEach( fileName -> {
					LogWrapper.info( MessageFormat.format( "Processing file: {0}", fileName ) );
					try {
						final Properties properties = new Properties();
						properties.load( new FileInputStream( fileName ) );
						// Copy properties to globals.
						configurationProperties.putAll( properties );
					} catch (IOException ioe) {
						LogWrapper.error( "Exception reading properties file " + fileName, ioe );
					}
				} );
		LogWrapper.exit( MessageFormat.format( "> Total properties number: {0}", this.contentCount() + "" ) );
	}

	private List<String> listFilesForFolder( final File folder ) throws IOException {
		final List<String> filenames = new ArrayList<>();
		try {
			Objects.requireNonNull( folder.listFiles() );
		} catch (NullPointerException npe) {
			throw new IOException( "The properties location is empty or not found." );
		}
		for (final File fileEntry : Objects.requireNonNull( folder.listFiles() )) {
			if (fileEntry.isDirectory()) listFilesForFolder( fileEntry );
			else filenames.add( fileEntry.getPath() );
		}
		return filenames;
	}
}
