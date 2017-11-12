package de.jClipCorn.gui.resources;

import java.util.HashMap;
import java.util.Map.Entry;

import de.jClipCorn.util.Tuple;
import de.jClipCorn.util.formatter.PathFormatter;

@SuppressWarnings("nls")
public class Resources {
	private static HashMap<Tuple<String, CachedIconType>, IconRef> icn_ressources = new HashMap<>();
	private static HashMap<String, ImageRef> img_ressources = new HashMap<>();
	
	// ##############################################  <COMMON>  #############################################
	public final static ImageRef IMG_FRAME_ICON 						= registerImage("/app.png");
	public final static ImageRef IMG_COVER_STANDARD 					= registerImage("/standard.png");
	public final static ImageRef IMG_COVER_STANDARD_SMALL				= registerImage("/standard_small.png");
	public final static ImageRef IMG_COVER_NOTFOUND 					= registerImage("/covernotfound.png");
	public final static ImageRef IMG_COVER_SERIES_MASK					= registerImage("/mask_series.png");
	public final static ImageRef IMG_COVER_ERROR_ON						= registerImage("/cover_errror_on.png");
	public final static ImageRef IMG_COVER_ERROR_OFF					= registerImage("/cover_errror_off.png");
	// #############################################  </COMMON>  #############################################
	
	
	// #############################################  <MENUBAR>  #############################################
	public final static MultiIconRef ICN_MENUBAR_CLOSE		 			= registerMultisizeIcon("/icons/toolbar/door_open.png");
	public final static MultiIconRef ICN_MENUBAR_PARSEXML 				= registerMultisizeIcon("/icons/toolbar/blueprint.png");
	public final static MultiIconRef ICN_MENUBAR_PLAY					= registerMultisizeIcon("/icons/toolbar/control_play.png");
	public final static MultiIconRef ICN_MENUBAR_HIDDENPLAY				= registerMultisizeIcon("/icons/toolbar/control_hiddenplay.png");
	public final static MultiIconRef ICN_MENUBAR_ADD_MOV				= registerMultisizeIcon("/icons/toolbar/add.png");
	public final static MultiIconRef ICN_MENUBAR_ADD_SER				= registerMultisizeIcon("/icons/toolbar/addSer.png");
	public final static MultiIconRef ICN_MENUBAR_ADD_SEA				= registerMultisizeIcon("/icons/toolbar/addSea.png");
	public final static MultiIconRef ICN_MENUBAR_DBCHECK				= registerMultisizeIcon("/icons/toolbar/application_osx_terminal.png");
	public final static MultiIconRef ICN_MENUBAR_CLEARDB				= registerMultisizeIcon("/icons/toolbar/fire_damage.png");
	public final static MultiIconRef ICN_MENUBAR_SETTINGS				= registerMultisizeIcon("/icons/toolbar/gear_in.png");
	public final static MultiIconRef ICN_MENUBAR_PREVIEW_MOV			= registerMultisizeIcon("/icons/toolbar/magnifier.png");
	public final static MultiIconRef ICN_MENUBAR_PREVIEW_SER			= registerMultisizeIcon("/icons/toolbar/magnifier_ser.png");
	public final static MultiIconRef ICN_MENUBAR_REMOVE					= registerMultisizeIcon("/icons/toolbar/bin.png");
	public final static MultiIconRef ICN_MENUBAR_EDIT_MOV				= registerMultisizeIcon("/icons/toolbar/screwdriver_mov.png");
	public final static MultiIconRef ICN_MENUBAR_EDIT_SER				= registerMultisizeIcon("/icons/toolbar/screwdriver_ser.png");
	public final static MultiIconRef ICN_MENUBAR_LOG					= registerMultisizeIcon("/icons/toolbar/system_monitor.png");
	public final static MultiIconRef ICN_MENUBAR_FOLDER					= registerMultisizeIcon("/icons/toolbar/folder.png");
	public final static MultiIconRef ICN_MENUBAR_VIEWED					= registerMultisizeIcon("/icons/toolbar/viewed.png");
	public final static MultiIconRef ICN_MENUBAR_UNVIEWED				= registerMultisizeIcon("/icons/toolbar/unviewed.png");
	public final static MultiIconRef ICN_MENUBAR_UNDOVIEWED				= registerMultisizeIcon("/icons/toolbar/undoviewed.png");
	public final static MultiIconRef ICN_MENUBAR_SCANFOLDER				= registerMultisizeIcon("/icons/toolbar/scanner.png");
	public final static MultiIconRef ICN_MENUBAR_ABOUT					= registerMultisizeIcon("/icons/toolbar/info_rhombus.png");
	public final static MultiIconRef ICN_MENUBAR_MCHANGE_VIEWED			= registerMultisizeIcon("/icons/toolbar/knot.png");
	public final static MultiIconRef ICN_MENUBAR_MCHANGE_SCORE			= registerMultisizeIcon("/icons/toolbar/knotscore.png");
	public final static MultiIconRef ICN_MENUBAR_RESETVIEWED			= registerMultisizeIcon("/icons/toolbar/scroller_bar.png");
	public final static MultiIconRef ICN_MENUBAR_REGENERATEDUUID		= registerMultisizeIcon("/icons/toolbar/omelet.png");
	public final static MultiIconRef ICN_MENUBAR_AUTOFINDREF			= registerMultisizeIcon("/icons/toolbar/cash_register.png");
	public final static MultiIconRef ICN_MENUBAR_MOVESERIES				= registerMultisizeIcon("/icons/toolbar/move_to_folder.png");
	public final static MultiIconRef ICN_MENUBAR_MOVEALLSERIES			= registerMultisizeIcon("/icons/toolbar/move_to_folder2.png");
	public final static MultiIconRef ICN_MENUBAR_MOVEALLMOVIES			= registerMultisizeIcon("/icons/toolbar/move_to_folder3.png");
	public final static MultiIconRef ICN_MENUBAR_FINDINCOMPLETEZYKLUS	= registerMultisizeIcon("/icons/toolbar/blackboard_steps.png");
	public final static MultiIconRef ICN_MENUBAR_UPDATEMETADATA			= registerMultisizeIcon("/icons/toolbar/update_contact_info.png");
	public final static MultiIconRef ICN_MENUBAR_FILENAMERULES			= registerMultisizeIcon("/icons/toolbar/books.png");
	public final static MultiIconRef ICN_MENUBAR_CREATE_JXMLBKP			= registerMultisizeIcon("/icons/toolbar/source_code.png");
	public final static MultiIconRef ICN_MENUBAR_COMPARE				= registerMultisizeIcon("/icons/toolbar/balance_unbalance.png");
	public final static MultiIconRef ICN_MENUBAR_SEARCH					= registerMultisizeIcon("/icons/toolbar/bubblechart.png");
	public final static MultiIconRef ICN_MENUBAR_ONLINEREFERENCE		= registerMultisizeIcon("/icons/toolbar/www_page.png");
	public final static MultiIconRef ICN_MENUBAR_EXPORTPLAINDB			= registerMultisizeIcon("/icons/toolbar/external.png");
	public final static MultiIconRef ICN_MENUBAR_OPENFILE				= registerMultisizeIcon("/icons/toolbar/open_folder.png");
	public final static MultiIconRef ICN_MENUBAR_RESTARTAPP				= registerMultisizeIcon("/icons/toolbar/recycle_bag.png");
	public final static MultiIconRef ICN_MENUBAR_IMPORTDB				= registerMultisizeIcon("/icons/toolbar/source_code_bullet_down.png");
	public final static MultiIconRef ICN_MENUBAR_IMPORMULTIPLEELEMENTS	= registerMultisizeIcon("/icons/toolbar/file_manager_bullet_add.png");
	public final static MultiIconRef ICN_MENUBAR_IMPORTMOVIE			= registerMultisizeIcon("/icons/toolbar/script_bullet_add_mov.png");
	public final static MultiIconRef ICN_MENUBAR_EXPORTMOVIE			= registerMultisizeIcon("/icons/toolbar/script_bullet_save_mov.png");
	public final static MultiIconRef ICN_MENUBAR_EXPORTELEMENTS			= registerMultisizeIcon("/icons/toolbar/file_manager_bullet_save.png");
	public final static MultiIconRef ICN_MENUBAR_IMPORTSERIES			= registerMultisizeIcon("/icons/toolbar/script_bullet_add_ser.png");
	public final static MultiIconRef ICN_MENUBAR_EXPORTSERIES			= registerMultisizeIcon("/icons/toolbar/script_bullet_save_ser.png");
	public final static MultiIconRef ICN_MENUBAR_EPISODEGUIDE			= registerMultisizeIcon("/icons/toolbar/document_quote.png");
	public final static MultiIconRef ICN_MENUBAR_RANDOMMOVIE			= registerMultisizeIcon("/icons/toolbar/magic_wand_2.png");
	public final static MultiIconRef ICN_MENUBAR_TAGS					= registerMultisizeIcon("/icons/toolbar/tags_cloud.png");
	public final static MultiIconRef ICN_MENUBAR_TAG_0_0 				= registerMultisizeIcon("/icons/tags/tag_0_off.png");
	public final static MultiIconRef ICN_MENUBAR_TAG_0_1 				= registerMultisizeIcon("/icons/tags/tag_0_on.png");
	public final static MultiIconRef ICN_MENUBAR_TAG_1_0 				= registerMultisizeIcon("/icons/tags/tag_1_off.png");
	public final static MultiIconRef ICN_MENUBAR_TAG_1_1 				= registerMultisizeIcon("/icons/tags/tag_1_on.png");
	public final static MultiIconRef ICN_MENUBAR_TAG_2_0 				= registerMultisizeIcon("/icons/tags/tag_2_off.png");
	public final static MultiIconRef ICN_MENUBAR_TAG_2_1 				= registerMultisizeIcon("/icons/tags/tag_2_on.png");
	public final static MultiIconRef ICN_MENUBAR_TAG_3_0 				= registerMultisizeIcon("/icons/tags/tag_3_off.png");
	public final static MultiIconRef ICN_MENUBAR_TAG_3_1 				= registerMultisizeIcon("/icons/tags/tag_3_on.png");
	public final static MultiIconRef ICN_MENUBAR_TAG_4_0 				= registerMultisizeIcon("/icons/tags/tag_4_off.png");
	public final static MultiIconRef ICN_MENUBAR_TAG_4_1 				= registerMultisizeIcon("/icons/tags/tag_4_on.png");
	public final static MultiIconRef ICN_MENUBAR_TAG_5_0 				= registerMultisizeIcon("/icons/tags/tag_5_off.png");
	public final static MultiIconRef ICN_MENUBAR_TAG_5_1 				= registerMultisizeIcon("/icons/tags/tag_5_on.png");
	public final static MultiIconRef ICN_MENUBAR_CREATEFOLDERSTRUCTURE	= registerMultisizeIcon("/icons/toolbar/folders_explorer.png");
	public final static MultiIconRef ICN_MENUBAR_SAVE					= registerMultisizeIcon("/icons/toolbar/save_as.png");
	public final static MultiIconRef ICN_MENUBAR_STATISTICS				= registerMultisizeIcon("/icons/toolbar/statistics.png");
	public final static MultiIconRef ICN_MENUBAR_SHUFFLE				= registerMultisizeIcon("/icons/toolbar/cards.png");
	public final static MultiIconRef ICN_MENUBAR_UPDATES				= registerMultisizeIcon("/icons/toolbar/upload_for_cloud.png");
	public final static MultiIconRef ICN_MENUBAR_BACKUPMANAGER			= registerMultisizeIcon("/icons/toolbar/backup_manager.png");
	public final static MultiIconRef ICN_MENUBAR_WATCHDATA				= registerMultisizeIcon("/icons/toolbar/inbox_table.png");
	public final static MultiIconRef ICN_MENUBAR_OPENLASTSERIES			= registerMultisizeIcon("/icons/toolbar/paper_airplane.png");
	public final static MultiIconRef ICN_MENUBAR_GROUPS					= registerMultisizeIcon("/icons/toolbar/function_recently_used.png");
	public final static MultiIconRef ICN_MENUBAR_MANAGEGROUPS			= registerMultisizeIcon("/icons/toolbar/function_recently_used_edited.png");
	public final static MultiIconRef ICN_MENUBAR_WATCHHISTORY			= registerMultisizeIcon("/icons/toolbar/radiolocator.png");
	// #############################################  </MENUBAR>  ############################################
	
	
	// #############################################  <TABLE>  ###############################################
	public final static IconRef ICN_TABLE_SERIES 			= register16x16Icon("/icons/table/series.png");
	public final static IconRef ICN_TABLE_MOVIE 			= register16x16Icon("/icons/table/movie.png");
	
	public final static IconRef ICN_TABLE_VIEWED_FALSE 		= registerOtherIcon("/icons/table/viewed_0.png");
	public final static IconRef ICN_TABLE_VIEWED_TRUE  		= registerOtherIcon("/icons/table/viewed_1.png");
	public final static IconRef ICN_TABLE_VIEWED_LATER 		= registerOtherIcon("/icons/table/viewed_2.png");
	public final static IconRef ICN_TABLE_VIEWED_NEVER 		= registerOtherIcon("/icons/table/viewed_3.png");
	public final static IconRef ICN_TABLE_VIEWED_PARTIAL	= registerOtherIcon("/icons/table/viewed_4.png");
	
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
	
	public final static MultiIconRef ICN_TABLE_SCORE_0 		= registerMultisizeIcon("/icons/table/poo.png");
	public final static MultiIconRef ICN_TABLE_SCORE_1 		= registerMultisizeIcon("/icons/table/emotion_sick.png");
	public final static MultiIconRef ICN_TABLE_SCORE_2 		= registerMultisizeIcon("/icons/table/thumb_down.png");
	public final static MultiIconRef ICN_TABLE_SCORE_3 		= registerMultisizeIcon("/icons/table/thumb_up.png");
	public final static MultiIconRef ICN_TABLE_SCORE_4 		= registerMultisizeIcon("/icons/table/heart.png");
	public final static MultiIconRef ICN_TABLE_SCORE_5 		= registerMultisizeIcon("/icons/table/award_star_gold_3.png");
	
	public final static IconRef ICN_TABLE_FORMAT_0 			= register16x16Icon("/icons/table/ext0.png");
	public final static IconRef ICN_TABLE_FORMAT_1 			= register16x16Icon("/icons/table/ext1.png");
	public final static IconRef ICN_TABLE_FORMAT_2 			= register16x16Icon("/icons/table/ext2.png");
	public final static IconRef ICN_TABLE_FORMAT_3 			= register16x16Icon("/icons/table/ext3.png");
	public final static IconRef ICN_TABLE_FORMAT_4 			= register16x16Icon("/icons/table/ext4.png");
	public final static IconRef ICN_TABLE_FORMAT_5 			= register16x16Icon("/icons/table/ext0.png");
	public final static IconRef ICN_TABLE_FORMAT_6 			= register16x16Icon("/icons/table/ext6.png");
	public final static IconRef ICN_TABLE_FORMAT_7 			= register16x16Icon("/icons/table/ext7.png");
	public final static IconRef ICN_TABLE_FORMAT_8 			= register16x16Icon("/icons/table/ext8.png");
	
	public final static MultiIconRef ICN_TABLE_TAG_0_0 		= registerMultisizeIcon("/icons/tags/tag_0_off.png");
	public final static MultiIconRef ICN_TABLE_TAG_0_1 		= registerMultisizeIcon("/icons/tags/tag_0_on.png");
	public final static MultiIconRef ICN_TABLE_TAG_1_0 		= registerMultisizeIcon("/icons/tags/tag_1_off.png");
	public final static MultiIconRef ICN_TABLE_TAG_1_1 		= registerMultisizeIcon("/icons/tags/tag_1_on.png");
	public final static MultiIconRef ICN_TABLE_TAG_2_0 		= registerMultisizeIcon("/icons/tags/tag_2_off.png");
	public final static MultiIconRef ICN_TABLE_TAG_2_1 		= registerMultisizeIcon("/icons/tags/tag_2_on.png");
	public final static MultiIconRef ICN_TABLE_TAG_3_0 		= registerMultisizeIcon("/icons/tags/tag_3_off.png");
	public final static MultiIconRef ICN_TABLE_TAG_3_1 		= registerMultisizeIcon("/icons/tags/tag_3_on.png");
	public final static MultiIconRef ICN_TABLE_TAG_4_0 		= registerMultisizeIcon("/icons/tags/tag_4_off.png");
	public final static MultiIconRef ICN_TABLE_TAG_4_1 		= registerMultisizeIcon("/icons/tags/tag_4_on.png");
	public final static MultiIconRef ICN_TABLE_TAG_5_0 		= registerMultisizeIcon("/icons/tags/tag_5_off.png");
	public final static MultiIconRef ICN_TABLE_TAG_5_1 		= registerMultisizeIcon("/icons/tags/tag_5_on.png");

	public final static MultiIconRef[] ICN_TABLE_TAG_X_0 	= new MultiIconRef[] {ICN_TABLE_TAG_0_0, ICN_TABLE_TAG_1_0, ICN_TABLE_TAG_2_0, ICN_TABLE_TAG_3_0, ICN_TABLE_TAG_4_0, ICN_TABLE_TAG_5_0};
	public final static MultiIconRef[] ICN_TABLE_TAG_X_1 	= new MultiIconRef[] {ICN_TABLE_TAG_0_1, ICN_TABLE_TAG_1_1, ICN_TABLE_TAG_2_1, ICN_TABLE_TAG_3_1, ICN_TABLE_TAG_4_1, ICN_TABLE_TAG_5_1};
	// #############################################  </TABLE>  ##############################################

	
	// ############################################  <HISTORY>  ##############################################
	public final static IconRef ICN_HISTORY_CHRONIK 		= register16x16Icon("/icons/toolbar/radiolocator.png");
	public final static IconRef ICN_HISTORY_ELEMENT 		= register16x16Icon("/icons/history/document_index.png");
	public final static IconRef ICN_HISTORY_MOVIES 			= register16x16Icon("/icons/table/movie.png");	
	public final static IconRef ICN_HISTORY_SERIES 			= register16x16Icon("/icons/table/series.png");
	// ############################################  </HISTORY>  #############################################
	
	
	// ############################################  <FRAMES>  ##############################################
	public final static MultiIconRef ICN_FRAMES_SEARCH 		= registerMultisizeIcon("/icons/previewSeriesFrame/magnifier.png");
	public final static ImageRef IMG_FRAMES_ABOUT 			= registerImage("/UberDialog.png");	
	public final static MultiIconRef ICN_FRAMES_DELETE 		= registerMultisizeIcon("/icons/common/delete.png");
	// ############################################  </FRAMES>  #############################################

	
	// ##############################################  <LOG>  ###############################################
	public final static MultiIconRef ICN_LOG_UNDEFINIED		= registerMultisizeIcon("/icons/log/question.png");
	public final static MultiIconRef ICN_LOG_ERROR 			= registerMultisizeIcon("/icons/log/cross_shield.png");
	public final static MultiIconRef ICN_LOG_WARNING	 	= registerMultisizeIcon("/icons/log/caution_high_voltage.png");
	public final static MultiIconRef ICN_LOG_OK	 			= registerMultisizeIcon("/icons/log/tick.png");
	// ##############################################  </LOG>  ##############################################
	
	
	// #############################################  <SIDEBAR>  ############################################
	public final static IconRef ICN_SIDEBAR_ALL	 				= register16x16Icon("/icons/sidebar/jar_empty.png");
	public final static IconRef ICN_SIDEBAR_ZYKLUS 				= register16x16Icon("/icons/sidebar/asterisk_orange.png");
	public final static IconRef ICN_SIDEBAR_GROUPS 				= register16x16Icon("/icons/sidebar/function_recently_used.png");
	public final static IconRef ICN_SIDEBAR_GENRE 				= register16x16Icon("/icons/sidebar/book_spelling.png");
	public final static IconRef ICN_SIDEBAR_ONLINESCORE			= register16x16Icon("/icons/sidebar/star.png");
	public final static MultiIconRef ICN_SIDEBAR_SCORE 			= registerMultisizeIcon("/icons/sidebar/to_do_list.png");
	public final static IconRef ICN_SIDEBAR_YEAR	 			= register16x16Icon("/icons/sidebar/calendar.png");
	public final static IconRef ICN_SIDEBAR_FORMAT 				= register16x16Icon("/icons/sidebar/file_extension_txt.png");
	public final static IconRef ICN_SIDEBAR_QUALITY 			= register16x16Icon("/icons/sidebar/server_components.png");
	public final static IconRef ICN_SIDEBAR_TAGS 				= register16x16Icon("/icons/sidebar/tags_cloud.png");
	public final static IconRef ICN_SIDEBAR_LANGUAGE			= register16x16Icon("/icons/sidebar/flag_red.png");
	public final static IconRef ICN_SIDEBAR_TYP	 				= register16x16Icon("/icons/sidebar/color_swatch.png");
	public final static IconRef ICN_SIDEBAR_VIEWED 				= register16x16Icon("/icons/sidebar/viewed.png");
	public final static IconRef ICN_SIDEBAR_UNVIEWED			= register16x16Icon("/icons/sidebar/unviewed.png");
	public final static IconRef ICN_SIDEBAR_LATER				= register16x16Icon("/icons/sidebar/later.png");
	public final static IconRef ICN_SIDEBAR_NEVER				= register16x16Icon("/icons/sidebar/never.png");
	public final static IconRef ICN_SIDEBAR_PARTIALLY			= register16x16Icon("/icons/sidebar/partially.png");
	public final static IconRef ICN_SIDEBAR_CUSTOM				= register16x16Icon("/icons/sidebar/tag.png");
	public final static IconRef ICN_SIDEBAR_CUSTOM_OPERATOR 	= register16x16Icon("/icons/sidebar/box_front_open.png");
	public final static IconRef ICN_SIDEBAR_CUSTOM_AGGREGATOR 	= register16x16Icon("/icons/sidebar/hbox.png");
	// #############################################  </SIDEBAR>  ###########################################

	
	// ##############################################  <LOG>  ################################################
	public final static MultiIconRef ICN_REF_0				= registerMultisizeIcon("/icons/onlineReferences/ref0.png");
	public final static IconRef ICN_REF_0_BUTTON			= registerOtherIcon("/icons/onlineReferences/ref0_large.png");
	
	public final static MultiIconRef ICN_REF_1				= registerMultisizeIcon("/icons/onlineReferences/ref1.png");
	public final static IconRef ICN_REF_1_BUTTON			= registerOtherIcon("/icons/onlineReferences/ref1_large.png");
	
	public final static MultiIconRef ICN_REF_2				= registerMultisizeIcon("/icons/onlineReferences/ref2.png");
	public final static IconRef ICN_REF_2_BUTTON			= registerOtherIcon("/icons/onlineReferences/ref2_large.png");
	
	public final static MultiIconRef ICN_REF_3				= registerMultisizeIcon("/icons/onlineReferences/ref3.png");
	public final static IconRef ICN_REF_3_BUTTON			= registerOtherIcon("/icons/onlineReferences/ref3_large.png");
	
	public final static MultiIconRef ICN_REF_4				= registerMultisizeIcon("/icons/onlineReferences/ref4.png");
	public final static IconRef ICN_REF_4_BUTTON			= registerOtherIcon("/icons/onlineReferences/ref4_large.png");
	
	public final static MultiIconRef ICN_REF_5				= registerMultisizeIcon("/icons/onlineReferences/ref5.png");
	public final static IconRef ICN_REF_5_BUTTON			= registerOtherIcon("/icons/onlineReferences/ref5_large.png");
	
	public final static MultiIconRef ICN_REF_6				= registerMultisizeIcon("/icons/onlineReferences/ref6.png");
	public final static IconRef ICN_REF_6_BUTTON			= registerOtherIcon("/icons/onlineReferences/ref6_large.png");
	// ###############################################  </LOG>  ##############################################
	
	
	// #######################################################################################################
	// #######################################################################################################
	// #######################################################################################################
	
	
	private static MultiIconRef registerMultisizeIcon(String s) {
		IconRef i16 = register16x16Icon(s);
		IconRef i32 = register32x32Icon(s);
		
		return new MultiIconRef(i16, i32);
	}
	
	private static IconRef register16x16Icon(String s) {
		s = PathFormatter.getWithoutExtension(s) + "_16x16." + PathFormatter.getExtension(s);
		
		return register(s, CachedIconType.ICON_16);
	}
	
	private static IconRef register32x32Icon(String s) {
		return register(s, CachedIconType.ICON_32);
	}
	
	private static IconRef registerOtherIcon(String s) {
		return register(s, CachedIconType.ICON_OTHER);
	}
	
	private static IconRef register(String s, CachedIconType t) {
		Tuple<String, CachedIconType> tuple = Tuple.Create(s, t);
		
		if (icn_ressources.containsKey(tuple)) return icn_ressources.get(tuple);
		
		IconRef ref = new IconRef(s, t);
		icn_ressources.put(tuple, ref);
		
		return ref;
	}
	
	private static ImageRef registerImage(String s) {
		ImageRef ref = new ImageRef(s);

		if (img_ressources.containsKey(s)) return img_ressources.get(s);
		
		img_ressources.put(s, ref);
		
		return ref;
	}
	
	public static void preload() {
		for (Entry<Tuple<String, CachedIconType>, IconRef> s : icn_ressources.entrySet()) {
			CachedResourceLoader.preload(s.getValue());
		}
		
		for (Entry<String, ImageRef> s : img_ressources.entrySet()) {
			CachedResourceLoader.preload(s.getValue());
		}
	}
}
