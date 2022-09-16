package com.smartbear.ready.plugin.jira.clients;

import com.atlassian.jira.rest.client.api.domain.CimProject;
import com.atlassian.jira.rest.client.internal.json.GenericJsonArrayParser;
import com.atlassian.jira.rest.client.internal.json.JsonObjectParser;
import com.smartbear.ready.plugin.jira.parsers.CimProjectServerJsonParser;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class CreateIssueMetadataJsonParserServer implements JsonObjectParser<Iterable<CimProject>> {
    private Logger logger = LoggerFactory.getLogger(CreateIssueMetadataJsonParserServer.class);
    private Map<String, Object> extraFields;
    private final GenericJsonArrayParser<CimProject> projectsParser = new GenericJsonArrayParser(new CimProjectServerJsonParser());

    public CreateIssueMetadataJsonParserServer() {
        extraFields = new HashMap<>();
    }

    public Iterable<CimProject> parse(JSONObject json) throws JSONException {
        logger.info("[CreateIssueMetadataJsonParserExt].[parse] json: {}", json.toString());
        JSONArray jsonArray = new JSONArray();
        json.put("key", extraFields.get("key"));
        json.put("self", extraFields.get("self"));
        jsonArray.put(json);
        JSONObject result = new JSONObject();
        result.put("projects", jsonArray);
        return this.projectsParser.parse(result.getJSONArray("projects"));
    }

    public void setExtraFields(Map<String, Object> extraFields) {
        this.extraFields = extraFields;
    }
}
