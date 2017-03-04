package com.swyam.fisiomer;

import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Reyes Yam on 22/02/2017.
 */
public class Helpers {

    public static String Upper(String str){
        return str.toUpperCase();
    }

    public static String Lower(String str){
        return str.toLowerCase();
    }

    public static String Capitalize(String str){
        String tokens[] = str.trim().split(" ");
        String strCap="";
        for(String s:tokens){
            if(s.length()>1)
                strCap+=( Upper(s.charAt(0)+"")+s.substring(1) );
            else
                strCap+=Upper(s);

            strCap+=" ";
        }
        return strCap;
    }

    public static String MD5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes("UTF-8"));
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static final int getColor2(Context context, int id) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 23) {
            return ContextCompat.getColor(context, id);
        } else {
            return context.getResources().getColor(id);
        }
    }
}

