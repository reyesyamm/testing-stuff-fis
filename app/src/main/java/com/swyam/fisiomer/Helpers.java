package com.swyam.fisiomer;

import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * Created by Reyes Yam on 22/02/2017.
 */
public class Helpers {

    public static final String SEPARATOR_STRING_SER = "##-##";



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
            String result;
            MessageDigest md = MessageDigest.getInstance("MD5"); // or "SHA-1"
            md.update(s.getBytes("UTF-8"));
            BigInteger hash = new BigInteger(1, md.digest());
            result = hash.toString(16);
            while (result.length() < 32) { // 40 for SHA-1
                result = "0" + result;
            }
            return result;
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

    public static String SerializarLista(ArrayList<String> lista){
        String retorno="";
        if(lista.size()>0){
            retorno = lista.get(0);
            if(lista.size()>1){
                for(String str:lista){
                    retorno+=(SEPARATOR_STRING_SER+str);
                }
            }
        }
        return retorno;
    }
}

