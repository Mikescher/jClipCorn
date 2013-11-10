package de.jClipCorn.gui;

import java.util.ArrayList;
import java.util.List;

import de.jClipCorn.util.formatter.PathFormatter;

@SuppressWarnings("nls")
public class Resources {
	private static List<String> icn_ressources = new ArrayList<>();
	
	// ##############################################  <COMMON>  #############################################
	public final static String IMG_FRAME_ICON 			= registerIMG("/app.png");
	public final static String IMG_COVER_STANDARD 		= registerIMG("/standard.png");
	public final static String IMG_COVER_NOTFOUND 		= registerIMG("/covernotfound.png");
	public final static String IMG_COVER_SERIES_MASK	= registerIMG("/mask_series.png");
	public final static String IMG_COVER_ERROR_ON		= registerIMG("/cover_errror_on.png");
	public final static String IMG_COVER_ERROR_OFF		= registerIMG("/cover_errror_off.png");
	// #############################################  </COMMON>  #############################################
	
	
	
	// #############################################  <MENUBAR>  #############################################
	public final static String ICN_MENUBAR_CLOSE		 			= registerICN("/icons/toolbar/door_open.png");
	public final static String ICN_MENUBAR_PARSEXML 				= registerICN("/icons/toolbar/blueprint.png");
	public final static String ICN_MENUBAR_PLAY						= registerICN("/icons/toolbar/control_play.png");
	public final static String ICN_MENUBAR_HIDDENPLAY				= registerICN("/icons/toolbar/control_hiddenplay.png");
	public final static String ICN_MENUBAR_ADD_MOV					= registerICN("/icons/toolbar/add.png");
	public final static String ICN_MENUBAR_ADD_SER					= registerICN("/icons/toolbar/addSer.png");
	public final static String ICN_MENUBAR_ADD_SEA					= registerICN("/icons/toolbar/addSea.png");
	public final static String ICN_MENUBAR_DBCHECK					= registerICN("/icons/toolbar/application_osx_terminal.png");
	public final static String ICN_MENUBAR_CLEARDB					= registerICN("/icons/toolbar/fire_damage.png");
	public final static String ICN_MENUBAR_SETTINGS					= registerICN("/icons/toolbar/gear_in.png");
	public final static String ICN_MENUBAR_PREVIEW_MOV				= registerICN("/icons/toolbar/magnifier.png");
	public final static String ICN_MENUBAR_PREVIEW_SER				= registerICN("/icons/toolbar/magnifier_ser.png");
	public final static String ICN_MENUBAR_REMOVE					= registerICN("/icons/toolbar/bin.png");
	public final static String ICN_MENUBAR_EDIT_MOV					= registerICN("/icons/toolbar/screwdriver_mov.png");
	public final static String ICN_MENUBAR_EDIT_SER					= registerICN("/icons/toolbar/screwdriver_ser.png");
	public final static String ICN_MENUBAR_LOG						= registerICN("/icons/toolbar/system_monitor.png");
	public final static String ICN_MENUBAR_FOLDER					= registerICN("/icons/toolbar/folder.png");
	public final static String ICN_MENUBAR_VIEWED					= registerICN("/icons/toolbar/viewed.png");
	public final static String ICN_MENUBAR_UNVIEWED					= registerICN("/icons/toolbar/unviewed.png");
	public final static String ICN_MENUBAR_SCANFOLDER				= registerICN("/icons/toolbar/scanner.png");
	public final static String ICN_MENUBAR_ABOUT					= registerICN("/icons/toolbar/info_rhombus.png");
	public final static String ICN_MENUBAR_MCHANGE_VIEWED			= registerICN("/icons/toolbar/knot.png");
	public final static String ICN_MENUBAR_MCHANGE_SCORE			= registerICN("/icons/toolbar/knotscore.png");
	public final static String ICN_MENUBAR_RESETVIEWED				= registerICN("/icons/toolbar/scroller_bar.png");
	public final static String ICN_MENUBAR_MOVESERIES				= registerICN("/icons/toolbar/move_to_folder.png");
	public final static String ICN_MENUBAR_FILENAMERULES			= registerICN("/icons/toolbar/books.png");
	public final static String ICN_MENUBAR_CREATE_JXMLBKP			= registerICN("/icons/toolbar/source_code.png");
	public final static String ICN_MENUBAR_COMPARE					= registerICN("/icons/toolbar/balance_unbalance.png");
	public final static String ICN_MENUBAR_SEARCH					= registerICN("/icons/toolbar/bubblechart.png");
	public final static String ICN_MENUBAR_IMDB						= registerICN("/icons/toolbar/imdb.png");
	public final static String ICN_MENUBAR_OPENFILE					= registerICN("/icons/toolbar/open_folder.png");
	public final static String ICN_MENUBAR_IMPORTDB					= registerICN("/icons/toolbar/source_code_bullet_down.png");
	public final static String ICN_MENUBAR_IMPORMULTIPLEELEMENTS	= registerICN("/icons/toolbar/file_manager_bullet_add.png");
	public final static String ICN_MENUBAR_IMPORTMOVIE				= registerICN("/icons/toolbar/script_bullet_add_mov.png");
	public final static String ICN_MENUBAR_EXPORTMOVIE				= registerICN("/icons/toolbar/script_bullet_save_mov.png");
	public final static String ICN_MENUBAR_EXPORTELEMENTS			= registerICN("/icons/toolbar/file_manager_bullet_save.png");
	public final static String ICN_MENUBAR_IMPORTSERIES				= registerICN("/icons/toolbar/script_bullet_add_ser.png");
	public final static String ICN_MENUBAR_EXPORTSERIES				= registerICN("/icons/toolbar/script_bullet_save_ser.png");
	public final static String ICN_MENUBAR_EPISODEGUIDE				= registerICN("/icons/toolbar/document_quote.png");
	public final static String ICN_MENUBAR_RANDOMMOVIE				= registerICN("/icons/toolbar/magic_wand_2.png");
	public final static String ICN_MENUBAR_TAGS						= registerICN("/icons/toolbar/tags_cloud.png");
	public final static String ICN_MENUBAR_TAG_0_0 					= registerICN("/icons/tags/tag_0_off.png");
	public final static String ICN_MENUBAR_TAG_0_1 					= registerICN("/icons/tags/tag_0_on.png");
	public final static String ICN_MENUBAR_TAG_1_0 					= registerICN("/icons/tags/tag_1_off.png");
	public final static String ICN_MENUBAR_TAG_1_1 					= registerICN("/icons/tags/tag_1_on.png");
	public final static String ICN_MENUBAR_TAG_2_0 					= registerICN("/icons/tags/tag_2_off.png");
	public final static String ICN_MENUBAR_TAG_2_1 					= registerICN("/icons/tags/tag_2_on.png");
	public final static String ICN_MENUBAR_TAG_3_0 					= registerICN("/icons/tags/tag_3_off.png");
	public final static String ICN_MENUBAR_TAG_3_1 					= registerICN("/icons/tags/tag_3_on.png");
	public final static String ICN_MENUBAR_TAG_4_0 					= registerICN("/icons/tags/tag_4_off.png");
	public final static String ICN_MENUBAR_TAG_4_1 					= registerICN("/icons/tags/tag_4_on.png");
	public final static String ICN_MENUBAR_CREATEFOLDERSTRUCTURE	= registerICN("/icons/toolbar/folders_explorer.png");
	public final static String ICN_MENUBAR_SAVE						= registerICN("/icons/toolbar/save_as.png");
	public final static String ICN_MENUBAR_STATISTICS				= registerICN("/icons/toolbar/statistics.png");
	public final static String ICN_MENUBAR_UPDATES					= registerICN("/icons/toolbar/www_page.png");
	public final static String ICN_MENUBAR_BACKUPMANAGER			= registerICN("/icons/toolbar/backup_manager.png");
	public final static String ICN_MENUBAR_WATCHDATA				= registerICN("/icons/toolbar/inbox_table.png");
	// #############################################  </MENUBAR>  ############################################
	

	
	// #############################################  <TABLE>  ###############################################
	public final static String ICN_TABLE_SERIES 		= registerICN("/icons/table/series.png");
	public final static String ICN_TABLE_MOVIE 			= registerICN("/icons/table/film.png");
	
	public final static String ICN_TABLE_VIEWED_FALSE 	= registerICN("/icons/table/viewed_0.png");
	public final static String ICN_TABLE_VIEWED_TRUE  	= registerICN("/icons/table/viewed_1.png");
	
	public final static String ICN_TABLE_QUALITY_0 		= registerICN("/icons/table/quality_0.png");
	public final static String ICN_TABLE_QUALITY_1 		= registerICN("/icons/table/quality_1.png");
	public final static String ICN_TABLE_QUALITY_2 		= registerICN("/icons/table/quality_2.png");
	public final static String ICN_TABLE_QUALITY_3 		= registerICN("/icons/table/quality_3.png");
	public final static String ICN_TABLE_QUALITY_4 		= registerICN("/icons/table/quality_4.png");
	
	public final static String ICN_TABLE_ONLINESCORE_0  = registerICN("/icons/table/stars_0.png");
	public final static String ICN_TABLE_ONLINESCORE_1  = registerICN("/icons/table/stars_1.png");
	public final static String ICN_TABLE_ONLINESCORE_2  = registerICN("/icons/table/stars_2.png");
	public final static String ICN_TABLE_ONLINESCORE_3  = registerICN("/icons/table/stars_3.png");
	public final static String ICN_TABLE_ONLINESCORE_4  = registerICN("/icons/table/stars_4.png");
	public final static String ICN_TABLE_ONLINESCORE_5  = registerICN("/icons/table/stars_5.png");
	public final static String ICN_TABLE_ONLINESCORE_6  = registerICN("/icons/table/stars_6.png");
	public final static String ICN_TABLE_ONLINESCORE_7  = registerICN("/icons/table/stars_7.png");
	public final static String ICN_TABLE_ONLINESCORE_8  = registerICN("/icons/table/stars_8.png");
	public final static String ICN_TABLE_ONLINESCORE_9  = registerICN("/icons/table/stars_9.png");
	public final static String ICN_TABLE_ONLINESCORE_10 = registerICN("/icons/table/stars_10.png");
	
	public final static String ICN_TABLE_FSK_0 			= registerICN("/icons/table/fsk_0.png");
	public final static String ICN_TABLE_FSK_1 			= registerICN("/icons/table/fsk_1.png");
	public final static String ICN_TABLE_FSK_2 			= registerICN("/icons/table/fsk_2.png");
	public final static String ICN_TABLE_FSK_3 			= registerICN("/icons/table/fsk_3.png");
	public final static String ICN_TABLE_FSK_4 			= registerICN("/icons/table/fsk_4.png");
	
	public final static String ICN_TABLE_LANGUAGE_0 	= registerICN("/icons/table/language_0.png");
	public final static String ICN_TABLE_LANGUAGE_1 	= registerICN("/icons/table/language_1.png");
	public final static String ICN_TABLE_LANGUAGE_2 	= registerICN("/icons/table/language_2.png");
	public final static String ICN_TABLE_LANGUAGE_3 	= registerICN("/icons/table/language_3.png");
	
	public final static String ICN_TABLE_SCORE_0 		= registerICN("/icons/table/poo.png");
	public final static String ICN_TABLE_SCORE_1 		= registerICN("/icons/table/emotion_sick.png");
	public final static String ICN_TABLE_SCORE_2 		= registerICN("/icons/table/thumb_down.png");
	public final static String ICN_TABLE_SCORE_3 		= registerICN("/icons/table/thumb_up.png");
	public final static String ICN_TABLE_SCORE_4 		= registerICN("/icons/table/heart.png");
	public final static String ICN_TABLE_SCORE_5 		= registerICN("/icons/table/award_star_gold_3.png");
	
	public final static String ICN_TABLE_FORMAT_0 		= registerICN("/icons/table/ext0.png");
	public final static String ICN_TABLE_FORMAT_1 		= registerICN("/icons/table/ext1.png");
	public final static String ICN_TABLE_FORMAT_2 		= registerICN("/icons/table/ext2.png");
	public final static String ICN_TABLE_FORMAT_3 		= registerICN("/icons/table/ext3.png");
	public final static String ICN_TABLE_FORMAT_4 		= registerICN("/icons/table/ext4.png");
	public final static String ICN_TABLE_FORMAT_5 		= registerICN("/icons/table/ext0.png");
	public final static String ICN_TABLE_FORMAT_6 		= registerICN("/icons/table/ext6.png");
	public final static String ICN_TABLE_FORMAT_7 		= registerICN("/icons/table/ext7.png");
	public final static String ICN_TABLE_FORMAT_8 		= registerICN("/icons/table/ext8.png");
	
	public final static String ICN_TABLE_TAG_0_0 		= register16x16ICN("/icons/tags/tag_0_off.png");
	public final static String ICN_TABLE_TAG_0_1 		= register16x16ICN("/icons/tags/tag_0_on.png");
	public final static String ICN_TABLE_TAG_1_0 		= register16x16ICN("/icons/tags/tag_1_off.png");
	public final static String ICN_TABLE_TAG_1_1 		= register16x16ICN("/icons/tags/tag_1_on.png");
	public final static String ICN_TABLE_TAG_2_0 		= register16x16ICN("/icons/tags/tag_2_off.png");
	public final static String ICN_TABLE_TAG_2_1 		= register16x16ICN("/icons/tags/tag_2_on.png");
	public final static String ICN_TABLE_TAG_3_0 		= register16x16ICN("/icons/tags/tag_3_off.png");
	public final static String ICN_TABLE_TAG_3_1 		= register16x16ICN("/icons/tags/tag_3_on.png");
	public final static String ICN_TABLE_TAG_4_0 		= register16x16ICN("/icons/tags/tag_4_off.png");
	public final static String ICN_TABLE_TAG_4_1 		= register16x16ICN("/icons/tags/tag_4_on.png");
	// #############################################  </TABLE>  ##############################################
	
	
	
	// ############################################  </FRAMES>  ##############################################
	public final static String ICN_FRAMES_IMDB 			= registerICN("/icons/addMovieFrame/imdb.png");	
	public final static String ICN_FRAMES_SEARCH 		= registerICN("/icons/previewSeriesFrame/magnifier.png");
	public final static String IMG_FRAMES_ABOUT 		= registerICN("/UberDialog.png");	
	// #############################################  <FRAMES>  ##############################################

	
	// ##############################################  </LOG>  ###############################################
	public final static String ICN_LOG_UNDEFINIED		= registerICN("/icons/log/question.png");
	public final static String ICN_LOG_ERROR 			= registerICN("/icons/log/cross_shield.png");
	public final static String ICN_LOG_WARNING	 		= registerICN("/icons/log/caution_high_voltage.png");
	public final static String ICN_LOG_OK	 			= registerICN("/icons/log/tick.png");
	// ###############################################  <LOG>  ###############################################
	
	
	
	// #############################################  </SIDEBAR>  ############################################
	public final static String ICN_SIDEBAR_ALL	 		= registerICN("/icons/sidebar/jar_empty.png");
	public final static String ICN_SIDEBAR_ZYKLUS 		= registerICN("/icons/sidebar/asterisk_orange.png");
	public final static String ICN_SIDEBAR_GENRE 		= registerICN("/icons/sidebar/book_spelling.png");
	public final static String ICN_SIDEBAR_ONLINESCORE	= registerICN("/icons/sidebar/star.png");
	public final static String ICN_SIDEBAR_SCORE 		= registerICN("/icons/sidebar/to_do_list.png");
	public final static String ICN_SIDEBAR_YEAR	 		= registerICN("/icons/sidebar/calendar.png");
	public final static String ICN_SIDEBAR_FORMAT 		= registerICN("/icons/sidebar/file_extension_txt.png");
	public final static String ICN_SIDEBAR_QUALITY 		= registerICN("/icons/sidebar/server_components.png");
	public final static String ICN_SIDEBAR_TAGS 		= registerICN("/icons/sidebar/tags_cloud.png");
	public final static String ICN_SIDEBAR_LANGUAGE		= registerICN("/icons/sidebar/flag_red.png");
	public final static String ICN_SIDEBAR_TYP	 		= registerICN("/icons/sidebar/color_swatch.png");
	public final static String ICN_SIDEBAR_VIEWED 		= registerICN("/icons/sidebar/viewed.png");
	public final static String ICN_SIDEBAR_UNVIEWED		= registerICN("/icons/sidebar/unviewed.png");
	public final static String ICN_SIDEBAR_CUSTOM		= registerICN("/icons/sidebar/tag.png");
	// ##############################################  <SIDEBAR>  ############################################
	
	
	// #######################################################################################################
	// #######################################################################################################
	// #######################################################################################################
	
	
	private static String registerICN(String s) {
		icn_ressources.add(s);
		
		return s;
	}
	
	private static String register16x16ICN(String s) {
		s = PathFormatter.getWithoutExtension(s) + "_16x16." + PathFormatter.getExtension(s);
		
		return registerICN(s);
	}
	
	private static String registerIMG(String s) {
		return s;
	}
	
	public static void preload() {
		for (String s : icn_ressources) {
			CachedResourceLoader.preload(s);
		}
	}
}
