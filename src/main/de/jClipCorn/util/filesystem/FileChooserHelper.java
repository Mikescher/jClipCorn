package de.jClipCorn.util.filesystem;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.Validator;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class FileChooserHelper {
	public static FileFilter createFileFilter(final String description, final String[] extensions) {
		return new FileFilter() {
			@Override
			public String getDescription() {
				return description; 
			}

			@Override
			public boolean accept(File _f)
			{
				var f = FSPath.create(_f);

				if (f.isDirectory()) return true;

				String ext = f.getExtension();
				for (String extension : extensions)
				{
					if (ext.equalsIgnoreCase(extension)) return true;
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

	public static FileFilter createLocalFileFilter(String description, String ext1, String ext2, String ext3, String ext4, String ext5, String ext6) {
		String[] arr = {ext1, ext2, ext3, ext4, ext5, ext6};
		return createFileFilter(LocaleBundle.getString(description), arr);
	}

	public static FileFilter createLocalFileFilter(String description, String ext1, String ext2, String ext3, String ext4, String ext5, String ext6, String ext7) {
		String[] arr = {ext1, ext2, ext3, ext4, ext5, ext6, ext7};
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
			public boolean accept(File _f)
			{
				var f = FSPath.create(_f);

				if (f.isDirectory()) return true;

				String ext = f.getExtension();
				if (validator.validate(ext)) return true;

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
			public boolean accept(File _f)
			{
				var f = FSPath.create(_f);

				if (f.isDirectory()) return true;
				
				if (validator.validate(f.toAbsolutePathString())) {
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
