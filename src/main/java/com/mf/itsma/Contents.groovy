package com.mf.itsma

import groovy.json.JsonOutput

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.Method.GET
import static groovyx.net.http.Method.PUT

/**
 * Created by zhongtao on 1/12/2018.
 */
class Contents {
    String getContents(path, token) {
        def http = GroovyHttpBuilder.getHTTPBuilder();
        http.headers.'Authorization' = "token ${token}"
        http.request(GET, JSON) { req ->
            uri = path;
            response.success = { resp, json ->
                def jsonOutput = new JsonOutput();
                return jsonOutput.toJson(json);
            }
            response.failure = { resp, json ->
                println("Error happened ===> " + json);
                throw Exception();
            }
        }
    }

    void updateContent(path, token, message, content, sha, branch) {
        def http = GroovyHttpBuilder.getHTTPBuilder();
        http.request(PUT, JSON) { req ->
            uri = path;
            body = [
                    message: message,
                    content: content,
                    sha    : sha,
                    branch : branch
            ]
            response.success = { resp, json ->
                println(path + ' has been updated successfully!');
            }
            response.failure = { resp, json ->
                println("Error happened ===> " + json);
                throw Exception();
            }
        }
    }
}