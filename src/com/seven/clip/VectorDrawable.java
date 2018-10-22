package com.seven.clip;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

public class VectorDrawable {
    String viewportWidth;
    String viewportHeight;
    String width;
    String height;
    String xmlns;
    ArrayList<Path> paths;
    String fileName;

    public VectorDrawable(JSONObject jsonObject,String fileName){
        paths = new ArrayList<>();
        this.fileName = fileName.substring(0,fileName.lastIndexOf("."));
        VDUtil util = new VDUtil();
        util.parse(jsonObject,false);
        Map<String,JSONObject> objects = util.getObjects();
        Map<String,JSONArray> arrays = util.getArrays();
        if(fileName.endsWith("svg")){
            for(Map.Entry<String,JSONObject> entry : objects.entrySet()){
                if(entry.getKey().equals("svg")){
                        String[] bounds = entry.getValue().getString("viewBox").split(" ");
                        viewportWidth = bounds[2];
                        viewportHeight = bounds[3];
                        System.out.println("Set viewport to " + viewportWidth + "x" + viewportHeight);
                        width = String.valueOf(entry.getValue().getInt("width"));
                        height = String.valueOf(entry.getValue().getInt("height"));
                        System.out.println("Set dimensions to " + width + "x" + height);
                        break;
                }
            }
            for(Map.Entry<String,JSONArray> entry : arrays.entrySet()){
                if(entry.getKey().equals("path")){
                    JSONArray array = entry.getValue();
                    for(int i=0; i<array.length(); i++){
                        Path path = new Path();
                        path.pathName = "path" + i;
                        path.pathData = array.getJSONObject(i).getString("d");
                        //
                        if(array.getJSONObject(i).has("fill")) path.fillColor = util.rbgToHexString(array.getJSONObject(i).getString("fill"));
                        if(array.getJSONObject(i).has("fill-opacity")) path.fillAlpha = String.valueOf(array.getJSONObject(i).getDouble("fill-opacity"));
                        if(array.getJSONObject(i).has("stroke")) path.strokeColor = util.rbgToHexString(array.getJSONObject(i).getString("stroke"));
                        if(array.getJSONObject(i).has("stroke-width")) path.strokeWidth = String.valueOf(array.getJSONObject(i).getDouble("stroke-width"));
                        if(array.getJSONObject(i).has("stroke-opacity")) path.strokeAlpha = String.valueOf(array.getJSONObject(i).getDouble("stroke-opacity"));
                        paths.add(path);
                    }
                    System.out.println(paths.size() + " paths added.");
                }
            }
        }
        else if(fileName.endsWith("xml")){
            for(Map.Entry<String,JSONObject> entry : objects.entrySet()){
                if(entry.getKey().equals("vector")){
                    viewportWidth = String.valueOf(entry.getValue().getInt("android:viewportWidth"));
                    viewportHeight = String.valueOf(entry.getValue().getInt("android:viewportHeight"));
                    System.out.println("Set viewport to " + viewportWidth + "x" + viewportHeight);
                    width = entry.getValue().getString("android:width");
                    height = entry.getValue().getString("android:height");
                    width = width.substring(0,width.lastIndexOf("dp"));
                    height = height.substring(0,height.lastIndexOf("dp"));
                    System.out.println("Set dimensions to " + width + "x" + height);
                    break;
                }
            }
            for(Map.Entry<String,JSONArray> entry : arrays.entrySet()){
                if(entry.getKey().equals("path")){
                    JSONArray array = entry.getValue();
                    for(int i=0; i<array.length(); i++){
                        Path path = new Path();
                        if(array.getJSONObject(i).has("android:name")) path.pathName = array.getJSONObject(i).getString("android:name");
                        else path.pathName = "path" + i;
                        path.pathData = array.getJSONObject(i).getString("android:pathData");
                        //
                        if(array.getJSONObject(i).has("android:fillColor")) path.fillColor = util.rbgToHexString(array.getJSONObject(i).getString("android:fillColor"));
                        if(array.getJSONObject(i).has("android:fillAlpha")) path.fillAlpha = String.valueOf(array.getJSONObject(i).getDouble("android:fillAlpha"));
                        if(array.getJSONObject(i).has("android:strokeColor")) path.strokeColor = util.rbgToHexString(array.getJSONObject(i).getString("android:strokeColor"));
                        if(array.getJSONObject(i).has("android:strokeWidth")) path.strokeWidth = String.valueOf(array.getJSONObject(i).getDouble("android:strokeWidth"));
                        if(array.getJSONObject(i).has("android:strokeAlpha")) path.strokeAlpha = String.valueOf(array.getJSONObject(i).getDouble("android:strokeAlpha"));
                        paths.add(path);
                    }
                    System.out.println(paths.size() + " paths added.");
                }
            }
        }
        else {
            System.out.println("Invalid File Format");
            return;
        }
        try {
            while (!menu().equals("0"));
        } catch (Exception e){
            System.out.println(e.toString());
        }
    }

    public void printSummary(){
        System.out.println("Vector Drawable Data");
        System.out.println("Viewport: " + viewportWidth + "x" + viewportHeight);
        System.out.println("Dimensions: " + width + "x" + height);
        System.out.println("Paths: " + paths.size());
        if(paths.size()<1)
            return;
        System.out.println("First Path Data:");
        System.out.println("Path Name: " + paths.get(0).pathName);
        System.out.println("Path Fill Color: " + paths.get(0).fillColor);
        System.out.println("Path Stroke Color: " + paths.get(0).strokeColor);
        System.out.println("Path Fill Alpha: " + paths.get(0).fillAlpha);
        System.out.println("Path Stroke Alpha: " + paths.get(0).strokeAlpha);
        System.out.println("Path Stroke Width: " + paths.get(0).strokeWidth);
        System.out.println("Path Data: " + paths.get(0).pathData.substring(1,16));

    }

    public String menu() throws IOException {
        System.out.println("Choose an option");
        System.out.println(Main.indent(1) + "1. Match stroke color with fill color");
        System.out.println(Main.indent(1) + "2. Make animated vector drawable");
        System.out.println(Main.indent(1) + "3. Save as xml file");
        System.out.println(Main.indent(1) + "0. Quit");

        Scanner input = new Scanner(System.in);
        String choice = input.nextLine();
        switch (choice){
            case "1":
                matchStrokeAndFill();
                break;
            case "2":
                new AnimatedVectorDrawable(this);
                break;
            case "3":
                toXML();
                break;
        }
        return choice;

    }

    public void matchStrokeAndFill(){
        for (Path path : paths) path.strokeColor = path.fillColor;
        System.out.println("Done editing stroke color.");
    }

    public void toXML() throws IOException {
        File xmlFile = new File(fileName + ".xml");
        if(xmlFile.createNewFile()){
            System.out.println("Created new file " + fileName + ".xml");
        }
        else{
            System.out.println("File exists. Overwrite? (y/n)");
            Scanner input = new Scanner(System.in);
            String choice = input.nextLine();
            if(!choice.equals("y")){
                return;
            }
        }
        FileWriter writer = new FileWriter(xmlFile);
        writer.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
        writer.write("<vector \n");
        writer.write(Main.indent(1) + "android:width=\"" + width + "dp\" \n");
        writer.write(Main.indent(1) + "android:height=\"" + height + "dp\" \n");
        writer.write(Main.indent(1) + "android:viewportWidth=\"" + viewportWidth + "\" \n");
        writer.write(Main.indent(1) + "android:viewportHeight=\"" + viewportHeight + "\" \n");
        writer.write(Main.indent(1) + "xmlns:android=\"http://schemas.android.com/apk/res/android\">");
        writer.write("\n");

        for(int i=0; i<paths.size(); i++){
            writer.write(Main.indent(1) + "<path" + "\n");
            writer.write(Main.indent(2) + "android:name=\"" + paths.get(i).pathName + "\"\n");
            writer.write(Main.indent(2) + "android:pathData=\"" + paths.get(i).pathData + "\"\n");
            writer.write(Main.indent(2) + "android:fillColor=\"" + paths.get(i).fillColor + "\"\n");
            writer.write(Main.indent(2) + "android:strokeColor=\"" + paths.get(i).strokeColor + "\"\n");
            writer.write(Main.indent(2) + "android:strokeWidth=\"" + paths.get(i).strokeWidth + "\"\n");
            writer.write(Main.indent(2) + "android:strokeAlpha=\"" + paths.get(i).strokeAlpha + "\"\n");
            writer.write(Main.indent(2) + "android:fillAlpha=\"" + paths.get(i).fillAlpha + "\"\n");
            writer.write(Main.indent(2) + "/>" + "\n");
        }
        writer.write("</vector> \n");
        writer.close();
    }
}
