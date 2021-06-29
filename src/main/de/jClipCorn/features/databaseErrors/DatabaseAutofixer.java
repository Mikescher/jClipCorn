package de.jClipCorn.features.databaseErrors;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.*;
import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.DriveMap;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.filesystem.CCPath;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.formatter.FileSizeFormatter;
import de.jClipCorn.util.helper.ImageUtilities;
import de.jClipCorn.util.listener.ProgressCallbackListener;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class DatabaseAutofixer {
	public static boolean fixErrors(List<DatabaseError> list, ProgressCallbackListener pcl) {
		DriveMap.tryWait();
		
		pcl.setMax(list.size());
		pcl.reset();
		
		boolean fullsuccess = true;
		
		for (int i = 0; i < list.size(); i++) {
			pcl.step();
			
			DatabaseError error = list.get(i);
			
			if (! canFix(list, error)) continue;
			
			boolean succval = error.autoFix();
			
			if(! succval) {
				CCLog.addWarning(LocaleBundle.getFormattedString("CheckDatabaseDialog.Autofix.problem", error.getErrorString(), error.getElement1Name())); //$NON-NLS-1$
				fullsuccess = false;
			}
		}
		
		pcl.reset();
		
		return fullsuccess;
	}
	
	private static List<DatabaseError> getAllWithSameElement(List<DatabaseError> list, DatabaseError element) {
		List<DatabaseError> result = new ArrayList<>();
		
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).elementsEquals(element)) {
				result.add(list.get(i));
			}
		}
		
		return result;
	}
	
	public static boolean canFix(List<DatabaseError> list, List<DatabaseError> error) {
		for (DatabaseError err : error) {
			if (! canFix(list, err)) return false;
		}
		
		return true;
	}
	
	public static boolean canFix(List<DatabaseError> list, DatabaseError error) {
		if (! error.isAutoFixable()) { // Test if this File is Fixable
			return false;
		}
		
		List<DatabaseError> errlist = getAllWithSameElement(list, error); // Test if all Errors with this File are Fixable
		
		boolean succ = true;
		for (int j = 0; j < errlist.size(); j++) {
			DatabaseError lerror = errlist.get(j);
			succ &= lerror.isAutoFixable();
		}
		
		if (! succ) {
			return false;
		}
		
		return true;
	}

	public static boolean fixError_Duplicate_Genre(CCMovieList ml, DatabaseError err) {
		if (err.getElement1() instanceof CCDatabaseElement) {
			CCDatabaseElement elem = (CCDatabaseElement) err.getElement1();
			CCGenreList ls = CCGenreList.EMPTY;
			
			boolean[] already_used = new boolean[256];
			for (int i = 0; i < CCGenreList.getMaxListSize(); i++) {
				CCGenre g = elem.Genres.get().getGenre(i);
				
				if (! (g.isEmpty() || already_used[g.asInt()])) {
					ls = ls.getTryAddGenre(g);
					
					already_used[g.asInt()] = true;
				}
			}
			
			elem.Genres.set(ls);
			
			return true;
		}
		
		return false;
	}
	
	public static boolean fixError_Incontinous_Genrelist(CCMovieList ml, DatabaseError err) {
		if (err.getElement1() instanceof CCDatabaseElement) {
			CCDatabaseElement elem = (CCDatabaseElement) err.getElement1();
			CCGenreList ls = CCGenreList.EMPTY;
			
			for (int i = 0; i < CCGenreList.getMaxListSize(); i++) {
				if (! elem.Genres.get().getGenre(i).isEmpty()) {
					ls = ls.getTryAddGenre(elem.Genres.get().getGenre(i));
				}
			}
			
			elem.Genres.set(ls);
			
			return true;
		}
		
		return false;
	}
	
	public static boolean fixError_Wrong_Filesize(CCMovieList ml, DatabaseError err) {
		if (err.getElement1() instanceof CCMovie) {
			CCFileSize size = CCFileSize.ZERO;
			for (int i = 0; i < ((CCMovie) err.getElement1()).getPartcount(); i++) {
				size = CCFileSize.addBytes(size, FileSizeFormatter.getFileSize(((CCMovie) err.getElement1()).Parts.get(i).toFSPath()));
			}
			
			if (size.getBytes() == 0) {
				return false;
			}
			
			((CCMovie) err.getElement1()).FileSize.set(size);
			
			return true;
		} else if (err.getElement1() instanceof CCSeries) {
			return false;
		} else if (err.getElement1() instanceof CCSeason) {
			return false;
		} else if (err.getElement1() instanceof CCEpisode) {
			long size = FileSizeFormatter.getFileSize(((CCEpisode) err.getElement1()).getPart().toFSPath());
			
			if (size == 0) {
				return false;
			}
			
			((CCEpisode)err.getElement1()).FileSize.set(size);
			
			return true;
		}
		
		return false;
	}
	
	public static boolean fixError_Incontinous_Parts(CCMovieList ml, DatabaseError err) {
		if (err.getElement1() instanceof CCMovie) {
			List<CCPath> parts = new ArrayList<>();
			
			for (int i = 0; i < ((CCMovie)err.getElement1()).getPartcount(); i++) {
				if (! ((CCMovie)err.getElement1()).Parts.get(i).isEmpty()) {
					parts.add(((CCMovie)err.getElement1()).Parts.get(i));
				}
			}
			
			for (int i = 0; i < CCMovie.PARTCOUNT_MAX; i++) {
				((CCMovie)err.getElement1()).Parts.reset(i);
			}
			
			for (int i = 0; i < parts.size(); i++) {
				((CCMovie)err.getElement1()).Parts.set(i, parts.get(i));
			}
			
			return true;
		} else if (err.getElement1() instanceof CCSeries) {
			return false;
		} else if (err.getElement1() instanceof CCSeason) {
			return false;
		} else if (err.getElement1() instanceof CCEpisode) {
			return false;
		}
		
		return false;
	}
	
	public static boolean fixError_Format_Not_Found(CCMovieList ml, DatabaseError err) {
		if (err.getElement1() instanceof CCMovie) {
			CCFileFormat fmt = CCFileFormat.getMovieFormat(((CCMovie)err.getElement1()).Parts.get(0).getExtension());
			if (fmt != null) {
				((CCMovie)err.getElement1()).Format.set(fmt);
				return true;
			} else {
				return false;
			}
		} else if (err.getElement1() instanceof CCSeries) {
			return false;
		} else if (err.getElement1() instanceof CCSeason) {
			return false;
		} else if (err.getElement1() instanceof CCEpisode) {
			((CCEpisode)err.getElement1()).Format.set(CCFileFormat.getMovieFormat(((CCEpisode)err.getElement1()).getPart().getExtension()));
			return true;
		}
		
		return false;
	}
	
	public static boolean fixError_Not_Trimmed(CCMovieList ml, DatabaseError err) {
		if (err.getElement1() instanceof CCMovie) {
			((CCMovie)err.getElement1()).Title.set(((CCMovie)err.getElement1()).getTitle().trim());
			((CCMovie)err.getElement1()).Zyklus.setTitle(((CCMovie)err.getElement1()).getZyklus().getTitle().trim());
			return true;
		} else if (err.getElement1() instanceof CCSeries) {
			((CCSeries)err.getElement1()).Title.set(((CCSeries)err.getElement1()).getTitle().trim());
			return true;
		} else if (err.getElement1() instanceof CCSeason) {
			((CCSeason)err.getElement1()).Title.set(((CCSeason)err.getElement1()).getTitle().trim());
			return true;
		} else if (err.getElement1() instanceof CCEpisode) {
			((CCEpisode)err.getElement1()).Title.set(((CCEpisode)err.getElement1()).getTitle().trim());
			return true;
		}
		
		return false;
	}
	
	public static boolean fixError_Wrong_Filename(CCMovieList ml, DatabaseError err) {
		if (err.getElement1() instanceof CCMovie) {
			CCMovie mov = ((CCMovie)err.getElement1());
			
			for (int i = 0; i < mov.getPartcount(); i++) {
				FSPath opath = mov.Parts.get(i).toFSPath();
				if (! opath.exists()) return false;
				FSPath npath = opath.replaceFilename(mov.generateFilename(i));

				boolean succ = opath.renameToSafe(npath);
				mov.Parts.set(i, CCPath.createFromFSPath(npath));
				
				if (! succ) return false;
			}

			return true;
		} else if (err.getElement1() instanceof CCSeries) {
			return false;
		} else if (err.getElement1() instanceof CCSeason) {
			return false;
		} else if (err.getElement1() instanceof CCEpisode) {
			return false;
		}
		
		return false;
	}
	
	public static boolean fixError_Impossible_WatchNever(CCMovieList ml, DatabaseError err) {
		if (err.getElement1() instanceof CCMovie) {
			CCMovie mov = ((CCMovie)err.getElement1());
			
			mov.Tags.set(CCSingleTag.TAG_WATCH_NEVER, false);

			return true;
		} else if (err.getElement1() instanceof CCSeries) {
			return false;
		} else if (err.getElement1() instanceof CCSeason) {
			return false;
		} else if (err.getElement1() instanceof CCEpisode) {
			CCEpisode epi = ((CCEpisode)err.getElement1());
			
			epi.Tags.set(CCSingleTag.TAG_WATCH_NEVER, false);

			return true;
		}
		
		return false;
	}

	public static boolean fixError_MissingHistory(CCMovieList ml, DatabaseError err) {
		if (err.getElement1() instanceof CCMovie) {
			CCMovie mov = ((CCMovie)err.getElement1());
			
			if (! mov.isViewed()) return false;
			if (mov.ViewedHistory.get().any()) return false;
			
			mov.ViewedHistory.add(CCDateTime.getUnspecified());

			return true;
		} else if (err.getElement1() instanceof CCSeries) {
			return false;
		} else if (err.getElement1() instanceof CCSeason) {
			return false;
		} else if (err.getElement1() instanceof CCEpisode) {
			CCEpisode epi = ((CCEpisode)err.getElement1());
			
			if (! epi.isViewed()) return false;
			if (epi.ViewedHistory.get().any()) return false;
			
			epi.addToViewedHistory(CCDateTime.getUnspecified());

			return true;
		}
		
		return false;
	}

	public static boolean fixError_CoverTooSmall(CCMovieList ml, DatabaseError err) {
		if (err.getElement1() instanceof CCMovie) {
			CCMovie mov = ((CCMovie)err.getElement1());
			
			BufferedImage cover = mov.getCover();
			cover = ImageUtilities.resizeCoverImageForStorage(cover);
			mov.setCover(cover);

			return true;
		} else if (err.getElement1() instanceof CCSeries) {
			CCSeries ser = ((CCSeries)err.getElement1());
			
			BufferedImage cover = ser.getCover();
			cover = ImageUtilities.resizeCoverImageForStorage(cover);
			ser.setCover(cover);

			return true;
		} else if (err.getElement1() instanceof CCSeason) {
			CCSeason sea = ((CCSeason)err.getElement1());
			
			BufferedImage cover = sea.getCover();
			cover = ImageUtilities.resizeCoverImageForStorage(cover);
			sea.setCover(cover);

			return true;
		} else if (err.getElement1() instanceof CCEpisode) {
			return false;
		}
		
		return false;
	}

	public static boolean fixError_CoverTooBig(CCMovieList ml, DatabaseError err) {
		if (err.getElement1() instanceof CCMovie) {
			CCMovie mov = ((CCMovie)err.getElement1());
			
			BufferedImage cover = mov.getCover();
			cover = ImageUtilities.resizeCoverImageForStorage(cover);
			mov.setCover(cover);

			return true;
		} else if (err.getElement1() instanceof CCSeries) {
			CCSeries ser = ((CCSeries)err.getElement1());
			
			BufferedImage cover = ser.getCover();
			cover = ImageUtilities.resizeCoverImageForStorage(cover);
			ser.setCover(cover);

			return true;
		} else if (err.getElement1() instanceof CCSeason) {
			CCSeason sea = ((CCSeason)err.getElement1());
			
			BufferedImage cover = sea.getCover();
			cover = ImageUtilities.resizeCoverImageForStorage(cover);
			sea.setCover(cover);

			return true;
		} else if (err.getElement1() instanceof CCEpisode) {
			return false;
		}
		
		return false;
	}

	public static boolean fixError_UnusedGroup(CCMovieList ml, DatabaseError err) {
		if (err.getElement1() instanceof CCGroup) {

			ml.removeGroup((CCGroup)err.getElement1());
			return true;
		}

		return false;
	}

	public static boolean fixError_NonNormalizedPath(CCMovieList ml, DatabaseError err) {
		if (err.getElement1() instanceof CCEpisode) {

			var pold = ((CCEpisode)err.getElement1()).getPart();
			var pnew = CCPath.createFromFSPath(pold.toFSPath());

			if (pold.equals(pnew)) return false;

			((CCEpisode)err.getElement1()).Part.set(pnew);

			return true;
		} else if (err.getElement1() instanceof CCMovie) {
			for (int i = 0; i < ((CCMovie)err.getElement1()).getPartcount(); i++) {
				var pold = ((CCMovie)err.getElement1()).Parts.get(i);
				var pnew = CCPath.createFromFSPath(pold.toFSPath());

				if (pold.equals(pnew)) continue;

				((CCMovie)err.getElement1()).Parts.set(i, pnew);
				return true;
			}

			return false;
		}

		return false;
	}

	public static boolean fixError_MediaInfoFilesizeMismatch(CCMovieList ml, DatabaseError err) {
		if (err.getElement1() instanceof ICCPlayableElement) {
			ICCPlayableElement elem = (ICCPlayableElement) err.getElement1();
			CCMediaInfo mi = elem.mediaInfo().get();
			if (!mi.isSet()) return false;

			long fsz = 0;
			for (var p : elem.getParts()) {
				File f = p.toFSPath().toFile();
				if (!f.exists()) return false;
				fsz += f.length();
			}

			if (fsz != mi.getFilesize()) return false;

			elem.fileSize().set(fsz);
			return true;
		}

		return false;
	}

	public static boolean fixError_InvalidCharacters(CCMovieList ml, DatabaseError err) {
		if (err.getElement1() instanceof CCMovie)
		{
			var elem = (CCMovie) err.getElement1();

			var newtitle  = DatabaseStringNormalization.fixInvalidCharacters(elem.getTitle());
			if (!newtitle.equals(elem.getTitle())) { elem.Title.set(newtitle); return true; }

			var newzyklus = DatabaseStringNormalization.fixInvalidCharacters(elem.getZyklus().getTitle());
			if (!newzyklus.equals(elem.getZyklus().getTitle())) { elem.Zyklus.setTitle(newzyklus); return true; }
		}
		else if (err.getElement1() instanceof CCSeries)
		{
			var elem = (CCSeries) err.getElement1();

			var newtitle  = DatabaseStringNormalization.fixInvalidCharacters(elem.getTitle());
			if (!newtitle.equals(elem.getTitle())) { elem.Title.set(newtitle); return true; }
		}
		else if (err.getElement1() instanceof CCSeason)
		{
			var elem = (CCSeason) err.getElement1();

			var newtitle  = DatabaseStringNormalization.fixInvalidCharacters(elem.getTitle());
			if (!newtitle.equals(elem.getTitle())) { elem.Title.set(newtitle); return true; }
		}
		else if (err.getElement1() instanceof CCEpisode)
		{
			var elem = (CCEpisode) err.getElement1();

			var newtitle  = DatabaseStringNormalization.fixInvalidCharacters(elem.getTitle());
			if (!newtitle.equals(elem.getTitle())) { elem.Title.set(newtitle); return true; }
		}

		return false;
	}

	public static boolean fixError_MediaInfoCDate(CCMovieList ml, DatabaseError err) {
		if (err.getElement1() instanceof ICCPlayableElement) {
			ICCPlayableElement elem = (ICCPlayableElement) err.getElement1();
			CCMediaInfo mi = elem.mediaInfo().get();
			if (!mi.isSet()) return false;

			try {
				BasicFileAttributes attr = elem.getParts().get(0).toFSPath().readFileAttr();

				var cdate = attr.creationTime().toMillis();

				elem.mediaInfo().CDate.set(cdate);
				return true;

			} catch (IOException ex) {
				return false;
			}
		}

		return false;
	}

	public static boolean fixError_MediaInfoMDate(CCMovieList ml, DatabaseError err) {
		if (err.getElement1() instanceof ICCPlayableElement) {
			ICCPlayableElement elem = (ICCPlayableElement) err.getElement1();
			CCMediaInfo mi = elem.mediaInfo().get();
			if (!mi.isSet()) return false;

			try {
				BasicFileAttributes attr = elem.getParts().get(0).toFSPath().readFileAttr();

				var mdate = attr.lastModifiedTime().toMillis();

				elem.mediaInfo().MDate.set(mdate);
				return true;

			} catch (IOException ex) {
				return false;
			}
		}

		return false;
	}
}
