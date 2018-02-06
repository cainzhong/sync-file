package com.mf.itsma

import groovyx.net.http.HTTPBuilder

/**
 * Created by zhongtao on 1/15/2018.
 */
class GroovyHttpBuilder {
    def static http
    static {
        def baseURI = "https://github.houston.softwaregrp.net/api/v3";
        http = new HTTPBuilder(baseURI);
        http.headers.'Accept' = 'application/vnd.github.v3.text-match+json'
        http.headers.'User-Agent' = 'Mozilla/5.0'
    }

    synchronized static HTTPBuilder getHTTPBuilder() {
        if (http == null) {
            http = new HTTPBuilder();
        }
        return http;
    }
}
