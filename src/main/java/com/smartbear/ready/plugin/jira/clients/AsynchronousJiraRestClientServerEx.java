package com.smartbear.ready.plugin.jira.clients;

import com.atlassian.jira.rest.client.internal.async.DisposableHttpClient;

import java.net.URI;

public class AsynchronousJiraRestClientServerEx extends AsynchronousJiraRestClientServer {
    private final AsynchronousUserSearchRestClientExt userSearchRestClientExt;

    public AsynchronousJiraRestClientServerEx(final URI serverUri, final DisposableHttpClient httpClient) {
        super(serverUri, httpClient);
        userSearchRestClientExt = new AsynchronousUserSearchRestClientExt(serverUri, httpClient);
    }

    public AsynchronousUserSearchRestClientExt getUserSearchRestClient() {
        return userSearchRestClientExt;
    }
}
