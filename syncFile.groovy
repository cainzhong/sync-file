package com.mf.itsma

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovyx.net.http.HTTPBuilder

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.Method.GET
import static groovyx.net.http.Method.PUT

/**
 * Created by zhongtao on 1/15/2018.
 */
def baseFile = project.properties['baseFile'];
def needSyncFile = project.properties['needSyncFile'];
println("baseFile ==> " + baseFile);
println("needSyncFile ==> " + needSyncFile);

def owner = 'tzhong';
def repo = 'suite-deployer';
def branch = 'master';
def token = "0e90d45adb96852e64022640daab481dbfcafa20";

def baseURI = "https://github.houston.softwaregrp.net/api/v3";

def jsonSlurper = new JsonSlurper();
//get the sha & content of [needSyncFile] in [owner/repo/branch].
def needSyncFilePath = baseURI + '/repos/' + owner + '/' + repo + '/contents/' + needSyncFile + '?ref=' + branch;
def needSyncFileMap = jsonSlurper.parseText(getContents(needSyncFilePath, token));

//get the content of [baseFile] in [owner/repo/branch].
def baseFilePath = baseURI + '/repos/' + owner + '/' + repo + '/contents/' + baseFile + '?ref=' + branch;
def baseFileMap = jsonSlurper.parseText(getContents(baseFilePath, token));

if (baseFileMap.getAt('content').equals(needSyncFileMap.getAt('content'))) {
    println("The content between " + baseFilePath + " and " + needSyncFilePath + " is same. There is no need to update.");
} else {
    // Using file [baseFile] to update the file [needSyncFile]
    def updatePath = baseURI + '/repos/' + owner + '/' + repo + '/contents/' + needSyncFile;
    def message = 'Sync ' + baseFile + ' with ' + needSyncFile;
    updateContent(updatePath, token, message, baseFileMap.getAt('content'), needSyncFileMap.getAt('sha'), branch);
}

String getContents(path, token) {
    http = new HTTPBuilder(path);
    http.headers.'Accept' = 'application/vnd.github.v3.text-match+json'
    http.headers.'User-Agent' = 'Mozilla/5.0'
    http.headers.'Authorization' = "token ${token}"

    http.request(GET, JSON) { req ->
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
    http = new HTTPBuilder(path);
    http.headers.'Accept' = 'application/vnd.github.v3.text-match+json'
    http.headers.'User-Agent' = 'Mozilla/5.0'
    http.headers.'Authorization' = "token ${token}"

    http.request(PUT, JSON) { req ->
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