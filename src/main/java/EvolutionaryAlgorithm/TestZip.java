package EvolutionaryAlgorithm;

import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class TestZip {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		try{
		FileOutputStream fos = new FileOutputStream("MyFile.zip");
		ZipOutputStream zos = new ZipOutputStream(fos);
		ZipEntry ze= new ZipEntry("folder/test.txt");
		zos.putNextEntry(ze);
		String buffer= "Hello as well!\n";
		zos.write(buffer.getBytes());
		zos.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

}
