package com.smartbear.ready.plugin.jira.parsers;

import com.atlassian.jira.rest.client.api.domain.BasicProject;
import com.atlassian.jira.rest.client.api.domain.CimIssueType;
import com.atlassian.jira.rest.client.api.domain.CimProject;
import com.atlassian.jira.rest.client.internal.json.CimIssueTypeJsonParser;
import com.atlassian.jira.rest.client.internal.json.CimProjectJsonParser;
import com.atlassian.jira.rest.client.internal.json.GenericJsonArrayParser;
import com.atlassian.jira.rest.client.internal.json.JsonArrayParser;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CimProjectServerJsonParser extends CimProjectJsonParser {
    private final JsonArrayParser<Iterable<CimIssueType>> issueTypesParser = GenericJsonArrayParser.create(new CimIssueTypeJsonParser());
    private final BasicProjectServerJsonParser basicProjectJsonParser = new BasicProjectServerJsonParser();
    public CimProjectServerJsonParser() {}

    @Override
    public CimProject parse(JSONObject json) throws JSONException {
        BasicProject basicProject = this.basicProjectJsonParser.parse(json);
        JSONArray projectsArray = json.optJSONArray("projects");
        if (projectsArray.length() > 0) {
            JSONObject issueTypesJsonObject = (JSONObject) projectsArray.get(0);
            JSONArray issueTypesArray = issueTypesJsonObject.optJSONArray("issuetypes");

            Iterable<CimIssueType> issueTypes = issueTypesArray != null ? this.issueTypesParser.parse(issueTypesArray) : Collections.emptyList();
            Map<String, URI> avatarUris = new HashMap<>();
            return new CimProject(basicProject.getSelf(), basicProject.getKey(), basicProject.getId(), basicProject.getName(), avatarUris, issueTypes);
        }
        return new CimProject(basicProject.getSelf(), basicProject.getKey(), basicProject.getId(), basicProject.getName(), new HashMap<>(), Collections.emptyList());
    }
}
