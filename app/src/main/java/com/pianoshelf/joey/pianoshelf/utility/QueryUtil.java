package com.pianoshelf.joey.pianoshelf.utility;

import com.pianoshelf.joey.pianoshelf.C;

/**
 * Created by joey on 21/11/15.
 */
public class QueryUtil {
    private static final String SERVER_SHEETMUSIC_SUFFIX = "api/sheetmusic/";
    private static final String SERVER_PROFILE_SUFFIX = "api/profile/";

    private static final String QUERY_PREFIX = "?";

    private static final String QUERY_PAGE = "page";
    private static final String QUERY_PAGE_SIZE = "page_size";

    private static final String QUERY_ADD_ARG = "&";
    private static final String QUERY_ASSIGN = "=";

    private static final String DEFAULT_QUERY_TYPE = "order_by";

    private static final String QUERY_USERNAME = "username";

    /**
     * Helper Functions
     */
    // Parse the query by type and page number
    // Example:  /api/sheetmusic/?order_by=popular&page_size=9
    public static String parse(String query, String queryType, int page, int pageSize) {
        return appendArguments(C.SERVER_ADDR + SERVER_SHEETMUSIC_SUFFIX
                + QUERY_PREFIX + queryType + QUERY_ASSIGN + query
                , QUERY_PAGE + QUERY_ASSIGN + page
                , QUERY_PAGE_SIZE + QUERY_ASSIGN + pageSize);
    }

    public static String parse(String query, int page, int pageSize) {
        return parse(query, DEFAULT_QUERY_TYPE, page, pageSize);
    }

    // Helper function to chain additional arguments
    private static String appendArguments(String prefix, String... arguments) {
        for (String arg : arguments) {
            prefix = prefix + QUERY_ADD_ARG + arg;
        }
        return prefix;
    }

    public static String createProfile(String username) {
        return C.SERVER_ADDR + SERVER_PROFILE_SUFFIX + QUERY_PREFIX
                + QUERY_USERNAME + QUERY_ASSIGN + username;
    }
}
