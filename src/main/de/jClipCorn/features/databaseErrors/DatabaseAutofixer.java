package de.jClipCorn.features.databaseErrors;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.*;
import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.DriveMap;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.formatter.FileSizeFormatter;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.helper.ImageUtilities;
import de.jClipCorn.util.listener.ProgressCallbackListener;

import java.awt.image.BufferedImage;
import java.io.File;
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
	
	public static boolean fixError_Invalid_Chars(DatabaseError err) {
		if (err.getElement1() instanceof CCMovie) {
			CCMovie mov = (CCMovie) err.getElement1();
			
			for (int i = 0; i < CCMovie.PARTCOUNT_MAX; i++) {
				if (! mov.getPart(i).isEmpty()) {
					String old = mov.getPart(i);
					String abs = mov.getAbsolutePart(i);
					String rel = PathFormatter.getCCPath(abs.replace("\\", PathFormatter.SERIALIZATION_SEPERATOR)); //$NON-NLS-1$
				
					if (old.length() == rel.length() && rel.length() > 3) {
						mov.setPart(i, rel);
					} else {
						return false;
					}
				}
			}
			
			return true;
		} else if (err.getElement1() instanceof CCEpisode) {
			CCEpisode episode = (CCEpisode) err.getElement1();
			
			if (! episode.getPart().isEmpty()) {
				String old = episode.getPart();
				String abs = episode.getAbsolutePart();
				String rel = PathFormatter.getCCPath(abs.replace("\\", PathFormatter.SERIALIZATION_SEPERATOR)); //$NON-NLS-1$
			
				if (old.length() == rel.length() && rel.length() > 3) {
					episode.setPart(rel);
				} else {
					return false;
				}
			}
			
			return true;
		}
		
		return false;
	}
	
	public static boolean fixError_Duplicate_Genre(DatabaseError err) {
		if (err.getElement1() instanceof CCDatabaseElement) {
			CCDatabaseElement elem = (CCDatabaseElement) err.getElement1();
			CCGenreList ls = CCGenreList.EMPTY;
			
			boolean[] already_used = new boolean[256];
			for (int i = 0; i < CCGenreList.getMaxListSize(); i++) {
				CCGenre g = elem.getGenre(i);
				
				if (! (g.isEmpty() || already_used[g.asInt()])) {
					ls = ls.getTryAddGenre(g);
					
					already_used[g.asInt()] = true;
				}
			}
			
			elem.setGenres(ls);
			
			return true;
		}
		
		return false;
	}
	
	public static boolean fixError_Incontinous_Genrelist(DatabaseError err) {
		if (err.getElement1() instanceof CCDatabaseElement) {
			CCDatabaseElement elem = (CCDatabaseElement) err.getElement1();
			CCGenreList ls = CCGenreList.EMPTY;
			
			for (int i = 0; i < CCGenreList.getMaxListSize(); i++) {
				if (! elem.getGenre(i).isEmpty()) {
					ls = ls.getTryAddGenre(elem.getGenre(i));
				}
			}
			
			elem.setGenres(ls);
			
			return true;
		}
		
		return false;
	}
	
	public static boolean fixError_Wrong_Filesize(DatabaseError err) {
		if (err.getElement1() instanceof CCMovie) {
			CCFileSize size = CCFileSize.ZERO;
			for (int i = 0; i < ((CCMovie) err.getElement1()).getPartcount(); i++) {
				size = CCFileSize.addBytes(size, FileSizeFormatter.getFileSize(((CCMovie) err.getElement1()).getAbsolutePart(i)));
			}
			
			if (size.getBytes() == 0) {
				return false;
			}
			
			((CCMovie) err.getElement1()).setFilesize(size);
			
			return true;
		} else if (err.getElement1() instanceof CCSeries) {
			return false;
		} else if (err.getElement1() instanceof CCSeason) {
			return false;
		} else if (err.getElement1() instanceof CCEpisode) {
			long size = FileSizeFormatter.getFileSize(((CCEpisode) err.getElement1()).getAbsolutePart());
			
			if (size == 0) {
				return false;
			}
			
			((CCEpisode)err.getElement1()).setFilesize(size);
			
			return true;
		}
		
		return false;
	}
	
	public static boolean fixError_Incontinous_Parts(DatabaseError err) {
		if (err.getElement1() instanceof CCMovie) {
			List<String> parts = new ArrayList<>();
			
			for (int i = 0; i < ((CCMovie)err.getElement1()).getPartcount(); i++) {
				if (! ((CCMovie)err.getElement1()).getPart(i).isEmpty()) {
					parts.add(((CCMovie)err.getElement1()).getPart(i));
				}
			}
			
			for (int i = 0; i < CCMovie.PARTCOUNT_MAX; i++) {
				((CCMovie)err.getElement1()).resetPart(i);
			}
			
			for (int i = 0; i < parts.size(); i++) {
				((CCMovie)err.getElement1()).setPart(i, parts.get(i));
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
	
	public static boolean fixError_Format_Not_Found(DatabaseError err) {
		if (err.getElement1() instanceof CCMovie) {
			CCFileFormat fmt = CCFileFormat.getMovieFormat(PathFormatter.getExtension(((CCMovie)err.getElement1()).getAbsolutePart(0)));
			if (fmt != null) {
				((CCMovie)err.getElement1()).setFormat(fmt);
				return true;
			} else {
				return false;
			}
		} else if (err.getElement1() instanceof CCSeries) {
			return false;
		} else if (err.getElement1() instanceof CCSeason) {
			return false;
		} else if (err.getElement1() instanceof CCEpisode) {
			((CCEpisode)err.getElement1()).setFormat(CCFileFormat.getMovieFormat(PathFormatter.getExtension(((CCEpisode)err.getElement1()).getAbsolutePart())));
			return true;
		}
		
		return false;
	}
	
	public static boolean fixError_Not_Trimmed(DatabaseError err) {
		if (err.getElement1() instanceof CCMovie) {
			((CCMovie)err.getElement1()).setTitle(((CCMovie)err.getElement1()).getTitle().trim());
			((CCMovie)err.getElement1()).setZyklusTitle(((CCMovie)err.getElement1()).getZyklus().getTitle().trim());
			return true;
		} else if (err.getElement1() instanceof CCSeries) {
			((CCSeries)err.getElement1()).setTitle(((CCSeries)err.getElement1()).getTitle().trim());
			return true;
		} else if (err.getElement1() instanceof CCSeason) {
			((CCSeason)err.getElement1()).setTitle(((CCSeason)err.getElement1()).getTitle().trim());
			return true;
		} else if (err.getElement1() instanceof CCEpisode) {
			((CCEpisode)err.getElement1()).setTitle(((CCEpisode)err.getElement1()).getTitle().trim());
			return true;
		}
		
		return false;
	}
	
	public static boolean fixError_Wrong_Filename(DatabaseError err) {
		if (err.getElement1() instanceof CCMovie) {
			CCMovie mov = ((CCMovie)err.getElement1());
			
			for (int i = 0; i < mov.getPartcount(); i++) {
				String opath = mov.getAbsolutePart(i);
				File fold = new File(opath);
				if (! fold.exists()) {
					return false;
				}
				String npath = PathFormatter.rename(opath, mov.generateFilename(i));
				File fnew = new File(npath);
				
				boolean succ = fold.renameTo(fnew);
				mov.setPart(i, PathFormatter.getCCPath(npath));
				
				if (! succ) {
					return false;
				}
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
	
	public static boolean fixError_Impossible_WatchNever(DatabaseError err) {
		if (err.getElement1() instanceof CCMovie) {
			CCMovie mov = ((CCMovie)err.getElement1());
			
			mov.setTag(CCTagList.TAG_WATCH_NEVER, false);

			return true;
		} else if (err.getElement1() instanceof CCSeries) {
			return false;
		} else if (err.getElement1() instanceof CCSeason) {
			return false;
		} else if (err.getElement1() instanceof CCEpisode) {
			CCEpisode epi = ((CCEpisode)err.getElement1());
			
			epi.setTag(CCTagList.TAG_WATCH_NEVER, false);

			return true;
		}
		
		return false;
	}

	public static boolean fixError_MissingHistory(DatabaseError err) {
		if (err.getElement1() instanceof CCMovie) {
			CCMovie mov = ((CCMovie)err.getElement1());
			
			if (! mov.isViewed()) return false;
			if (mov.getViewedHistory().any()) return false;
			
			mov.addToViewedHistory(CCDateTime.getUnspecified());

			return true;
		} else if (err.getElement1() instanceof CCSeries) {
			return false;
		} else if (err.getElement1() instanceof CCSeason) {
			return false;
		} else if (err.getElement1() instanceof CCEpisode) {
			CCEpisode epi = ((CCEpisode)err.getElement1());
			
			if (! epi.isViewed()) return false;
			if (epi.getViewedHistory().any()) return false;
			
			epi.addToViewedHistory(CCDateTime.getUnspecified());

			return true;
		}
		
		return false;
	}

	public static boolean fixError_CoverTooSmall(DatabaseError err) {
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

	public static boolean fixError_CoverTooBig(DatabaseError err) {
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

	public static boolean fixError_UnusedGroup(DatabaseError err) {
		if (err.getElement1() instanceof CCGroup) {

			CCMovieList.getInstance().removeGroup((CCGroup)err.getElement1());
			return true;
		}

		return false;
	}

	public static boolean fixError_NonNormalizedPath(DatabaseError err) {
		if (err.getElement1() instanceof CCEpisode) {

			String pold = ((CCEpisode)err.getElement1()).getPart();
			String pnew = PathFormatter.getCCPath(((CCEpisode)err.getElement1()).getAbsolutePart());

			if (pold.equals(pnew)) return false;

			((CCEpisode)err.getElement1()).setPart(pnew);

			return true;
		} else if (err.getElement1() instanceof CCMovie) {
			for (int i = 0; i < ((CCMovie)err.getElement1()).getPartcount(); i++) {
				String pold = ((CCMovie)err.getElement1()).getPart(i);
				String pnew = PathFormatter.getCCPath(((CCMovie)err.getElement1()).getAbsolutePart(i));

				if (pold.equals(pnew)) continue;

				((CCMovie)err.getElement1()).setPart(i, pnew);
				return true;
			}

			return false;
		}

		return false;
	}

	public static boolean fixError_MediaInfoFilesizeMismatch(DatabaseError err) {
		if (err.getElement1() instanceof ICCPlayableElement) {
			ICCPlayableElement elem = (ICCPlayableElement) err.getElement1();
			CCMediaInfo mi = elem.getMediaInfo();
			if (!mi.isSet()) return false;

			long fsz = 0;
			for (String p : elem.getParts()) {
				File f = new File(PathFormatter.fromCCPath(p));
				if (!f.exists()) return false;
				fsz += f.length();
			}

			if (fsz != mi.getFilesize()) return false;

			elem.setFilesize(fsz);
			return true;
		}

		return false;
	}

	public static boolean fixError_InvalidCharacters(DatabaseError err) {
		if (err.getElement1() instanceof CCMovie)
		{
			var elem = (CCMovie) err.getElement1();

			var newtitle  = DatabaseStringNormalization.fixInvalidCharacters(elem.getTitle());
			if (!newtitle.equals(elem.getTitle())) { elem.setTitle(newtitle); return true; }

			var newzyklus = DatabaseStringNormalization.fixInvalidCharacters(elem.getZyklus().getTitle());
			if (!newzyklus.equals(elem.getZyklus().getTitle())) { elem.setZyklusTitle(newzyklus); return true; }
		}
		else if (err.getElement1() instanceof CCSeries)
		{
			var elem = (CCSeries) err.getElement1();

			var newtitle  = DatabaseStringNormalization.fixInvalidCharacters(elem.getTitle());
			if (!newtitle.equals(elem.getTitle())) { elem.setTitle(newtitle); return true; }
		}
		else if (err.getElement1() instanceof CCSeason)
		{
			var elem = (CCSeason) err.getElement1();

			var newtitle  = DatabaseStringNormalization.fixInvalidCharacters(elem.getTitle());
			if (!newtitle.equals(elem.getTitle())) { elem.setTitle(newtitle); return true; }
		}
		else if (err.getElement1() instanceof CCEpisode)
		{
			var elem = (CCEpisode) err.getElement1();

			var newtitle  = DatabaseStringNormalization.fixInvalidCharacters(elem.getTitle());
			if (!newtitle.equals(elem.getTitle())) { elem.setTitle(newtitle); return true; }
		}

		return false;
	}
}
