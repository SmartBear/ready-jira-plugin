package com.smartbear.ready.plugin.jira.clients;

import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.jira.rest.client.api.GetCreateIssueMetadataOptions;
import com.atlassian.jira.rest.client.api.MetadataRestClient;
import com.atlassian.jira.rest.client.api.SessionRestClient;
import com.atlassian.jira.rest.client.api.domain.CimProject;
import com.atlassian.jira.rest.client.internal.async.AsynchronousIssueRestClient;
import com.atlassian.jira.rest.client.internal.json.CreateIssueMetadataJsonParser;
import com.google.common.base.Joiner;
import io.atlassian.util.concurrent.Promise;

import javax.annotation.Nullable;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.Iterator;

public class AsynchronousIssueRestClientServerEx extends AsynchronousIssueRestClient {
    URI baseUri;
    private final CreateIssueMetadataJsonParserServer createIssueMetadataJsonParser = new CreateIssueMetadataJsonParserServer();
    public AsynchronousIssueRestClientServerEx(URI baseUri, HttpClient client, SessionRestClient sessionRestClient, MetadataRestClient metadataRestClient) {
        super(baseUri, client, sessionRestClient, metadataRestClient);
        this.baseUri = baseUri;
    }

    @Override
    public Promise<Iterable<CimProject>> getCreateIssueMetadata(@Nullable GetCreateIssueMetadataOptions options) {
        UriBuilder uriBuilder = UriBuilder.fromUri(this.baseUri).path("issue/createmeta");
        if (options != null && options.projectKeys != null) {
            uriBuilder.path(String.format("/%s", Joiner.on(",").join(options.projectKeys)));
        } else if(options != null && options.projectIds != null) {
            uriBuilder.path(String.format("/%s", Joiner.on(",").join(options.projectIds)));
        }

        uriBuilder.path("/issuetypes");

        return this.getAndParse(uriBuilder.build(), this.createIssueMetadataJsonParser);
    }
}
