package de.jClipCorn.database.databaseErrors;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.columnTypes.CCFileFormat;
import de.jClipCorn.database.databaseElement.columnTypes.CCFileSize;
import de.jClipCorn.database.databaseElement.columnTypes.CCGenre;
import de.jClipCorn.database.databaseElement.columnTypes.CCGenreList;
import de.jClipCorn.database.databaseElement.columnTypes.CCQuality;
import de.jClipCorn.database.databaseElement.columnTypes.CCTagList;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.DriveMap;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.formatter.FileSizeFormatter;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.helper.ImageUtilities;
import de.jClipCorn.util.listener.ProgressCallbackListener;

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
					String rel = PathFormatter.getCCPath(abs.replace("\\", PathFormatter.SERIALIZATION_SEPERATOR), CCProperties.getInstance().PROP_ADD_MOVIE_RELATIVE_AUTO.getValue()); //$NON-NLS-1$
				
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
				String rel = PathFormatter.getCCPath(abs.replace("\\", PathFormatter.SERIALIZATION_SEPERATOR), CCProperties.getInstance().PROP_ADD_MOVIE_RELATIVE_AUTO.getValue()); //$NON-NLS-1$
			
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
			CCGenreList ls = new CCGenreList();
			
			boolean[] already_used = new boolean[256];
			for (int i = 0; i < CCGenreList.getMaxListSize(); i++) {
				CCGenre g = elem.getGenre(i);
				
				if (! (g.isEmpty() || already_used[g.asInt()])) {
					ls.addGenre(g);
					
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
			CCGenreList ls = new CCGenreList();
			
			for (int i = 0; i < CCGenreList.getMaxListSize(); i++) {
				if (! elem.getGenre(i).isEmpty()) {
					ls.addGenre(elem.getGenre(i));
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
	
	public static boolean fixError_Wrong_Quality(DatabaseError err) {
		if (err.getElement1() instanceof CCMovie) {
			((CCMovie)err.getElement1()).setQuality(CCQuality.calculateQuality(((CCMovie)err.getElement1()).getFilesize(), ((CCMovie)err.getElement1()).getLength(), ((CCMovie)err.getElement1()).getPartcount()));
			return true;
		} else if (err.getElement1() instanceof CCSeries) {
			return false;
		} else if (err.getElement1() instanceof CCSeason) {
			return false;
		} else if (err.getElement1() instanceof CCEpisode) {
			((CCEpisode)err.getElement1()).setQuality(CCQuality.calculateQuality(((CCEpisode)err.getElement1()).getFilesize(), ((CCEpisode)err.getElement1()).getLength(), 1));
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
				mov.setPart(i, PathFormatter.getCCPath(npath, CCProperties.getInstance().PROP_ADD_MOVIE_RELATIVE_AUTO.getValue()));
				
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
	
	public static boolean fixError_Impossible_WatchLater(DatabaseError err) {
		if (err.getElement1() instanceof CCMovie) {
			CCMovie mov = ((CCMovie)err.getElement1());
			
			mov.setTag(CCTagList.TAG_WATCH_LATER, false);

			return true;
		} else if (err.getElement1() instanceof CCSeries) {
			return false;
		} else if (err.getElement1() instanceof CCSeason) {
			return false;
		} else if (err.getElement1() instanceof CCEpisode) {
			CCEpisode epi = ((CCEpisode)err.getElement1());
			
			epi.setTag(CCTagList.TAG_WATCH_LATER, false);

			return true;
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
}
