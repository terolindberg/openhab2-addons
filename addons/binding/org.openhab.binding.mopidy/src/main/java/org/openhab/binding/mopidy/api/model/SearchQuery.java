package org.openhab.binding.mopidy.api.model;

/**
 *
 * @author Tero Lindberg
 *
 */
public class SearchQuery {
    String any;

    public SearchQuery(String query) {
        any = query;
    }
}
