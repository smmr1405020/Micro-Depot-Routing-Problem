package EvolutionaryAlgorithm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipHelper {
	public static void addDirToArchive(ZipOutputStream zos, File srcFile, String dir) {

		File[] files = srcFile.listFiles();

		System.out.println("Adding directory: " + srcFile.getName());

		for (int i = 0; i < files.length; i++) {
			
			// if the file is directory, use recursion
			if (files[i].isDirectory()) {
				addDirToArchive(zos, files[i], files[i].getName());
				continue;
			}

			try {
				
				System.out.println("tAdding file: " + files[i].getName());

				// create byte buffer
				byte[] buffer = new byte[1024];

				FileInputStream fis = new FileInputStream(files[i]);

				zos.putNextEntry(new ZipEntry(dir +"/"+files[i].getName()));
				
				int length;

				while ((length = fis.read(buffer)) > 0) {
					zos.write(buffer, 0, length);
				}

				zos.closeEntry();

				// close the InputStream
				fis.close();

			} catch (IOException ioe) {
				System.out.println("IOException :" + ioe);
			}
			
		}

	}
}
