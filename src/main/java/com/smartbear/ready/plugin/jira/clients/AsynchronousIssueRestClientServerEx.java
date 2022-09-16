package com.smartbear.ready.plugin.jira.clients;

import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.jira.rest.client.api.GetCreateIssueMetadataOptions;
import com.atlassian.jira.rest.client.api.MetadataRestClient;
import com.atlassian.jira.rest.client.api.SessionRestClient;
import com.atlassian.jira.rest.client.api.domain.CimFieldInfo;
import com.atlassian.jira.rest.client.api.domain.CimProject;
import com.atlassian.jira.rest.client.internal.async.AsynchronousIssueRestClient;
import com.smartbear.ready.plugin.jira.parsers.CimFieldInfoJsonParserServer;
import io.atlassian.util.concurrent.Promise;

import javax.annotation.Nullable;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AsynchronousIssueRestClientServerEx extends AsynchronousIssueRestClient {
    URI baseUri;
    private final CreateIssueMetadataJsonParserServer createIssueMetadataJsonParser = new CreateIssueMetadataJsonParserServer();
    private final CimFieldInfoJsonParserServer issueFieldJsonParser = new CimFieldInfoJsonParserServer();
    public AsynchronousIssueRestClientServerEx(URI baseUri, HttpClient client, SessionRestClient sessionRestClient, MetadataRestClient metadataRestClient) {
        super(baseUri, client, sessionRestClient, metadataRestClient);
        this.baseUri = baseUri;
    }

    @Override
    public Promise<Iterable<CimProject>> getCreateIssueMetadata(@Nullable GetCreateIssueMetadataOptions options) {
        Map<String, Object> extraFields = new HashMap<>();
        UriBuilder uriBuilder = UriBuilder.fromUri(this.baseUri).path("issue/createmeta");
        if (options != null && options.projectKeys != null) {

            uriBuilder.path(String.format("/%s", buildPathFromString(options.projectKeys)));
            extraFields.put("key", buildPathFromString(options.projectKeys));
        } else if(options != null && options.projectIds != null) {
            uriBuilder.path(String.format("/%s", buildPathFromLong(options.projectIds)));
        }
        extraFields.put("self", uriBuilder.build());

        uriBuilder.path("/issuetypes");
        this.createIssueMetadataJsonParser.setExtraFields(extraFields);
        return this.getAndParse(uriBuilder.build(), this.createIssueMetadataJsonParser);
    }
    public Promise<Iterable<CimFieldInfo>> getFieldsByIssueId(@Nullable GetCreateIssueMetadataOptions options, Long issueId) {
        UriBuilder uriBuilder = UriBuilder.fromUri(this.baseUri).path("issue/createmeta");
        if (options != null && options.projectKeys != null) {
            uriBuilder.path(String.format("/%s", buildPathFromString(options.projectKeys)));
        } else if(options != null && options.projectIds != null) {
            uriBuilder.path(String.format("/%s", buildPathFromLong(options.projectIds)));
        }
        uriBuilder.path("/issuetypes/"+ issueId);
        return this.getAndParse(uriBuilder.build(), this.issueFieldJsonParser);
    }

    private String buildPathFromString(Iterable<String> input) {
        List<String> output = (List<String>)input;
        return String.join(",", output);
    }
    private String buildPathFromLong(Iterable<Long> input) {
        List<Long> output = (List<Long>)input;
        return output.stream().map(Object::toString).collect(Collectors.joining(","));
    }
}
