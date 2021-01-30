/*
 * Copyright (c) 2019, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.news.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class NewsItemTest {

    @Test
    public void shouldReturnFirstSentence() {
        NewsItem item = NewsItem.builder()
                .item("This is the first line. And this is the second.")
                .build();
        assertThat(item.getFirstSentence()).isEqualTo("This is the first line");
    }

    @Test
    public void shouldReturnFirstSentenceItemIsWhenOnlyOneSentence() {
        NewsItem item = NewsItem.builder()
                .item("This is the first line.")
                .build();
        assertThat(item.getFirstSentence()).isEqualTo("This is the first line");
    }

    @Test
    public void shouldReturnFirstSentenceItemIsWhenNoFullStop() {
        NewsItem item = NewsItem.builder()
                .item("This is the first line")
                .build();
        assertThat(item.getFirstSentence()).isEqualTo("This is the first line");
    }

    @Test
    public void shouldReturnNothingWhenItemIsEmptyString() {
        NewsItem item = NewsItem.builder()
                .item("")
                .build();
        assertThat(item.getFirstSentence()).isEqualTo("");
    }

    @Test
    public void shouldReturnNothingWhenItemIsNull() {
        NewsItem item = NewsItem.builder()
                .item(null)
                .build();
        assertThat(item.getFirstSentence()).isEqualTo("");
    }

    @Test
    public void shouldReturnHeadlineAsEncodedString() {
        NewsItem item = NewsItem.builder()
                .headline("This is the first line")
                .build();
        assertThat(item.getHeadlineAsEncodedString()).isEqualTo("This+is+the+first+line");
    }

    @Test
    public void shouldReturnHeadlineAsEncodedStringWithFixedSlash() {
        NewsItem item = NewsItem.builder()
                .headline("Season 2020/21")
                .build();
        assertThat(item.getHeadlineAsEncodedString()).isEqualTo("Season+2020%2B21");
    }

    @Test
    public void shouldReturnEmptyStringWhenHeadlineIsEmptyString() {
        NewsItem item = NewsItem.builder()
                .headline("")
                .build();
        assertThat(item.getHeadlineAsEncodedString()).isEqualTo("");
    }

    @Test
    public void shouldReturnEmptyStringWhenHeadlineIsNull() {
        NewsItem item = NewsItem.builder()
                .headline(null)
                .build();
        assertThat(item.getHeadlineAsEncodedString()).isEqualTo("");
    }

    @Test
    public void shouldConvertItemToHTML() {
        NewsItem item = NewsItem.builder()
                .item("Hello.")
                .build();
        assertThat(item.getStoryAsHtml()).isEqualTo("<p>Hello.</p>");
    }

    @Test
    public void shouldConvertItemToHTMLWithNewLine() {
        NewsItem item = NewsItem.builder()
                .item("Hello.\r\nSecond line.")
                .build();
        assertThat(item.getStoryAsHtml()).isEqualTo("<p>Hello.<br/>Second line.</p>");
    }

    @Test
    public void shouldConvertItemToHTMLWithTwoParagraphs() {
        NewsItem item = NewsItem.builder()
                .item("Hello.\r\n\r\nSecond line.")
                .build();
        assertThat(item.getStoryAsHtml()).isEqualTo("<p>Hello.</p><p>Second line.</p>");
    }

    @Test
    public void shouldConvertItemToHTMLWithSpecialCharacters() {
        NewsItem item = NewsItem.builder()
                .item("Hello& < > Second line.")
                .build();
        assertThat(item.getStoryAsHtml()).isEqualTo("<p>Hello&amp; &lt; &gt; Second line.</p>");
    }

    @Test
    public void shouldConvertItemToHTMLWithLink() {
        NewsItem item = NewsItem.builder()
                .item("[url=http://example.com]Hello. Second[/url] line.")
                .build();
        assertThat(item.getStoryAsHtml()).isEqualTo("<p><a href=http://example.com>Hello. Second</a> line.</p>");
    }

    @Test
    public void shouldConvertItemToHTMLWithUrl() {
        NewsItem item = NewsItem.builder()
                .item("[img=http://example.com/a.jpg]Hello. Second line.")
                .build();
        assertThat(item.getStoryAsHtml()).isEqualTo("<p><img src=http://example.com/a.jpg>Hello. Second line.</p>");
    }

    @Test
    public void shouldConvertItemToHTMLWithUrlAndLink() {
        NewsItem item = NewsItem.builder()
                .item("[url=http://example.com][img=http://example.com/a.jpg][/url]")
                .build();
        assertThat(item.getStoryAsHtml()).isEqualTo("<p><a href=http://example.com><img src=http://example.com/a.jpg></a></p>");
    }

    @Test
    public void shouldConvertItemToHTMLWhenItemIsEmpty() {
        NewsItem item = NewsItem.builder()
                .item("")
                .build();
        assertThat(item.getStoryAsHtml()).isEqualTo("<p></p>");
    }

    @Test
    public void shouldConvertItemToHTMLWhenItemIsNull() {
        NewsItem item = NewsItem.builder()
                .item(null)
                .build();
        assertThat(item.getStoryAsHtml()).isEqualTo("<p></p>");
    }
}
