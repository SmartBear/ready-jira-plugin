package com.smartbear.ready.plugin.jira.parsers;

import com.atlassian.jira.rest.client.api.domain.CimFieldInfo;
import com.atlassian.jira.rest.client.internal.json.CimFieldsInfoMapJsonParser;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.glassfish.jersey.internal.guava.Maps;

import java.util.Iterator;
import java.util.Map;

public class CimFieldsInfoMapJsonParserExt extends CimFieldsInfoMapJsonParser {

    private final CimFieldsInfoJsonParserExt cimFieldsInfoJsonParserExt = new CimFieldsInfoJsonParserExt();

    public CimFieldsInfoMapJsonParserExt(){}

    public Map<String, CimFieldInfo> parse(JSONObject json) throws JSONException {
        Map<String, CimFieldInfo> res = Maps.newHashMapWithExpectedSize(json.length());
        Iterator keysIterator = json.keys();

        while (keysIterator.hasNext()) {
            String id = (String)keysIterator.next();
            res.put(id, this.cimFieldsInfoJsonParserExt.parse(json.getJSONObject(id), id));
        }
        return res;
    }

}
