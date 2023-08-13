<Query Kind="Program" />

string TEMPLATE_UPDATE = @"
CREATE TRIGGER LOGTRIGGER_{TAB}_{COL} AFTER UPDATE ON {TAB} WHEN NOT (COALESCE(OLD.`{COL}` = NEW.`{COL}`, 1=0) OR (COALESCE(OLD.`{COL}`,NEW.`{COL}`) IS NULL))
BEGIN 
	INSERT INTO HISTORY (`TABLE`, `ID`, `DATE`, `ACTION`, `FIELD`, `OLD`, `NEW`) VALUES ('{TAB}', OLD.`{ID}`, STRFTIME('%Y-%m-%d %H:%M:%f', 'NOW'), 'UPDATE', '{COL}', OLD.`{COL}`, NEW.`{COL}`);
END;
".Trim();

string TEMPLATE_INSERT = @"INSERT INTO HISTORY (`TABLE`, `ID`, `DATE`, `ACTION`, `FIELD`, `OLD`, `NEW`) VALUES ('{TAB}', NEW.`{ID}`, STRFTIME('%Y-%m-%d %H:%M:%f', 'NOW'), 'ADD', '{COL}', NULL, NEW.`{COL}`);";
string TEMPLATE_DELETE = @"INSERT INTO HISTORY (`TABLE`, `ID`, `DATE`, `ACTION`, `FIELD`, `OLD`, `NEW`) VALUES ('{TAB}', OLD.`{ID}`, STRFTIME('%Y-%m-%d %H:%M:%f', 'NOW'), 'DELETE', '{COL}', OLD.`{COL}`, NULL);";

const int T_VARCHAR  = 100;
const int T_INTEGER  = 101;
const int T_BIGINT   = 102;
const int T_BIT      = 103;
const int T_SMALLINT = 104;
const int T_DATE     = 105;
const int T_TINYINT  = 106;
const int T_BLOB     = 107;

void Main()
{
	$"BEGIN TRANSACTION;".Dump();
	"".Dump();
	"".Dump();

	var data = new[]
	{
		("COVERS",   ("ID", T_INTEGER),       new[] { ("FILENAME", T_VARCHAR), ("WIDTH", T_INTEGER), ("HEIGHT", T_INTEGER), ("HASH_FILE", T_VARCHAR), ("FILESIZE", T_BIGINT), ("PREVIEW_TYPE", T_INTEGER), ("PREVIEW", T_BLOB), ("CREATED", T_VARCHAR) }),
		("ELEMENTS", ("LOCALID", T_INTEGER),  new[] { ("NAME", T_VARCHAR), ("VIEWED", T_BIT), ("VIEWED_HISTORY", T_VARCHAR), ("ZYKLUS", T_VARCHAR), ("ZYKLUSNUMBER", T_INTEGER), ("QUALITY", T_TINYINT), ("LANGUAGE", T_BIGINT), ("GENRE", T_BIGINT), ("LENGTH", T_INTEGER), ("ADDDATE", T_DATE), ("ONLINESCORE", T_TINYINT), ("FSK", T_TINYINT), ("FORMAT", T_TINYINT), ("MOVIEYEAR", T_SMALLINT), ("ONLINEREF", T_VARCHAR), ("GROUPS", T_VARCHAR), ("FILESIZE", T_BIGINT), ("TAGS", T_SMALLINT), ("PART1", T_VARCHAR), ("PART2", T_VARCHAR), ("PART3", T_VARCHAR), ("PART4", T_VARCHAR), ("PART5", T_VARCHAR), ("PART6", T_VARCHAR), ("SCORE", T_TINYINT), ("COVERID", T_INTEGER), ("TYPE", T_TINYINT), ("SERIESID", T_INTEGER) }),
		("EPISODES", ("LOCALID", T_INTEGER),  new[] { ("SEASONID", T_INTEGER), ("EPISODE", T_SMALLINT), ("NAME", T_VARCHAR), ("VIEWED", T_BIT), ("VIEWED_HISTORY", T_VARCHAR), ("QUALITY", T_TINYINT), ("LENGTH", T_INTEGER), ("FORMAT", T_TINYINT), ("FILESIZE", T_BIGINT), ("PART1", T_VARCHAR), ("ADDDATE", T_DATE), ("TAGS", T_SMALLINT), ("LANGUAGE", T_BIGINT) }),
		("GROUPS",   ("NAME", T_VARCHAR),     new[] { ("ORDERING", T_INTEGER), ("COLOR", T_INTEGER), ("SERIALIZE", T_BIT), ("PARENTGROUP", T_VARCHAR), ("VISIBLE", T_BIT) }),
		("INFO",     ("IKEY", T_VARCHAR),     new[] { ("IVALUE", T_VARCHAR) }),
		("SEASONS",  ("SEASONID", T_INTEGER), new[] { ("SERIESID", T_INTEGER), ("NAME", T_VARCHAR), ("SEASONYEAR", T_SMALLINT), ("COVERID", T_INTEGER) }),
	};

	foreach (var (table_name, (idcol_name, idcol_type), columns) in data)
	{
		foreach (var (col_name, col_type) in columns)
		{
			var templ = TEMPLATE_UPDATE;
			if (col_type == T_BLOB)
			{
				templ = templ.Replace("OLD.`{COL}`", "(CASE WHEN OLD.`{COL}` IS NULL THEN NULL ELSE hex(OLD.`{COL}`) END)");
				templ = templ.Replace("NEW.`{COL}`", "(CASE WHEN NEW.`{COL}` IS NULL THEN NULL ELSE hex(NEW.`{COL}`) END)");
			}
			var trigger = templ.Replace("{TAB}", table_name).Replace("{ID}", idcol_name).Replace("{COL}", col_name);
			trigger.Dump();
			"".Dump();
		}
	}

	foreach (var (table_name, (idcol_name, idcol_type), columns) in data)
	{
		$"CREATE TRIGGER LOGTRIGGER_{table_name}_ADD AFTER INSERT ON {table_name}".Dump();
		$"BEGIN".Dump();
		foreach (var (col_name, col_type) in columns)
		{
			var templ = TEMPLATE_INSERT;
			if (col_type == T_BLOB)
			{
				templ = templ.Replace("OLD.`{COL}`", "(CASE WHEN OLD.`{COL}` IS NULL THEN NULL ELSE hex(OLD.`{COL}`) END)");
				templ = templ.Replace("NEW.`{COL}`", "(CASE WHEN NEW.`{COL}` IS NULL THEN NULL ELSE hex(NEW.`{COL}`) END)");
			}
			("  " + templ.Replace("{TAB}", table_name).Replace("{ID}", idcol_name).Replace("{COL}", col_name)).Dump();
		}
		$"END;".Dump();
		"".Dump();
	}

	foreach (var (table_name, (idcol_name, idcol_type), columns) in data)
	{
		$"CREATE TRIGGER LOGTRIGGER_{table_name}_DELETE BEFORE DELETE ON {table_name}".Dump();
		$"BEGIN".Dump();
		foreach (var (col_name, col_type) in columns)
		{
			var templ = TEMPLATE_DELETE;
			if (col_type == T_BLOB)
			{
				templ = templ.Replace("OLD.`{COL}`", "(CASE WHEN OLD.`{COL}` IS NULL THEN NULL ELSE hex(OLD.`{COL}`) END)");
				templ = templ.Replace("NEW.`{COL}`", "(CASE WHEN NEW.`{COL}` IS NULL THEN NULL ELSE hex(NEW.`{COL}`) END)");
			}
			("  " + templ.Replace("{TAB}", table_name).Replace("{ID}", idcol_name).Replace("{COL}", col_name)).Dump();
		}
		$"END;".Dump();
		"".Dump();
	}

	"".Dump();
	"".Dump();
	$"END TRANSACTION;".Dump();
}
