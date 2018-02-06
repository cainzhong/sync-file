package com.mf.itsma

import groovy.json.JsonSlurper

/**
 * Created by zhongtao on 1/15/2018.
 */
class Main {
    def static baseURI = "https://github.houston.softwaregrp.net/api/v3";

    static void main(String[] args) {
        def owner = 'tzhong';
        def branch = 'master';
        def token = "0e90d45adb96852e64022640daab481dbfcafa20";

        Contents contents = new Contents();

        def jsonSlurper = new JsonSlurper();
        //get the sha & content of '/docker/2018.02/itsmaProducts.json' in SMA-RnD/suite-deployer, branch 'master'.
        def itsmaProductsMap201802Path = baseURI + '/repos/' + owner + '/suite-deployer/contents/docker/2018.02/itsmaProducts.json?ref=' + branch;
        def itsmaProductsMap201802 = jsonSlurper.parseText(contents.getContents(itsmaProductsMap201802Path));

        //get the content of '/docker/profiles/ci/itsmaProducts.json' in SMA-RnD/suite-deployer, branch 'master'.
        def itsmaProductsMapCIPath = baseURI + '/repos/' + owner + '/suite-deployer/contents/docker/profiles/ci/itsmaProducts.json?ref' + branch;
        def itsmaProductsMapCI = jsonSlurper.parseText(contents.getContents(itsmaProductsMapCIPath));

        if (itsmaProductsMapCI.getAt('content').equals(itsmaProductsMap201802.getAt('content'))) {
            println("The content between /docker/profiles/ci/itsmaProducts.json' and /docker/2018.02/itsmaProducts.json is same. There is no need to update.");
        } else {
            // Using file '/docker/profiles/ci/itsmaProducts.json' to update the file '/docker/2018.02/itsmaProducts.json'
            def updatePath = baseURI + '/repos/' + owner + '/suite-deployer/contents/docker/2018.02/itsmaProducts.json';
            def message = 'Sync \'\\2018.02\\itsmaProducts.json\' with \'\\profiles\\ci\\itsmaProducts.json\'';
            contents.updateContent(updatePath, message, itsmaProductsMapCI.getAt('content'), itsmaProductsMap201802.getAt('sha'), branch);
        }
    }
}
