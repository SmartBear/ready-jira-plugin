package com.smartbear.ready.plugin.jira.clients;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.internal.async.AsynchronousIssueRestClient;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClient;
import com.atlassian.jira.rest.client.internal.async.DisposableHttpClient;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

public class AsynchronousJiraRestClientEx extends AsynchronousJiraRestClient {
    private final AsynchronousUserSearchRestClient userSearchRestClient;

    private final AsynchronousIssueRestClientEx issueRestClientEx;

    public AsynchronousJiraRestClientEx(final URI serverUri, final DisposableHttpClient httpClient) {
        super(serverUri, httpClient);
        URI baseUri = UriBuilder.fromUri(serverUri).path("/rest/api/latest").build(new Object[0]);
        userSearchRestClient = new AsynchronousUserSearchRestClient(serverUri, httpClient);
        issueRestClientEx = new AsynchronousIssueRestClientEx(baseUri, httpClient, this.getSessionClient(), this.getMetadataClient());
    }

    public AsynchronousUserSearchRestClient getUserSearchRestClient() {
        return userSearchRestClient;
    }

    @Override
    public IssueRestClient getIssueClient() {
        return this.issueRestClientEx;
    }

}
