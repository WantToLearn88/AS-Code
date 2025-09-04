package app.ascode;

import android.webkit.JavascriptInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Set;

import arman.common.infocodes.InfoCode;

public class JsonDataHelper {

    WebAppInterface webAppInterface;

    public JsonDataHelper(WebAppInterface webAppInterface){
        this.webAppInterface = webAppInterface;
    }

    public void init(){

    }


    String[] parseStringArray(String stringArray) {
        String[] array = null;
        try {
            JSONArray jsonArray = new JSONArray(stringArray);
            int len = jsonArray.length();
            array = new String[len];
            for (int i = 0; i < len; i++) {
                array[i] = jsonArray.getString(i);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();

        }
        return array;
    }

    int[] parseIntArray(String intArray) {
        int[] array = null;
        try {
            JSONArray jsonArray = new JSONArray(intArray);
            int len = jsonArray.length();
            array = new int[len];
            for (int i = 0; i < len; i++) {
                array[i] = jsonArray.getInt(i);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();

        }
        return array;
    }

    String encodeStringArray(String[] strings) {
        JSONArray jsonArray = new JSONArray();
        for (String string : strings) {
            jsonArray.put(string);
        }
        return jsonArray.toString();
    }

    String encodeStringArray(Set<String> strings) {
        JSONArray jsonArray = new JSONArray();
        for (String string : strings) {
            jsonArray.put(string);
        }
        return jsonArray.toString();
    }

    String encodeStringArray(ArrayList<String> strings) {
        JSONArray jsonArray = new JSONArray();
        for (String string : strings) {
            jsonArray.put(string);
        }
        return jsonArray.toString();
    }


    String encodeIntArray(int[] integers) {
        JSONArray jsonArray = new JSONArray();
        for (int integer : integers) {
            jsonArray.put(integer);
        }
        return jsonArray.toString();
    }

}

















