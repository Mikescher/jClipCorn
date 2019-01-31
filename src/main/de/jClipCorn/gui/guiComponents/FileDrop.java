package de.jClipCorn.gui.guiComponents;

import de.jClipCorn.util.stream.CCStreams;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.io.*;
import java.util.ArrayList;

/**
 * This class makes it easy to drag and drop files from the operating
 * system to a Java program. Any <tt>java.awt.Component</tt> can be
 * dropped onto, but only <tt>javax.swing.JComponent</tt>s will indicate
 * the drop event with a changed border.
 * <p/>
 * To use this class, construct a new <tt>FileDrop</tt> by passing
 * it the target component and a <tt>Listener</tt> to receive notification
 * when file(s) have been dropped. Here is an example:
 * <p/>
 * <code><pre>
 *      JPanel myPanel = new JPanel();
 *      new FileDrop( myPanel, new FileDrop.Listener()
 *      {   public void filesDropped( java.io.File[] files )
 *          {
 *              // handle file drop
 *              ...
 *          }   // end filesDropped
 *      }); // end FileDrop.Listener
 * </pre></code>
 * <p/>
 * You can specify the border that will appear when files are being dragged by
 * calling the constructor with a <tt>javax.swing.border.Border</tt>. Only
 * <tt>JComponent</tt>s will show any indication with a border.
 * <p/>
 * You can turn on some debugging features by passing a <tt>PrintStream</tt>
 * object (such as <tt>System.out</tt>) into the full constructor. A <tt>null</tt>
 * value will result in no extra debugging information being output.
 * <p/>
 *
 * <p>I'm releasing this code into the Public Domain. Enjoy.
 * </p>
 * <p><em>Original author: Robert Harder, rharder@usa.net</em></p>
 * <p>2007-09-12 Nathan Blomquist -- Linux (KDE/Gnome) support added.</p>
 *
 * ====================================================================================================================
 * https://github.com/mattdesl/lwjgl-basics/blob/cdf5c33c5365848e7e052036e1ff549dfc6c109f/test/mdesl/test/FileDrop.java
 * ====================================================================================================================
 *
 * @author Robert Harder
 * @author rharder@users.sf.net
 * @version 1.0.1
 */
@SuppressWarnings({"nls", "unused"})
public class FileDrop {
	private transient javax.swing.border.Border normalBorder;
	private transient java.awt.dnd.DropTargetListener dropListener;

	private static Boolean supportsDnD;

	private static java.awt.Color defaultBorderColor = new java.awt.Color(0x33, 0x99, 0xFF);

	public FileDrop(final Component c, final Listener listener) {
		this(null, c, BorderFactory.createMatteBorder(2, 2, 2, 2, defaultBorderColor), true, listener);
	}

	public FileDrop(final Component c, final boolean recursive, final Listener listener) {
		this(null, c, BorderFactory.createMatteBorder(2, 2, 2, 2, defaultBorderColor), recursive, listener);
	}

	public FileDrop(final PrintStream out, final Component c, final Listener listener) {
		this(out, c, BorderFactory.createMatteBorder(2, 2, 2, 2, defaultBorderColor), false, listener);
	}

	public FileDrop(final PrintStream out, final Component c, final boolean recursive, final Listener listener) {
		this(out, c, BorderFactory.createMatteBorder(2, 2, 2, 2, defaultBorderColor), recursive, listener);
	}

	public FileDrop(final Component c, final Border dragBorder, final Listener listener) {
		this(null, c, dragBorder, false, listener);
	}

	public FileDrop(final Component c, final Border dragBorder, final boolean recursive, final Listener listener) {
		this(null, c, dragBorder, recursive, listener);
	}

	public FileDrop(final PrintStream out, final Component c, final Border dragBorder, final Listener listener) {
		this(out, c, dragBorder, false, listener);
	}

	private FileDrop(final PrintStream out, final Component c, final Border dragBorder, final boolean recursive, final Listener listener) {

		if (supportsDnD()) {
			dropListener = new java.awt.dnd.DropTargetListener() {
				public void dragEnter(java.awt.dnd.DropTargetDragEvent evt) {
					log(out, "FileDrop: dragEnter event.");

					if (isDragOk(out, evt)) {
						if (c instanceof javax.swing.JComponent) {
							javax.swing.JComponent jc = (javax.swing.JComponent) c;
							normalBorder = jc.getBorder();
							log(out, "FileDrop: normal border saved.");
							jc.setBorder(dragBorder);
							log(out, "FileDrop: drag border set.");
						}

						evt.acceptDrag(java.awt.dnd.DnDConstants.ACTION_COPY);
						log(out, "FileDrop: event accepted.");
					} else {
						evt.rejectDrag();
						log(out, "FileDrop: event rejected.");
					}
				}

				public void dragOver(java.awt.dnd.DropTargetDragEvent evt) {
					// over the drag target.
				}

				public void drop(java.awt.dnd.DropTargetDropEvent evt) {
					log(out, "FileDrop: drop event.");
					try {
						java.awt.datatransfer.Transferable tr = evt.getTransferable();

						if (tr.isDataFlavorSupported(java.awt.datatransfer.DataFlavor.javaFileListFlavor)) {
							evt.acceptDrop(java.awt.dnd.DnDConstants.ACTION_COPY);
							log(out, "FileDrop: file list accepted.");

							java.util.List fileList = (java.util.List) tr.getTransferData(java.awt.datatransfer.DataFlavor.javaFileListFlavor);
							java.util.Iterator iterator = fileList.iterator();

							final java.io.File[] files = CCStreams.iterateList(fileList).cast(File.class).toArray(new File[0]);

							if (listener != null) listener.filesDropped(files);

							evt.getDropTargetContext().dropComplete(true);
							log(out, "FileDrop: drop complete.");
						}
						else
						{
							DataFlavor[] flavors = tr.getTransferDataFlavors();
							boolean handled = false;
							for (DataFlavor flavor : flavors) {
								if (flavor.isRepresentationClassReader()) {
									evt.acceptDrop(DnDConstants.ACTION_COPY);
									log(out, "FileDrop: reader accepted.");

									Reader reader = flavor.getReaderForText(tr);

									BufferedReader br = new BufferedReader(reader);

									if (listener != null) listener.filesDropped(createFileArray(br, out));

									evt.getDropTargetContext().dropComplete(true);
									log(out, "FileDrop: drop complete.");
									handled = true;
									break;
								}
							}
							if (!handled) {
								log(out, "FileDrop: not a file list or reader - abort.");
								evt.rejectDrop();
							}
						}
					}
					catch (java.io.IOException io) {
						log(out, "FileDrop: IOException - abort:");
						io.printStackTrace(out);
						evt.rejectDrop();
					}
					catch (java.awt.datatransfer.UnsupportedFlavorException ufe) {
						log(out, "FileDrop: UnsupportedFlavorException - abort:");
						ufe.printStackTrace(out);
						evt.rejectDrop();
					}
					finally {
						if (c instanceof javax.swing.JComponent) {
							javax.swing.JComponent jc = (javax.swing.JComponent) c;
							jc.setBorder(normalBorder);
							log(out, "FileDrop: normal border restored.");
						}
					}
				}

				public void dragExit(java.awt.dnd.DropTargetEvent evt) {
					log(out, "FileDrop: dragExit event.");

					if (c instanceof javax.swing.JComponent) {
						javax.swing.JComponent jc = (javax.swing.JComponent) c;
						jc.setBorder(normalBorder);
						log(out, "FileDrop: normal border restored.");
					}
				}

				public void dropActionChanged(java.awt.dnd.DropTargetDragEvent evt) {
					log(out, "FileDrop: dropActionChanged event.");

					if (isDragOk(out, evt)) {
						evt.acceptDrag(java.awt.dnd.DnDConstants.ACTION_COPY);
						log(out, "FileDrop: event accepted.");
					}
					else {
						evt.rejectDrag();
						log(out, "FileDrop: event rejected.");
					}
				}
			};

			makeDropTarget(out, c, recursive);
		} else {
			log(out, "FileDrop: Drag and drop is not supported with this JVM");
		}
	}

	private static boolean supportsDnD() {   // Static Boolean
		if (supportsDnD == null) {
			boolean support = false;
			try {
				Class arbitraryDndClass = Class.forName("java.awt.dnd.DnDConstants");
				support = true;
			}
			catch (Exception e) {
				// support = false;
			}
			supportsDnD = support;
		}
		return supportsDnD;
	}

	private final static String ZERO_CHAR_STRING = "" + (char) 0;

	private static File[] createFileArray(BufferedReader bReader, PrintStream out) {
		try {
			java.util.List<File> list = new ArrayList<>();
			String line;
			while ((line = bReader.readLine()) != null) {
				try {
					if (ZERO_CHAR_STRING.equals(line)) continue;

					java.io.File file = new java.io.File(new java.net.URI(line));
					list.add(file);
				} catch (Exception ex) {
					log(out, "Error with " + line + ": " + ex.getMessage());
				}
			}

			return list.toArray(new File[0]);
		} catch (IOException ex) {
			log(out, "FileDrop: IOException");
		}
		return new File[0];
	}

	private void makeDropTarget(final java.io.PrintStream out, final java.awt.Component c, boolean recursive) {
		final java.awt.dnd.DropTarget dt = new java.awt.dnd.DropTarget();

		try {
			dt.addDropTargetListener(dropListener);
		} catch (java.util.TooManyListenersException e) {
			e.printStackTrace();
			log(out, "FileDrop: Drop will not work due to previous error. Do you have another listener attached?");
		}

		c.addHierarchyListener(evt ->
		{
			log(out, "FileDrop: Hierarchy changed.");
			Component parent = c.getParent();
			if (parent == null) {
				c.setDropTarget(null);
				log(out, "FileDrop: Drop target cleared from component.");
			} else {
				new java.awt.dnd.DropTarget(c, dropListener);
				log(out, "FileDrop: Drop target added to component.");
			}
		});

		if (c.getParent() != null) new java.awt.dnd.DropTarget(c, dropListener);

		if (recursive && (c instanceof java.awt.Container)) {
			java.awt.Container cont = (java.awt.Container) c;

			java.awt.Component[] comps = cont.getComponents();

			for (Component comp : comps) makeDropTarget(out, comp, true);
		}
	}

	private boolean isDragOk(final java.io.PrintStream out, final java.awt.dnd.DropTargetDragEvent evt) {
		boolean ok = false;

		java.awt.datatransfer.DataFlavor[] flavors = evt.getCurrentDataFlavors();

		int i = 0;
		while (!ok && i < flavors.length) {
			final DataFlavor curFlavor = flavors[i];
			if (curFlavor.equals(java.awt.datatransfer.DataFlavor.javaFileListFlavor) ||
					curFlavor.isRepresentationClassReader()) {
				ok = true;
			}

			i++;
		}

		if (out != null) {
			if (flavors.length == 0)
				log(out, "FileDrop: no data flavors.");
			for (i = 0; i < flavors.length; i++)
				log(out, flavors[i].toString());
		}

		return ok;
	}

	private static void log(java.io.PrintStream out, String message) {   // Log message if requested
		if (out != null)
			out.println(message);
	}

	public static boolean remove(java.awt.Component c) {
		return remove(null, c, true);
	}

	public static boolean remove(java.io.PrintStream out, java.awt.Component c, boolean recursive) {   // Make sure we support dnd.
		if (supportsDnD()) {
			log(out, "FileDrop: Removing drag-and-drop hooks.");
			c.setDropTarget(null);
			if (recursive && (c instanceof java.awt.Container)) {
				java.awt.Component[] comps = ((java.awt.Container) c).getComponents();
				for (Component comp : comps) remove(out, comp, true);
				return true;
			}
			else return false;
		}
		else return false;
	}

	public interface Listener {
		void filesDropped(java.io.File[] files);
	}

	public static class Event extends java.util.EventObject {
		private java.io.File[] files;

		public Event(java.io.File[] files, Object source) {
			super(source);
			this.files = files;
		}

		public java.io.File[] getFiles() {
			return files;
		}
	}

	public static class TransferableObject implements java.awt.datatransfer.Transferable {
		final static String MIME_TYPE = "application/x-net.iharder.dnd.TransferableObject";

		final static DataFlavor DATA_FLAVOR = new DataFlavor(FileDrop.TransferableObject.class, MIME_TYPE);

		private Fetcher fetcher;
		private Object data;

		private DataFlavor customFlavor;

		public TransferableObject(Object data) {
			this.data = data;
			this.customFlavor = new java.awt.datatransfer.DataFlavor(data.getClass(), MIME_TYPE);
		}

		public TransferableObject(Fetcher fetcher) {
			this.fetcher = fetcher;
		}

		public TransferableObject(Class dataClass, Fetcher fetcher) {
			this.fetcher = fetcher;
			this.customFlavor = new java.awt.datatransfer.DataFlavor(dataClass, MIME_TYPE);
		}

		public DataFlavor getCustomDataFlavor() {
			return customFlavor;
		}

		public java.awt.datatransfer.DataFlavor[] getTransferDataFlavors() {
			if (customFlavor != null)
				return new DataFlavor[] {customFlavor, DATA_FLAVOR, DataFlavor.stringFlavor };
			else
				return new DataFlavor[] { DATA_FLAVOR, DataFlavor.stringFlavor };
		}

		public Object getTransferData(java.awt.datatransfer.DataFlavor flavor) throws java.awt.datatransfer.UnsupportedFlavorException {
			if (flavor.equals(DATA_FLAVOR)) return fetcher == null ? data : fetcher.getObject();

			if (flavor.equals(java.awt.datatransfer.DataFlavor.stringFlavor)) return fetcher == null ? data.toString() : fetcher.getObject().toString();

			throw new java.awt.datatransfer.UnsupportedFlavorException(flavor);
		}

		public boolean isDataFlavorSupported(java.awt.datatransfer.DataFlavor flavor) {
			if (flavor.equals(DATA_FLAVOR)) return true;

			return flavor.equals(DataFlavor.stringFlavor);

		}

		public interface Fetcher {
			Object getObject();
		}
	}
}
