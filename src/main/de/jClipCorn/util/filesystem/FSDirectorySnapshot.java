package de.jClipCorn.util.filesystem;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * An in-memory snapshot of one or more directory trees, gathered with a single {@link Files#walkFileTree} pass per root.
 *
 * The file-type (file vs directory) is taken from the {@link BasicFileAttributes} that the walk hands out for free, so
 * there is no extra per-entry stat call (which is the expensive part on network shares).
 *
 * The database-validator uses this to answer the empty-directory and orphaned-file checks from a single shared walk
 * instead of traversing the (potentially NFS-mounted) movie/series trees multiple times.
 */
public final class FSDirectorySnapshot {

	public static final class DirNode {
		public final FSPath Path;
		public final List<DirNode> SubDirs = new ArrayList<>();
		public final List<FSPath>  Files   = new ArrayList<>();

		private DirNode(FSPath path) { this.Path = path; }
	}

	private final List<DirNode> roots    = new ArrayList<>();
	private final List<FSPath>  allFiles = new ArrayList<>();

	private FSDirectorySnapshot() { }

	/** one node per scanned root directory */
	public List<DirNode> getRoots() { return roots; }

	/** every regular file found beneath any of the scanned roots */
	public List<FSPath> getAllFiles() { return allFiles; }

	public static FSDirectorySnapshot build(List<FSPath> rootDirs, Consumer<FSPath> onVisit) throws IOException {
		var snap = new FSDirectorySnapshot();
		for (FSPath root : rootDirs) snap.walk(root, onVisit);
		return snap;
	}

	private void walk(FSPath root, Consumer<FSPath> onVisit) throws IOException {
		final Map<Path, DirNode> index = new HashMap<>();

		Files.walkFileTree(root.toPath(), new SimpleFileVisitor<>() {
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
				DirNode node = new DirNode(FSPath.create(dir.toString()));
				index.put(dir, node);

				Path parent = dir.getParent();
				DirNode pn = (parent == null) ? null : index.get(parent);
				if (pn != null) pn.SubDirs.add(node);
				else            roots.add(node); // the scanned root itself - its parent is outside the walk

				if (onVisit != null) onVisit.accept(node.Path);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
				FSPath fsfile = FSPath.create(file.toString());
				allFiles.add(fsfile);

				Path parent = file.getParent();
				DirNode pn = (parent == null) ? null : index.get(parent);
				if (pn != null) pn.Files.add(fsfile);

				if (onVisit != null) onVisit.accept(fsfile);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFileFailed(Path file, IOException exc) {
				return FileVisitResult.CONTINUE; // skip unreadable entries instead of aborting the whole walk
			}
		});
	}
}
