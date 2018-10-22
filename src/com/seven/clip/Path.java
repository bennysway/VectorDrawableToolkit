package com.seven.clip;

class Path {
    String pathName;
    String pathData;
    String fillColor;
    String fillAlpha;
    String strokeColor;
    String strokeAlpha;
    String strokeWidth;

    Path(){
        pathName = "path";
        pathData = "";
        fillColor = "#000000";
        fillAlpha = "1.0";
        strokeColor = "#000000";
        strokeAlpha = "1.0";
        strokeWidth = "1";
    }

    // extras
    /*public double getDistanceFromOrigin(){
        double dist = 0;
        if(!pathData.equals("")){
            String buff = pathData.substring(0,32);
            String[] buffList = buff.split(" ");
            float x,y;
            x = Float.valueOf(buffList[1]);
            y = Float.valueOf(buffList[2]);
            dist = Math.sqrt(x*x + y*y);
        }
        return dist;
    }*/

}
