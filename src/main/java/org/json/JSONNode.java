package org.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JSONNode {

    JSONObject selfIfJSONObject;
    JSONArray selfIfJSONArray;
    Object selfIfObject;
    Map<String, JSONNode> childMaps;
    List<JSONNode> childLists;

    public JSONNode(Object jsonInput) {
        try {
            selfIfJSONArray = new JSONArray(jsonInput);
            childLists = new ArrayList<>();
            for(int i =0;i<selfIfJSONArray.length();i++) {
                JSONNode childNode = new JSONNode(selfIfJSONArray.get(i));
                childLists.add(childNode);
            }
        } catch (JSONException e1) {
            try {
                selfIfJSONObject = new JSONObject(jsonInput);
                childMaps = new HashMap<>();
                for(Map.Entry<String,Object> entry:selfIfJSONObject.entrySet()) {
                    childMaps.put(entry.getKey(), new JSONNode(entry.getValue()));
                }
            } catch (JSONException e2) {
                selfIfObject=jsonInput;
            }
        }
    }

}
