package de.jClipCorn.features.metadata;

import de.jClipCorn.features.metadata.exceptions.MetadataQueryException;
import de.jClipCorn.util.filesystem.FSPath;

import java.io.IOException;

public interface MetadataSource {

	PartialMediaInfo run(FSPath filename) throws IOException, MetadataQueryException;

	String getFullOutput(FSPath filename, PartialMediaInfo result) throws IOException, MetadataQueryException;

	boolean isConfiguredAndRunnable();
}
