package de.jClipCorn.features.databaseErrors;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.covertab.CCCoverData;
import de.jClipCorn.database.covertab.CCDefaultCoverCache;
import de.jClipCorn.database.covertab.ICoverCache;
import de.jClipCorn.database.databaseElement.*;
import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.database.driver.PublicDatabaseInterface;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.RefParam;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.filesystem.CCPath;
import de.jClipCorn.util.formatter.RomanNumberFormatter;
import de.jClipCorn.util.helper.ChecksumHelper;
import de.jClipCorn.util.helper.ImageUtilities;
import de.jClipCorn.util.lambda.Func0to1WithIOException;
import de.jClipCorn.util.listener.DoubleProgressCallbackListener;
import de.jClipCorn.util.stream.CCStreams;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class CCDatabaseValidator extends AbstractDatabaseValidator
{
	public CCDatabaseValidator(CCMovieList ml) {
		super(ml);
	}

	@Override
	protected void init() {
		initMovies();
		initSeries();
		initSeasons();
		initEpisodes();
	}

	@SuppressWarnings({"Convert2MethodRef", "nls"})
	private void initMovies()
	{
		// Hole in Genres
		addMovieValidation(
				DatabaseErrorType.ERROR_INCONTINUOUS_GENRELIST,
				o -> o.ValidateMovies,
				mov -> mov.hasHoleInGenres(),
				mov -> DatabaseError.createSingle(movielist, DatabaseErrorType.ERROR_INCONTINUOUS_GENRELIST, mov));

		// Genre with wrong ID
		addMovieValidation(
				DatabaseErrorType.ERROR_WRONG_GENREID,
				o -> o.ValidateMovies,
				(mov, e) ->
				{
					for (int i = 0; i < mov.Genres.get().getGenreCount(); i++) {
						if (!mov.Genres.get().getGenre(i).isValid()) {
							e.add(DatabaseError.createSingleAdditional(
									movielist,
									DatabaseErrorType.ERROR_WRONG_GENREID, mov, i,
									"Index", String.valueOf(i),
									"Value", String.valueOf(mov.Genres.get().getGenre(i).asInt())
							));
						}
					}
				});

		// Moviesize <> Real size
		addMovieValidation(
				DatabaseErrorType.ERROR_WRONG_FILESIZE,
				o -> o.ValidateVideoFiles,
				(mov, e) ->
				{
					CCFileSize size = CCFileSize.ZERO;
					for (int i = 0; i < mov.getPartcount(); i++) size = CCFileSize.add(size, mov.Parts.get(i).toFSPath(this).filesize());
					if (getRelativeDifference(size.getBytes(), mov.getFilesize().getBytes()) > getMaxSizeFileDrift()) {
						e.add(DatabaseError.createSingle(
								movielist,
								DatabaseErrorType.ERROR_WRONG_FILESIZE, mov,
								"Filesize[Filesystem]", String.valueOf(size.getBytes()),
								"Filesize[Database]", String.valueOf(size.getBytes()),
								"MaxSizeFileDrift", String.valueOf(getMaxSizeFileDrift())
						));
					}
				});

		// Genrecount <= 0
		addMovieValidation(
				DatabaseErrorType.ERROR_NOGENRE_SET,
				o -> o.ValidateMovies,
				mov -> mov.Genres.get().getGenreCount() <= 0,
				mov -> DatabaseError.createSingle(movielist, DatabaseErrorType.ERROR_NOGENRE_SET, mov));

		// Genrecount <= 0
		addMovieValidation(
				DatabaseErrorType.ERROR_NOCOVERSET,
				o -> o.ValidateMovies,
				mov -> mov.getCoverID() == -1,
				mov -> DatabaseError.createSingle(movielist, DatabaseErrorType.ERROR_NOCOVERSET, mov));

		// cover not found
		addMovieValidation(
				DatabaseErrorType.ERROR_COVER_NOT_FOUND,
				o -> o.ValidateCoverFiles,
				(mov, movielist) -> !movielist.getCoverCache().coverFileExists(mov.getCoverID()),
				(mov, movielist) -> DatabaseError.createSingle(movielist, DatabaseErrorType.ERROR_COVER_NOT_FOUND, mov));

		// no title set
		addMovieValidation(
				DatabaseErrorType.ERROR_TITLE_NOT_SET,
				o -> o.ValidateMovies,
				mov -> mov.getTitle().isEmpty(),
				mov -> DatabaseError.createSingle(movielist, DatabaseErrorType.ERROR_TITLE_NOT_SET, mov));

		// ZyklusID == 0
		addMovieValidation(
				DatabaseErrorType.ERROR_ZYKLUSNUMBER_IS_NEGONE,
				o -> o.ValidateMovies,
				mov -> !mov.getZyklus().getTitle().isEmpty() && mov.getZyklus().getNumber() == -1,
				mov -> DatabaseError.createSingle(movielist, DatabaseErrorType.ERROR_ZYKLUSNUMBER_IS_NEGONE, mov));

		// ZyklusID <> -1
		addMovieValidation(
				DatabaseErrorType.ERROR_ZYKLUSTITLE_IS_EMPTY,
				o -> o.ValidateMovies,
				mov -> mov.getZyklus().getTitle().isEmpty() && mov.getZyklus().getNumber() != -1,
				mov -> DatabaseError.createSingle(movielist, DatabaseErrorType.ERROR_ZYKLUSTITLE_IS_EMPTY, mov));

		// Hole in Parts
		addMovieValidation(
				DatabaseErrorType.ERROR_INCONTINUOUS_PARTS,
				o -> o.ValidateMovies,
				mov -> mov.hasHoleInParts(),
				mov -> DatabaseError.createSingle(movielist, DatabaseErrorType.ERROR_INCONTINUOUS_PARTS, mov));

		// Wrong Format
		addMovieValidation(
				DatabaseErrorType.ERROR_FORMAT_NOT_FOUND_IN_PARTS,
				o -> o.ValidateMovies,
				(mov, e) ->
				{
					boolean rform = false;
					for (int i = 0; i < mov.getPartcount(); i++) {
						CCFileFormat mfmt = CCFileFormat.getMovieFormat(mov.Parts.get(i).getExtension());
						if (mfmt != null && mfmt.equals(mov.getFormat())) rform = true;
					}
					if (!rform) e.add(DatabaseError.createSingle(
							movielist,
							DatabaseErrorType.ERROR_FORMAT_NOT_FOUND_IN_PARTS, mov,
							"Format[Database]", mov.getFormat().asString(),
							"Format[Files]", CCStreams.iterate(mov.getParts()).map(p -> CCFileFormat.getMovieFormat(p.getExtension())).stringjoin(p->p.asString(), ";")
					));
				});

		// Inexistent Paths
		addMovieValidation(
				DatabaseErrorType.ERROR_PATH_NOT_FOUND,
				o -> o.ValidateVideoFiles,
				(mov, e) ->
				{
					for (int i = 0; i < mov.getPartcount(); i++) {
						if (! mov.Parts.get(i).toFSPath(this).exists()) {
							e.add(DatabaseError.createSingle(
									movielist,
									DatabaseErrorType.ERROR_PATH_NOT_FOUND, mov,
									"Index", String.valueOf(i),
									"CCPath", mov.Parts.get(i).toString(),
									"FSPath", mov.Parts.get(i).toFSPath(this).toString()
							));
						}
					}
				});

		// Wrong AddDate
		addMovieValidation(
				DatabaseErrorType.ERROR_WRONG_ADDDATE,
				o -> o.ValidateMovies,
				mov -> mov.getAddDate().isLessEqualsThan(CCDate.getMinimumDate()) || mov.getAddDate().isGreaterThan(CCDate.getCurrentDate()),
				mov -> DatabaseError.createSingle(
						movielist,
						DatabaseErrorType.ERROR_WRONG_ADDDATE, mov,
						"Date", mov.getAddDate().toStringSQL()
				));

		// Zyklus/Title ends/starts with a space
		addMovieValidation(
				DatabaseErrorType.ERROR_NOT_TRIMMED,
				o -> o.ValidateMovies,
				mov -> Str.isUntrimmed(mov.getTitle()) || Str.isUntrimmed(mov.getZyklus().getTitle()),
				mov -> DatabaseError.createSingle(
						movielist,
						DatabaseErrorType.ERROR_NOT_TRIMMED, mov,
						"Title", "'"+mov.getTitle()+"'",
						"Zyklus", "'"+mov.getZyklus().getTitle()+"'"
				));

		// Zyklus ends with Roman
		addMovieValidation(
				DatabaseErrorType.ERROR_ZYKLUS_ENDS_WITH_ROMAN,
				o -> o.ValidateMovies,
				mov -> RomanNumberFormatter.endsWithRoman(mov.getZyklus().getTitle()),
				mov -> DatabaseError.createSingle(
						movielist,
						DatabaseErrorType.ERROR_ZYKLUS_ENDS_WITH_ROMAN, mov,
						"Zyklus", mov.getZyklus().getTitle()
						));

		// Duplicate Name
		addMovieValidation(
				DatabaseErrorType.ERROR_DUPLICATE_TITLE,
				o -> o.ValidateMovies,
				(mov, movielist, e) ->
				{
					for (CCMovie imov : movielist.iteratorMovies()) {
						if (StringUtils.equalsIgnoreCase(imov.getCompleteTitle(), mov.getCompleteTitle()) && (imov.getLanguage().isSubsetOf(mov.getLanguage()) || mov.getLanguage().isSubsetOf(imov.getLanguage()))) {
							if (mov.getLocalID() != imov.getLocalID()) {
								e.add(DatabaseError.createDouble(
										movielist,
										DatabaseErrorType.ERROR_DUPLICATE_TITLE, mov, imov,
										"Movie1.LocalID", String.valueOf(mov.getLocalID()),
										"Movie2.LocalID", String.valueOf(imov.getLocalID()),
										"Movie1.CompleteTitle", mov.getCompleteTitle(),
										"Movie2.CompleteTitle", imov.getCompleteTitle()
										));
							}
							break;
						}
					}
				});

		// Wrong filename
		addMovieValidation(
				DatabaseErrorType.ERROR_WRONG_FILENAME,
				o -> o.ValidateMovies,
				(mov, movielist, e) ->
				{
					var md = new ArrayList<Tuple<String, String>>();
					boolean wrongfn = false;
					for (int i = 0; i < mov.getPartcount(); i++) {
						var should = mov.generateFilename(i);
						var actual = mov.Parts.get(i).toFSPath(this).getFilenameWithExt();
						if (!Str.equals(actual, should))
						{
							wrongfn = true;
							md.add(Tuple.Create("Part["+i+"].Filename.Should", should));
							md.add(Tuple.Create("Part["+i+"].Filename.Actual", actual));
						}
					}
					if (wrongfn) e.add(DatabaseError.createSingle(movielist, DatabaseErrorType.ERROR_WRONG_FILENAME, mov, md));
				});

		// Watch never <> ViewedState
		addMovieValidation(
				DatabaseErrorType.ERROR_IMPOSSIBLE_WATCH_NEVER,
				o -> o.ValidateMovies,
				mov -> mov.isViewed() && mov.Tags.get(CCSingleTag.TAG_WATCH_NEVER),
				mov -> DatabaseError.createSingle(movielist, DatabaseErrorType.ERROR_IMPOSSIBLE_WATCH_NEVER, mov));

		// Duplicate Genre
		addMovieValidation(
				DatabaseErrorType.ERROR_DUPLICATE_GENRE,
				o -> o.ValidateMovies,
				(mov, e) ->
				{
					int[] g_count = new int[256];
					for (int i = 0; i < CCGenreList.getMaxListSize(); i++) g_count[mov.Genres.get().getGenre(i).asInt()]++;

					for (int i = 1; i < 256; i++) {
						if (g_count[i] > 1) {
							e.add(DatabaseError.createSingle(
									movielist,
									DatabaseErrorType.ERROR_DUPLICATE_GENRE, mov,
									"Genre[ID]:", String.valueOf(i),
									"Genre[Str]:", CCGenre.getWrapper().findOrNull(i).asString(),
									"Genre.Count:", String.valueOf(g_count[i])
							));
							break;
						}
					}
				});

		// Invalid path characters
		addMovieValidation(
				DatabaseErrorType.ERROR_INVALID_CHARS_IN_PATH,
				o -> o.ValidateMovies,
				(mov, e) ->
				{
					for (int i = 0; i < CCMovie.PARTCOUNT_MAX; i++) {
						if (CCPath.containsIllegalSymbols(mov.Parts.get(i))){
							e.add(DatabaseError.createSingle(
									movielist,
									DatabaseErrorType.ERROR_INVALID_CHARS_IN_PATH, mov,
									"Index", String.valueOf(i),
									"Part", mov.Parts.get(i).toString()));
							break;
						}
					}
				});

		// Invalid CCOnlineRef
		addMovieValidation(
				DatabaseErrorType.ERROR_INVALID_ONLINEREF,
				o -> o.ValidateMovies,
				mov -> !mov.getOnlineReference().isValid(),
				mov -> DatabaseError.createSingle(
						movielist,
						DatabaseErrorType.ERROR_INVALID_ONLINEREF, mov,
						"OnlineRef", mov.getOnlineReference().toSerializationString(),
						"OnlineRef.Invalid", mov.getOnlineReference().ccstream().filter(p -> p.isInvalid()).stringjoin(p -> p.toSerializationString(), " ; "),
						"OnlineRef.Unset", mov.getOnlineReference().ccstream().filter(p -> p.isUnset()).stringjoin(p -> p.toSerializationString(), " ; ")
				));

		// History but not viewed
		addMovieValidation(
				DatabaseErrorType.ERROR_HISTORY_BUT_UNVIEWED,
				o -> o.ValidateMovies,
				mov -> !mov.isViewed() && !mov.ViewedHistory.get().isEmpty(),
				mov -> DatabaseError.createSingle(movielist, DatabaseErrorType.ERROR_HISTORY_BUT_UNVIEWED, mov));

		// History is invalid
		addMovieValidation(
				DatabaseErrorType.ERROR_INVALID_HISTORY,
				o -> o.ValidateMovies,
				mov -> !mov.ViewedHistory.get().isValid(),
				mov -> DatabaseError.createSingle(movielist, DatabaseErrorType.ERROR_INVALID_HISTORY, mov));

		// Duplicate/Empty Groups
		addMovieValidation(
				DatabaseErrorType.ERROR_INVALID_GROUPLIST,
				o -> o.ValidateMovies,
				(mov, e) ->
				{
					Set<String> groupSet = new HashSet<>();
					for (CCGroup group : mov.getGroups()) {
						if (! groupSet.add(group.Name.toLowerCase().trim())) {
							e.add(DatabaseError.createSingle(
									movielist,
									DatabaseErrorType.ERROR_INVALID_GROUPLIST, mov,
									"Problem", "Duplicate",
									"Group", group.Name
							));
							break;
						}
						if (group.Name.toLowerCase().trim().isEmpty()) {
							e.add(DatabaseError.createSingle(
									movielist,
									DatabaseErrorType.ERROR_INVALID_GROUPLIST, mov,
									"Problem", "Name.IsEmpty",
									"Group", group.Name
							));
							break;
						}
						if (! CCGroup.isValidGroupName(group.Name)) {
							e.add(DatabaseError.createSingle(
									movielist,
									DatabaseErrorType.ERROR_INVALID_GROUPLIST, mov,
									"Problem", "InvalidGroupName",
									"Group", group.Name
							));
							break;
						}
					}
				});

		// Viewed but no history
		addMovieValidation(
				DatabaseErrorType.ERROR_VIEWED_BUT_NO_HISTORY,
				o -> o.ValidateMovies,
				mov -> mov.isViewed() && mov.ViewedHistory.get().isEmpty(),
				mov -> DatabaseError.createSingle(movielist, DatabaseErrorType.ERROR_VIEWED_BUT_NO_HISTORY, mov));

		// Cover too small
		addMovieValidation(
				DatabaseErrorType.ERROR_COVER_TOO_SMALL,
				o -> o.ValidateMovies,
				mov -> mov.getCoverDimensions().Item1 < ImageUtilities.BASE_COVER_WIDTH && mov.getCoverDimensions().Item2 < ImageUtilities.BASE_COVER_HEIGHT,
				mov -> DatabaseError.createSingle(
						movielist,
						DatabaseErrorType.ERROR_COVER_TOO_SMALL, mov,
						"Cover.Width", String.valueOf(mov.getCoverDimensions().Item1),
						"Cover.Height", String.valueOf(mov.getCoverDimensions().Item2)
						));

		// Cover too big
		addMovieValidation(
				DatabaseErrorType.ERROR_COVER_TOO_BIG,
				o -> o.ValidateMovies,
				mov -> mov.getCoverDimensions().Item1 > ImageUtilities.getCoverWidth(movielist.ccprops()) && mov.getCoverDimensions().Item2 < ImageUtilities.getCoverHeight(movielist.ccprops()),
				mov -> DatabaseError.createSingle(
						movielist, DatabaseErrorType.ERROR_COVER_TOO_BIG, mov,
						"Cover.Width", String.valueOf(mov.getCoverDimensions().Item1),
						"Cover.Height", String.valueOf(mov.getCoverDimensions().Item2)
				));

		// Invalid Tag for movies
		addMovieValidation(
				DatabaseErrorType.ERROR_TAG_NOT_VALID_ON_MOVIE,
				o -> o.ValidateMovies,
				(mov, e) ->
				{
					for (CCSingleTag t : mov.getTags().ccstream()) {
						if (!t.IsMovieTag)
						{
							e.add(DatabaseError.createSingle(
									movielist,
									DatabaseErrorType.ERROR_TAG_NOT_VALID_ON_MOVIE, mov,
									"Tag.Index", String.valueOf(t.Index),
									"Tag.Description", t.Description
									));
							break;
						}
					}
				});

		// Not-normalized path
		addMovieValidation(
				DatabaseErrorType.ERROR_NON_NORMALIZED_PATH,
				o -> o.ValidateMovies,
				(mov, e) ->
				{
					for (int i = 0; i < mov.getPartcount(); i++) {
						var should = CCPath.createFromFSPath(mov.Parts.get(i).toFSPath(this), this);
						var actual = mov.Parts.get(i);
						if (!CCPath.isEqual(should, actual)) {
							e.add(DatabaseError.createSingle(
									movielist,
									DatabaseErrorType.ERROR_NON_NORMALIZED_PATH, mov,
									"Path.Index", String.valueOf(i),
									"Path.FSPath", mov.Parts.get(i).toFSPath(this).toString(),
									"Path.CCPath[Actual]", actual.toString(),
									"Path.CCPath[Should]", should.toString()
									));
							break;
						}
					}
				});

		// No language
		addMovieValidation(
				DatabaseErrorType.ERROR_NO_LANG,
				o -> o.ValidateMovies,
				mov -> mov.getLanguage().isEmpty(),
				mov -> DatabaseError.createSingle(movielist, DatabaseErrorType.ERROR_NO_LANG, mov));

		// Muted language must be single
		addMovieValidation(
				DatabaseErrorType.ERROR_LANG_MUTED_SUBSET,
				o -> o.ValidateMovies,
				mov -> mov.getLanguage().contains(CCDBLanguage.MUTED) && !mov.getLanguage().isSingle(),
				mov -> DatabaseError.createSingle(movielist, DatabaseErrorType.ERROR_LANG_MUTED_SUBSET, mov));

		// MediaInfo not set
		addMovieValidation(
				DatabaseErrorType.ERROR_MEDIAINFO_UNSET,
				o -> o.ValidateMovies,
				mov -> mov.mediaInfo().get().isUnset(),
				mov -> DatabaseError.createSingle(
						movielist,
						DatabaseErrorType.ERROR_MEDIAINFO_UNSET, mov,
						CCStreams.iterate(mov.mediaInfo().getPartial().validate(false)).map(p -> Tuple.Create("MediaInfo."+p+".IsSet", "false")).toList()
				));

		// invalid MediaInfo
		addMovieValidation(
				DatabaseErrorType.ERROR_MEDIAINFO_INVALID,
				o -> o.ValidateMovies,
				mov -> mov.mediaInfo().get().isSet() && !mov.mediaInfo().getPartial().validate().isEmpty(),
				mov -> DatabaseError.createSingleAdditional(
						movielist,
						DatabaseErrorType.ERROR_MEDIAINFO_INVALID, mov,
						mov.mediaInfo().getPartial().validate().stream().findFirst().orElse("?"),
						"MediaInfo.validate", String.join(";", mov.mediaInfo().getPartial().validate())
				));

		// MediaInfo size does not match movie filesize
		addMovieValidation(
				DatabaseErrorType.ERROR_MEDIAINFO_SIZE_MISMATCH,
				o -> o.ValidateMovies,
				mov -> mov.mediaInfo().get().isSet() && (mov.getPartcount()==1 && !CCFileSize.isEqual(mov.mediaInfo().get().getFilesize(), mov.getFilesize())),
				mov -> DatabaseError.createSingle(
						movielist,
						DatabaseErrorType.ERROR_MEDIAINFO_SIZE_MISMATCH, mov,
						"Partcount", String.valueOf(mov.getPartcount()),
						"MediaInfo.Filesize", String.valueOf(mov.mediaInfo().get().getFilesize().getBytes()),
						"Database.Filesize", String.valueOf(mov.getFilesize().getBytes())
						));

		// MediaInfo length does not match movie length
		addMovieValidation(
				DatabaseErrorType.ERROR_MEDIAINFO_LENGTH_MISMATCH,
				o -> o.ValidateMovies,
				mov -> mov.mediaInfo().get().isSet() &&
						mov.getPartcount()==1 &&
						!mov.Tags.get(CCSingleTag.TAG_MISSING_TIME) &&
						!mov.Tags.get(CCSingleTag.TAG_FILE_CORRUPTED) &&
						!mov.Tags.get(CCSingleTag.TAG_WATCH_CAMRIP) &&
						!mov.Tags.get(CCSingleTag.TAG_WATCH_MICDUBBED) &&
						!mov.Tags.get(CCSingleTag.TAG_WRONG_LANGUAGE) &&
						isDiff(mov.mediaInfo().get().getDurationInMinutes(), mov.getLength(), 0.10, 10),
				mov -> DatabaseError.createSingle(
						movielist,
						DatabaseErrorType.ERROR_MEDIAINFO_LENGTH_MISMATCH, mov,
						"MediaInfo.Duration", String.valueOf(mov.mediaInfo().get().getDurationInMinutes()),
						"Database.Duration", String.valueOf(mov.getLength())
				));

		// Mediainfo attributes not match actual file
		addMovieValidation(
				DatabaseErrorType.ERROR_MEDIAINFO_CDATE_CHANGED,
				o -> o.ValidateVideoFiles,
				(mov, e) ->
				{
					if (mov.mediaInfo().get().isSet()) {
						try {
							BasicFileAttributes attr = mov.Parts.get(0).toFSPath(this).readFileAttr();
							if (attr.creationTime().toMillis() != mov.mediaInfo().get().getCDate())
								e.add(DatabaseError.createSingle(
										movielist,
										DatabaseErrorType.ERROR_MEDIAINFO_CDATE_CHANGED, mov,
										"Filesystem.CDate.Milliseconds", String.valueOf(attr.creationTime().toMillis()),
										"MediaInfo.CDate.Milliseconds", String.valueOf(mov.mediaInfo().get().getCDate())
										));
						} catch (IOException ex) {
							/**/
						}
					}
				});
		addMovieValidation(
				DatabaseErrorType.ERROR_MEDIAINFO_MDATE_CHANGED,
				o -> o.ValidateVideoFiles,
				(mov, e) ->
				{
					if (mov.mediaInfo().get().isSet()) {
						try {
							BasicFileAttributes attr = mov.Parts.get(0).toFSPath(this).readFileAttr();
							if (attr.lastModifiedTime().toMillis() != mov.mediaInfo().get().getMDate())
								e.add(DatabaseError.createSingle(
										movielist,
										DatabaseErrorType.ERROR_MEDIAINFO_MDATE_CHANGED, mov,
										"Filesystem.MDate.Milliseconds", String.valueOf(attr.lastModifiedTime().toMillis()),
										"MediaInfo.MDate.Milliseconds", String.valueOf(mov.mediaInfo().get().getMDate())
								));
						} catch (IOException ex) {
							/**/
						}
					}
				});

		// Mediainfo does not match actual file
		addMovieValidation(
				DatabaseErrorType.ERROR_MEDIAINFO_FILE_CHANGED,
				o -> o.ValidateVideoFiles,
				(mov, e) ->
				{
					if (mov.mediaInfo().get().isSet()) {
						if (!CCFileSize.isEqual(mov.Parts.get(0).toFSPath(this).filesize(), mov.mediaInfo().get().getFilesize()))
							e.add(DatabaseError.createSingle(movielist, DatabaseErrorType.ERROR_MEDIAINFO_FILE_CHANGED, mov));
					}
				});

		// Title / Zyklus contains invalid characters
		addMovieValidation(
				DatabaseErrorType.ERROR_INVALID_CHARACTERS,
				o -> o.ValidateMovies,
				mov -> DatabaseStringNormalization.hasInvalidCharacters(mov.getTitle()) || DatabaseStringNormalization.hasInvalidCharacters(mov.getZyklus().getTitle()),
				mov -> DatabaseError.createSingle(
						movielist,
						DatabaseErrorType.ERROR_INVALID_CHARACTERS, mov,
						"Title", mov.getTitle(),
						"Zyklus", mov.getZyklus().getTitle()
				));

		// Hash is invalid
		addMovieValidation(
				DatabaseErrorType.ERROR_INVALID_HASH,
				o -> o.ValidateMovies,
				mov -> mov.mediaInfo().get().isSet() && !ChecksumHelper.isValidVideoHash(mov.mediaInfo().get().getChecksum()),
				mov -> DatabaseError.createSingle(
						movielist,
						DatabaseErrorType.ERROR_INVALID_HASH, mov,
						"Checksum", mov.mediaInfo().get().getChecksum()
				));

		// Hash is impossible (length/fislesize mismatch)
		addMovieValidation(
				DatabaseErrorType.ERROR_IMPOSSIBLE_HASH,
				o -> o.ValidateMovies,
				mov -> mov.mediaInfo().get().isSet() &&
					   ChecksumHelper.isValidVideoHash(mov.mediaInfo().get().getChecksum()) &&
					   !ChecksumHelper.isPossibleVideoHash(mov.mediaInfo().get().getChecksum(), mov.mediaInfo().get()),
				mov -> DatabaseError.createSingle(
						movielist,
						DatabaseErrorType.ERROR_IMPOSSIBLE_HASH, mov,
						"Checksum", mov.mediaInfo().get().getChecksum()
				));

		// Muted language in subtitles
		addMovieValidation(
				DatabaseErrorType.ERROR_SUBTITLE_MUTED,
				o -> o.ValidateMovies,
				mov -> mov.getSubtitles().contains(CCDBLanguage.MUTED),
				mov -> DatabaseError.createSingle(movielist, DatabaseErrorType.ERROR_SUBTITLE_MUTED, mov));

		// Muted language in subtitles
		addMovieValidation(
				DatabaseErrorType.ERROR_INVALID_ONLINESCORE,
				o -> o.ValidateMovies,
				mov -> !mov.getOnlinescore().isValid(),
				mov -> DatabaseError.createSingle(movielist, DatabaseErrorType.ERROR_INVALID_ONLINESCORE, mov));
	}

	@SuppressWarnings({"Convert2MethodRef", "nls"})
	private void initSeries()
	{
		// no title set
		addSeriesValidation(
				DatabaseErrorType.ERROR_TITLE_NOT_SET,
				o -> o.ValidateSeries,
				series -> series.getTitle().isEmpty(),
				series -> DatabaseError.createSingle(movielist, DatabaseErrorType.ERROR_TITLE_NOT_SET, series));

		// Genre with wrong ID
		addSeriesValidation(
				DatabaseErrorType.ERROR_WRONG_GENREID,
				o -> o.ValidateSeries,
				(series, e) ->
				{
					for (int i = 0; i < series.Genres.get().getGenreCount(); i++) {
						if (! series.Genres.get().getGenre(i).isValid()) {
							e.add(DatabaseError.createSingleAdditional(
									movielist,
									DatabaseErrorType.ERROR_WRONG_GENREID, series, i,
									"Index", String.valueOf(i),
									"Value", String.valueOf(series.Genres.get().getGenre(i).asInt())));
						}
					}
				});

		// cover not set
		addSeriesValidation(
				DatabaseErrorType.ERROR_NOCOVERSET,
				o -> o.ValidateSeries,
				series -> series.getCoverID() == -1,
				series -> DatabaseError.createSingle(movielist, DatabaseErrorType.ERROR_NOCOVERSET, series));

		// cover not found
		addSeriesValidation(
				DatabaseErrorType.ERROR_COVER_NOT_FOUND,
				o -> o.ValidateCoverFiles,
				(series, movielist) -> !movielist.getCoverCache().coverFileExists(series.getCoverID()),
				(series, movielist) -> DatabaseError.createSingle(movielist, DatabaseErrorType.ERROR_COVER_NOT_FOUND, series));

		// Wrong AddDate
		addSeriesValidation(
				DatabaseErrorType.ERROR_WRONG_ADDDATE,
				o -> o.ValidateSeries,
				series -> !series.isEmpty() && (series.getAddDate().isLessEqualsThan(CCDate.getMinimumDate()) || series.getAddDate().isGreaterThan(CCDate.getCurrentDate())),
				series -> DatabaseError.createSingle(
						movielist,
						DatabaseErrorType.ERROR_WRONG_ADDDATE, series,
						"Date", series.getAddDate().toStringSQL()
				));

		// Zyklus/Title ends/starts with a space
		addSeriesValidation(
				DatabaseErrorType.ERROR_NOT_TRIMMED,
				o -> o.ValidateSeries,
				series -> Str.isUntrimmed(series.getTitle()),
				series -> DatabaseError.createSingle(
						movielist,
						DatabaseErrorType.ERROR_NOT_TRIMMED, series,
						"Title", "'"+series.getTitle()+"'"
				));

		// Duplicate Genre
		addSeriesValidation(
				DatabaseErrorType.ERROR_DUPLICATE_GENRE,
				o -> o.ValidateSeries,
				(series, e) ->
				{
					int[] g_count = new int[256];
					for (int i = 0; i < CCGenreList.getMaxListSize(); i++) g_count[series.Genres.get().getGenre(i).asInt()]++;
					for (int i = 1; i < 256; i++) {
						if (g_count[i] > 1)
						{
							e.add(DatabaseError.createSingle(
									movielist,
									DatabaseErrorType.ERROR_DUPLICATE_GENRE, series,
									"Genre[ID]:", String.valueOf(i),
									"Genre[Str]:", CCGenre.getWrapper().findOrNull(i).asString(),
									"Genre.Count:", String.valueOf(g_count[i])
							));
							break;
						}
					}
				});

		// Invalid CCOnlineRef
		addSeriesValidation(
				DatabaseErrorType.ERROR_INVALID_ONLINEREF,
				o -> o.ValidateSeries,
				series -> !series.getOnlineReference().isValid(),
				series -> DatabaseError.createSingle(
						movielist,
						DatabaseErrorType.ERROR_INVALID_ONLINEREF, series,
						"OnlineRef", series.getOnlineReference().toSerializationString(),
						"OnlineRef.Invalid", series.getOnlineReference().ccstream().filter(p -> p.isInvalid()).stringjoin(p -> p.toSerializationString(), " ; "),
						"OnlineRef.Unset", series.getOnlineReference().ccstream().filter(p -> p.isUnset()).stringjoin(p -> p.toSerializationString(), " ; ")
				));

		// Duplicate/Empty Groups
		addSeriesValidation(
				DatabaseErrorType.ERROR_INVALID_GROUPLIST,
				o -> o.ValidateSeries,
				(series, e) ->
				{
					Set<String> groupSet = new HashSet<>();
					for (CCGroup group : series.getGroups()) {
						if (! groupSet.add(group.Name.toLowerCase().trim())) {
							e.add(DatabaseError.createSingle(
									movielist,
									DatabaseErrorType.ERROR_INVALID_GROUPLIST, series,
									"Problem", "Duplicate",
									"Group", group.Name
							));
							break;
						}
						if (group.Name.toLowerCase().trim().isEmpty()) {
							e.add(DatabaseError.createSingle(
									movielist,
									DatabaseErrorType.ERROR_INVALID_GROUPLIST, series,
									"Problem", "Name.IsEmpty",
									"Group", group.Name
							));
							break;
						}
						if (! CCGroup.isValidGroupName(group.Name)) {
							e.add(DatabaseError.createSingle(
									movielist,
									DatabaseErrorType.ERROR_INVALID_GROUPLIST, series,
									"Problem", "InvalidGroupName",
									"Group", group.Name
							));
							break;
						}
					}
				});

		// Cover too small
		addSeriesValidation(
				DatabaseErrorType.ERROR_COVER_TOO_SMALL,
				o -> o.ValidateSeries,
				series -> series.getCoverDimensions().Item1 < ImageUtilities.BASE_COVER_WIDTH && series.getCoverDimensions().Item2 < ImageUtilities.BASE_COVER_HEIGHT,
				series -> DatabaseError.createSingle(
						movielist,
						DatabaseErrorType.ERROR_COVER_TOO_SMALL, series,
						"Cover.Width", String.valueOf(series.getCoverDimensions().Item1),
						"Cover.Height", String.valueOf(series.getCoverDimensions().Item2)
				));

		// Cover too big
		addSeriesValidation(
				DatabaseErrorType.ERROR_COVER_TOO_BIG,
				o -> o.ValidateSeries,
				series -> series.getCoverDimensions().Item1 > ImageUtilities.getCoverWidth(movielist.ccprops()) && series.getCoverDimensions().Item2 < ImageUtilities.getCoverHeight(movielist.ccprops()),
				series -> DatabaseError.createSingle(
						movielist,
						DatabaseErrorType.ERROR_COVER_TOO_BIG, series,
						"Cover.Width", String.valueOf(series.getCoverDimensions().Item1),
						"Cover.Height", String.valueOf(series.getCoverDimensions().Item2)
				));

		// Invalid Tag for series
		addSeriesValidation(
				DatabaseErrorType.ERROR_TAG_NOT_VALID_ON_SERIES,
				o -> o.ValidateSeries,
				(series, e) ->
				{
					for (CCSingleTag t : series.getTags().ccstream()) {
						if (!t.IsSeriesTag) e.add(DatabaseError.createSingle(
								movielist,
								DatabaseErrorType.ERROR_TAG_NOT_VALID_ON_SERIES, series,
								"Tag.Index", String.valueOf(t.Index),
								"Tag.Description", t.Description
							));
					}
				});

		// Title contains invalid characters
		addSeriesValidation(
				DatabaseErrorType.ERROR_INVALID_CHARACTERS,
				o -> o.ValidateSeries,
				series -> DatabaseStringNormalization.hasInvalidCharacters(series.getTitle()),
				series -> DatabaseError.createSingle(
						movielist,
						DatabaseErrorType.ERROR_INVALID_CHARACTERS, series,
						"Title", series.getTitle()
				));

		// Muted language in subtitles
		addSeriesValidation(
				DatabaseErrorType.ERROR_INVALID_ONLINESCORE,
				o -> o.ValidateMovies,
				series -> !series.getOnlinescore().isValid(),
				series -> DatabaseError.createSingle(movielist, DatabaseErrorType.ERROR_INVALID_ONLINESCORE, series));
	}

	@SuppressWarnings({"nls"})
	private void initSeasons()
	{
		// no title set
		addSeasonValidation(
				DatabaseErrorType.ERROR_TITLE_NOT_SET,
				o -> o.ValidateSeasons,
				season -> season.getTitle().isEmpty(),
				season -> DatabaseError.createSingle(movielist, DatabaseErrorType.ERROR_TITLE_NOT_SET, season));

		// cover not set
		addSeasonValidation(
				DatabaseErrorType.ERROR_NOCOVERSET,
				o -> o.ValidateSeries,
				season -> season.getCoverID() == -1,
				season -> DatabaseError.createSingle(movielist, DatabaseErrorType.ERROR_NOCOVERSET, season));

		// cover not found
		addSeasonValidation(
				DatabaseErrorType.ERROR_COVER_NOT_FOUND,
				o -> o.ValidateCoverFiles,
				(season, movielist) -> !movielist.getCoverCache().coverFileExists(season.getCoverID()),
				(season, movielist) -> DatabaseError.createSingle(movielist, DatabaseErrorType.ERROR_COVER_NOT_FOUND, season));

		// Wrong AddDate
		addSeasonValidation(
				DatabaseErrorType.ERROR_WRONG_ADDDATE,
				o -> o.ValidateSeasons,
				season -> !season.isEmpty() && (season.getAddDate().isLessEqualsThan(CCDate.getMinimumDate()) || season.getAddDate().isGreaterThan(CCDate.getCurrentDate())),
				season -> DatabaseError.createSingle(
						movielist,
						DatabaseErrorType.ERROR_WRONG_ADDDATE, season,
						"Date", season.getAddDate().toStringSQL()
				));

		// Zyklus/Title ends/starts with a space
		addSeasonValidation(
				DatabaseErrorType.ERROR_NOT_TRIMMED,
				o -> o.ValidateSeasons,
				season -> Str.isUntrimmed(season.getTitle()),
				season -> DatabaseError.createSingle(
						movielist,
						DatabaseErrorType.ERROR_NOT_TRIMMED, season,
						"Title", "'"+season.getTitle()+"'"
				));

		// Non-continoous episode numbers
		addSeasonValidation(
				DatabaseErrorType.ERROR_NON_CONTINOOUS_EPISODES,
				o -> o.ValidateSeasons,
				season -> !season.isContinoousEpisodeNumbers(),
				season -> DatabaseError.createSingle(
						movielist,
						DatabaseErrorType.ERROR_NON_CONTINOOUS_EPISODES, season,
						"Numbers", season.iteratorEpisodes().stringjoin(p -> String.valueOf(p.EpisodeNumber.get()), "..")
				));

		// Cover too small
		addSeasonValidation(
				DatabaseErrorType.ERROR_COVER_TOO_SMALL,
				o -> o.ValidateSeasons,
				season -> season.getCoverDimensions().Item1 < ImageUtilities.BASE_COVER_WIDTH && season.getCoverDimensions().Item2 < ImageUtilities.BASE_COVER_HEIGHT,
				season -> DatabaseError.createSingle(
						movielist,
						DatabaseErrorType.ERROR_COVER_TOO_SMALL, season,
						"Cover.Width", String.valueOf(season.getCoverDimensions().Item1),
						"Cover.Height", String.valueOf(season.getCoverDimensions().Item2)
				));

		// Cover too big
		addSeasonValidation(
				DatabaseErrorType.ERROR_COVER_TOO_BIG,
				o -> o.ValidateSeasons,
				season -> season.getCoverDimensions().Item1 > ImageUtilities.getCoverWidth(movielist.ccprops()) && season.getCoverDimensions().Item2 < ImageUtilities.getCoverHeight(movielist.ccprops()),
				season -> DatabaseError.createSingle(
						movielist,
						DatabaseErrorType.ERROR_COVER_TOO_BIG, season,
						"Cover.Width", String.valueOf(season.getCoverDimensions().Item1),
						"Cover.Height", String.valueOf(season.getCoverDimensions().Item2)
				));

		// Title contains invalid characters
		addSeriesValidation(
				DatabaseErrorType.ERROR_INVALID_CHARACTERS,
				o -> o.ValidateSeasons,
				season -> DatabaseStringNormalization.hasInvalidCharacters(season.getTitle()),
				season -> DatabaseError.createSingle(
						movielist,
						DatabaseErrorType.ERROR_INVALID_CHARACTERS, season,
						"Title", season.getTitle()
				));
	}

	@SuppressWarnings({"nls"})
	private void initEpisodes()
	{
		// no title set
		addEpisodeValidation(
				DatabaseErrorType.ERROR_TITLE_NOT_SET,
				o -> o.ValidateEpisodes,
				episode -> episode.getTitle().isEmpty(),
				episode -> DatabaseError.createSingle(movielist, DatabaseErrorType.ERROR_TITLE_NOT_SET, episode));

		// Wrong Format
		addEpisodeValidation(
				DatabaseErrorType.ERROR_FORMAT_NOT_FOUND_IN_PARTS,
				o -> o.ValidateEpisodes,
				episode -> !episode.getFormat().equals(CCFileFormat.getMovieFormat(episode.getPart().getExtension())),
				episode -> DatabaseError.createSingle(
						movielist,
						DatabaseErrorType.ERROR_FORMAT_NOT_FOUND_IN_PARTS, episode,
						"Format[Database]", episode.getFormat().asString(),
						"Format[Files]", String.valueOf(CCFileFormat.getMovieFormat(episode.getPart().getExtension()))
				));

		// Inexistent Paths
		addEpisodeValidation(
				DatabaseErrorType.ERROR_PATH_NOT_FOUND,
				o -> o.ValidateVideoFiles,
				episode -> !episode.getPart().toFSPath(this).exists(),
				episode -> DatabaseError.createSingle(
						movielist,
						DatabaseErrorType.ERROR_PATH_NOT_FOUND, episode,
						"CCPath", episode.Part.get().toString(),
						"FSPath", episode.Part.get().toFSPath(this).toString()
				));

		// Moviesize <> Real size
		addEpisodeValidation(
				DatabaseErrorType.ERROR_WRONG_FILESIZE,
				o -> o.ValidateVideoFiles,
				episode -> getRelativeDifference(episode.getPart().toFSPath(this).filesize().getBytes(), episode.getFilesize().getBytes()) > getMaxSizeFileDrift(),
				episode -> DatabaseError.createSingle(
						movielist,
						DatabaseErrorType.ERROR_WRONG_FILESIZE, episode,
						"Filesize[Filesystem]", String.valueOf(episode.getPart().toFSPath(this).filesize().getBytes()),
						"Filesize[Database]", String.valueOf(episode.getFilesize().getBytes()),
						"MaxSizeFileDrift", String.valueOf(getMaxSizeFileDrift())
				));

		// Wrong AddDate
		addEpisodeValidation(
				DatabaseErrorType.ERROR_WRONG_ADDDATE,
				o -> o.ValidateEpisodes,
				episode -> episode.getAddDate().isLessEqualsThan(CCDate.getMinimumDate()) || episode.getAddDate().isGreaterThan(CCDate.getCurrentDate()),
				episode -> DatabaseError.createSingle(
						movielist,
						DatabaseErrorType.ERROR_WRONG_ADDDATE, episode,
						"Date", episode.getAddDate().toStringSQL()
				));

		// Wrong LastViewedDate
		addEpisodeValidation(
				DatabaseErrorType.ERROR_WRONG_LASTVIEWEDDATE,
				o -> o.ValidateEpisodes,
				episode -> episode.getViewedHistoryLast().isGreaterThan(CCDate.getCurrentDate()),
				episode -> DatabaseError.createSingle(
						movielist,
						DatabaseErrorType.ERROR_WRONG_LASTVIEWEDDATE, episode,
						"Date", episode.getViewedHistoryLast().toStringSQL()
				));

		// Wrong Viewed State // Dar nicht benutzt werden - da leider durch den Import der alten CC-Database viele solche Fälle vorkommen
		//addEpisodeValidation(
		//		DatabaseErrorType.ERROR_LASTVIEWED_AND_ISVIEWED_IS_PARADOX,
		//		o -> o.ValidateEpisodes,
		//		episode -> episode.getLastViewed().isGreaterThan(MIN_DATE) ^ episode.isViewed(),
		//		episode -> DatabaseError.createSingle(movielist, DatabaseErrorType.ERROR_LASTVIEWED_AND_ISVIEWED_IS_PARADOX, episode));

		// Zyklus ends/starts with a space
		addEpisodeValidation(
				DatabaseErrorType.ERROR_NOT_TRIMMED,
				o -> o.ValidateEpisodes,
				episode -> Str.isUntrimmed(episode.getTitle()),
				episode -> DatabaseError.createSingle(
						movielist,
						DatabaseErrorType.ERROR_NOT_TRIMMED, episode,
						"Title", "'"+episode.getTitle()+"'"
						));

		// MediaInfo not set
		addEpisodeValidation(
				DatabaseErrorType.ERROR_MEDIAINFO_UNSET,
				o -> o.ValidateEpisodes,
				episode -> episode.mediaInfo().get().isUnset(),
				episode -> DatabaseError.createSingle(
						movielist,
						DatabaseErrorType.ERROR_MEDIAINFO_UNSET, episode,
						CCStreams.iterate(episode.mediaInfo().getPartial().validate(false)).map(p -> Tuple.Create("MediaInfo."+p+".IsSet", "false")).toList()));

		// MediaInfo size does not match movie filesize
		addEpisodeValidation(
				DatabaseErrorType.ERROR_MEDIAINFO_SIZE_MISMATCH,
				o -> o.ValidateEpisodes,
				episode -> episode.mediaInfo().get().isSet() && !CCFileSize.isEqual(episode.mediaInfo().get().getFilesize(), episode.getFilesize()),
				episode -> DatabaseError.createSingle(
						movielist,
						DatabaseErrorType.ERROR_MEDIAINFO_SIZE_MISMATCH, episode,
						"MediaInfo.Filesize", String.valueOf(episode.mediaInfo().get().getFilesize().getBytes()),
						"Database.Filesize", String.valueOf(episode.getFilesize().getBytes())
						));

		// MediaInfo length does not match movie length
		addEpisodeValidation(
				DatabaseErrorType.ERROR_MEDIAINFO_LENGTH_MISMATCH,
				o -> o.ValidateEpisodes,
				episode -> episode.mediaInfo().get().isSet() &&
						!episode.Tags.get(CCSingleTag.TAG_MISSING_TIME) &&
						!episode.Tags.get(CCSingleTag.TAG_FILE_CORRUPTED) &&
						!episode.Tags.get(CCSingleTag.TAG_WATCH_CAMRIP) &&
						!episode.Tags.get(CCSingleTag.TAG_WATCH_MICDUBBED) &&
						!episode.Tags.get(CCSingleTag.TAG_WRONG_LANGUAGE) &&
						isDiff(episode.mediaInfo().get().getDurationInMinutes(), episode.getLength(), 0.33, 5),
				episode -> DatabaseError.createSingle(
						movielist,
						DatabaseErrorType.ERROR_MEDIAINFO_LENGTH_MISMATCH, episode,
						"MediaInfo.Duration", String.valueOf(episode.mediaInfo().get().getDurationInMinutes()),
						"Database.Duration", String.valueOf(episode.getLength())
						));

		// Mediainfo attributes not match actual file
		addEpisodeValidation(
				DatabaseErrorType.ERROR_MEDIAINFO_CDATE_CHANGED,
				o -> o.ValidateVideoFiles,
				(episode, e) ->
				{
					if (episode.mediaInfo().get().isSet()) {
						try {
							BasicFileAttributes attr = episode.getPart().toFSPath(this).readFileAttr();
							if (attr.creationTime().toMillis() != episode.mediaInfo().get().getCDate())
								e.add(DatabaseError.createSingle(
										movielist,
										DatabaseErrorType.ERROR_MEDIAINFO_CDATE_CHANGED, episode,
										"Filesystem.CDate.Milliseconds", String.valueOf(attr.creationTime().toMillis()),
										"MediaInfo.CDate.Milliseconds", String.valueOf(episode.mediaInfo().get().getCDate())
								));
						} catch (IOException ex) {
							/**/
						}
					}
				});
		addEpisodeValidation(
				DatabaseErrorType.ERROR_MEDIAINFO_MDATE_CHANGED,
				o -> o.ValidateVideoFiles,
				(episode, e) ->
				{
					if (episode.mediaInfo().get().isSet()) {
						try {
							BasicFileAttributes attr = episode.getPart().toFSPath(this).readFileAttr();
							if (attr.lastModifiedTime().toMillis() != episode.mediaInfo().get().getMDate())
								e.add(DatabaseError.createSingle(
										movielist,
										DatabaseErrorType.ERROR_MEDIAINFO_MDATE_CHANGED, episode,
										"Filesystem.MDate.Milliseconds", String.valueOf(attr.lastModifiedTime().toMillis()),
										"MediaInfo.MDate.Milliseconds", String.valueOf(episode.mediaInfo().get().getMDate())
								));
						} catch (IOException ex) {
							/**/
						}
					}
				});

		// Mediainfo does not match actual file
		addEpisodeValidation(
				DatabaseErrorType.ERROR_MEDIAINFO_FILE_CHANGED,
				o -> o.ValidateVideoFiles,
				(episode, e) ->
				{
					if (episode.mediaInfo().get().isSet()) {
						if (!CCFileSize.isEqual(episode.getPart().toFSPath(this).filesize(), episode.mediaInfo().get().getFilesize()))
							e.add(DatabaseError.createSingle(movielist, DatabaseErrorType.ERROR_MEDIAINFO_FILE_CHANGED, episode));
					}
				});

		// Watch never <> ViewedState
		addEpisodeValidation(
				DatabaseErrorType.ERROR_IMPOSSIBLE_WATCH_NEVER,
				o -> o.ValidateEpisodes,
				episode -> episode.isViewed() && episode.Tags.get(CCSingleTag.TAG_WATCH_NEVER),
				episode -> DatabaseError.createSingle(movielist, DatabaseErrorType.ERROR_IMPOSSIBLE_WATCH_NEVER, episode));

		// LastViewed too small
		addEpisodeValidation(
				DatabaseErrorType.ERROR_LASTWATCHED_TOO_OLD,
				o -> o.ValidateEpisodes,
				episode -> !episode.getViewedHistoryLast().isMinimum() && episode.getViewedHistoryLast().isLessThan(CCDate.create(1, 6, 1900)),
				episode -> DatabaseError.createSingle(
						movielist,
						DatabaseErrorType.ERROR_LASTWATCHED_TOO_OLD, episode,
						"ViewedHistoryLast", episode.getViewedHistoryLast().toStringSQL()));

		// Folderstructure not formatted
		addEpisodeValidation(
				DatabaseErrorType.ERROR_INVALID_SERIES_STRUCTURE,
				o -> o.ValidateEpisodes,
				episode -> Options.ValidateSeriesStructure && ! episode.checkFolderStructure(),
				episode -> DatabaseError.createSingle(
						movielist,
						DatabaseErrorType.ERROR_INVALID_SERIES_STRUCTURE, episode,
						"Actual<CCPath>", episode.getPart().toString(),
						"Should<CCPath> (relative)", episode.getRelativeFileForCreatedFolderstructure(),
						"Actual<FSPath>", episode.getPart().toFSPath(this).toString(),
						"Should<FSPath> (relative)", episode.getRelativeFileForCreatedFolderstructure()));

		// Invalid path characters
		addEpisodeValidation(
				DatabaseErrorType.ERROR_INVALID_CHARS_IN_PATH,
				o -> o.ValidateEpisodes,
				episode -> CCPath.containsIllegalSymbols(episode.getPart()),
				episode -> DatabaseError.createSingle(
						movielist,
						DatabaseErrorType.ERROR_INVALID_CHARS_IN_PATH, episode,
						"Part", episode.Part.get().toString()
				));

		// History but not viewed
		addEpisodeValidation(
				DatabaseErrorType.ERROR_HISTORY_BUT_UNVIEWED,
				o -> o.ValidateEpisodes,
				episode -> !episode.isViewed() && !episode.ViewedHistory.get().isEmpty(),
				episode -> DatabaseError.createSingle(movielist, DatabaseErrorType.ERROR_HISTORY_BUT_UNVIEWED, episode));

		// Viewed but no history
		addEpisodeValidation(
				DatabaseErrorType.ERROR_VIEWED_BUT_NO_HISTORY,
				o -> o.ValidateEpisodes,
				episode -> episode.isViewed() && episode.ViewedHistory.get().isEmpty(),
				episode -> DatabaseError.createSingle(movielist, DatabaseErrorType.ERROR_VIEWED_BUT_NO_HISTORY, episode));

		// History is invalid
		addEpisodeValidation(
				DatabaseErrorType.ERROR_INVALID_HISTORY,
				o -> o.ValidateEpisodes,
				episode -> !episode.ViewedHistory.get().isValid(),
				episode -> DatabaseError.createSingle(movielist, DatabaseErrorType.ERROR_INVALID_HISTORY, episode));

		// Invalid Tag for episode
		addEpisodeValidation(
				DatabaseErrorType.ERROR_TAG_NOT_VALID_ON_EPISODE,
				o -> o.ValidateEpisodes,
				(episode, e) ->
				{
					for (CCSingleTag t : episode.getTags().ccstream()) {
						if (!t.IsEpisodeTag) e.add(DatabaseError.createSingle(
								movielist,
								DatabaseErrorType.ERROR_TAG_NOT_VALID_ON_EPISODE, episode,
								"Tag.Index", String.valueOf(t.Index),
								"Tag.Description", t.Description
						));
					}
				});

		// Not-normalized path
		addEpisodeValidation(
				DatabaseErrorType.ERROR_NON_NORMALIZED_PATH,
				o -> o.ValidateEpisodes,
				episode -> !CCPath.isEqual(CCPath.createFromFSPath(episode.getPart().toFSPath(this), this), episode.getPart()),
				episode -> DatabaseError.createSingle(
						movielist,
						DatabaseErrorType.ERROR_NON_NORMALIZED_PATH, episode,
						"Path.FSPath", episode.Part.get().toFSPath(this).toString(),
						"Path.CCPath[Actual]", episode.getPart().toString(),
						"Path.CCPath[Should]", CCPath.createFromFSPath(episode.getPart().toFSPath(this), this).toString()
				));

		// No language
		addEpisodeValidation(
				DatabaseErrorType.ERROR_NO_LANG,
				o -> o.ValidateEpisodes,
				episode -> episode.getLanguage().isEmpty(),
				episode -> DatabaseError.createSingle(movielist, DatabaseErrorType.ERROR_NO_LANG, episode));

		// Muted language must be single
		addEpisodeValidation(
				DatabaseErrorType.ERROR_LANG_MUTED_SUBSET,
				o -> o.ValidateEpisodes,
				episode -> episode.getLanguage().contains(CCDBLanguage.MUTED) && !episode.getLanguage().isSingle(),
				episode -> DatabaseError.createSingle(movielist, DatabaseErrorType.ERROR_LANG_MUTED_SUBSET, episode));

		// Title contains invalid characters
		addEpisodeValidation(
				DatabaseErrorType.ERROR_INVALID_CHARACTERS,
				o -> o.ValidateEpisodes,
				episode -> DatabaseStringNormalization.hasInvalidCharacters(episode.getTitle()),
				episode -> DatabaseError.createSingle(
						movielist,
						DatabaseErrorType.ERROR_INVALID_CHARACTERS, episode,
						"Title", episode.getTitle()
				));

		// invalid MediaInfo
		addEpisodeValidation(
				DatabaseErrorType.ERROR_MEDIAINFO_INVALID,
				o -> o.ValidateEpisodes,
				episode -> episode.mediaInfo().get().isSet() && !episode.mediaInfo().getPartial().validate().isEmpty(),
				episode -> DatabaseError.createSingleAdditional(
						movielist, DatabaseErrorType.ERROR_MEDIAINFO_INVALID, episode,
						episode.mediaInfo().getPartial().validate().stream().findFirst().orElse("?"),
						"MediaInfo.validate", String.join(";", episode.mediaInfo().getPartial().validate())
				));

		// Hash is invalid
		addEpisodeValidation(
				DatabaseErrorType.ERROR_INVALID_HASH,
				o -> o.ValidateEpisodes,
				episode -> episode.mediaInfo().get().isSet() && !ChecksumHelper.isValidVideoHash(episode.mediaInfo().get().getChecksum()),
				episode -> DatabaseError.createSingle(
						movielist, DatabaseErrorType.ERROR_INVALID_HASH, episode,
						"Checksum", episode.mediaInfo().get().getChecksum()
				));

		// Hash is impossible (length/fislesize mismatch)
		addEpisodeValidation(
				DatabaseErrorType.ERROR_IMPOSSIBLE_HASH,
				o -> o.ValidateEpisodes,
				episode -> episode.mediaInfo().get().isSet() &&
						ChecksumHelper.isValidVideoHash(episode.mediaInfo().get().getChecksum()) &&
						!ChecksumHelper.isPossibleVideoHash(episode.mediaInfo().get().getChecksum(), episode.mediaInfo().get()),
				episode -> DatabaseError.createSingle(
						movielist,
						DatabaseErrorType.ERROR_IMPOSSIBLE_HASH, episode,
						"Checksum", episode.mediaInfo().get().getChecksum()
				));

		// Muted language in subtitles
		addEpisodeValidation(
				DatabaseErrorType.ERROR_SUBTITLE_MUTED,
				o -> o.ValidateEpisodes,
				episode -> episode.getSubtitles().contains(CCDBLanguage.MUTED),
				episode -> DatabaseError.createSingle(movielist, DatabaseErrorType.ERROR_SUBTITLE_MUTED, episode));
	}

	@Override
	@SuppressWarnings("nls")
	protected void findCoverErrors(List<DatabaseError> e, DoubleProgressCallbackListener pcl)
	{
		ICoverCache cc = movielist.getCoverCache();

		pcl.stepRootAndResetSub("Find cover errors",  //$NON-NLS-1$
				movielist.getElementCount() +         // Duplicate Covers
				movielist.getMovieCount() +           // Duplicate/Invalid CoverIDs
				movielist.getSeriesCount() +          // Duplicate/Invalid CoverIDs
				movielist.getSeasonCount() +          // Duplicate/Invalid CoverIDs
				cc.getCoverCount() +                  // Unused CoverIDs
				cc.getCoverCount());                  // Reused cover filenames

		// ###############################################
		// Duplicate Covers
		// ###############################################

		List<DatabaseCoverElement> cvrList = new ArrayList<>();

		for (CCDatabaseElement el : movielist.iteratorElements()) {
			pcl.stepSub(el.getFullDisplayTitle());

			cvrList.add(new DatabaseCoverElement(el.getCoverID(), el));

			if (el.isSeries()) {
				for (int j = 0; j < el.asSeries().getSeasonCount(); j++) {
					cvrList.add(new DatabaseCoverElement(el.asSeries().getSeasonByArrayIndex(j).getCoverID(), el.asSeries().getSeasonByArrayIndex(j)));
				}
			}
		}

		Collections.sort(cvrList);

		for (int i = 1; i < cvrList.size(); i++) {
			if (cvrList.get(i).equalsCover(cvrList.get(i-1))) {
				e.add(DatabaseError.createDouble(
						movielist,
						DatabaseErrorType.ERROR_DUPLICATE_COVERLINK, cvrList.get(i-1).getElement(), cvrList.get(i).getElement(),
						"Cover1.ID", String.valueOf(cvrList.get(i).getCoverID()),
						"Cover2.ID", String.valueOf(cvrList.get(i-1).getCoverID()),
						"Cover1.Filename", cvrList.get(i).getElement().getCoverInfo().Filename,
						"Cover2.Filename", cvrList.get(i-1).getElement().getCoverInfo().Filename,
						"Cover1.Element.ID", String.valueOf(cvrList.get(i).getElement().getLocalID()),
						"Cover2.Element.ID", String.valueOf(cvrList.get(i-1).getElement().getLocalID()),
						"Cover1.Element.Title", cvrList.get(i).getElement().getQualifiedTitle(),
						"Cover2.Element.Title", cvrList.get(i-1).getElement().getQualifiedTitle()
				));
			}
		}

		// ###############################################
		// Duplicate/Invalid CoverIDs
		// ###############################################

		Set<Integer> coverIDsTable = CCStreams.iterate(cc.listCovers()).map(p->p.ID).toSet();

		HashMap<Integer, ICCCoveredElement> coversDatabase = new HashMap<>();

		for (CCMovie m : movielist.iteratorMovies())
		{
			pcl.stepSub(m.getFullDisplayTitle());

			if (m.getCoverID() == -1) continue;

			if (coversDatabase.containsKey(m.getCoverID())) {
				e.add(DatabaseError.createDouble(
						movielist,
						DatabaseErrorType.ERROR_DUPLICATE_COVERID, m,
						coversDatabase.get(m.getCoverID()),
						"CoverID", String.valueOf(m.getCoverID()),
						"Element1.ID", String.valueOf(coversDatabase.get(m.getCoverID()).getLocalID()),
						"Element1.Title", coversDatabase.get(m.getCoverID()).getQualifiedTitle(),
						"Element2.ID", String.valueOf(m.getLocalID()),
						"Element2.Title", m.getQualifiedTitle()
					));
				continue;
			}

			coversDatabase.put(m.getCoverID(), m);

			if (!coverIDsTable.contains(m.getCoverID())) e.add(
					DatabaseError.createSingle(
							movielist, DatabaseErrorType.ERROR_COVERID_NOT_FOUND, m,
							"CoverID", String.valueOf(m.getCoverID()),
							"SourceElement.ID", String.valueOf(m.getLocalID()),
							"SourceElement.Title", m.getQualifiedTitle()
						));
		}

		for (CCSeries s : movielist.iteratorSeries())
		{
			pcl.stepSub(s.getTitle());

			if (s.getCoverID() == -1) continue;

			if (coversDatabase.containsKey(s.getCoverID())) {
				e.add(DatabaseError.createDouble(
						movielist, DatabaseErrorType.ERROR_DUPLICATE_COVERID, s,
						coversDatabase.get(s.getCoverID()),
						"CoverID", String.valueOf(s.getCoverID()),
						"Element1.ID", String.valueOf(coversDatabase.get(s.getCoverID()).getLocalID()),
						"Element1.Title", coversDatabase.get(s.getCoverID()).getQualifiedTitle(),
						"Element2.ID", String.valueOf(s.getLocalID()),
						"Element2.Title", s.getQualifiedTitle()
				));
				continue;
			}

			coversDatabase.put(s.getCoverID(), s);

			if (!coverIDsTable.contains(s.getCoverID())) e.add(DatabaseError.createSingle(
					movielist,
					DatabaseErrorType.ERROR_COVERID_NOT_FOUND, s,
					"CoverID", String.valueOf(s.getCoverID()),
					"SourceElement.ID", String.valueOf(s.getLocalID()),
					"SourceElement.Title", s.getQualifiedTitle()
					));
		}

		for (CCSeason s : movielist.iteratorSeasons())
		{
			pcl.stepSub(s.getSeries().getTitle() + " S" + s.getSeasonNumber()); //$NON-NLS-1$

			if (s.getCoverID() == -1) continue;

			if (coversDatabase.containsKey(s.getCoverID())) {
				e.add(DatabaseError.createDouble(
						movielist,
						DatabaseErrorType.ERROR_DUPLICATE_COVERID, s,
						coversDatabase.get(s.getCoverID()),
						"CoverID", String.valueOf(s.getCoverID()),
						"Element1.ID", String.valueOf(coversDatabase.get(s.getCoverID()).getLocalID()),
						"Element1.Title", coversDatabase.get(s.getCoverID()).getQualifiedTitle(),
						"Element2.ID", String.valueOf(s.getLocalID()),
						"Element2.Title", s.getQualifiedTitle()
					));
				continue;
			}

			coversDatabase.put(s.getCoverID(), s);

			if (!coverIDsTable.contains(s.getCoverID())) e.add(DatabaseError.createSingle(
					movielist,
					DatabaseErrorType.ERROR_COVERID_NOT_FOUND, s,
					"CoverID", String.valueOf(s.getCoverID()),
					"SourceElement.ID", String.valueOf(s.getLocalID()),
					"SourceElement.Title", s.getQualifiedTitle()
					));
		}

		// ###############################################
		// Unused CoverIDs
		// ###############################################

		for (CCCoverData cce : cc.listCovers()) {
			pcl.stepSub(cce.Filename);

			if (!coversDatabase.containsKey(cce.ID)) {
				e.add(DatabaseError.createSingle(
						movielist,
						DatabaseErrorType.ERROR_UNUSED_COVER_ENTRY, cce.Filename,
						"Cover.ID", String.valueOf(cce.ID),
						"Cover.Filename", cce.Filename,
						"Cover.Timestamp", cce.Timestamp.toStringSQL()
						));
			}
		}

		// ###############################################
		// Reused cover filenames
		// ###############################################

		HashSet<String> coverFileMapping = new HashSet<>();
		for (CCCoverData cce : cc.listCovers()) {
			pcl.stepSub(cce.Filename);

			if (coverFileMapping.contains(cce.Filename.toLowerCase())) {
				e.add(DatabaseError.createSingle(
						movielist, DatabaseErrorType.ERROR_DUPLICATE_REFERENCES_COVER_FILE, cce.Filename,
						"Cover.ID", String.valueOf(cce.ID),
						"Cover.Filename", cce.Filename,
						"Cover.Timestamp", cce.Timestamp.toStringSQL()
						));
				continue;
			}
			coverFileMapping.add(cce.Filename.toLowerCase());
		}
	}

	@Override
	@SuppressWarnings({"nls", "ConstantConditions"})
	protected void findCoverFileErrors(List<DatabaseError> e, DoubleProgressCallbackListener pcl)
	{
		pcl.stepRootAndResetSub("Validate cover files", 1); //$NON-NLS-1$

		ICoverCache cc = movielist.getCoverCache();
		List<Tuple<String, Func0to1WithIOException<BufferedImage>>> files = new ArrayList<>();
		if (cc instanceof CCDefaultCoverCache) files = ((CCDefaultCoverCache)cc).listCoversInFilesystem();

		files = CCStreams.iterate(files).filter(p -> !Str.equals(p.Item1, "Thumbs.db")).toList();

		pcl.setSubMax(files.size() + cc.getCoverCount());

		// ###############################################
		// Too much Cover in Folder
		// ###############################################

		List<DatabaseCoverElement> cvrList = new ArrayList<>();

		for (CCDatabaseElement el : movielist.iteratorElements()) {
			cvrList.add(new DatabaseCoverElement(el.getCoverID(), el));

			if (el.isSeries()) {
				for (int j = 0; j < el.asSeries().getSeasonCount(); j++) {
					cvrList.add(new DatabaseCoverElement(el.asSeries().getSeasonByArrayIndex(j).getCoverID(), el.asSeries().getSeasonByArrayIndex(j)));
				}
			}
		}

		for (int i = 0; i < files.size(); i++)
		{
			String cvrname = files.get(i).Item1;
			pcl.stepSub(cvrname);

			boolean found = false;
			for (int j = 0; j < cvrList.size(); j++)
			{
				CCCoverData cce = cc.getInfoOrNull(cvrList.get(j).getCoverID());
				found |= cce.Filename.equalsIgnoreCase(cvrname);
			}
			if (! found) {
				if (cc instanceof CCDefaultCoverCache) {
					e.add(DatabaseError.createSingle(
							movielist, DatabaseErrorType.ERROR_NONLINKED_COVERFILE, ((CCDefaultCoverCache)cc).getCoverPath().append(cvrname).toFile(),
							"Filename", cvrname,
							"Path", ((CCDefaultCoverCache)cc).getCoverPath().append(cvrname).toString()
					));
				} else {
					e.add(DatabaseError.createSingle(
							movielist, DatabaseErrorType.ERROR_NONLINKED_COVERFILE, cvrname,
							"Filename", cvrname
					));
				}
			}
		}

		// ###############################################
		// Cover metadata
		// ###############################################

		for (CCCoverData cce : cc.listCovers())
		{
			pcl.stepSub(cce.Filename);

			var fp = cc.getFilepath(cce);
			if (fp == null || fp.isEmpty()) continue;

			String checksum = "ERR"; //$NON-NLS-1$
			try (FileInputStream fis = new FileInputStream(fp.toFile())) { checksum = DigestUtils.sha256Hex(fis).toUpperCase(); } catch (IOException ex) {/**/}
			if (! checksum.equals(cce.Checksum)) e.add(DatabaseError.createSingle(
					movielist,
					DatabaseErrorType.ERROR_COVER_CHECKSUM_MISMATCH, cce.Filename,
					"Should", checksum,
					"Actual", cce.Checksum
					));

			BufferedImage img = cc.getCover(cce);
			if (img.getWidth() != cce.Width || img.getHeight() != cce.Height) e.add(DatabaseError.createSingle(
					movielist,
					DatabaseErrorType.ERROR_COVER_DIMENSIONS_MISMATCH, cce.Filename,
					"Width.Should", String.valueOf(img.getWidth()),
					"Width.Actual", String.valueOf(cce.Width),
					"Height.Should", String.valueOf(img.getHeight()),
					"Height.Actual", String.valueOf(cce.Height)
					));

			if (!CCFileSize.isEqual(fp.filesize(), cce.Filesize)) e.add(DatabaseError.createSingle(
					movielist,
					DatabaseErrorType.ERROR_COVER_FILESIZE_MISMATCH, cce.Filename,
					"Filesize.Should", String.valueOf(fp.filesize().getBytes()),
					"Filesize.Actual", String.valueOf(cce.Filesize)
					));
		}
	}

	@Override
	@SuppressWarnings("nls")
	protected void findDuplicateFilesByPath(List<DatabaseError> e, DoubleProgressCallbackListener pcl)
	{
		pcl.stepRootAndResetSub("Find duplicate files (path)", 2 * movielist.getTotalDatabaseCount()); //$NON-NLS-1$

		List<DatabaseFileElement> flList = new ArrayList<>();

		for (CCDatabaseElement el : movielist.iteratorElements()) {
			if (el.isMovie()) {
				for (int i = 0; i < el.asMovie().getPartcount(); i++) {
					if (Options.IgnoreDuplicateIfos && CCFileFormat.getMovieFormat(el.asMovie().Parts.get(i).getExtension()) == CCFileFormat.IFO) {
						continue;
					}

					flList.add(new DatabaseFileElement(el.asMovie().Parts.get(i), el.asMovie()));
				}
			} else if (el.isSeries()) {
				CCSeries s = el.asSeries();
				for (int i = 0; i < s.getSeasonCount(); i++) {
					CCSeason se = s.getSeasonByArrayIndex(i);
					for (int j = 0; j < se.getEpisodeCount(); j++) {
						if (Options.IgnoreDuplicateIfos && CCFileFormat.getMovieFormat(se.getEpisodeByArrayIndex(j).getPart().getExtension()) == CCFileFormat.IFO) {
							continue;
						}

						flList.add(new DatabaseFileElement(se.getEpisodeByArrayIndex(j).getPart(), se.getEpisodeByArrayIndex(j)));
					}
				}
			}

			pcl.stepSub(el.getFullDisplayTitle());
		}

		Collections.sort(flList);

		for (int i = 1; i < flList.size(); i++) {
			if (!flList.get(i).getPath().isEmpty() && flList.get(i).equalsPath(flList.get(i-1))) {
				e.add(DatabaseError.createDouble(
						movielist,
						DatabaseErrorType.ERROR_DUPLICATE_FILELINK, flList.get(i-1).getElement(), flList.get(i).getElement(),
						"Element1.Path",  flList.get(i-1).getPath().toString(),
						"Element1.ID",    String.valueOf(flList.get(i-1).getElement().getLocalID()),
						"Element1.Title", flList.get(i-1).getElement().getQualifiedTitle(),
						"Element2.Path",  flList.get(i-1).getPath().toString(),
						"Element2.ID",    String.valueOf(flList.get(i-1).getElement().getLocalID()),
						"Element2.Title", flList.get(i-1).getElement().getQualifiedTitle()
				));
			}

			pcl.stepSub(flList.get(i).getPath().getFilenameWithExt());
		}
	}

	@Override
	@SuppressWarnings("nls")
	protected void findDuplicateFilesByMediaInfo(List<DatabaseError> e, DoubleProgressCallbackListener pcl)
	{
		pcl.stepRootAndResetSub("Find duplicate files (mediainfo)", movielist.getMovieCount() + movielist.getEpisodeCount()); //$NON-NLS-1$

		HashMap<String, ICCPlayableElement> xmap = new HashMap<>();

		for (ICCPlayableElement el : movielist.iteratorPlayables())
		{
			if (el.mediaInfo().get().isUnset()) continue;
			if (Str.isNullOrWhitespace(el.mediaInfo().get().getChecksum())) continue;

			var key = el.mediaInfo().get().getChecksum();

			if (!xmap.containsKey(key))
			{
				xmap.put(key, el);
			}
			else
			{
				e.add(DatabaseError.createDouble(
						movielist,
						DatabaseErrorType.ERROR_DUPLICATE_FILE, el, xmap.get(key),
						"Element1.ID", String.valueOf(xmap.get(key).getLocalID()),
						"Element1.Title", xmap.get(key).getQualifiedTitle(),
						"Element1.MediaInfo.Checksum", xmap.get(key).mediaInfo().get().getChecksum(),
						"Element2.ID", String.valueOf(xmap.get(key).getLocalID()),
						"Element2.Title", xmap.get(key).getQualifiedTitle(),
						"Element2.MediaInfo.Checksum", xmap.get(key).mediaInfo().get().getChecksum()
				));
			}

			pcl.stepSub(el.title().get());
		}
	}

	@Override
	@SuppressWarnings("nls")
	protected void findErrorInGroups(List<DatabaseError> e, DoubleProgressCallbackListener pcl)
	{
		pcl.stepRootAndResetSub("Validate groups", 4 * movielist.getGroupCount()); //$NON-NLS-1$

		Set<String> groupSet = new HashSet<>();
		for (CCGroup group : movielist.getGroupList()) {
			pcl.stepSub(group.Name);

			if (! groupSet.add(group.Name.toLowerCase().trim())) {
				e.add(DatabaseError.createSingle(
						movielist, DatabaseErrorType.ERROR_INVALID_GROUP, group,
						"Problem", "Duplicate",
						"Group.Name", group.Name
						));
				break;
			}

			if (group.Name.toLowerCase().trim().isEmpty()) {
				e.add(DatabaseError.createSingle(
						movielist,
						DatabaseErrorType.ERROR_INVALID_GROUP, group,
						"Problem", "Empty name",
						"Group.Name", group.Name
				));
				break;
			}

			if (! group.Name.trim().equals(group.Name)) {
				e.add(DatabaseError.createSingle(
						movielist,
						DatabaseErrorType.ERROR_INVALID_GROUP, group,
						"Problem", "Not trimmed",
						"Group.Name", group.Name
				));
				break;
			}
		}

		for (CCGroup group : movielist.getGroupList()) {
			pcl.stepSub(group.Name);

			if (group.Parent.isEmpty()) continue;

			CCGroup parent = movielist.getGroupOrNull(group.Parent);
			if (parent == null) {
				e.add(DatabaseError.createSingle(
						movielist,
						DatabaseErrorType.ERROR_INVALID_GROUP_PARENT, group,
						"Group.Name", group.Name,
						"Group.Parent", group.Parent
				));
				break;
			}

		}

		for (CCGroup group : movielist.getGroupList()) {
			pcl.stepSub(group.Name);

			CCGroup g = group;

			StringBuilder recpath = new StringBuilder(g.Name);

			for(int i = 0; i < CCGroup.MAX_SUBGROUP_DEPTH; i++) {
				if (g.Parent.isEmpty()) {
					g = null;
					recpath.append(" -> [/root]");
					break;
				}

				CCGroup parent = movielist.getGroupOrNull(g.Parent);
				if (parent == null) {
					// parent is invalid - error was throw above
					g = null;
					recpath.append(" -> [[ERROR]]");
					break;
				} else {
					g = parent;
					recpath.append(" -> ").append(g.Name);
				}
			}

			if (g != null) {
				e.add(DatabaseError.createSingle(
						movielist,
						DatabaseErrorType.ERROR_GROUP_NESTING_TOO_DEEP, group,
						"Group.Name", group.Name,
						"Group.Parent", group.Parent,
						"RecursivePath<Group>", recpath.toString()
						));
			}

		}

		for (CCGroup group : movielist.getGroupList()) {
			pcl.stepSub(group.Name);

			if (movielist.getDatabaseElementsbyGroup(group).isEmpty() && movielist.getSubGroups(group).isEmpty()) {
				e.add(DatabaseError.createSingle(
						movielist,
						DatabaseErrorType.ERROR_UNUSED_GROUP, group,
						"Group.Name", group.Name
				));
			}
		}

	}

	@Override
	@SuppressWarnings("nls")
	protected void findDuplicateOnlineRef(List<DatabaseError> e, DoubleProgressCallbackListener pcl)
	{
		pcl.stepRootAndResetSub("Validate online references", movielist.getElementCount()); //$NON-NLS-1$

		var refSet = new HashMap<String, CCDatabaseElement>();

		for (CCMovie el : movielist.iteratorMovies())
		{
			for(CCSingleOnlineReference soref : el.getOnlineReference())
			{
				var key = el.getLanguage().serializeToLong() + '_' + soref.toSerializationString();
				if (!refSet.containsKey(key))
				{
					refSet.put(key, el);
				}
				else
				{
					var el2 = refSet.get(key);
					if (el == el2)
						e.add(DatabaseError.createSingle(
								movielist,
								DatabaseErrorType.ERROR_DUPLICATE_REF, el,
								"Element1+2.ID", String.valueOf(el.getLocalID()),
								"Element1+2.Title", el.getQualifiedTitle()
								));
					else
						e.add(DatabaseError.createDouble(
								movielist,
								DatabaseErrorType.ERROR_DUPLICATE_REF, el, el2,
								"Element1.ID", String.valueOf(el.getLocalID()),
								"Element1.Title", el.getQualifiedTitle(),
								"Element2.ID", String.valueOf(el2.getLocalID()),
								"Element2.Title", el2.getQualifiedTitle()
						));
				}
			}

			pcl.stepSub(el.getFullDisplayTitle());
		}

		for (CCSeries el : movielist.iteratorSeries())
		{
			for(CCSingleOnlineReference soref : el.getOnlineReference())
			{
				var key = el.getSemiCommonLanguages().serializeToLong() + '_' + soref.toSerializationString();
				if (!refSet.containsKey(key))
				{
					refSet.put(key, el);
				}
				else
				{
					var el2 = refSet.get(key);
					if (el == el2)
						e.add(DatabaseError.createSingle(
								movielist, DatabaseErrorType.ERROR_DUPLICATE_REF, el,
								"Element1+2.ID", String.valueOf(el.getLocalID()),
								"Element1+2.Title", el.getQualifiedTitle()
						));
					else
						e.add(DatabaseError.createDouble(
								movielist, DatabaseErrorType.ERROR_DUPLICATE_REF, el, el2,
								"Element1.ID", String.valueOf(el.getLocalID()),
								"Element1.Title", el.getQualifiedTitle(),
								"Element2.ID", String.valueOf(el2.getLocalID()),
								"Element2.Title", el2.getQualifiedTitle()
						));
				}
			}

			pcl.stepSub(el.getFullDisplayTitle());
		}
	}

	@Override
	@SuppressWarnings("nls")
	protected void findInternalDatabaseErrors(List<DatabaseError> e, DoubleProgressCallbackListener pcl)
	{
		pcl.stepRootAndResetSub("Validate database layer", 7);

		PublicDatabaseInterface db = movielist.getInternalDatabaseDirectly();

		// ### 1 ###
		try
		{
			pcl.stepSub("Global ID uniqueness");

			List<Integer> ids_mov = db.querySQL("SELECT LOCALID FROM MOVIES",                1, o -> (int)o[0]);
			List<Integer> ids_ser = db.querySQL("SELECT LOCALID FROM SERIES",                1, o -> (int)o[0]);
			List<Integer> ids_sea = db.querySQL("SELECT LOCALID FROM SEASONS",               1, o -> (int)o[0]);
			List<Integer> ids_epi = db.querySQL("SELECT LOCALID FROM EPISODES",              1, o -> (int)o[0]);

			List<Integer> duplicates = CCStreams
					.iterate(ids_mov, ids_ser, ids_sea, ids_epi)
					.groupBy(p->p)
					.filter(p->p.getValue().size()>1)
					.map(Map.Entry::getKey)
					.enumerate();

			for (int dup : duplicates) {
				e.add(DatabaseError.createSingle(
						movielist,
						DatabaseErrorType.ERROR_DB_DUPLICATE_ID, dup,
						"ID", String.valueOf(dup)
						));
			}

			{
				int last_id = db.querySingleIntSQLThrow("SELECT CAST(IVALUE AS INTEGER) FROM INFO WHERE IKEY='LAST_ID'", 0);
				for (int tlid : CCStreams.iterate(ids_mov, ids_ser, ids_sea, ids_epi).filter(p -> p > last_id)) {
					e.add(DatabaseError.createSingle(
							movielist,
							DatabaseErrorType.ERROR_DB_TOO_LARGE_ID, tlid,
							"ID", String.valueOf(tlid),
							"LAST_ID", String.valueOf(last_id)
							));
				}
			}

			{
				int last_cid = db.querySingleIntSQLThrow("SELECT CAST(IVALUE AS INTEGER) FROM INFO WHERE IKEY='LAST_COVERID'", 0);
				for (int tlcid : CCStreams.iterate(movielist.getCoverCache().listCovers()).map(p -> p.ID).filter(p -> p > last_cid)) {
					e.add(DatabaseError.createSingle(
							movielist,
							DatabaseErrorType.ERROR_DB_TOO_LARGE_COVERID, tlcid,
							"ID", String.valueOf(tlcid),
							"LAST_COVERID", String.valueOf(last_cid)
					));
				}
			}
		}
		catch (Exception ex)
		{
			e.add(DatabaseError.createSingle(movielist, DatabaseErrorType.ERROR_DB_EXCEPTION, ex));
		}


		// ### 2 ###
		try
		{
			pcl.stepSub("Missing series entry");

			List<Integer> errs = db.querySQL("SELECT DISTINCT SEASONS.SERIESID FROM SEASONS LEFT JOIN SERIES ON SEASONS.SERIESID=SERIES.LOCALID WHERE SERIES.LOCALID IS NULL", 1, o -> (int)o[0]);

			for (int errid : errs) {
				e.add(DatabaseError.createSingle(
						movielist,
						DatabaseErrorType.ERROR_DB_MISSING_SERIES, errid,
						"ID", String.valueOf(errid)
				));
			}
		}
		catch (Exception ex)
		{
			e.add(DatabaseError.createSingle(movielist, DatabaseErrorType.ERROR_DB_EXCEPTION, ex));
		}


		// ### 3 ###
		try
		{
			pcl.stepSub("Missing seasons entry");

			List<Integer> errs = db.querySQL("SELECT DISTINCT EPISODES.SEASONID FROM EPISODES LEFT JOIN SEASONS ON EPISODES.SEASONID=SEASONS.LOCALID WHERE SEASONS.LOCALID IS NULL", 1, o -> (int)o[0]);

			for (int errid : errs) {
				e.add(DatabaseError.createSingle(
						movielist,
						DatabaseErrorType.ERROR_DB_MISSING_SEASON, errid,
						"ID", String.valueOf(errid)
				));
			}
		}
		catch (Exception ex)
		{
			e.add(DatabaseError.createSingle(movielist, DatabaseErrorType.ERROR_DB_EXCEPTION, ex));
		}


		// ### 4 ###
		try
		{
			pcl.stepSub("Validate CoverIDs");

			List<Integer> cvr1 = db.querySQL("SELECT COVERID FROM MOVIES WHERE COVERID <> -1", 1, o -> (int)o[0]);
			List<Integer> cvr2 = db.querySQL("SELECT COVERID FROM SERIES WHERE COVERID <> -1", 1, o -> (int)o[0]);
			List<Integer> cvr3 = db.querySQL("SELECT COVERID FROM SEASONS WHERE COVERID <> -1",  1, o -> (int)o[0]);

			List<Integer> cvrall = db.querySQL("SELECT ID FROM COVERS",  1, o -> (int)o[0]);

			List<Integer> duplicates = CCStreams
					.iterate(cvr1, cvr2, cvr3)
					.groupBy(p->p)
					.filter(p->p.getValue().size()>1)
					.map(Map.Entry::getKey)
					.enumerate();

			for (int dup : duplicates) {
				e.add(DatabaseError.createSingle(
						movielist,
						DatabaseErrorType.ERROR_DB_MULTI_REF_COVER, dup,
						"ID", String.valueOf(dup)
				));
			}

			for (int cvrid : CCStreams.iterate(cvrall).filter(p -> !cvr1.contains(p) && !cvr2.contains(p) && !cvr3.contains(p) )) {
				e.add(DatabaseError.createSingle(
						movielist,
						DatabaseErrorType.ERROR_DB_UNUSED_COVERID, cvrid,
						"ID", String.valueOf(cvrid)
				));
			}

			for (int cvrid : CCStreams.iterate(cvr1, cvr2, cvr3).filter(p -> !cvrall.contains(p) )) {
				e.add(DatabaseError.createSingle(
						movielist,
						DatabaseErrorType.ERROR_DB_DANGLING_COVERID, cvrid,
						"ID", String.valueOf(cvrid)
				));
			}
		}
		catch (Exception ex)
		{
			e.add(DatabaseError.createSingle(movielist, DatabaseErrorType.ERROR_DB_EXCEPTION, ex));
		}

		// ### 5 ###
		try
		{
			pcl.stepSub("Validate History trigger");

			var ref_err = new RefParam<String>();

			if (movielist.getHistory().isHistoryActive()) {
				if (!movielist.getHistory().testTrigger(true, ref_err))
					e.add(DatabaseError.createNone(
							movielist,
							DatabaseErrorType.ERROR_HTRIGGER_ENABLED_ERR,
							"IsHistoryActive", "true",
							"Trigger.Errors", ref_err.Value
					));
			} else {
				if (!movielist.getHistory().testTrigger(false, ref_err))
					e.add(DatabaseError.createNone(
							movielist,
							DatabaseErrorType.ERROR_HTRIGGER_DISABLED_ERR,
							"IsHistoryActive", "false",
							"Trigger.Errors", ref_err.Value
					));
			}

		}
		catch (Exception ex)
		{
			e.add(DatabaseError.createSingle(movielist, DatabaseErrorType.ERROR_DB_EXCEPTION, ex));
		}

		// ### 6 ###
		try
		{
			pcl.stepSub("Check database foreign keys");

			var r = db.querySQL("PRAGMA foreign_key_check;", 4);

			if (r.size() > 0) throw new Exception("foreign_key_check returned " + r.size() + " errors");
		}
		catch (Exception ex)
		{
			e.add(DatabaseError.createSingle(movielist, DatabaseErrorType.ERROR_DB_EXCEPTION, ex));
		}

		// ### 7 ###
		try
		{
			pcl.stepSub("Check database foreign keys");

			var r1 = db.querySingleStringSQLThrow("PRAGMA integrity_check;", 0);
			if (!r1.equalsIgnoreCase("ok")) throw new Exception("integrity_check == '" + r1 + "'");

			var r2 = db.querySingleStringSQLThrow("PRAGMA quick_check;", 0);
			if (!r2.equalsIgnoreCase("ok")) throw new Exception("integrity_check == '" + r2 + "'");
		}
		catch (Exception ex)
		{
			e.add(DatabaseError.createSingle(movielist, DatabaseErrorType.ERROR_DB_EXCEPTION, ex));
		}

	}
}
