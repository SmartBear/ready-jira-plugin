package com.smartbear.ready.plugin.jira.clients;

import com.atlassian.jira.rest.client.api.*;
import com.atlassian.jira.rest.client.internal.async.*;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;

public class AsynchronousJiraRestClientServer implements JiraRestClient {
    private final IssueRestClient issueRestClient;
    private final SessionRestClient sessionRestClient;
    private final UserRestClient userRestClient;
    private final GroupRestClient groupRestClient;
    private final ProjectRestClient projectRestClient;
    private final ComponentRestClient componentRestClient;
    private final MetadataRestClient metadataRestClient;
    private final SearchRestClient searchRestClient;
    private final VersionRestClient versionRestClient;
    private final ProjectRolesRestClient projectRolesRestClient;
    private final DisposableHttpClient httpClient;

    public AsynchronousJiraRestClientServer(URI serverUri, DisposableHttpClient httpClient) {
        URI baseUri = UriBuilder.fromUri(serverUri).path("/rest/api/2").build(new Object[0]);
        this.httpClient = httpClient;
        this.metadataRestClient = new AsynchronousMetadataRestClient(baseUri, httpClient);
        this.sessionRestClient = new AsynchronousSessionRestClient(serverUri, httpClient);
        this.issueRestClient = new AsynchronousIssueRestClient(baseUri, httpClient, this.sessionRestClient, this.metadataRestClient);
        this.userRestClient = new AsynchronousUserRestClient(baseUri, httpClient);
        this.groupRestClient = new AsynchronousGroupRestClient(baseUri, httpClient);
        this.projectRestClient = new AsynchronousProjectRestClient(baseUri, httpClient);
        this.componentRestClient = new AsynchronousComponentRestClient(baseUri, httpClient);
        this.searchRestClient = new AsynchronousSearchRestClient(baseUri, httpClient);
        this.versionRestClient = new AsynchronousVersionRestClient(baseUri, httpClient);
        this.projectRolesRestClient = new AsynchronousProjectRolesRestClient(serverUri, httpClient);
    }

    public IssueRestClient getIssueClient() {
        return this.issueRestClient;
    }

    public SessionRestClient getSessionClient() {
        return this.sessionRestClient;
    }

    public UserRestClient getUserClient() {
        return this.userRestClient;
    }

    public GroupRestClient getGroupClient() {
        return this.groupRestClient;
    }

    public ProjectRestClient getProjectClient() {
        return this.projectRestClient;
    }

    public ComponentRestClient getComponentClient() {
        return this.componentRestClient;
    }

    public MetadataRestClient getMetadataClient() {
        return this.metadataRestClient;
    }

    public SearchRestClient getSearchClient() {
        return this.searchRestClient;
    }

    public VersionRestClient getVersionRestClient() {
        return this.versionRestClient;
    }

    public ProjectRolesRestClient getProjectRolesRestClient() {
        return this.projectRolesRestClient;
    }

    @Override
    public AuditRestClient getAuditRestClient() {
        return null;
    }

    @Override
    public MyPermissionsRestClient getMyPermissionsRestClient() {
        return null;
    }

    public void close() throws IOException {
        try {
            this.httpClient.destroy();
        } catch (Exception var2) {
            throw var2 instanceof IOException ? (IOException)var2 : new IOException(var2);
        }
    }

}
