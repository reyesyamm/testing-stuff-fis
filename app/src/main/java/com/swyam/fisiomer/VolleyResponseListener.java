package com.swyam.fisiomer;

import com.android.volley.VolleyError;

import org.json.JSONObject;

/**
 * Created by Reyes Yam on 05/03/2017.
 */

public interface VolleyResponseListener {
    void onError(VolleyError error);
    void onResponse(JSONObject response);
}
