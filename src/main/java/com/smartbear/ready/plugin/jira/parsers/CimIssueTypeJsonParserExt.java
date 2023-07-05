package com.smartbear.ready.plugin.jira.parsers;

import com.atlassian.jira.rest.client.api.domain.CimFieldInfo;
import com.atlassian.jira.rest.client.api.domain.CimIssueType;
import com.atlassian.jira.rest.client.api.domain.IssueType;
import com.atlassian.jira.rest.client.internal.json.CimFieldsInfoMapJsonParser;
import com.atlassian.jira.rest.client.internal.json.CimIssueTypeJsonParser;
import com.atlassian.jira.rest.client.internal.json.IssueTypeJsonParser;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.Collections;
import java.util.Map;

public class CimIssueTypeJsonParserExt extends CimIssueTypeJsonParser {

    private final IssueTypeJsonParser issueTypeJsonParser = new IssueTypeJsonParser();
    private final CimFieldsInfoMapJsonParser fieldsParser = new CimFieldsInfoMapJsonParserExt();

    public CimIssueTypeJsonParserExt() {
    }

    public CimIssueType parse(JSONObject json) throws JSONException {
        IssueType issueType = this.issueTypeJsonParser.parse(json);
        JSONObject jsonFieldsMap = json.optJSONObject("fields");
        Map<String, CimFieldInfo> fields = jsonFieldsMap == null ? Collections.emptyMap() : this.fieldsParser.parse(jsonFieldsMap);
        return new CimIssueType(issueType.getSelf(), issueType.getId(), issueType.getName(), issueType.isSubtask(),
                issueType.getDescription(), issueType.getIconUri(), fields);
    }

}
