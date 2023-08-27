package de.jClipCorn.features.databaseErrors;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.*;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.ICCPropertySource;
import de.jClipCorn.util.lambda.Func1to1;
import de.jClipCorn.util.lambda.Func2to0;
import de.jClipCorn.util.lambda.Func2to1;
import de.jClipCorn.util.lambda.Func3to0;
import de.jClipCorn.util.listener.DoubleProgressCallbackListener;
import de.jClipCorn.util.stream.CCStreams;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDatabaseValidator implements ICCPropertySource {

	public enum ValidationTarget { MOVIE, SERIES, SEASON, EPISODE }

	public static class ValidationMethod {
		public ValidationTarget Target;
		public DatabaseErrorType ErrorType;
		public Func1to1<DatabaseValidatorOptions, Boolean> Precondition;
		public Func3to0<Object, CCMovieList, List<DatabaseError>> Method;
	}

	private final List<ValidationMethod> _methods = new ArrayList<>();

	protected final CCMovieList movielist;

	protected DatabaseValidatorOptions Options = null;

	protected AbstractDatabaseValidator(CCMovieList ml) {
		movielist = ml;
		init();
	}

	public CCProperties ccprops() {
		return movielist.ccprops();
	}

	protected abstract void init();

	public void validate(List<DatabaseError> e, DatabaseValidatorOptions opt, DoubleProgressCallbackListener pcl)
	{
		Options = opt;

		List<ValidationMethod> vmMovies  = CCStreams.iterate(_methods).filter(m->m.Target==ValidationTarget.MOVIE).filter(m->m.Precondition.invoke(opt)).enumerate();
		List<ValidationMethod> vmSeries  = CCStreams.iterate(_methods).filter(m->m.Target==ValidationTarget.SERIES).filter(m->m.Precondition.invoke(opt)).enumerate();
		List<ValidationMethod> vmSeasons = CCStreams.iterate(_methods).filter(m->m.Target==ValidationTarget.SEASON).filter(m->m.Precondition.invoke(opt)).enumerate();
		List<ValidationMethod> vmEpisode = CCStreams.iterate(_methods).filter(m->m.Target==ValidationTarget.EPISODE).filter(m->m.Precondition.invoke(opt)).enumerate();

		boolean validateElements = vmMovies.size() + vmSeries.size() + vmSeasons.size() + vmEpisode.size() > 0;

		int outerCount = 0;
		if (validateElements) outerCount++;                       // [1]
		if (opt.ValidateCovers) outerCount++;                     // [2]
		if (opt.ValidateCoverFiles) outerCount++;                 // [3]
		if (opt.ValidateDuplicateFilesByPath) outerCount++;       // [4]
		if (opt.ValidateDuplicateFilesByMediaInfo) outerCount++;  // [5]
		if (opt.ValidateGroups) outerCount++;                     // [6]
		if (opt.ValidateOnlineReferences) outerCount++;           // [7]
		if (opt.ValidateDatabaseConsistence) outerCount++;        // [8]
		if (opt.FindEmptyDirectories) outerCount++;               // [9]

		pcl.setMaxAndResetValueBoth(outerCount+1, 1);

		if (validateElements) findElementErrors(e, pcl, vmMovies, vmSeries, vmSeasons, vmEpisode); // [1]

		if (opt.ValidateCovers) findCoverErrors(e, pcl); // [2]

		if (opt.ValidateCoverFiles) findCoverFileErrors(e, pcl); // [3]

		if (opt.ValidateDuplicateFilesByPath) findDuplicateFilesByPath(e, pcl); // [4]

		if (opt.ValidateDuplicateFilesByMediaInfo) findDuplicateFilesByMediaInfo(e, pcl); // [5]

		if (opt.ValidateGroups) findErrorInGroups(e, pcl); // [6]

		if (opt.ValidateOnlineReferences) findDuplicateOnlineRef(e, pcl); // [7]

		if (opt.ValidateDatabaseConsistence) findInternalDatabaseErrors(e, pcl); // [8]

		if (opt.FindEmptyDirectories) findEmptyDirectories(e, pcl); // [9]

		pcl.reset();
	}

	protected double getMaxSizeFileDrift()
	{
		return movielist.ccprops().PROP_VALIDATE_FILESIZEDRIFT.getValue() / 100d;
	}

	protected double getRelativeDifference(long size1, long size2) {
		double diff = Math.abs(size1 - size2);
		long average = (size1 + size2)/2;
		return diff / average;
	}

	protected boolean isDiff(long a, long b, double percDiff, long absDiff) {
		double rd = getRelativeDifference(a, b);
		long   ad = Math.abs(a-b);

		return (rd >= percDiff && ad >= absDiff);
	}

	protected void addMovieValidation(DatabaseErrorType type, Func1to1<DatabaseValidatorOptions, Boolean> precond, Func1to1<CCMovie, Boolean> check, Func1to1<CCMovie, DatabaseError> error) {
		addMovieValidation(type, precond, (m,e) -> { if (check.invoke(m)) e.add(error.invoke(m)); });
	}

	protected void addMovieValidation(DatabaseErrorType type, Func1to1<DatabaseValidatorOptions, Boolean> precond, Func2to1<CCMovie, CCMovieList, Boolean> check, Func2to1<CCMovie, CCMovieList, DatabaseError> error) {
		addMovieValidation(type, precond, (m,ml,e) -> { if (check.invoke(m,ml)) e.add(error.invoke(m,ml)); });
	}

	protected void addMovieValidation(DatabaseErrorType type, Func1to1<DatabaseValidatorOptions, Boolean> precond, Func2to0<CCMovie, List<DatabaseError>> runner) {
		addValidation(ValidationTarget.MOVIE, type, precond, (o,ml,e) -> runner.invoke((CCMovie)o, e));
	}

	protected void addMovieValidation(DatabaseErrorType type, Func1to1<DatabaseValidatorOptions, Boolean> precond, Func3to0<CCMovie, CCMovieList, List<DatabaseError>> runner) {
		addValidation(ValidationTarget.MOVIE, type, precond, (o,ml,e) -> runner.invoke((CCMovie)o, ml, e));
	}

	protected void addSeriesValidation(DatabaseErrorType type, Func1to1<DatabaseValidatorOptions, Boolean> precond, Func1to1<CCSeries, Boolean> check, Func1to1<CCSeries, DatabaseError> error) {
		addSeriesValidation(type, precond, (m,e) -> { if (check.invoke(m)) e.add(error.invoke(m)); });
	}

	protected void addSeriesValidation(DatabaseErrorType type, Func1to1<DatabaseValidatorOptions, Boolean> precond, Func2to1<CCSeries, CCMovieList, Boolean> check, Func2to1<CCSeries, CCMovieList, DatabaseError> error) {
		addSeriesValidation(type, precond, (m,ml,e) -> { if (check.invoke(m,ml)) e.add(error.invoke(m,ml)); });
	}

	protected void addSeriesValidation(DatabaseErrorType type, Func1to1<DatabaseValidatorOptions, Boolean> precond, Func2to0<CCSeries, List<DatabaseError>> runner) {
		addValidation(ValidationTarget.SERIES, type, precond, (o,ml,e) -> runner.invoke((CCSeries)o, e));
	}

	protected void addSeriesValidation(DatabaseErrorType type, Func1to1<DatabaseValidatorOptions, Boolean> precond, Func3to0<CCSeries, CCMovieList, List<DatabaseError>> runner) {
		addValidation(ValidationTarget.SERIES, type, precond, (o,ml,e) -> runner.invoke((CCSeries)o, ml, e));
	}

	protected void addSeasonValidation(DatabaseErrorType type, Func1to1<DatabaseValidatorOptions, Boolean> precond, Func1to1<CCSeason, Boolean> check, Func1to1<CCSeason, DatabaseError> error) {
		addSeasonValidation(type, precond, (m,e) -> { if (check.invoke(m)) e.add(error.invoke(m)); });
	}

	protected void addSeasonValidation(DatabaseErrorType type, Func1to1<DatabaseValidatorOptions, Boolean> precond, Func2to1<CCSeason, CCMovieList, Boolean> check, Func2to1<CCSeason, CCMovieList, DatabaseError> error) {
		addSeasonValidation(type, precond, (m,ml,e) -> { if (check.invoke(m,ml)) e.add(error.invoke(m,ml)); });
	}

	protected void addSeasonValidation(DatabaseErrorType type, Func1to1<DatabaseValidatorOptions, Boolean> precond, Func2to0<CCSeason, List<DatabaseError>> runner) {
		addValidation(ValidationTarget.SEASON, type, precond, (o,ml,e) -> runner.invoke((CCSeason)o, e));
	}

	protected void addSeasonValidation(DatabaseErrorType type, Func1to1<DatabaseValidatorOptions, Boolean> precond, Func3to0<CCSeason, CCMovieList, List<DatabaseError>> runner) {
		addValidation(ValidationTarget.SEASON, type, precond, (o,ml,e) -> runner.invoke((CCSeason)o, ml, e));
	}

	protected void addEpisodeValidation(DatabaseErrorType type, Func1to1<DatabaseValidatorOptions, Boolean> precond, Func1to1<CCEpisode, Boolean> check, Func1to1<CCEpisode, DatabaseError> error) {
		addEpisodeValidation(type, precond, (m,e) -> { if (check.invoke(m)) e.add(error.invoke(m)); });
	}

	protected void addEpisodeValidation(DatabaseErrorType type, Func1to1<DatabaseValidatorOptions, Boolean> precond, Func2to1<CCEpisode, CCMovieList, Boolean> check, Func2to1<CCEpisode, CCMovieList, DatabaseError> error) {
		addEpisodeValidation(type, precond, (m,ml,e) -> { if (check.invoke(m,ml)) e.add(error.invoke(m,ml)); });
	}

	protected void addEpisodeValidation(DatabaseErrorType type, Func1to1<DatabaseValidatorOptions, Boolean> precond, Func2to0<CCEpisode, List<DatabaseError>> runner) {
		addValidation(ValidationTarget.EPISODE, type, precond, (o,ml,e) -> runner.invoke((CCEpisode)o, e));
	}

	protected void addEpisodeValidation(DatabaseErrorType type, Func1to1<DatabaseValidatorOptions, Boolean> precond, Func3to0<CCEpisode, CCMovieList, List<DatabaseError>> runner) {
		addValidation(ValidationTarget.EPISODE, type, precond, (o,ml,e) -> runner.invoke((CCEpisode)o, ml, e));
	}

	private void addValidation(ValidationTarget target, DatabaseErrorType type, Func1to1<DatabaseValidatorOptions, Boolean> precond, Func3to0<Object, CCMovieList, List<DatabaseError>> runner)
	{
		ValidationMethod m = new ValidationMethod();
		m.Target = target;
		m.ErrorType = type;
		m.Precondition = precond;
		m.Method = runner;
		_methods.add(m);
	}

	private void findElementErrors(List<DatabaseError> e, DoubleProgressCallbackListener pcl, List<ValidationMethod> vmMovies, List<ValidationMethod> vmSeries, List<ValidationMethod> vmSeasons, List<ValidationMethod> vmEpisode)
	{
		pcl.stepRootAndResetSub("Validate database elements", movielist.getTotalDatabaseElementCount()); //$NON-NLS-1$

		for (CCDatabaseElement el : movielist.getRawList()) {

			if (el.isMovie()) {
				CCMovie mov = el.asMovie();
				pcl.stepSub(mov.getFullDisplayTitle());

				for (ValidationMethod vm : vmMovies) vm.Method.invoke(mov, movielist, e);
			}
			else // is Series
			{
				CCSeries series = el.asSeries();
				pcl.stepSub(series.getFullDisplayTitle());

				for (ValidationMethod vm : vmSeries) vm.Method.invoke(series, movielist, e);

				for (int seasi = 0; seasi < series.getSeasonCount(); seasi++) {
					CCSeason season = series.getSeasonByArrayIndex(seasi);
					pcl.stepSub(series.getTitle() + " - S" + season.getSeasonNumber()); //$NON-NLS-1$

					for (ValidationMethod vm : vmSeasons) vm.Method.invoke(season, movielist, e);

					for (int epi = 0; epi < season.getEpisodeCount(); epi++) {
						CCEpisode episode = season.getEpisodeByArrayIndex(epi);
						pcl.stepSub(series.getTitle() + " - S" + season.getSeasonNumber() + "E" + episode.getEpisodeNumber()); //$NON-NLS-1$ //$NON-NLS-2$

						for (ValidationMethod vm : vmEpisode) vm.Method.invoke(episode, movielist, e);
					}
				}
			}
		}
	}

	protected abstract void findCoverErrors(List<DatabaseError> e, DoubleProgressCallbackListener pcl);
	protected abstract void findCoverFileErrors(List<DatabaseError> e, DoubleProgressCallbackListener pcl);
	protected abstract void findDuplicateFilesByPath(List<DatabaseError> e, DoubleProgressCallbackListener pcl);
	protected abstract void findDuplicateFilesByMediaInfo(List<DatabaseError> e, DoubleProgressCallbackListener pcl);
	protected abstract void findErrorInGroups(List<DatabaseError> e, DoubleProgressCallbackListener pcl);
	protected abstract void findDuplicateOnlineRef(List<DatabaseError> e, DoubleProgressCallbackListener pcl);
	protected abstract void findInternalDatabaseErrors(List<DatabaseError> e, DoubleProgressCallbackListener pcl);
	protected abstract void findEmptyDirectories(List<DatabaseError> e, DoubleProgressCallbackListener pcl);
}
