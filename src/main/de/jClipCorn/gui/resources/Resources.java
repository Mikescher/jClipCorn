package de.jClipCorn.gui.resources;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import de.jClipCorn.Globals;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.resources.reftypes.*;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.helper.ThreadUtils;
import de.jClipCorn.util.stream.CCStreams;

import javax.swing.*;

@SuppressWarnings("nls")
public class Resources {
	private static HashMap<Tuple<String, ResourceRefType>, ResourceRef> resources = new HashMap<>();

	// ##############################################  <COMMON>  #############################################
	public final static ImageRef IMG_FRAME_ICON 							= registerImage("/app.png");

	public final static ImageRef IMG_COVER_STANDARD 						= registerImage("/standard.png");
	public final static IconRef  ICN_COVER_STANDARD 						= registerOtherIcon("/standard.png");
	public final static IconRef  ICN_COVER_STANDARD_SMALL					= registerOtherIcon("/standard_small.png");
	public final static ImageRef IMG_COVER_NOTFOUND 						= registerImage("/covernotfound.png");
	public final static ImageRef IMG_COVER_SERIES_MASK						= registerImage("/mask_series.png");
	public final static ImageRef IMG_COVER_ERROR_ON							= registerImage("/cover_errror_on.png");
	public final static IconRef  ICN_COVER_ERROR_ON							= registerOtherIcon("/cover_errror_on.png");
	public final static IconRef  ICN_COVER_ERROR_OFF						= registerOtherIcon("/cover_errror_off.png");
	public final static IconRef  ICN_COVER_LOADING_P0						= registerOtherIcon("/loading_p0.png");
	public final static IconRef  ICN_COVER_LOADING_P1						= registerOtherIcon("/loading_p1.png");
	public final static IconRef  ICN_COVER_LOADING_P2						= registerOtherIcon("/loading_p2.png");
	public final static IconRef  ICN_COVER_LOADING_P3						= registerOtherIcon("/loading_p3.png");
	public final static IconRef  ICN_COVER_LOADING_P4						= registerOtherIcon("/loading_p4.png");
	public final static IconRef  ICN_COVER_LOADING_P5						= registerOtherIcon("/loading_p5.png");
	public final static IconRef  ICN_COVER_LOADING_P6						= registerOtherIcon("/loading_p6.png");
	public final static IconRef  ICN_COVER_LOADING_P7						= registerOtherIcon("/loading_p7.png");

	public final static MultiSizeIconRef ICN_WARNING_TRIANGLE		 		= registerMultisizeIcon("/icons/common/error.png");

	public final static MultiSizeIconRef ICN_GENERIC_ORB_GRAY		 		= registerMultisizeIcon("/icons/common/orb_gray.png");
	public final static MultiSizeIconRef ICN_GENERIC_ORB_ORANGE		 		= registerMultisizeIcon("/icons/common/orb_orange.png");
	public final static MultiSizeIconRef ICN_GENERIC_ORB_RED		 		= registerMultisizeIcon("/icons/common/orb_red.png");
	public final static MultiSizeIconRef ICN_GENERIC_ORB_GREEN		 		= registerMultisizeIcon("/icons/common/orb_green.png");
	// #############################################  </COMMON>  #############################################
	
	
	// #############################################  <MENUBAR>  #############################################
	public final static MultiSizeIconRef ICN_MENUBAR_CLOSE		 			= registerMultisizeIcon("/icons/toolbar/door_open.png");
	public final static MultiSizeIconRef ICN_MENUBAR_PARSEXML 				= registerMultisizeIcon("/icons/toolbar/blueprint.png");
	public final static MultiSizeIconRef ICN_MENUBAR_PLAY					= registerMultisizeIcon("/icons/toolbar/control_play.png");
	public final static MultiSizeIconRef ICN_MENUBAR_HIDDENPLAY				= registerMultisizeIcon("/icons/toolbar/control_hiddenplay.png");
	public final static MultiSizeIconRef ICN_MENUBAR_ADD_MOV				= registerMultisizeIcon("/icons/toolbar/add.png");
	public final static MultiSizeIconRef ICN_MENUBAR_ADD_SER				= registerMultisizeIcon("/icons/toolbar/addSer.png");
	public final static MultiSizeIconRef ICN_MENUBAR_ADD_SEA				= registerMultisizeIcon("/icons/toolbar/addSea.png");
	public final static MultiSizeIconRef ICN_MENUBAR_DBCHECK				= registerMultisizeIcon("/icons/toolbar/application_osx_terminal.png");
	public final static MultiSizeIconRef ICN_MENUBAR_CLEARDB				= registerMultisizeIcon("/icons/toolbar/fire_damage.png");
	public final static MultiSizeIconRef ICN_MENUBAR_SETTINGS				= registerMultisizeIcon("/icons/toolbar/gear_in.png");
	public final static MultiSizeIconRef ICN_MENUBAR_PREVIEW_MOV			= registerMultisizeIcon("/icons/toolbar/magnifier.png");
	public final static MultiSizeIconRef ICN_MENUBAR_PREVIEW_SER			= registerMultisizeIcon("/icons/toolbar/magnifier_ser.png");
	public final static MultiSizeIconRef ICN_MENUBAR_REMOVE					= registerMultisizeIcon("/icons/toolbar/bin.png");
	public final static MultiSizeIconRef ICN_MENUBAR_EDIT_MOV				= registerMultisizeIcon("/icons/toolbar/screwdriver_mov.png");
	public final static MultiSizeIconRef ICN_MENUBAR_EDIT_SER				= registerMultisizeIcon("/icons/toolbar/screwdriver_ser.png");
	public final static MultiSizeIconRef ICN_MENUBAR_LOG					= registerMultisizeIcon("/icons/toolbar/system_monitor.png");
	public final static MultiSizeIconRef ICN_MENUBAR_FOLDER					= registerMultisizeIcon("/icons/toolbar/folder.png");
	public final static MultiSizeIconRef ICN_MENUBAR_VIEWED					= registerMultisizeIcon("/icons/toolbar/viewed.png");
	public final static MultiSizeIconRef ICN_MENUBAR_UNVIEWED				= registerMultisizeIcon("/icons/toolbar/unviewed.png");
	public final static MultiSizeIconRef ICN_MENUBAR_UNDOVIEWED				= registerMultisizeIcon("/icons/toolbar/undoviewed.png");
	public final static MultiSizeIconRef ICN_MENUBAR_SCANFOLDER				= registerMultisizeIcon("/icons/toolbar/scanner.png");
	public final static MultiSizeIconRef ICN_MENUBAR_ABOUT					= registerMultisizeIcon("/icons/toolbar/info_rhombus.png");
	public final static MultiSizeIconRef ICN_MENUBAR_MCHANGE_VIEWED			= registerMultisizeIcon("/icons/toolbar/knot.png");
	public final static MultiSizeIconRef ICN_MENUBAR_MCHANGE_SCORE			= registerMultisizeIcon("/icons/toolbar/knotscore.png");
	public final static MultiSizeIconRef ICN_MENUBAR_RESETVIEWED			= registerMultisizeIcon("/icons/toolbar/scroller_bar.png");
	public final static MultiSizeIconRef ICN_MENUBAR_REGENERATEDUUID		= registerMultisizeIcon("/icons/toolbar/omelet.png");
	public final static MultiSizeIconRef ICN_MENUBAR_AUTOFINDREF			= registerMultisizeIcon("/icons/toolbar/cash_register.png");
	public final static MultiSizeIconRef ICN_MENUBAR_MOVESERIES				= registerMultisizeIcon("/icons/toolbar/move_to_folder.png");
	public final static MultiSizeIconRef ICN_MENUBAR_MOVEALLSERIES			= registerMultisizeIcon("/icons/toolbar/move_to_folder2.png");
	public final static MultiSizeIconRef ICN_MENUBAR_MOVEALLMOVIES			= registerMultisizeIcon("/icons/toolbar/move_to_folder3.png");
	public final static MultiSizeIconRef ICN_MENUBAR_FINDINCOMPLETEZYKLUS	= registerMultisizeIcon("/icons/toolbar/blackboard_steps.png");
	public final static MultiSizeIconRef ICN_MENUBAR_UPDATEMETADATA			= registerMultisizeIcon("/icons/toolbar/update_contact_info.png");
	public final static MultiSizeIconRef ICN_MENUBAR_FILENAMERULES			= registerMultisizeIcon("/icons/toolbar/books.png");
	public final static MultiSizeIconRef ICN_MENUBAR_CREATE_JXMLBKP			= registerMultisizeIcon("/icons/toolbar/source_code.png");
	public final static MultiSizeIconRef ICN_MENUBAR_COMPARE				= registerMultisizeIcon("/icons/toolbar/balance_unbalance.png");
	public final static MultiSizeIconRef ICN_MENUBAR_SEARCH					= registerMultisizeIcon("/icons/toolbar/bubblechart.png");
	public final static MultiSizeIconRef ICN_MENUBAR_ONLINEREFERENCE		= registerMultisizeIcon("/icons/toolbar/www_page.png");
	public final static MultiSizeIconRef ICN_MENUBAR_EXPORTPLAINDB			= registerMultisizeIcon("/icons/toolbar/external.png");
	public final static MultiSizeIconRef ICN_MENUBAR_OPENFILE				= registerMultisizeIcon("/icons/toolbar/open_folder.png");
	public final static MultiSizeIconRef ICN_MENUBAR_RESTARTAPP				= registerMultisizeIcon("/icons/toolbar/recycle_bag.png");
	public final static MultiSizeIconRef ICN_MENUBAR_IMPORTDB				= registerMultisizeIcon("/icons/toolbar/source_code_bullet_down.png");
	public final static MultiSizeIconRef ICN_MENUBAR_IMPORMULTIPLEELEMENTS	= registerMultisizeIcon("/icons/toolbar/file_manager_bullet_add.png");
	public final static MultiSizeIconRef ICN_MENUBAR_IMPORTMOVIE			= registerMultisizeIcon("/icons/toolbar/script_bullet_add_mov.png");
	public final static MultiSizeIconRef ICN_MENUBAR_EXPORTMOVIE			= registerMultisizeIcon("/icons/toolbar/script_bullet_save_mov.png");
	public final static MultiSizeIconRef ICN_MENUBAR_EXPORTELEMENTS			= registerMultisizeIcon("/icons/toolbar/file_manager_bullet_save.png");
	public final static MultiSizeIconRef ICN_MENUBAR_IMPORTSERIES			= registerMultisizeIcon("/icons/toolbar/script_bullet_add_ser.png");
	public final static MultiSizeIconRef ICN_MENUBAR_EXPORTSERIES			= registerMultisizeIcon("/icons/toolbar/script_bullet_save_ser.png");
	public final static MultiSizeIconRef ICN_MENUBAR_EPISODEGUIDE			= registerMultisizeIcon("/icons/toolbar/document_quote.png");
	public final static MultiSizeIconRef ICN_MENUBAR_RANDOMMOVIE			= registerMultisizeIcon("/icons/toolbar/magic_wand_2.png");
	public final static MultiSizeIconRef ICN_MENUBAR_TAGS					= registerMultisizeIcon("/icons/toolbar/tags_cloud.png");
	public final static MultiSizeIconRef ICN_MENUBAR_TAG_0	 				= registerMultisizeIcon("/icons/tags/tag_0_on.png");
	public final static MultiSizeIconRef ICN_MENUBAR_TAG_1	 				= registerMultisizeIcon("/icons/tags/tag_1_on.png");
	public final static MultiSizeIconRef ICN_MENUBAR_TAG_2	 				= registerMultisizeIcon("/icons/tags/tag_2_on.png");
	public final static MultiSizeIconRef ICN_MENUBAR_TAG_3	 				= registerMultisizeIcon("/icons/tags/tag_3_on.png");
	public final static MultiSizeIconRef ICN_MENUBAR_TAG_4	 				= registerMultisizeIcon("/icons/tags/tag_4_on.png");
	public final static MultiSizeIconRef ICN_MENUBAR_TAG_5	 				= registerMultisizeIcon("/icons/tags/tag_5_on.png");
	public final static MultiSizeIconRef ICN_MENUBAR_TAG_6	 				= registerMultisizeIcon("/icons/tags/tag_6_on.png");
	public final static MultiSizeIconRef ICN_MENUBAR_TAG_7	 				= registerMultisizeIcon("/icons/tags/tag_7_on.png");
	public final static MultiSizeIconRef ICN_MENUBAR_TAG_8	 				= registerMultisizeIcon("/icons/tags/tag_7_on.png");
	public final static MultiSizeIconRef ICN_MENUBAR_CREATEFOLDERSTRUCTURE	= registerMultisizeIcon("/icons/toolbar/folders_explorer.png");
	public final static MultiSizeIconRef ICN_MENUBAR_SAVE					= registerMultisizeIcon("/icons/toolbar/save_as.png");
	public final static MultiSizeIconRef ICN_MENUBAR_SHOWCOVER				= registerMultisizeIcon("/icons/toolbar/images.png");
	public final static MultiSizeIconRef ICN_MENUBAR_STATISTICS				= registerMultisizeIcon("/icons/toolbar/statistics.png");
	public final static MultiSizeIconRef ICN_MENUBAR_SHUFFLE				= registerMultisizeIcon("/icons/toolbar/cards.png");
	public final static MultiSizeIconRef ICN_MENUBAR_UPDATES				= registerMultisizeIcon("/icons/toolbar/upload_for_cloud.png");
	public final static MultiSizeIconRef ICN_MENUBAR_BACKUPMANAGER			= registerMultisizeIcon("/icons/toolbar/backup_manager.png");
	public final static MultiSizeIconRef ICN_MENUBAR_WATCHDATA				= registerMultisizeIcon("/icons/toolbar/inbox_table.png");
	public final static MultiSizeIconRef ICN_MENUBAR_OPENLASTSERIES			= registerMultisizeIcon("/icons/toolbar/paper_airplane.png");
	public final static MultiSizeIconRef ICN_MENUBAR_MANAGEGROUPS			= registerMultisizeIcon("/icons/toolbar/function_recently_used_edited.png");
	public final static MultiSizeIconRef ICN_MENUBAR_WATCHHISTORY			= registerMultisizeIcon("/icons/toolbar/radiolocator.png");
	// #############################################  </MENUBAR>  ############################################
	
	
	// #############################################  <TABLE>  ###############################################
	public final static IconRef ICN_TABLE_SERIES 			= register16x16Icon("/icons/table/series.png");
	public final static IconRef ICN_TABLE_MOVIE 			= register16x16Icon("/icons/table/movie.png");
	
	public final static IconRef ICN_TABLE_VIEWED_FALSE 		= registerOtherIcon("/icons/table/viewed_0.png");
	public final static IconRef ICN_TABLE_VIEWED_TRUE  		= registerOtherIcon("/icons/table/viewed_1.png");
	public final static IconRef ICN_TABLE_VIEWED_LATER 		= registerOtherIcon("/icons/table/viewed_2.png");
	public final static IconRef ICN_TABLE_VIEWED_NEVER 		= registerOtherIcon("/icons/table/viewed_3.png");
	public final static IconRef ICN_TABLE_VIEWED_PARTIAL	= registerOtherIcon("/icons/table/viewed_4.png");
	public final static IconRef ICN_TABLE_VIEWED_AGAIN		= registerOtherIcon("/icons/table/viewed_5.png");

	public final static IconRef ICN_TABLE_VIEWED_TRUE_CTR_00		= registerCombinedIcon("/icons/table/viewed_1.png", "/icons/table/counter_00.png");
	public final static IconRef ICN_TABLE_VIEWED_TRUE_CTR_01		= registerCombinedIcon("/icons/table/viewed_1.png", "/icons/table/counter_01.png");
	public final static IconRef ICN_TABLE_VIEWED_TRUE_CTR_02		= registerCombinedIcon("/icons/table/viewed_1.png", "/icons/table/counter_02.png");
	public final static IconRef ICN_TABLE_VIEWED_TRUE_CTR_03		= registerCombinedIcon("/icons/table/viewed_1.png", "/icons/table/counter_03.png");
	public final static IconRef ICN_TABLE_VIEWED_TRUE_CTR_04		= registerCombinedIcon("/icons/table/viewed_1.png", "/icons/table/counter_04.png");
	public final static IconRef ICN_TABLE_VIEWED_TRUE_CTR_05		= registerCombinedIcon("/icons/table/viewed_1.png", "/icons/table/counter_05.png");
	public final static IconRef ICN_TABLE_VIEWED_TRUE_CTR_06		= registerCombinedIcon("/icons/table/viewed_1.png", "/icons/table/counter_06.png");
	public final static IconRef ICN_TABLE_VIEWED_TRUE_CTR_07		= registerCombinedIcon("/icons/table/viewed_1.png", "/icons/table/counter_07.png");
	public final static IconRef ICN_TABLE_VIEWED_TRUE_CTR_08		= registerCombinedIcon("/icons/table/viewed_1.png", "/icons/table/counter_08.png");
	public final static IconRef ICN_TABLE_VIEWED_TRUE_CTR_09		= registerCombinedIcon("/icons/table/viewed_1.png", "/icons/table/counter_09.png");
	public final static IconRef ICN_TABLE_VIEWED_TRUE_CTR_10		= registerCombinedIcon("/icons/table/viewed_1.png", "/icons/table/counter_10.png");
	public final static IconRef ICN_TABLE_VIEWED_TRUE_CTR_11		= registerCombinedIcon("/icons/table/viewed_1.png", "/icons/table/counter_11.png");
	public final static IconRef ICN_TABLE_VIEWED_TRUE_CTR_12		= registerCombinedIcon("/icons/table/viewed_1.png", "/icons/table/counter_12.png");
	public final static IconRef ICN_TABLE_VIEWED_TRUE_CTR_13		= registerCombinedIcon("/icons/table/viewed_1.png", "/icons/table/counter_13.png");
	public final static IconRef ICN_TABLE_VIEWED_TRUE_CTR_14		= registerCombinedIcon("/icons/table/viewed_1.png", "/icons/table/counter_14.png");
	public final static IconRef ICN_TABLE_VIEWED_TRUE_CTR_15		= registerCombinedIcon("/icons/table/viewed_1.png", "/icons/table/counter_15.png");
	public final static IconRef ICN_TABLE_VIEWED_TRUE_CTR_16		= registerCombinedIcon("/icons/table/viewed_1.png", "/icons/table/counter_16.png");
	public final static IconRef ICN_TABLE_VIEWED_TRUE_CTR_17		= registerCombinedIcon("/icons/table/viewed_1.png", "/icons/table/counter_17.png");
	public final static IconRef ICN_TABLE_VIEWED_TRUE_CTR_18		= registerCombinedIcon("/icons/table/viewed_1.png", "/icons/table/counter_18.png");
	public final static IconRef ICN_TABLE_VIEWED_TRUE_CTR_19		= registerCombinedIcon("/icons/table/viewed_1.png", "/icons/table/counter_19.png");
	public final static IconRef ICN_TABLE_VIEWED_TRUE_CTR_20		= registerCombinedIcon("/icons/table/viewed_1.png", "/icons/table/counter_20.png");
	public final static IconRef ICN_TABLE_VIEWED_TRUE_CTR_21		= registerCombinedIcon("/icons/table/viewed_1.png", "/icons/table/counter_21.png");
	public final static IconRef ICN_TABLE_VIEWED_TRUE_CTR_22		= registerCombinedIcon("/icons/table/viewed_1.png", "/icons/table/counter_22.png");
	public final static IconRef ICN_TABLE_VIEWED_TRUE_CTR_23		= registerCombinedIcon("/icons/table/viewed_1.png", "/icons/table/counter_23.png");
	public final static IconRef ICN_TABLE_VIEWED_TRUE_CTR_24		= registerCombinedIcon("/icons/table/viewed_1.png", "/icons/table/counter_24.png");
	public final static IconRef ICN_TABLE_VIEWED_TRUE_CTR_MORE		= registerCombinedIcon("/icons/table/viewed_1.png", "/icons/table/counter_dot.png");

	public final static IconRef[] ICN_TABLE_VIEWED_TRUE_CTR = new IconRef[]
	{
		ICN_TABLE_VIEWED_TRUE_CTR_00, ICN_TABLE_VIEWED_TRUE_CTR_01, ICN_TABLE_VIEWED_TRUE_CTR_02, ICN_TABLE_VIEWED_TRUE_CTR_03, ICN_TABLE_VIEWED_TRUE_CTR_04,
		ICN_TABLE_VIEWED_TRUE_CTR_05, ICN_TABLE_VIEWED_TRUE_CTR_06, ICN_TABLE_VIEWED_TRUE_CTR_07, ICN_TABLE_VIEWED_TRUE_CTR_08, ICN_TABLE_VIEWED_TRUE_CTR_09,
		ICN_TABLE_VIEWED_TRUE_CTR_10, ICN_TABLE_VIEWED_TRUE_CTR_11, ICN_TABLE_VIEWED_TRUE_CTR_12, ICN_TABLE_VIEWED_TRUE_CTR_13, ICN_TABLE_VIEWED_TRUE_CTR_14,
		ICN_TABLE_VIEWED_TRUE_CTR_15, ICN_TABLE_VIEWED_TRUE_CTR_16, ICN_TABLE_VIEWED_TRUE_CTR_17, ICN_TABLE_VIEWED_TRUE_CTR_18, ICN_TABLE_VIEWED_TRUE_CTR_19,
		ICN_TABLE_VIEWED_TRUE_CTR_20, ICN_TABLE_VIEWED_TRUE_CTR_21, ICN_TABLE_VIEWED_TRUE_CTR_22, ICN_TABLE_VIEWED_TRUE_CTR_23, ICN_TABLE_VIEWED_TRUE_CTR_24
	};

	public final static IconRef ICN_TABLE_VIEWED_AGAIN_CTR_00		= registerCombinedIcon("/icons/table/viewed_5.png", "/icons/table/counter_00.png");
	public final static IconRef ICN_TABLE_VIEWED_AGAIN_CTR_01		= registerCombinedIcon("/icons/table/viewed_5.png", "/icons/table/counter_01.png");
	public final static IconRef ICN_TABLE_VIEWED_AGAIN_CTR_02		= registerCombinedIcon("/icons/table/viewed_5.png", "/icons/table/counter_02.png");
	public final static IconRef ICN_TABLE_VIEWED_AGAIN_CTR_03		= registerCombinedIcon("/icons/table/viewed_5.png", "/icons/table/counter_03.png");
	public final static IconRef ICN_TABLE_VIEWED_AGAIN_CTR_04		= registerCombinedIcon("/icons/table/viewed_5.png", "/icons/table/counter_04.png");
	public final static IconRef ICN_TABLE_VIEWED_AGAIN_CTR_05		= registerCombinedIcon("/icons/table/viewed_5.png", "/icons/table/counter_05.png");
	public final static IconRef ICN_TABLE_VIEWED_AGAIN_CTR_06		= registerCombinedIcon("/icons/table/viewed_5.png", "/icons/table/counter_06.png");
	public final static IconRef ICN_TABLE_VIEWED_AGAIN_CTR_07		= registerCombinedIcon("/icons/table/viewed_5.png", "/icons/table/counter_07.png");
	public final static IconRef ICN_TABLE_VIEWED_AGAIN_CTR_08		= registerCombinedIcon("/icons/table/viewed_5.png", "/icons/table/counter_08.png");
	public final static IconRef ICN_TABLE_VIEWED_AGAIN_CTR_09		= registerCombinedIcon("/icons/table/viewed_5.png", "/icons/table/counter_09.png");
	public final static IconRef ICN_TABLE_VIEWED_AGAIN_CTR_10		= registerCombinedIcon("/icons/table/viewed_5.png", "/icons/table/counter_10.png");
	public final static IconRef ICN_TABLE_VIEWED_AGAIN_CTR_11		= registerCombinedIcon("/icons/table/viewed_5.png", "/icons/table/counter_11.png");
	public final static IconRef ICN_TABLE_VIEWED_AGAIN_CTR_12		= registerCombinedIcon("/icons/table/viewed_5.png", "/icons/table/counter_12.png");
	public final static IconRef ICN_TABLE_VIEWED_AGAIN_CTR_13		= registerCombinedIcon("/icons/table/viewed_5.png", "/icons/table/counter_13.png");
	public final static IconRef ICN_TABLE_VIEWED_AGAIN_CTR_14		= registerCombinedIcon("/icons/table/viewed_5.png", "/icons/table/counter_14.png");
	public final static IconRef ICN_TABLE_VIEWED_AGAIN_CTR_15		= registerCombinedIcon("/icons/table/viewed_5.png", "/icons/table/counter_15.png");
	public final static IconRef ICN_TABLE_VIEWED_AGAIN_CTR_16		= registerCombinedIcon("/icons/table/viewed_5.png", "/icons/table/counter_16.png");
	public final static IconRef ICN_TABLE_VIEWED_AGAIN_CTR_17		= registerCombinedIcon("/icons/table/viewed_5.png", "/icons/table/counter_17.png");
	public final static IconRef ICN_TABLE_VIEWED_AGAIN_CTR_18		= registerCombinedIcon("/icons/table/viewed_5.png", "/icons/table/counter_18.png");
	public final static IconRef ICN_TABLE_VIEWED_AGAIN_CTR_19		= registerCombinedIcon("/icons/table/viewed_5.png", "/icons/table/counter_19.png");
	public final static IconRef ICN_TABLE_VIEWED_AGAIN_CTR_20		= registerCombinedIcon("/icons/table/viewed_5.png", "/icons/table/counter_20.png");
	public final static IconRef ICN_TABLE_VIEWED_AGAIN_CTR_21		= registerCombinedIcon("/icons/table/viewed_5.png", "/icons/table/counter_21.png");
	public final static IconRef ICN_TABLE_VIEWED_AGAIN_CTR_22		= registerCombinedIcon("/icons/table/viewed_5.png", "/icons/table/counter_22.png");
	public final static IconRef ICN_TABLE_VIEWED_AGAIN_CTR_23		= registerCombinedIcon("/icons/table/viewed_5.png", "/icons/table/counter_23.png");
	public final static IconRef ICN_TABLE_VIEWED_AGAIN_CTR_24		= registerCombinedIcon("/icons/table/viewed_5.png", "/icons/table/counter_24.png");
	public final static IconRef ICN_TABLE_VIEWED_AGAIN_CTR_MORE		= registerCombinedIcon("/icons/table/viewed_5.png", "/icons/table/counter_dot.png");

	public final static IconRef[] ICN_TABLE_VIEWED_AGAIN_CTR = new IconRef[]
	{
		ICN_TABLE_VIEWED_AGAIN_CTR_00, ICN_TABLE_VIEWED_AGAIN_CTR_01, ICN_TABLE_VIEWED_AGAIN_CTR_02, ICN_TABLE_VIEWED_AGAIN_CTR_03, ICN_TABLE_VIEWED_AGAIN_CTR_04,
		ICN_TABLE_VIEWED_AGAIN_CTR_05, ICN_TABLE_VIEWED_AGAIN_CTR_06, ICN_TABLE_VIEWED_AGAIN_CTR_07, ICN_TABLE_VIEWED_AGAIN_CTR_08, ICN_TABLE_VIEWED_AGAIN_CTR_09,
		ICN_TABLE_VIEWED_AGAIN_CTR_10, ICN_TABLE_VIEWED_AGAIN_CTR_11, ICN_TABLE_VIEWED_AGAIN_CTR_12, ICN_TABLE_VIEWED_AGAIN_CTR_13, ICN_TABLE_VIEWED_AGAIN_CTR_14,
		ICN_TABLE_VIEWED_AGAIN_CTR_15, ICN_TABLE_VIEWED_AGAIN_CTR_16, ICN_TABLE_VIEWED_AGAIN_CTR_17, ICN_TABLE_VIEWED_AGAIN_CTR_18, ICN_TABLE_VIEWED_AGAIN_CTR_19,
		ICN_TABLE_VIEWED_AGAIN_CTR_20, ICN_TABLE_VIEWED_AGAIN_CTR_21, ICN_TABLE_VIEWED_AGAIN_CTR_22, ICN_TABLE_VIEWED_AGAIN_CTR_23, ICN_TABLE_VIEWED_AGAIN_CTR_24
	};

	public final static IconRef ICN_TABLE_QUALITY_0 		= register16x16Icon("/icons/table/quality_0.png");
	public final static IconRef ICN_TABLE_QUALITY_1 		= register16x16Icon("/icons/table/quality_1.png");
	public final static IconRef ICN_TABLE_QUALITY_2 		= register16x16Icon("/icons/table/quality_2.png");
	public final static IconRef ICN_TABLE_QUALITY_3 		= register16x16Icon("/icons/table/quality_3.png");
	public final static IconRef ICN_TABLE_QUALITY_4 		= register16x16Icon("/icons/table/quality_4.png");
	
	public final static IconRef ICN_TABLE_ONLINESCORE_0 	= register16x16Icon("/icons/table/stars_0.png");
	public final static IconRef ICN_TABLE_ONLINESCORE_1 	= register16x16Icon("/icons/table/stars_1.png");
	public final static IconRef ICN_TABLE_ONLINESCORE_2  	= register16x16Icon("/icons/table/stars_2.png");
	public final static IconRef ICN_TABLE_ONLINESCORE_3 	= register16x16Icon("/icons/table/stars_3.png");
	public final static IconRef ICN_TABLE_ONLINESCORE_4 	= register16x16Icon("/icons/table/stars_4.png");
	public final static IconRef ICN_TABLE_ONLINESCORE_5 	= register16x16Icon("/icons/table/stars_5.png");
	public final static IconRef ICN_TABLE_ONLINESCORE_6 	= register16x16Icon("/icons/table/stars_6.png");
	public final static IconRef ICN_TABLE_ONLINESCORE_7 	= register16x16Icon("/icons/table/stars_7.png");
	public final static IconRef ICN_TABLE_ONLINESCORE_8  	= register16x16Icon("/icons/table/stars_8.png");
	public final static IconRef ICN_TABLE_ONLINESCORE_9 	= register16x16Icon("/icons/table/stars_9.png");
	public final static IconRef ICN_TABLE_ONLINESCORE_10 	= register16x16Icon("/icons/table/stars_10.png");
	
	public final static IconRef ICN_TABLE_FSK_0 			= register16x16Icon("/icons/table/fsk_0.png");
	public final static IconRef ICN_TABLE_FSK_1 			= register16x16Icon("/icons/table/fsk_1.png");
	public final static IconRef ICN_TABLE_FSK_2 			= register16x16Icon("/icons/table/fsk_2.png");
	public final static IconRef ICN_TABLE_FSK_3 			= register16x16Icon("/icons/table/fsk_3.png");
	public final static IconRef ICN_TABLE_FSK_4 			= register16x16Icon("/icons/table/fsk_4.png");
	
	public final static IconRef ICN_TABLE_LANGUAGE_0 		= register16x16Icon("/icons/table/language_0.png");
	public final static IconRef ICN_TABLE_LANGUAGE_1 		= register16x16Icon("/icons/table/language_1.png");
	public final static IconRef ICN_TABLE_LANGUAGE_2 		= register16x16Icon("/icons/table/language_2.png");
	public final static IconRef ICN_TABLE_LANGUAGE_3 		= register16x16Icon("/icons/table/language_3.png");
	public final static IconRef ICN_TABLE_LANGUAGE_4 		= register16x16Icon("/icons/table/language_4.png");
	
	public final static MultiSizeIconRef ICN_TABLE_SCORE_0 		= registerMultisizeIcon("/icons/table/poo.png");
	public final static MultiSizeIconRef ICN_TABLE_SCORE_1 		= registerMultisizeIcon("/icons/table/emotion_sick.png");
	public final static MultiSizeIconRef ICN_TABLE_SCORE_2 		= registerMultisizeIcon("/icons/table/thumb_down.png");
	public final static MultiSizeIconRef ICN_TABLE_SCORE_3 		= registerMultisizeIcon("/icons/table/thumb_up.png");
	public final static MultiSizeIconRef ICN_TABLE_SCORE_4 		= registerMultisizeIcon("/icons/table/heart.png");
	public final static MultiSizeIconRef ICN_TABLE_SCORE_5 		= registerMultisizeIcon("/icons/table/award_star_gold_3.png");
	
	public final static IconRef ICN_TABLE_FORMAT_0 			= register16x16Icon("/icons/table/ext0.png");
	public final static IconRef ICN_TABLE_FORMAT_1 			= register16x16Icon("/icons/table/ext1.png");
	public final static IconRef ICN_TABLE_FORMAT_2 			= register16x16Icon("/icons/table/ext2.png");
	public final static IconRef ICN_TABLE_FORMAT_3 			= register16x16Icon("/icons/table/ext3.png");
	public final static IconRef ICN_TABLE_FORMAT_4 			= register16x16Icon("/icons/table/ext4.png");
	public final static IconRef ICN_TABLE_FORMAT_5 			= register16x16Icon("/icons/table/ext0.png");
	public final static IconRef ICN_TABLE_FORMAT_6 			= register16x16Icon("/icons/table/ext6.png");
	public final static IconRef ICN_TABLE_FORMAT_7 			= register16x16Icon("/icons/table/ext7.png");
	public final static IconRef ICN_TABLE_FORMAT_8 			= register16x16Icon("/icons/table/ext8.png");
	
	public final static MultiSizeIconRef ICN_TABLE_TAG_0_0 		= registerMultisizeIcon("/icons/tags/tag_0_off.png");
	public final static MultiSizeIconRef ICN_TABLE_TAG_0_1 		= registerMultisizeIcon("/icons/tags/tag_0_on.png");
	public final static MultiSizeIconRef ICN_TABLE_TAG_1_0 		= registerMultisizeIcon("/icons/tags/tag_1_off.png");
	public final static MultiSizeIconRef ICN_TABLE_TAG_1_1 		= registerMultisizeIcon("/icons/tags/tag_1_on.png");
	public final static MultiSizeIconRef ICN_TABLE_TAG_2_0 		= registerMultisizeIcon("/icons/tags/tag_2_off.png");
	public final static MultiSizeIconRef ICN_TABLE_TAG_2_1 		= registerMultisizeIcon("/icons/tags/tag_2_on.png");
	public final static MultiSizeIconRef ICN_TABLE_TAG_3_0 		= registerMultisizeIcon("/icons/tags/tag_3_off.png");
	public final static MultiSizeIconRef ICN_TABLE_TAG_3_1 		= registerMultisizeIcon("/icons/tags/tag_3_on.png");
	public final static MultiSizeIconRef ICN_TABLE_TAG_4_0 		= registerMultisizeIcon("/icons/tags/tag_4_off.png");
	public final static MultiSizeIconRef ICN_TABLE_TAG_4_1 		= registerMultisizeIcon("/icons/tags/tag_4_on.png");
	public final static MultiSizeIconRef ICN_TABLE_TAG_5_0 		= registerMultisizeIcon("/icons/tags/tag_5_off.png");
	public final static MultiSizeIconRef ICN_TABLE_TAG_5_1 		= registerMultisizeIcon("/icons/tags/tag_5_on.png");
	public final static MultiSizeIconRef ICN_TABLE_TAG_6_0 		= registerMultisizeIcon("/icons/tags/tag_6_off.png");
	public final static MultiSizeIconRef ICN_TABLE_TAG_6_1 		= registerMultisizeIcon("/icons/tags/tag_6_on.png");
	public final static MultiSizeIconRef ICN_TABLE_TAG_7_0 		= registerMultisizeIcon("/icons/tags/tag_7_off.png");
	public final static MultiSizeIconRef ICN_TABLE_TAG_7_1 		= registerMultisizeIcon("/icons/tags/tag_7_on.png");
	public final static MultiSizeIconRef ICN_TABLE_TAG_8_0 		= registerMultisizeIcon("/icons/tags/tag_8_off.png");
	public final static MultiSizeIconRef ICN_TABLE_TAG_8_1 		= registerMultisizeIcon("/icons/tags/tag_8_on.png");

	// #############################################  </TABLE>  ##############################################

	
	// ############################################  <HISTORY>  ##############################################
	public final static IconRef ICN_HISTORY_CHRONIK 		= register16x16Icon("/icons/toolbar/radiolocator.png");
	public final static IconRef ICN_HISTORY_ELEMENT 		= register16x16Icon("/icons/history/document_index.png");
	public final static IconRef ICN_HISTORY_MOVIES 			= register16x16Icon("/icons/table/movie.png");	
	public final static IconRef ICN_HISTORY_SERIES 			= register16x16Icon("/icons/table/series.png");
	// ############################################  </HISTORY>  #############################################
	
	
	// ############################################  <FRAMES>  ##############################################
	public final static MultiSizeIconRef ICN_FRAMES_SEARCH 		= registerMultisizeIcon("/icons/previewSeriesFrame/magnifier.png");
	public final static IconRef ICN_FRAMES_ABOUT 				= registerOtherIcon("/UberDialog.png");
	public final static MultiSizeIconRef ICN_FRAMES_DELETE 		= registerMultisizeIcon("/icons/common/delete.png");
	// ############################################  </FRAMES>  #############################################

	
	// ##############################################  <LOG>  ###############################################
	public final static MultiSizeIconRef ICN_LOG_UNDEFINIED		= registerMultisizeIcon("/icons/log/question.png");
	public final static MultiSizeIconRef ICN_LOG_ERROR 			= registerMultisizeIcon("/icons/log/cross_shield.png");
	public final static MultiSizeIconRef ICN_LOG_WARNING	 	= registerMultisizeIcon("/icons/log/caution_high_voltage.png");
	public final static MultiSizeIconRef ICN_LOG_OK	 			= registerMultisizeIcon("/icons/log/tick.png");
	// ##############################################  </LOG>  ##############################################
	
	
	// #############################################  <SIDEBAR>  ############################################
	public final static IconRef ICN_SIDEBAR_ALL	 			= register16x16Icon("/icons/sidebar/jar_empty.png");
	public final static IconRef ICN_SIDEBAR_ZYKLUS 			= register16x16Icon("/icons/sidebar/asterisk_orange.png");
	public final static IconRef ICN_SIDEBAR_GROUPS 			= register16x16Icon("/icons/sidebar/function_recently_used.png");
	public final static IconRef ICN_SIDEBAR_GENRE 			= register16x16Icon("/icons/sidebar/book_spelling.png");
	public final static IconRef ICN_SIDEBAR_ONLINESCORE		= register16x16Icon("/icons/sidebar/star.png");
	public final static MultiSizeIconRef ICN_SIDEBAR_SCORE 	= registerMultisizeIcon("/icons/sidebar/to_do_list.png");
	public final static IconRef ICN_SIDEBAR_YEAR	 		= register16x16Icon("/icons/sidebar/calendar.png");
	public final static IconRef ICN_SIDEBAR_FORMAT 			= register16x16Icon("/icons/sidebar/file_extension_txt.png");
	public final static IconRef ICN_SIDEBAR_QUALITY 		= register16x16Icon("/icons/sidebar/server_components.png");
	public final static IconRef ICN_SIDEBAR_TAGS 			= register16x16Icon("/icons/sidebar/tags_cloud.png");
	public final static IconRef ICN_SIDEBAR_LANGUAGE		= register16x16Icon("/icons/sidebar/flag_red.png");
	public final static IconRef ICN_SIDEBAR_TYP	 			= register16x16Icon("/icons/sidebar/color_swatch.png");
	public final static IconRef ICN_SIDEBAR_VIEWED 			= register16x16Icon("/icons/sidebar/viewed.png");
	public final static IconRef ICN_SIDEBAR_UNVIEWED		= register16x16Icon("/icons/sidebar/unviewed.png");
	public final static IconRef ICN_SIDEBAR_LATER			= register16x16Icon("/icons/sidebar/later.png");
	public final static IconRef ICN_SIDEBAR_NEVER			= register16x16Icon("/icons/sidebar/never.png");
	public final static IconRef ICN_SIDEBAR_PARTIALLY		= register16x16Icon("/icons/sidebar/partially.png");
	public final static IconRef ICN_SIDEBAR_AGAIN			= register16x16Icon("/icons/sidebar/again.png");
	public final static IconRef ICN_SIDEBAR_CUSTOM			= register16x16Icon("/icons/sidebar/tag.png");
	// #############################################  </SIDEBAR>  ###########################################

	
	// ##############################################  <LOG>  ################################################
	public final static IconRef ICN_FILTER_METHOD			= register16x16Icon("/icons/filter/tag.png");
	public final static IconRef ICN_FILTER_OPERATOR 		= register16x16Icon("/icons/filter/box_front_open.png");
	public final static IconRef ICN_FILTER_AGGREGATOR 		= register16x16Icon("/icons/filter/hbox.png");
	public final static IconRef ICN_FILTER_COUNTER	 		= register16x16Icon("/icons/filter/counter.png");
	// #############################################  </SIDEBAR>  ###########################################

	
	// ##############################################  <LOG>  ################################################
	public final static MultiSizeIconRef ICN_REF_00				= registerMultisizeIcon("/icons/onlineReferences/ref00.png");
	public final static IconRef ICN_REF_00_BUTTON			= registerOtherIcon("/icons/onlineReferences/ref00_large.png");
	
	public final static MultiSizeIconRef ICN_REF_01				= registerMultisizeIcon("/icons/onlineReferences/ref01.png");
	public final static IconRef ICN_REF_01_BUTTON			= registerOtherIcon("/icons/onlineReferences/ref01_large.png");
	
	public final static MultiSizeIconRef ICN_REF_02				= registerMultisizeIcon("/icons/onlineReferences/ref02.png");
	public final static IconRef ICN_REF_02_BUTTON			= registerOtherIcon("/icons/onlineReferences/ref02_large.png");
	
	public final static MultiSizeIconRef ICN_REF_03				= registerMultisizeIcon("/icons/onlineReferences/ref03.png");
	public final static IconRef ICN_REF_03_BUTTON			= registerOtherIcon("/icons/onlineReferences/ref03_large.png");
	
	public final static MultiSizeIconRef ICN_REF_04				= registerMultisizeIcon("/icons/onlineReferences/ref04.png");
	public final static IconRef ICN_REF_04_BUTTON			= registerOtherIcon("/icons/onlineReferences/ref04_large.png");
	
	public final static MultiSizeIconRef ICN_REF_05				= registerMultisizeIcon("/icons/onlineReferences/ref05.png");
	public final static IconRef ICN_REF_05_BUTTON			= registerOtherIcon("/icons/onlineReferences/ref05_large.png");
	
	public final static MultiSizeIconRef ICN_REF_06				= registerMultisizeIcon("/icons/onlineReferences/ref06.png");
	public final static IconRef ICN_REF_06_BUTTON			= registerOtherIcon("/icons/onlineReferences/ref06_large.png");
	
	public final static MultiSizeIconRef ICN_REF_07				= registerMultisizeIcon("/icons/onlineReferences/ref07.png");
	public final static IconRef ICN_REF_07_BUTTON			= registerOtherIcon("/icons/onlineReferences/ref07_large.png");
	
	public final static MultiSizeIconRef ICN_REF_08				= registerMultisizeIcon("/icons/onlineReferences/ref08.png");
	public final static IconRef ICN_REF_08_BUTTON			= registerOtherIcon("/icons/onlineReferences/ref08_large.png");

	public final static MultiSizeIconRef ICN_REF_09				= registerMultisizeIcon("/icons/onlineReferences/ref09.png");
	public final static IconRef ICN_REF_09_BUTTON			= registerOtherIcon("/icons/onlineReferences/ref09_large.png");

	public final static MultiSizeIconRef ICN_REF_10				= registerMultisizeIcon("/icons/onlineReferences/ref10.png");
	public final static IconRef ICN_REF_10_BUTTON			= registerOtherIcon("/icons/onlineReferences/ref10_large.png");
	// ###############################################  </LOG>  ##############################################
	
	
	// #######################################################################################################
	// #######################################################################################################
	// #######################################################################################################
	
	public static void init() {
		// actual register code happens in static initialization
		
		CCLog.addDebug(String.format("%d resources registered (%d icons and %d images)",
				resources.size(),
				CCStreams.iterate(resources).count(r -> r.getKey().Item2.isIcon()),
				CCStreams.iterate(resources).count(r -> r.getKey().Item2.isImage())
		)); //$NON-NLS-1$
	}

	@SuppressWarnings("unused")
	private static void dumpInfo() {
		StringBuilder stroutbuilder = new StringBuilder();
		for (ResourceRef r : CCStreams.iterate(resources.values()).autosortByProperty(p -> p.ident))
		{
			stroutbuilder
					.append(r.ident)
					.append('\t')
					.append(r.id)
					.append('\t')
					.append(r.type)
					.append('\t')
					.append(CCStreams.iterate(r.getDirectDependencies()).stringjoin(d -> d.id.toString(), ";"))
					.append("\r\n");
		}
		System.out.println(stroutbuilder.toString());
	}

	private static MultiSizeIconRef registerMultisizeIcon(String s) {
		SingleIconRef i16 = register16x16Icon(s);
		SingleIconRef i32 = register32x32Icon(s);
		
		return new MultiSizeIconRef(i16, i32);
	}
	
	private static SingleIconRef register16x16Icon(String s) {
		s = PathFormatter.getWithoutExtension(s) + "_16x16." + PathFormatter.getExtension(s);
		
		return registerIcon(s, ResourceRefType.ICON_16);
	}
	
	private static SingleIconRef register32x32Icon(String s) {
		return registerIcon(s, ResourceRefType.ICON_32);
	}
	
	private static SingleIconRef registerOtherIcon(String s) {
		return registerIcon(s, ResourceRefType.ICON_OTHER);
	}

	private static CombinedImageRef registerCombinedImage(String[] s) {
		return register(new CombinedImageRef(CCStreams.iterate(s).map(Resources::registerImage).toArray(new SingleImageRef[0])));
	}

	private static IconRef registerCombinedIcon(String... s) {
		return register(new CombinedIconRef(registerCombinedImage(s)));
	}
	
	private static SingleIconRef registerIcon(String s, ResourceRefType t) {
		return register(new SingleIconRef(register(new SingleImageRef(s)), t));
	}
	
	private static SingleImageRef registerImage(String s) {
		return register(new SingleImageRef(s));
	}

	@SuppressWarnings("unchecked")
	private static <T extends ResourceRef> T register(T ref) {
		Tuple<String, ResourceRefType> key = Tuple.Create(ref.ident, ref.type);

		ResourceRef data = resources.getOrDefault(key, null);
		if (data != null) return (T)data;

		resources.put(key, ref);
		return ref;
	}

	public static void preload() {
		for (ResourceRef s : resources.values()) {
			s.preload();
		}
	}

	public static void preload_async() {
		new Thread(() ->
		{
			ThreadUtils.safeSleep(Globals.ASYNC_TIME_OFFSET_RESOURCES);

			Globals.TIMINGS.start(Globals.TIMING_BACKGROUND_PRELOADRESOURCES);
			for (ResourceRef s : resources.values()) {
				try {
					SwingUtilities.invokeAndWait(s::preload);
				} catch (InterruptedException | InvocationTargetException e) {
					CCLog.addError(e);
				}
			}

			Globals.TIMINGS.stop(Globals.TIMING_BACKGROUND_PRELOADRESOURCES);
		}, "RESOURCE_PRELOAD").start();
	}
}
