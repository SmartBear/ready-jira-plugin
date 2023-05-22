package com.smartbear.ready.plugin.jira.parsers;

import com.atlassian.jira.rest.client.api.domain.CimFieldInfo;
import com.atlassian.jira.rest.client.internal.json.GenericJsonArrayParser;
import com.atlassian.jira.rest.client.internal.json.JsonObjectParser;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class CimFieldInfoJsonParserServer implements JsonObjectParser<Iterable<CimFieldInfo>> {
    private final GenericJsonArrayParser<CimFieldInfo> fieldInfoParser = new GenericJsonArrayParser<>(new CimFieldsInfoJsonParserServerExt());

    public CimFieldInfoJsonParserServer() {
    }
    public Iterable<CimFieldInfo> parse(JSONObject json) throws JSONException {
        return this.fieldInfoParser.parse(json.getJSONArray("values"));
    }
}
