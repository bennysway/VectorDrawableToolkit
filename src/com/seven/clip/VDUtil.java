package com.seven.clip;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VDUtil {

    VDUtil(){
        jsonArrays = new HashMap<>();
        jsonObjects = new HashMap<>();
    }
    public Map<String,JSONObject> getObjects(){
        return jsonObjects;
    }
    public Map<String,JSONArray> getArrays(){
        return jsonArrays;
    }
    public void parse(JSONObject object,boolean debug){
        this.debug = debug;
        findKeysOfJsonObject(object,new ArrayList<>());
    }

    private Map<String,JSONObject> jsonObjects;
    private Map<String,JSONArray> jsonArrays;
    private boolean debug;



    private List<String> findKeysOfJsonObject(JSONObject jsonIn, List<String> keys) {

        Iterator<String> itr = jsonIn.keys();
        List<String> keysFromObj = makeList(itr);
        keys.addAll(keysFromObj);

        itr = jsonIn.keys();
        while (itr.hasNext()) {
            String itrStr = itr.next();
            JSONObject jsout = null;
            JSONArray jsArr = null;
            if (jsonIn.get(itrStr).getClass() == JSONObject.class) {
                jsout = jsonIn.getJSONObject(itrStr);
                jsonObjects.put(itrStr,jsout);
                if(debug)
                    System.out.println(itrStr);
                findKeysOfJsonObject(jsout, keys);
            } else if (jsonIn.get(itrStr).getClass() == JSONArray.class) {
                jsArr = jsonIn.getJSONArray(itrStr);
                if(jsonArrays.containsKey(itrStr)){
                    for(int i=0; i<jsArr.length(); i++)
                        jsonArrays.get(itrStr).put(jsArr.getJSONObject(i));
                }
                else {
                    jsonArrays.put(itrStr,jsArr);
                }
                if(debug)
                    System.out.println("[" + itrStr + "]");
                keys.addAll(findKeysOfJsonArray(jsArr, keys));
            } else if (jsonIn.get(itrStr).getClass() == String.class) {
                // meh
            }
        }
        return keys;
    }
    private List<String> findKeysOfJsonArray(JSONArray jsonIn, List<String> keys) {
        List<String> keysFromArr = new ArrayList<>();

        if (jsonIn != null && jsonIn.length() != 0) {
            for (int i = 0; i < jsonIn.length(); i++) {
                JSONObject jsonObjIn = jsonIn.getJSONObject(i);
                keysFromArr = findKeysOfJsonObject(jsonObjIn, keys);
            }
        }

        return keysFromArr;
    }
    public List<String> makeList(Iterator<String> iter) {
        List<String> list = new ArrayList<String>();
        while (iter.hasNext()) {
            list.add(iter.next());
        }
        return list;
    }

    public String rbgToHexString(String rgb){
        StringBuilder hex = new StringBuilder("#");
        Pattern p = Pattern.compile("-?\\d+");
        Matcher m = p.matcher(rgb);
        while (m.find()) {
            String code = Integer.toHexString(Integer.valueOf(m.group()));
            if(code.length()==1) code = "0" + code;
            hex.append(code);
        }
        return hex.toString();
    }
}
