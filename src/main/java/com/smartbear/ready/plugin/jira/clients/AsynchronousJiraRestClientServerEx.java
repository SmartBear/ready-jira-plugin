package com.smartbear.ready.plugin.jira.clients;

import com.atlassian.jira.rest.client.internal.async.DisposableHttpClient;

import java.net.URI;

public class AsynchronousJiraRestClientServerEx extends AsynchronousJiraRestClientServer{
    private final AsynchronousUserSearchRestClient userSearchRestClient;

    public AsynchronousJiraRestClientServerEx(final URI serverUri, final DisposableHttpClient httpClient) {
        super(serverUri, httpClient);
        userSearchRestClient = new AsynchronousUserSearchRestClient(serverUri, httpClient);
    }

    public AsynchronousUserSearchRestClient getUserSearchRestClient() {
        return userSearchRestClient;
    }
}
