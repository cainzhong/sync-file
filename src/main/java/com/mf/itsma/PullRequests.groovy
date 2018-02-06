package com.mf.itsma

import groovy.json.JsonOutput

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.Method.POST
import static groovyx.net.http.Method.PUT

/**
 * Created by zhongtao on 1/15/2018.
 */
class PullRequests {
    String createPullRequest(path, token, title, head, base) {
        def http = GroovyHttpBuilder.getHTTPBuilder();
        http.request(POST, JSON) { req ->
            uri = path;
            body = [
                    title: title,
                    head : head,
                    base : base
            ]
            response.success = { resp, json ->
                println(path + 'The Pull Request has been created successfully!');
                def jsonOutput = new JsonOutput();
                return jsonOutput.toJson(json);
            }
            response.failure = { resp, json ->
                println("Error happened ===> " + json);
                throw Exception();
            }
        }
    }

    void mergePullRequest(path, token) {
        def http = GroovyHttpBuilder.getHTTPBuilder();
        http.request(PUT, JSON) { req ->
            uri = path;
            response.success = { resp, json ->
                println(path + 'The Pull Request has been merged!');
            }
            response.failure = { resp, json ->
                println("Error happened ===> " + json);
                throw Exception();
            }
        }
    }
}
