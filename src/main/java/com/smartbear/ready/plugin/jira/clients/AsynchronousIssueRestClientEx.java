package com.smartbear.ready.plugin.jira.clients;

import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.jira.rest.client.api.GetCreateIssueMetadataOptions;
import com.atlassian.jira.rest.client.api.MetadataRestClient;
import com.atlassian.jira.rest.client.api.SessionRestClient;
import com.atlassian.jira.rest.client.api.domain.CimProject;
import com.atlassian.jira.rest.client.internal.async.AsynchronousIssueRestClient;
import com.smartbear.ready.plugin.jira.parsers.CreateIssueMetadataJsonParserExt;
import io.atlassian.util.concurrent.Promise;

import javax.annotation.Nullable;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class AsynchronousIssueRestClientEx extends AsynchronousIssueRestClient {
    URI baseUri;
    private final CreateIssueMetadataJsonParserExt createIssueMetadataJsonParserExt = new CreateIssueMetadataJsonParserExt();

    public AsynchronousIssueRestClientEx(URI baseUri, HttpClient client, SessionRestClient sessionRestClient, MetadataRestClient metadataRestClient) {
        super(baseUri, client, sessionRestClient, metadataRestClient);
        this.baseUri = baseUri;
    }

    @Override
    public Promise<Iterable<CimProject>> getCreateIssueMetadata(@Nullable GetCreateIssueMetadataOptions options) {
        UriBuilder uriBuilder = UriBuilder.fromUri(this.baseUri).path("issue/createmeta");
        if (options != null) {
            if (options.projectIds != null) {
                String joinedProjectIds = StreamSupport.stream(options.projectIds.spliterator(), false)
                        .map(String::valueOf).collect(Collectors.joining(","));
                uriBuilder.queryParam("projectIds", joinedProjectIds);
            }

            if (options.projectKeys != null) {
                String joinedProjectKeys = StreamSupport.stream(options.projectKeys.spliterator(), false).collect(Collectors.joining(","));
                uriBuilder.queryParam("projectKeys", joinedProjectKeys);
            }

            if (options.issueTypeIds != null) {
                String joinedIssueTypeIds = StreamSupport.stream(options.issueTypeIds.spliterator(), false)
                        .map(String::valueOf).collect(Collectors.joining(","));
                uriBuilder.queryParam("issuetypeIds", joinedIssueTypeIds);
            }

            Iterable<String> issueTypeNames = options.issueTypeNames;
            if (issueTypeNames != null) {
                for (String name : issueTypeNames) {
                    uriBuilder.queryParam("issuetypeNames", name);
                }
            }

            Iterable<String> expandos = options.expandos;
            if (expandos != null && expandos.iterator().hasNext()) {
                String joinedExpandos = StreamSupport.stream(options.expandos.spliterator(), false).collect(Collectors.joining(","));
                uriBuilder.queryParam("expand", joinedExpandos);
            }
        }

        return this.getAndParse(uriBuilder.build(), this.createIssueMetadataJsonParserExt);
    }
}
