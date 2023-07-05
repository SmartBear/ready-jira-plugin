package com.smartbear.ready.plugin.jira.parsers;

import com.atlassian.jira.rest.client.api.domain.CimProject;
import com.atlassian.jira.rest.client.internal.json.CreateIssueMetadataJsonParser;
import com.atlassian.jira.rest.client.internal.json.GenericJsonArrayParser;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class CreateIssueMetadataJsonParserExt extends CreateIssueMetadataJsonParser {

    private final GenericJsonArrayParser<CimProject> projectsParserExt = new GenericJsonArrayParser<>(new CimProjectJsonParserExt());

    public CreateIssueMetadataJsonParserExt() {
    }

    public Iterable<CimProject> parse(JSONObject json) throws JSONException {
        return this.projectsParserExt.parse(json.getJSONArray("projects"));
    }

}
