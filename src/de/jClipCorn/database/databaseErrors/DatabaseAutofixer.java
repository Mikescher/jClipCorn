package de.jClipCorn.database.databaseErrors;

import java.util.ArrayList;
import java.util.List;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.util.DriveMap;
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
}
