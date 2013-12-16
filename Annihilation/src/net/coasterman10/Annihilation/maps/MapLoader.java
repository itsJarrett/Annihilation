package net.coasterman10.Annihilation.maps;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

public class MapLoader {
    private final Logger log;
    private final File dataFolder;

    public MapLoader(Logger log, File dataFolder) {
	this.dataFolder = dataFolder;
	this.log = log;
    }

    public boolean loadMap(String name) {
	File mapsFolder = new File(dataFolder, "maps");
	if (!mapsFolder.exists())
	    return false;
	File source = new File(mapsFolder, name);
	if (!source.exists())
	    return false;

	File destination = new File(dataFolder.getParentFile().getParentFile(), name);
	try {
	    copyFolder(source, destination);
	    return true;
	} catch (IOException e) {
	    log.severe("Could not load map " + name);
	    e.printStackTrace();
	    return false;
	}
    }

    private void copyFolder(File src, File dest) throws IOException {
	if (!src.exists()) {
	    log.severe("File " + src.toString()
		    + " does not exist, cannot copy");
	    return;
	}

	if (src.isDirectory()) {
	    boolean existed = dest.exists();
	    if (!existed)
		dest.mkdir();

	    String[] files = src.list();

	    if (existed)
		log.info("Copying folder " + src.getName()
			+ " and overwriting " + dest.getName());
	    else
		log.info("Copying folder " + src.getName() + " to "
			+ dest.getName());

	    for (String file : files) {
		File srcFile = new File(src, file);
		File destFile = new File(dest, file);
		copyFolder(srcFile, destFile);
	    }

	    if (existed)
		log.info("Overwrote folder " + dest.getName());
	    else
		log.info("Copied folder " + dest.getName());

	} else {
	    InputStream in = new FileInputStream(src);
	    OutputStream out = new FileOutputStream(dest);

	    boolean existed = dest.exists();

	    byte[] buffer = new byte[1024];
	    int length;
	    while ((length = in.read(buffer)) > 0) {
		out.write(buffer, 0, length);
	    }

	    in.close();
	    out.close();

	    if (existed)
		log.info("Overwrote file " + dest.getName());
	    else
		log.info("Copied file " + dest.getName());
	}
    }
}
