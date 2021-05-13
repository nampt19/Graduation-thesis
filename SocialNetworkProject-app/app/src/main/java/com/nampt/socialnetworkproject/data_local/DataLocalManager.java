package com.nampt.socialnetworkproject.data_local;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.nampt.socialnetworkproject.model.MessActivityModel;
import com.nampt.socialnetworkproject.model.Post;
import com.nampt.socialnetworkproject.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DataLocalManager {
    private static final String PREF_FIRST_INSTALL = "MY_FIRST_INSTALL";
    private static final String PREF_MESSAGE_ACTIVITY_INSTALL = "PREF_MESSAGE_ACTIVITY_INSTALL";
    private static final String PREF_OBJECT_USER = "MY_INFO_USER";
    private static final String PREF_LIST_POST = "PREF_LIST_POST";
    private static DataLocalManager instance;
    private MySharedPreferences mySharedPreferences;

    public static void init(Context context) {
        instance = new DataLocalManager();
        instance.mySharedPreferences = new MySharedPreferences(context);
    }

    public static DataLocalManager getInstance() {
        if (instance == null) {
            instance = new DataLocalManager();
        }
        return instance;
    }

    public static void setMyFirstInstall(boolean isFirst) {
        DataLocalManager.getInstance().mySharedPreferences.putBooleanValue(PREF_FIRST_INSTALL, isFirst);
    }

    public static boolean getMyFirstInstall() {
        return DataLocalManager.getInstance().mySharedPreferences.getBooleanValue(PREF_FIRST_INSTALL);
    }


    public static void setMessageActivityInstall(MessActivityModel messActivity) {
        Gson gson = new GsonBuilder().setDateFormat("yyyyy-MM-dd HH:mm:ss").create();
        String jsonString = gson.toJson(messActivity);
        DataLocalManager.getInstance().mySharedPreferences.putStringValue(PREF_MESSAGE_ACTIVITY_INSTALL, jsonString);
    }

    public static MessActivityModel getMessageActivityInstall() {
        String jsonString = DataLocalManager.getInstance().mySharedPreferences.getStringValue(PREF_MESSAGE_ACTIVITY_INSTALL);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        return gson.fromJson(jsonString, MessActivityModel.class);
    }

    public static void setPrefUser(User user) {
        Gson gson = new GsonBuilder().setDateFormat("yyyyy-MM-dd HH:mm:ss").create();
        String jsonString = gson.toJson(user);
        DataLocalManager.getInstance().mySharedPreferences.putStringValue(PREF_OBJECT_USER, jsonString);
    }

    public static User getPrefUser() {
        String jsonString = DataLocalManager.getInstance().mySharedPreferences.getStringValue(PREF_OBJECT_USER);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        return gson.fromJson(jsonString, User.class);
    }
    public static void setPrefListPost(List<Post> postList){
        Gson gson = new GsonBuilder().setDateFormat("yyyyy-MM-dd HH:mm:ss").create();
        JsonArray jsonArray=gson.toJsonTree(postList).getAsJsonArray();
        String jsonString = jsonArray.toString();
        DataLocalManager.getInstance().mySharedPreferences.putStringValue(PREF_LIST_POST,jsonString);
    }

    public static List<Post> getPrefPostList(){
        String strJsonArray = DataLocalManager.getInstance().mySharedPreferences.getStringValue(PREF_LIST_POST);
        List<Post> posts=new ArrayList<>();
        try{
            JSONArray jsonArray = new JSONArray(strJsonArray);
            JSONObject jsonObject;
            Post post;
            Gson gson = new GsonBuilder().setDateFormat("yyyyy-MM-dd HH:mm:ss").create();
            for (int i=0;i<jsonArray.length();i++){
                jsonObject=jsonArray.getJSONObject(i);
                post =gson.fromJson(jsonObject.toString(),Post.class);
                posts.add(post);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

        return posts;
    }

}
