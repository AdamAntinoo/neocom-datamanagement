package org.dimensinfin.eveonline.neocom.support;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import com.annimon.stream.Stream;

import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;
import org.dimensinfin.eveonline.neocom.provider.AConfigurationService;
import org.dimensinfin.eveonline.neocom.service.logger.NeoComLogger;
import org.dimensinfin.logging.LogWrapper;

public class TestConfigurationService extends AConfigurationService {
	public void readAllProperties() {
		NeoComLogger.enter();
		try {
			final List<String> propertyFiles = this.getResourceFiles( this.getResourceLocation() );
			Stream.of( propertyFiles )
					.sorted()
					.forEach( ( fileName ) -> {
						NeoComLogger.info( MessageFormat.format( "Processing file: {0}", fileName ) );
						try {
							Properties properties = new Properties();
							properties.load( new FileInputStream( fileName ) );
							// Copy properties to globals.
							configurationProperties.putAll( properties );
						} catch (IOException ioe) {
							NeoComLogger.error( "E [TestConfigurationService.readAllProperties]> Exception reading properties file " +
									fileName, ioe );
							ioe.printStackTrace();
						}
					} );
		} catch (final IOException ioe) {
			NeoComLogger.error( ioe );
			throw new NeoComRuntimeException( ioe.getMessage() );
		}
		LogWrapper.exit( MessageFormat.format( "Total properties number: {0}", this.contentCount() ) );
	}

	/**
	 * Reads all the files found on the parameter directory path. Because the directory is read as a stream the method to read
	 * the directory does not use the file system isolation.
	 */
	protected List<String> getResourceFiles( final String initialPath ) throws IOException {
		final File rootFolder = new File( System.getProperty( "user.dir" ) + initialPath );
		NeoComLogger.info( MessageFormat.format( "Root directory: {0}", rootFolder.toString() ) );
		return listFilesForFolder( rootFolder );
	}

	public void setProperty( final String propertyName, final String value ) {
		this.configurationProperties.setProperty( propertyName, value );
	}

	private List<String> listFilesForFolder( final File folder ) {
		List<String> filenames = new ArrayList<>();
		for (final File fileEntry : Objects.requireNonNull( folder.listFiles() )) {
			if (fileEntry.isDirectory()) listFilesForFolder( fileEntry );
			else filenames.add( fileEntry.getPath() );
		}
		return filenames;
	}

	// - B U I L D E R
	public static class Builder extends AConfigurationService.Builder<TestConfigurationService, TestConfigurationService.Builder> {
		private TestConfigurationService onConstruction;

		// - G E T T E R S   &   S E T T E R S
		public TestConfigurationService build() {
			return (TestConfigurationService) super.build();
		}

		@Override
		protected TestConfigurationService getActual() {
			if (null == this.onConstruction) this.onConstruction = new TestConfigurationService();
			return this.onConstruction;
		}

		@Override
		protected TestConfigurationService.Builder getActualBuilder() {
			return this;
		}
	}
}
