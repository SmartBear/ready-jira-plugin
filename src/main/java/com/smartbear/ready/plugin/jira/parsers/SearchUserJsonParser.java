package com.smartbear.ready.plugin.jira.parsers;

import com.atlassian.jira.rest.client.api.domain.User;
import com.atlassian.jira.rest.client.internal.json.*;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;

public class SearchUserJsonParser implements JsonArrayParser<User> {

    private final UserJsonParser userJsonParser = new UserJsonParser();

    @Override
    public User parse(JSONArray json) throws JSONException {
        if (json.length() > 0) {
            return userJsonParser.parse(json.getJSONObject(0));
        }
        return null;
    }
}
