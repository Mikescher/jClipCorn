package de.jClipCorn.features.serialization.xmlimport.impl;

/**
 * V11 replaced the integer element ids with UUIDs (attributes {@code localid}/{@code seasonid}
 * became {@code id}). Those attributes are export-only, so reading is unchanged.
 */
public class DatabaseXMLImportImpl_V11 extends DatabaseXMLImportImpl_V10 {
}
