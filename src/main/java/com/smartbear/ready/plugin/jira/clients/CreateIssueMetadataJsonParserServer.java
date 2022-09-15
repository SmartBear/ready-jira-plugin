package com.smartbear.ready.plugin.jira.clients;

import com.atlassian.jira.rest.client.api.GetCreateIssueMetadataOptions;
import com.atlassian.jira.rest.client.api.domain.CimProject;
import com.atlassian.jira.rest.client.internal.json.GenericJsonArrayParser;
import com.atlassian.jira.rest.client.internal.json.JsonObjectParser;
import com.smartbear.ready.plugin.jira.parsers.CimProjectServerJsonParser;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateIssueMetadataJsonParserServer implements JsonObjectParser<Iterable<CimProject>> {
    private Logger logger = LoggerFactory.getLogger(CreateIssueMetadataJsonParserServer.class);
    private GetCreateIssueMetadataOptions options;
    private final GenericJsonArrayParser<CimProject> projectsParser = new GenericJsonArrayParser(new CimProjectServerJsonParser());

    public CreateIssueMetadataJsonParserServer() {
    }

    public Iterable<CimProject> parse(JSONObject json) throws JSONException {
        logger.info("[CreateIssueMetadataJsonParserExt].[parse] json: {}", json.toString());
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(json);
        JSONObject result = new JSONObject();
        result.put("projects", jsonArray);
        result.put("projectKey", options.projectKeys);
        return this.projectsParser.parse(result.getJSONArray("projects"));
    }

    public GetCreateIssueMetadataOptions getOptions() {
        return options;
    }

    public void setOptions(GetCreateIssueMetadataOptions options) {
        this.options = options;
    }
}
