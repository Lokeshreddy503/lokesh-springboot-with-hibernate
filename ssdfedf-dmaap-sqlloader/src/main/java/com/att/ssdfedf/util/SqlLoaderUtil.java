package com.lokesh.ssdfedf.util;



import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class SqlLoaderUtil {   
    
	public File[] getFilesFromDirectoryInSortOrder(String dirName) {
		File sourceDir = new File(dirName);
		File[] filesList = sourceDir.listFiles();
		
		Arrays.sort(filesList, new Comparator<File>() {
            public int compare(File o1, File o2) {
                int n1 = Integer.valueOf(o1.getName().substring(0, 10));
                int n2 = Integer.valueOf(o2.getName().substring(0, 10));
                return n1 - n2;
            }
        });
		
		return filesList;
	}
	
	public File[] getFilesFromDirectory(String dirName) {
		File sourceDir = new File(dirName);
		File[] filesList = sourceDir.listFiles();
		
		return filesList;
	}
	
	
    public void moveFiletoDiffFolder(String sourceFolder, String destFolder) {
        try {

            File sourceDir = new File(sourceFolder);
            File destDir = new File(destFolder);
            destDir.mkdirs(); // make sure that the destination directory exists

            Path destPath = destDir.toPath();
            for (File sourceFile : sourceDir.listFiles()) {
            	Path sourcePath = sourceFile.toPath();
                Files.copy(sourcePath, destPath.resolve(sourcePath.getFileName()));
            }

        }catch(Exception e) {
                e.printStackTrace();
        }
    }
    
    public void moveZipFiletoDiffFolder(String fileName, String sourceFolder, String destFolder) {
        try{

            File afile =new File(sourceFolder+"/"+fileName);

            if(afile.renameTo(new File(destFolder+"/"+afile.getName()))){
                 System.out.println(":::::::::::::::::::::::File is moved successful! :"+fileName);
            }else{
                 System.out.println(":::::::::::::::::::::::File is failed to move! :"+fileName);
            }

         }catch(Exception e){
                 e.printStackTrace();
         }
    }
    
    
    private void renameFileInDir(String sourceDir) {
        try {

            File srcDir = new File(sourceDir);
            //srcDir.mkdirs(); // make sure that the source directory exists

            for (File sourceFile : srcDir.listFiles()) {
                Path sourcePath = sourceFile.toPath();
                System.out.println(":::::::::::::::::::: Old File Name :"+sourceFile.getName());
                System.out.println(":::::::::::::::::::: New File Name :"+sourceFile.getName().substring(11));
                Files.move(sourcePath, sourcePath.resolveSibling(sourceFile.getName().substring(11)));
            }

        }catch(Exception e){
                e.printStackTrace();
        }
    }
    
    
    private void deleteFileFromFolder(String sourceFolder) {
        try {
            File sourceDir = new File(sourceFolder);
            
            for (File sourceFile : sourceDir.listFiles()) {
                Path sourcePath = sourceFile.toPath();
                Files.deleteIfExists(sourcePath);
            }

        }catch(Exception e){
                e.printStackTrace();
        }
    }
    public void makeFolder(String sourceFolder) {
        try {
            File sourceDir = new File(sourceFolder);
            sourceDir.mkdirs();
            }catch(Exception e){
                e.printStackTrace();
        }
    }
    
    
    private final int BUFFER_SIZE = 4096;
    public void unzip(String zipFilePath, String destDirectory) throws IOException {
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry entry = zipIn.getNextEntry();
        // iterates over entries in the zip file
        while (entry != null) {
            String filePath = destDirectory + File.separator + entry.getName();
            if (!entry.isDirectory()) {
                // if the entry is a file, extracts it
                extractFile(zipIn, filePath);
            } else {
                // if the entry is a directory, make the directory
                File dir = new File(filePath);
                dir.mkdir();
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        
        zipIn.close();
    }
    
    private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }
    
    public void updateControlFile(String fileNameWithPath, String pubEventIdDomain, String inFilePath, String badFilePath, String discardFilePath, String cpDataLoadId,String eventId) {
        try {
            List<String> newLines = new ArrayList<String>();
            for (String line : Files.readAllLines(Paths.get(fileNameWithPath), StandardCharsets.UTF_8)) {
                if (line.contains("INFILE_PATH")) {
                	newLines.add(line.replace("INFILE_PATH", inFilePath));
                }else if(line.contains("BADFILE_PATH")){
                	newLines.add(line.replace("BADFILE_PATH", badFilePath));
                }else if(line.contains("DISCARDFILE_PATH")){
                	newLines.add(line.replace("DISCARDFILE_PATH", discardFilePath));
                }else if(line.contains("cCP_DATA_LOAD_ID")){
                    newLines.add(line.replace("cCP_DATA_LOAD_ID", cpDataLoadId));
                }else if(line.contains("cPUBLISHED_EVENT_ID")){
                    newLines.add(line.replace("cPUBLISHED_EVENT_ID", pubEventIdDomain));
                }
                else {
                    newLines.add(line);
                }
            }
            Files.write(Paths.get(fileNameWithPath), newLines, StandardCharsets.UTF_8);
            
            newLines = new ArrayList<String>();
            for (String line : Files.readAllLines(Paths.get(fileNameWithPath), StandardCharsets.UTF_8)) {
                if (line.contains("AAAAAAAAAA")) {
                	newLines.add(line.replace("AAAAAAAAAA", eventId));
                	
                }
                else {
                    newLines.add(line);
                }
            }
            Files.write(Paths.get(fileNameWithPath), newLines, StandardCharsets.UTF_8);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public String getLineFromFile(int lineNumber, String fileNameWithPath) {
    	String lineText = "";
    	try {
    		
    		int lineCount = 0;
    		
	    	for (String line : Files.readAllLines(Paths.get(fileNameWithPath), StandardCharsets.UTF_8)) {
	    		lineCount++;
	    		if(lineCount == lineNumber) {
	    			lineText = line;
	    		}
	    	}
    	}catch(Exception e) {
            e.printStackTrace();
        }
    	
    	return lineText;
    }
    //Lokesh
    public void moveFiletoProcessFolder(String fileName, String sourceFolder, String destFolder) {
        try{

            File afile =new File(sourceFolder+"/"+fileName);

            if(afile.renameTo(new File(destFolder+"/"+afile.getName()))){
                 System.out.println(":::::::::::::::::::::::File is moved successful! :"+fileName);
            }else{
                 System.out.println(":::::::::::::::::::::::File is failed to move! :"+fileName);
            }

         }catch(Exception e){
                 e.printStackTrace();
         }
    }
    public int getLinesFromFile(String fileNameWithPath) {
    	int noOfLines = 0;
    	try {
    		List<String> lines = Files.readAllLines(Paths.get(fileNameWithPath), Charset.defaultCharset());
    		 noOfLines = lines.size();
	    		
	    	
    	}catch(Exception e) {
            e.printStackTrace();
        }
    	return noOfLines;
 }
    //Lokesh
}