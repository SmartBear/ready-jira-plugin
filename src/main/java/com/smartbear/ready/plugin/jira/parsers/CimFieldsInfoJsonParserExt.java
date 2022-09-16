package com.smartbear.ready.plugin.jira.parsers;

import com.atlassian.jira.rest.client.api.domain.CimFieldInfo;
import com.atlassian.jira.rest.client.internal.json.CimFieldsInfoJsonParser;
import com.atlassian.jira.rest.client.internal.json.JsonParseUtil;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class CimFieldsInfoJsonParserExt extends CimFieldsInfoJsonParser {
    public CimFieldsInfoJsonParserExt() {
    }

    @Override
    public CimFieldInfo parse(JSONObject json) throws JSONException {
        String id = JsonParseUtil.getOptionalString(json, "fieldId");
        return super.parse(json, id);
    }
}
