package com.smartbear.ready.plugin.jira.clients;

import com.atlassian.jira.rest.client.api.domain.CimProject;
import com.atlassian.jira.rest.client.internal.json.CimProjectJsonParser;
import com.atlassian.jira.rest.client.internal.json.GenericJsonArrayParser;
import com.atlassian.jira.rest.client.internal.json.JsonObjectParser;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateIssueMetadataJsonParserServer implements JsonObjectParser<Iterable<CimProject>> {
    private Logger logger = LoggerFactory.getLogger(CreateIssueMetadataJsonParserServer.class);
    private final GenericJsonArrayParser<CimProject> projectsParser = new GenericJsonArrayParser(new CimProjectJsonParser());

    public CreateIssueMetadataJsonParserServer() {
    }

    public Iterable<CimProject> parse(JSONObject json) throws JSONException {
        logger.info("[CreateIssueMetadataJsonParserExt].[parse] json: {}", json.toString());
        return this.projectsParser.parse(json.getJSONArray("projects"));
    }
}
