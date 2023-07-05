package com.smartbear.ready.plugin.jira.parsers;

import com.atlassian.jira.rest.client.api.domain.BasicProject;
import com.atlassian.jira.rest.client.api.domain.CimIssueType;
import com.atlassian.jira.rest.client.api.domain.CimProject;
import com.atlassian.jira.rest.client.internal.json.BasicProjectJsonParser;
import com.atlassian.jira.rest.client.internal.json.CimProjectJsonParser;
import com.atlassian.jira.rest.client.internal.json.GenericJsonArrayParser;
import com.atlassian.jira.rest.client.internal.json.JsonArrayParser;
import com.atlassian.jira.rest.client.internal.json.JsonParseUtil;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

public class CimProjectJsonParserExt extends CimProjectJsonParser {

    private final JsonArrayParser<Iterable<CimIssueType>> issueTypesParserExt = GenericJsonArrayParser.create(new CimIssueTypeJsonParserExt());
    private final BasicProjectJsonParser basicProjectJsonParser = new BasicProjectJsonParser();

    public CimProjectJsonParserExt() {
    }

    public CimProject parse(JSONObject json) throws JSONException {
        BasicProject basicProject = this.basicProjectJsonParser.parse(json);
        JSONArray issueTypesArray = json.optJSONArray("issuetypes");
        Iterable<CimIssueType> issueTypes = issueTypesArray != null ? (Iterable)this.issueTypesParserExt.parse(issueTypesArray) : Collections.emptyList();
        Map<String, URI> avatarUris = JsonParseUtil.getAvatarUris(json.getJSONObject("avatarUrls"));
        return new CimProject(basicProject.getSelf(), basicProject.getKey(), basicProject.getId(), basicProject.getName(), avatarUris, issueTypes);
    }

}
