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
        URI selfUri = null;
        try {
            selfUri = new URI("https://localhost:8080/rest/api/2/project/10000");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        String key = "VINH";
        Long id = JsonParseUtil.getOptionalLong(json, "id") == null ? 10000l : JsonParseUtil.getOptionalLong(json, "id");
        String name = JsonParseUtil.getOptionalString(json, "name") == null ? "VINH" : JsonParseUtil.getOptionalString(json, "name");
        return new BasicProject(selfUri, key, id, name);
    }
}
