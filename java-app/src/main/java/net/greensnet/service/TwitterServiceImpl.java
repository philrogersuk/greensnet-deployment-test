/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.service;

import com.rosaloves.net.shorturl.bitly.Bitly;
import com.rosaloves.net.shorturl.bitly.BitlyFactory;
import com.rosaloves.net.shorturl.bitly.url.BitlyUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

/**
 * @author Phil
 */
@Service
public class TwitterServiceImpl implements TwitterService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TwitterServiceImpl.class);

    private static final String BITLY_USER = "hendonfc";
    private static final String BITLY_API_KEY = "bit_ly key";
    private static final String TWITTER_ACCESS_TOKEN = "twitter_access_token";
    private static final String TWITTER_ACCESS_TOKEN_SECRET = "twitter_access_token_secret";
    private static final String TWITTER_CONSUMER_KEY = "twitter_consumer_key";
    private static final String TWITTER_CONSUMER_SECRET = "twitter_consumer_secret";
    private static final String TEST_TWITTER_ACCESS_TOKEN = "twitter_access_token";
    private static final String TEST_TWITTER_ACCESS_TOKEN_SECRET = "twitter_access_token_secret";
    private static final String TEST_TWITTER_CONSUMER_KEY = "twitter_consumer_key";
    private static final String TEST_TWITTER_CONSUMER_SECRET = "twitter_consumer_secret";


    private final Twitter twitter;

    public TwitterServiceImpl() {//boolean liveAccount) {
        twitter = TwitterFactory.getSingleton();
        // TODO: This code needs removing once Admin servlets are in spring and
        // TwitterService is injected
        try {
//            if (false) {
 //               twitter.setOAuthConsumer(TWITTER_CONSUMER_KEY, TWITTER_CONSUMER_SECRET);
  //              AccessToken accessToken = new AccessToken(TWITTER_ACCESS_TOKEN, TWITTER_ACCESS_TOKEN_SECRET);
  //              twitter.setOAuthAccessToken(accessToken);
  //          } else {
                twitter.setOAuthConsumer(TEST_TWITTER_CONSUMER_KEY, TEST_TWITTER_CONSUMER_SECRET);
                AccessToken accessToken = new AccessToken(TEST_TWITTER_ACCESS_TOKEN, TEST_TWITTER_ACCESS_TOKEN_SECRET);
                twitter.setOAuthAccessToken(accessToken);
  //          }
        } catch (IllegalStateException e) {
            LOGGER.error("Error connecting to Twitter", e);
        }
    }

    public boolean tweet(String content, String url) {
        try {
            if (null == url) {
                twitter.updateStatus(content.substring(0, Math.min(139, content.length())));
            } else {
                Bitly bitly = BitlyFactory.newInstance(BITLY_USER, BITLY_API_KEY);
                BitlyUrl shortUrl = bitly.shorten(url);
                twitter.updateStatus(content.substring(0, Math.min(120, content.length())) + " - "
                        + shortUrl.getShortUrl().toString());
            }
            return true;
        } catch (Exception e) {
            LOGGER.error("Unable to tweet item", e);
            return false;
        }
    }
}
