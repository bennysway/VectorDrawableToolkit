package com.seven.clip;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        String filePath;
        String fileData;
        JSONObject jsonObject;
        //Testing
        Scanner input = new Scanner(System.in);
        System.out.println("Enter Path:");
        filePath = input.nextLine();
        try{
            File file = new File(filePath);
            String fileName = file.getName();
            int fileType = checkFileType(file);
            if(fileType == -1){
                throw new InvalidFileExtensionException();
            }
            fileData = FileUtils.readFileToString(new File(filePath));
            System.out.println("Snippet of file");
            System.out.println(fileData.substring(0,32));
            jsonObject= XML.toJSONObject(fileData);
            System.out.println(jsonObject.toString(1));
            switch (fileType){
                case 1:
                    System.out.println("Svg detected");
                    new VectorDrawable(jsonObject,fileName);

                    break;
                case 2:
                    System.out.println("Vector Drawable detected");
                    new VectorDrawable(jsonObject,fileName);
            }


        }
        catch (Exception e){
            System.out.println(e);
        }
    }

    public static class InvalidFileExtensionException extends Exception {
        public InvalidFileExtensionException() {
            super("Invalid or unsupported file extension");
        }
    }
    
    public static String indent(int tabs){
        StringBuilder s = new StringBuilder("");
        for(int i=0; i<tabs; i++)
            s.append("\t");
        return s.toString();
    }

    public static int checkFileType(File file) throws FileNotFoundException {
        if(file.getName().endsWith("svg")){
            return 1;
        }
        else if(file.getName().endsWith("xml")){
            LineIterator it = IOUtils.lineIterator(
                    new BufferedReader(new FileReader(file.getAbsoluteFile())));
            for (int lineNumber = 0; it.hasNext(); lineNumber++) {
                String line = (String) it.next();
                if (line.contains("animated-vector")) {
                    return 3;
                }
                else if(line.contains("vector"))
                    return 2;
                else if(lineNumber > 4)
                    return -1;
            }
        }
        return -1;
    }



}
