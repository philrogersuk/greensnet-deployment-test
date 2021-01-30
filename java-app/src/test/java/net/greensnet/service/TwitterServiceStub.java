/*
 * Copyright (c) 2019, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.service;

public class TwitterServiceStub implements TwitterService {

    private String content;
    private String url;

    @Override
    public boolean tweet(String content, String url) {
        this.content = content;
        this.url = url;
        return true;
    }

    public String getContent() {
        return content;
    }

    public String getUrl() {
        return url;
    }

}
