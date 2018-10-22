package com.seven.clip;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class AnimatedVectorDrawable {

    class Target{
        class ObjectAnimator{
            String propertyName;
            int startOffset;
            int duration;
            float valueFrom;
            float valueTo;
            String valueType;


            public ObjectAnimator(String propertyName, int startOffset, int duration, float valueFrom, float valueTo, String valueType) {
                this.propertyName = propertyName;
                this.startOffset = startOffset;
                this.duration = duration;
                this.valueFrom = valueFrom;
                this.valueTo = valueTo;
                this.valueType = valueType;
            }
        }

        public Target(){
            objectAnimators = new ArrayList<>();
        }
        public ObjectAnimator createAnimator(String propertyName, int startOffset, int duration, float valueFrom, float valueTo, String valueType){
            return new ObjectAnimator(propertyName, startOffset, duration, valueFrom, valueTo, valueType);
        }

        String pathName;
        ArrayList<ObjectAnimator> objectAnimators;
    }

    private VectorDrawable vectorDrawable;
    private ArrayList<Target> targets;


    public AnimatedVectorDrawable(VectorDrawable vectorDrawable) {
        this.vectorDrawable = vectorDrawable;
        this.targets = new ArrayList<>();
        try {
            while (!menu().equals("0"));
        } catch (Exception e){
            System.out.println(e.toString());
        }
    }

    public String menu(){
        System.out.println("Animated vector for " + vectorDrawable.fileName);
        System.out.println(Main.indent(1) + "1. Choose template animation");
        System.out.println(Main.indent(1) + "2. Create Custom Animation");
        System.out.println(Main.indent(1) + "3. Save Animation to file");
        System.out.println(Main.indent(1) + "0. Quit");
        String choice = (new Scanner(System.in)).nextLine();
        switch (choice) {
            case "1":
                templatePicker();
                break;
            case "3":
                try {
                    toXML();
                } catch (IOException e) {
                    System.out.println(e.toString());
                }
                break;
        }
        return choice;
    }

    private void templatePicker(){
        System.out.println("Pick a template");
        System.out.println(Main.indent(1) + "1. Fade in");
        System.out.println(Main.indent(1) + "2. Fly in");
        System.out.println(Main.indent(1) + "3. Grow");
        System.out.println(Main.indent(1) + "0. Quit");
        String choice = (new Scanner(System.in)).nextLine();
        switch (choice) {
            case "1":
                fadeInTemplate();
                break;
        }

    }

    private void fadeInTemplate(){
        int currentTime;
        System.out.println("Choose order");
        System.out.println(Main.indent(1) + "1. Ascending");
        System.out.println(Main.indent(1) + "2. Descending");
        System.out.println(Main.indent(1) + "3. Random");
        System.out.println(Main.indent(1) + "0. Quit");
        int order = (new Scanner(System.in)).nextInt();
        System.out.println("Duration (ms):");
        int duration = (new Scanner(System.in)).nextInt();
        System.out.println("Start offset (ms):");
        int offset = (new Scanner(System.in)).nextInt();
        System.out.println("Apply? (y/n)");
        String choice = (new Scanner(System.in)).nextLine();
        if(choice.equals("y")){
            currentTime = offset;
            if(currentTime == 0){
                for(int i =0; i<vectorDrawable.paths.size(); i++){
                    vectorDrawable.paths.get(i).strokeAlpha = "0";
                    vectorDrawable.paths.get(i).fillAlpha = "0";
                }
            }
            switch (order){
                case 1:
                    for(int i=0;i<vectorDrawable.paths.size();i++){
                        Target target = new Target();
                        target.pathName = vectorDrawable.paths.get(i).pathName;
                        Target.ObjectAnimator fill = target.createAnimator("fillAlpha",currentTime,duration,0,1,"floatType");
                        Target.ObjectAnimator stroke = target.createAnimator("strokeAlpha",currentTime,duration,0,1,"floatType");
                        target.objectAnimators.add(fill);
                        target.objectAnimators.add(stroke);
                        currentTime += duration;
                        targets.add(target);
                    }
                    break;
                case 2:
                    for(int i=vectorDrawable.paths.size()-1;i>-1;i--){
                        Target target = new Target();
                        target.pathName = vectorDrawable.paths.get(i).pathName;
                        Target.ObjectAnimator fill = target.createAnimator("fillAlpha",currentTime,duration,0,1,"floatType");
                        Target.ObjectAnimator stroke = target.createAnimator("strokeAlpha",currentTime,duration,0,1,"floatType");
                        target.objectAnimators.add(fill);
                        target.objectAnimators.add(stroke);
                        currentTime += duration;
                        targets.add(target);
                    }
                    break;
                case 3:
                    ArrayList<Integer> indeces = new ArrayList<>();
                    for(int i=0;i<vectorDrawable.paths.size();i++)
                        indeces.add(i);
                    Collections.shuffle(indeces);
                    for(int i=0;i<vectorDrawable.paths.size();i++){
                        Target target = new Target();
                        target.pathName = vectorDrawable.paths.get(indeces.get(i)).pathName;
                        Target.ObjectAnimator fill = target.createAnimator("fillAlpha",currentTime,duration,0,1,"floatType");
                        Target.ObjectAnimator stroke = target.createAnimator("strokeAlpha",currentTime,duration,0,1,"floatType");
                        target.objectAnimators.add(fill);
                        target.objectAnimators.add(stroke);
                        currentTime += duration;
                        targets.add(target);
                    }
                    break;
            }
        }
    }

    public void toXML() throws IOException {
        File xmlFile = new File("animated_" + vectorDrawable.fileName + ".xml");
        if(xmlFile.createNewFile()){
            System.out.println("Created new file animated_" + vectorDrawable.fileName + ".xml");
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
        writer.write("<animated-vector \n");
        writer.write(Main.indent(1) + "xmlns:android=\"http://schemas.android.com/apk/res/android\"\n");
        writer.write(Main.indent(1) + "xmlns:aapt=\"http://schemas.android.com/aapt\">\n");
        writer.write(Main.indent(1) + "<aapt:attr name=\"android:drawable\">\n");


        writer.write(Main.indent(2) + "<vector \n");
        writer.write(Main.indent(3) + "android:width=\"" + vectorDrawable.width + "dp\" \n");
        writer.write(Main.indent(3) + "android:height=\"" + vectorDrawable.height + "dp\" \n");
        writer.write(Main.indent(3) + "android:viewportWidth=\"" + vectorDrawable.viewportWidth + "\" \n");
        writer.write(Main.indent(3) + "android:viewportHeight=\"" + vectorDrawable.viewportHeight + "\" \n");
        writer.write(Main.indent(3) + "xmlns:android=\"http://schemas.android.com/apk/res/android\">");
        writer.write("\n");


        for(Path path : vectorDrawable.paths){
            writer.write(Main.indent(3) + "<path" + "\n");
            writer.write(Main.indent(4) + "android:name=\"" + path.pathName + "\"\n");
            writer.write(Main.indent(4) + "android:pathData=\"" + path.pathData + "\"\n");
            writer.write(Main.indent(4) + "android:fillColor=\"" + path.fillColor + "\"\n");
            writer.write(Main.indent(4) + "android:strokeColor=\"" + path.strokeColor + "\"\n");
            writer.write(Main.indent(4) + "android:strokeWidth=\"" + path.strokeWidth + "\"\n");
            writer.write(Main.indent(4) + "android:strokeAlpha=\"" + path.strokeAlpha + "\"\n");
            writer.write(Main.indent(4) + "android:fillAlpha=\"" + path.fillAlpha + "\"\n");
            writer.write(Main.indent(4) + "/>" + "\n");
        }
        writer.write(Main.indent(2) + "</vector> \n");
        writer.write(Main.indent(1) + "</aapt:attr>\n");

        for (Target target : targets) {
            writer.write(Main.indent(1) + "<target android:name=\"" + target.pathName + "\">\n");
            writer.write(Main.indent(2) + "<aapt:attr name=\"android:animation\">\n");
            writer.write(Main.indent(3) + "<set>\n");
            for (Target.ObjectAnimator objectAnimator : target.objectAnimators) {
                writer.write(Main.indent(4) + "<objectAnimator\n");
                writer.write(Main.indent(5) + "android:propertyName=\"" + objectAnimator.propertyName +"\"\n");
                writer.write(Main.indent(5) + "android:startOffset=\"" + objectAnimator.startOffset +"\"\n");
                writer.write(Main.indent(5) + "android:duration=\"" + objectAnimator.duration +"\"\n");
                writer.write(Main.indent(5) + "android:valueFrom=\"" + objectAnimator.valueFrom +"\"\n");
                writer.write(Main.indent(5) + "android:valueTo=\"" + objectAnimator.valueTo +"\"\n");
                writer.write(Main.indent(5) + "android:valueType=\"" + objectAnimator.valueType +"\"/>\n");
            }
            writer.write(Main.indent(3) + "</set>\n");
            writer.write(Main.indent(2) + "</aapt:attr>\n");
            writer.write(Main.indent(1) + "</target>\n");
        }
        writer.write("</animated-vector>");
        writer.close();
    }

}
