package de.jClipCorn.util;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import de.jClipCorn.gui.localization.LocaleBundle;

public class FileChooserHelper {
	//TODO Diese ganze Klasse löschen und überall FileNameExtensionFilter nehmen
	public static FileFilter createFileFilter(final String description, final String[] extensions) {
		return new FileFilter() {

			@Override
			public String getDescription() {
				return description; 
			}

			@Override
			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				}

				String ext = PathFormatter.getExtension(f.getAbsolutePath());
				for (int i = 0; i < extensions.length; i++) {
					if (ext.equalsIgnoreCase(extensions[i])) {
						return true;
					}
				}

				return false;
			}
		};
	}
	
	public static FileFilter createFileFilter(String description, String ext1) {
		String[] arr = {ext1};
		return createFileFilter(description, arr);
	}
	
	public static FileFilter createFileFilter(String description, String ext1, String ext2) {
		String[] arr = {ext1, ext2};
		return createFileFilter(description, arr);
	}
	
	public static FileFilter createFileFilter(String description, String ext1, String ext2, String ext3) {
		String[] arr = {ext1, ext2, ext3};
		return createFileFilter(description, arr);
	}
	
	public static FileFilter createFileFilter(String description, String ext1, String ext2, String ext3, String ext4) {
		String[] arr = {ext1, ext2, ext3, ext4};
		return createFileFilter(description, arr);
	}
	
	public static FileFilter createFileFilter(String description, String ext1, String ext2, String ext3, String ext4, String ext5) {
		String[] arr = {ext1, ext2, ext3, ext4, ext5};
		return createFileFilter(description, arr);
	}
	
	//################################################################################################################################
	
	public static FileFilter createLocalFileFilter(String description, String ext1) {
		String[] arr = {ext1};
		return createFileFilter(LocaleBundle.getString(description), arr);
	}
	
	public static FileFilter createLocalFileFilter(String description, String ext1, String ext2) {
		String[] arr = {ext1, ext2};
		return createFileFilter(LocaleBundle.getString(description), arr);
	}
	
	public static FileFilter createLocalFileFilter(String description, String ext1, String ext2, String ext3) {
		String[] arr = {ext1, ext2, ext3};
		return createFileFilter(LocaleBundle.getString(description), arr);
	}
	
	public static FileFilter createLocalFileFilter(String description, String ext1, String ext2, String ext3, String ext4) {
		String[] arr = {ext1, ext2, ext3, ext4};
		return createFileFilter(LocaleBundle.getString(description), arr);
	}
	
	public static FileFilter createLocalFileFilter(String description, String ext1, String ext2, String ext3, String ext4, String ext5) {
		String[] arr = {ext1, ext2, ext3, ext4, ext5};
		return createFileFilter(LocaleBundle.getString(description), arr);
	}
	
	//################################################################################################################################
	
	public static FileFilter createFileFilter(final String description, final Validator<String> validator) {
		return new FileFilter() {

			@Override
			public String getDescription() {
				return description; 
			}

			@Override
			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				}

				String ext = PathFormatter.getExtension(f.getAbsolutePath());
				if (validator.validate(ext)) {
					return true;
				}

				return false;
			}
		};
	}
	
	public static FileFilter createFullValidateFileFilter(final String description, final Validator<String> validator) {
		return new FileFilter() {

			@Override
			public String getDescription() {
				return description; 
			}

			@Override
			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				}
				
				if (validator.validate(f.getAbsolutePath())) {
					return true;
				}

				return false;
			}
		};
	}
	
	public static FileFilter createLocalFileFilter(final String ident, final Validator<String> validator) {
		return createFileFilter(LocaleBundle.getString(ident), validator);
	}
}
