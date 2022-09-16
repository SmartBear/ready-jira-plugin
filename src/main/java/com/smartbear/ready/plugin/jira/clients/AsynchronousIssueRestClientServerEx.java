package com.smartbear.ready.plugin.jira.clients;

import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.jira.rest.client.api.GetCreateIssueMetadataOptions;
import com.atlassian.jira.rest.client.api.MetadataRestClient;
import com.atlassian.jira.rest.client.api.SessionRestClient;
import com.atlassian.jira.rest.client.api.domain.CimFieldInfo;
import com.atlassian.jira.rest.client.api.domain.CimProject;
import com.atlassian.jira.rest.client.internal.async.AsynchronousIssueRestClient;
import com.google.common.base.Joiner;
import com.smartbear.ready.plugin.jira.parsers.CimFieldInfoJsonParserServer;
import io.atlassian.util.concurrent.Promise;

import javax.annotation.Nullable;
import javax.ws.rs.core.UriBuilder;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

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
            uriBuilder.path(String.format("/%s", Joiner.on(",").join(options.projectKeys)));
            extraFields.put("key", Joiner.on(",").join(options.projectKeys));
        } else if(options != null && options.projectIds != null) {
            uriBuilder.path(String.format("/%s", Joiner.on(",").join(options.projectIds)));
        }
        extraFields.put("self", uriBuilder.build());

        uriBuilder.path("/issuetypes");
        this.createIssueMetadataJsonParser.setExtraFields(extraFields);
        return this.getAndParse(uriBuilder.build(), this.createIssueMetadataJsonParser);
    }
    public Promise<Iterable<CimFieldInfo>> getFieldsByIssueId(@Nullable GetCreateIssueMetadataOptions options, Long issueId) {
        UriBuilder uriBuilder = UriBuilder.fromUri(this.baseUri).path("issue/createmeta");
        if (options != null && options.projectKeys != null) {
            uriBuilder.path(String.format("/%s", Joiner.on(",").join(options.projectKeys)));
        } else if(options != null && options.projectIds != null) {
            uriBuilder.path(String.format("/%s", Joiner.on(",").join(options.projectIds)));
        }
        uriBuilder.path("/issuetypes/"+ issueId);
        return this.getAndParse(uriBuilder.build(), this.issueFieldJsonParser);
    }

    @Override
    public Promise<Void> addAttachment(URI attachmentsUri, InputStream inputStream, String filename) {
        return super.addAttachment(attachmentsUri, inputStream, filename);
    }
}
