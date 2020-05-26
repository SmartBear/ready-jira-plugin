package com.smartbear.ready.plugin.jira.clients;

import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.jira.rest.client.api.domain.User;
import com.atlassian.jira.rest.client.internal.async.AsynchronousUserRestClient;
import com.smartbear.ready.plugin.jira.parsers.SearchUserJsonParser;
import io.atlassian.util.concurrent.Promise;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

public class AsynchronousUserSearchRestClient extends AsynchronousUserRestClient {

    private static final String USER_SEARCH_PREFIX = "rest/api/2/user/search";
    private final SearchUserJsonParser searchUserJsonParser = new SearchUserJsonParser();

    private final URI baseUri;

    public AsynchronousUserSearchRestClient(final URI baseUri, final HttpClient client) {
        super(baseUri, client);
        this.baseUri = baseUri;
    }

    @Override
    public Promise<User> getUser(final String fullname) {
        final URI userUri = UriBuilder.fromUri(baseUri).path(USER_SEARCH_PREFIX)
                .queryParam("query", fullname).queryParam("maxResults", "1").build();
        return getUser(userUri);
    }

    @Override
    public Promise<User> getUser(final URI userUri) {
        return getAndParse(userUri, searchUserJsonParser);
    }
}