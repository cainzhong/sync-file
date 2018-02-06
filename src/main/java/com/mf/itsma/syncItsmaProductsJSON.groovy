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
def owner = 'tzhong';
def branch = 'master';
def token = "0e90d45adb96852e64022640daab481dbfcafa20";

def baseURI = "https://github.houston.softwaregrp.net/api/v3";

def jsonSlurper = new JsonSlurper();
//get the sha & content of '/docker/2018.02/itsmaProducts.json' in SMA-RnD/suite-deployer, branch 'master'.
def itsmaProductsMap201802Path = baseURI + '/repos/' + owner + '/suite-deployer/contents/docker/2018.02/itsmaProducts.json?ref=' + branch;
def itsmaProductsMap201802 = jsonSlurper.parseText(getContents(itsmaProductsMap201802Path, token));

//get the content of '/docker/profiles/ci/itsmaProducts.json' in SMA-RnD/suite-deployer, branch 'master'.
def itsmaProductsMapCIPath = baseURI + '/repos/' + owner + '/suite-deployer/contents/docker/profiles/ci/itsmaProducts.json?ref' + branch;
def itsmaProductsMapCI = jsonSlurper.parseText(getContents(itsmaProductsMapCIPath, token));

if (itsmaProductsMapCI.getAt('content').equals(itsmaProductsMap201802.getAt('content'))) {
    println("The content between /docker/profiles/ci/itsmaProducts.json' and /docker/2018.02/itsmaProducts.json is same. There is no need to update.");
} else {
    // Using file '/docker/profiles/ci/itsmaProducts.json' to update the file '/docker/2018.02/itsmaProducts.json'
    def updatePath = baseURI + '/repos/' + owner + '/suite-deployer/contents/docker/2018.02/itsmaProducts.json';
    def message = 'Sync \'\\2018.02\\itsmaProducts.json\' with \'\\profiles\\ci\\itsmaProducts.json\'';
    updateContent(updatePath, token, message, itsmaProductsMapCI.getAt('content'), itsmaProductsMap201802.getAt('sha'), branch);
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