package de.jClipCorn.database.migration;

import de.jClipCorn.database.covertab.CCDefaultCoverCache;
import de.jClipCorn.database.driver.GenericDatabase;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.colorquantizer.ColorQuantizer;
import de.jClipCorn.util.colorquantizer.ColorQuantizerMethod;
import de.jClipCorn.util.colorquantizer.util.ColorQuantizerConverter;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.sqlwrapper.CCSQLStatement;
import de.jClipCorn.util.sqlwrapper.SQLBuilder;
import org.apache.commons.codec.digest.DigestUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import static de.jClipCorn.database.driver.DatabaseStructure.*;

public class Migration_12_13 extends DBMigration {

	public Migration_12_13(GenericDatabase db, CCProperties ccprops, FSPath databaseDirectory, String databaseName, boolean readonly) {
		super(db, ccprops, databaseDirectory, databaseName, readonly);
	}

	@Override
	public String getFromVersion() {
		return "12";
	}

	@Override
	public String getToVersion() {
		return "13";
	}

	@Override
	@SuppressWarnings("nls")
	public List<UpgradeAction> migrate() throws Exception {
		CCLog.addInformation("[UPGRADE v12 -> v13] Change 'covername' column to 'coverid' in main table");
		CCLog.addInformation("[UPGRADE v12 -> v13] Change 'covername' column to 'coverid' in season table");
		CCLog.addInformation("[UPGRADE v12 -> v13] Add 'covers' table");
		CCLog.addInformation("[UPGRADE v12 -> v13] Fill 'covers' table with data");
		CCLog.addInformation("[UPGRADE v12 -> v13] Calculate cover previews");
		CCLog.addInformation("[UPGRADE v12 -> v13] Calculate cover checksums");

		db.executeSQLThrow("ALTER TABLE ELEMENTS ADD COLUMN COVERID INTEGER");
		db.executeSQLThrow("ALTER TABLE SEASONS ADD COLUMN COVERID INTEGER");

		db.executeSQLThrow("CREATE TABLE COVERS(ID INTEGER PRIMARY KEY,FILENAME VARCHAR NOT NULL,WIDTH INTEGER NOT NULL,HEIGHT INTEGER NOT NULL,HASH_FILE VARCHAR(64) NOT NULL,FILESIZE BIGINT NOT NULL,PREVIEW_TYPE INTEGER NOT NULL,PREVIEW BLOB NOT NULL,CREATED VARCHAR NOT NULL)");

		var cvrdir = databaseDirectory.append(databaseName, CCDefaultCoverCache.COVER_DIRECTORY_NAME);

		final String prefix = ccprops.PROP_COVER_PREFIX.getValue();
		final String suffix = "." + ccprops.PROP_COVER_TYPE.getValue();  //$NON-NLS-1$

		String[] files = cvrdir.listFilenames((path, name) -> name.startsWith(prefix) && name.endsWith(suffix));
		for (String _f : files) {
			var f = cvrdir.append(_f);
			BufferedImage img = ImageIO.read(f.toFile());

			int id = Integer.parseInt(f.getFilenameWithoutExt().substring(prefix.length()));
			String fn = f.getFilenameWithExt();

			String checksum;
			try (FileInputStream fis = new FileInputStream(f.toFile())) { checksum = DigestUtils.sha256Hex(fis).toUpperCase(); }

			ColorQuantizerMethod ptype = ccprops.PROP_DATABASE_COVER_QUANTIZER.getValue();
			ColorQuantizer quant = ptype.create();
			quant.analyze(img, 16);
			byte[] preview = ColorQuantizerConverter.quantizeTo4BitRaw(quant, ColorQuantizerConverter.shrink(img, ColorQuantizerConverter.PREVIEW_WIDTH));

			CCSQLStatement stmt = SQLBuilder.createInsert(TAB_COVERS)
					.addPreparedFields(COL_CVRS_ID, COL_CVRS_FILENAME, COL_CVRS_WIDTH)
					.addPreparedFields(COL_CVRS_HEIGHT, COL_CVRS_HASH_FILE)
					.addPreparedFields(COL_CVRS_FILESIZE, COL_CVRS_PREVIEW, COL_CVRS_PREVIEWTYPE, COL_CVRS_CREATED)
					.build(db::createPreparedStatement, new ArrayList<>());

			stmt.clearParameters();

			stmt.setInt(COL_CVRS_ID,          id);
			stmt.setStr(COL_CVRS_FILENAME,    fn);
			stmt.setInt(COL_CVRS_WIDTH,       img.getWidth());
			stmt.setInt(COL_CVRS_HEIGHT,      img.getHeight());
			stmt.setStr(COL_CVRS_HASH_FILE,   checksum);
			stmt.setLng(COL_CVRS_FILESIZE,    f.filesize().getBytes());
			stmt.setBlb(COL_CVRS_PREVIEW,     preview);
			stmt.setInt(COL_CVRS_PREVIEWTYPE, ptype.asInt());
			stmt.setCDT(COL_CVRS_CREATED,     CCDateTime.createFromFileTimestamp(f.toFile().lastModified(), TimeZone.getDefault()));

			stmt.executeUpdate();

			db.executeSQLThrow("UPDATE ELEMENTS SET COVERID = "+id+" WHERE COVERNAME = '"+fn+"'");
			db.executeSQLThrow("UPDATE SEASONS  SET COVERID = "+id+" WHERE COVERNAME = '"+fn+"'");

			CCLog.addDebug("Migrated file: " + fn);
		}

		db.executeSQLThrow("CREATE TABLE _ELEMENTS(LOCALID INTEGER PRIMARY KEY,NAME VARCHAR(128) NOT NULL,VIEWED BIT NOT NULL,VIEWED_HISTORY VARCHAR(4096) NOT NULL,ZYKLUS VARCHAR(128) NOT NULL,ZYKLUSNUMBER INTEGER NOT NULL,QUALITY TINYINT NOT NULL,LANGUAGE BIGINT NOT NULL,GENRE BIGINT NOT NULL,LENGTH INTEGER NOT NULL,ADDDATE DATE NOT NULL,ONLINESCORE TINYINT NOT NULL,FSK TINYINT NOT NULL,FORMAT TINYINT NOT NULL,MOVIEYEAR SMALLINT NOT NULL,ONLINEREF VARCHAR(128) NOT NULL,GROUPS VARCHAR(4096) NOT NULL,FILESIZE BIGINT NOT NULL,TAGS SMALLINT NOT NULL,PART1 VARCHAR(512) NOT NULL,PART2 VARCHAR(512) NOT NULL,PART3 VARCHAR(512) NOT NULL,PART4 VARCHAR(512) NOT NULL,PART5 VARCHAR(512) NOT NULL,PART6 VARCHAR(512) NOT NULL,SCORE TINYINT NOT NULL,COVERID INTEGER(256) NOT NULL,TYPE TINYINT NOT NULL,SERIESID INTEGER NOT NULL)");
		db.executeSQLThrow("CREATE TABLE _SEASONS(SEASONID INTEGER PRIMARY KEY,SERIESID INTEGER NOT NULL,NAME VARCHAR(128) NOT NULL,SEASONYEAR SMALLINT NOT NULL,COVERID INTEGER(256) NOT NULL,FOREIGN KEY(SERIESID) REFERENCES ELEMENTS(LOCALID))");

		db.executeSQLThrow("INSERT INTO _ELEMENTS SELECT LOCALID,NAME,VIEWED,VIEWED_HISTORY,ZYKLUS,ZYKLUSNUMBER,QUALITY,LANGUAGE,GENRE,LENGTH,ADDDATE,ONLINESCORE,FSK,FORMAT,MOVIEYEAR,ONLINEREF,GROUPS,FILESIZE,TAGS,PART1,PART2,PART3,PART4,PART5,PART6,SCORE,COVERID,TYPE,SERIESID FROM ELEMENTS");
		db.executeSQLThrow("INSERT INTO _SEASONS SELECT SEASONID,SERIESID,NAME,SEASONYEAR,COVERID FROM SEASONS");

		db.executeSQLThrow("DROP TABLE ELEMENTS");
		db.executeSQLThrow("DROP TABLE SEASONS");

		db.executeSQLThrow("ALTER TABLE _ELEMENTS RENAME TO ELEMENTS");
		db.executeSQLThrow("ALTER TABLE _SEASONS RENAME TO SEASONS");

		var cc = cvrdir.append("covercache.xml");
		if (cc.exists()) cc.deleteWithException();

		return new ArrayList<>();
	}
}
