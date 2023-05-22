package com.smartbear.ready.plugin.jira.parsers;

import com.atlassian.jira.rest.client.api.domain.CimFieldInfo;
import com.atlassian.jira.rest.client.internal.json.CimFieldsInfoJsonParser;
import com.atlassian.jira.rest.client.internal.json.GenericJsonArrayParser;

public class CimFieldInfoJsonParserExt extends CimFieldsInfoJsonParser {
    private final GenericJsonArrayParser<CimFieldInfo> fieldInfoParser = new GenericJsonArrayParser<>(new CimFieldsInfoJsonParserExt());

    public CimFieldInfoJsonParserExt() {
    }

}
