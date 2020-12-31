package org.dimensinfin.eveonline.neocom.provider;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface IFileSystem {
	InputStream openResource4Input( final String filePath ) throws IOException;

	OutputStream openResource4Output( String filePath ) throws IOException;

	InputStream openAsset4Input( final String filePath ) throws IOException;

	String accessAsset4Path( final String filePath ) throws IOException;

	String accessResource4Path( final String filePath ) throws IOException;

	String accessPublicResource4Path( final String filePath ) throws IOException;

	void copyFromAssets( final String sourceFileName, final String destinationDirectory );
}
