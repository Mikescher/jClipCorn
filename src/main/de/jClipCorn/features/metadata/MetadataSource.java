package de.jClipCorn.features.metadata;

import de.jClipCorn.features.metadata.exceptions.MetadataQueryException;

import java.io.IOException;

public interface MetadataSource {

	PartialMediaInfo run(String filename) throws IOException, MetadataQueryException;

	String getFullOutput(String filename, PartialMediaInfo result) throws IOException, MetadataQueryException;

	boolean isConfiguredAndRunnable();
}
