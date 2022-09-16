package com.smartbear.ready.plugin.jira.parsers;

import com.atlassian.jira.rest.client.api.domain.BasicProject;
import com.atlassian.jira.rest.client.internal.json.JsonObjectParser;
import com.atlassian.jira.rest.client.internal.json.JsonParseUtil;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

public class BasicProjectServerJsonParser implements JsonObjectParser<BasicProject> {
    public BasicProjectServerJsonParser() {
    }

    public BasicProject parse(JSONObject json) throws JSONException {
        URI selfUri = (URI) json.get("self");
        String key = json.getString("key");
        Long id = JsonParseUtil.getOptionalLong(json, "id");
        String name = JsonParseUtil.getOptionalString(json, "name") == null ? key : JsonParseUtil.getOptionalString(json, "name");
        return new BasicProject(selfUri, key, id, name);
    }
}
