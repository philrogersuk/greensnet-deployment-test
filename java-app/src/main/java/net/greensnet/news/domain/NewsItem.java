package net.greensnet.news.domain;

import lombok.Builder;
import lombok.Data;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Data
@Builder
public class NewsItem {
    private Long id;
    private String headline;
    private String item;
    private LocalDateTime timeOfRelease;
    private String author;

    public String getHeadlineAsEncodedString() {
        if (isNull(headline)) {
            return "";
        }

        try {
            return URLEncoder.encode(headline.replace('/', '+'),
                    StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            return headline;
        }
    }

    public String getFirstSentence() {
        if(isNull(item)) {
            return "";
        }
        return item.split("\\.")[0];
    }

    public String getStoryAsHtml() {
        if (isNull(item)) {
            return "<p></p>";
        }

        String storyWithHTML = item.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("[img=", "<img src=")
                .replace("[url=", "<a href=")
                .replace("[/url", "</a")
                .replace("]", ">");

        String storyWithParas = Arrays.stream(storyWithHTML.split("\\r\\n\\r\\n"))
                .map(sentence -> "<p>" + sentence + "</p>")
                .collect(Collectors.joining(""));
        return Arrays.stream(storyWithParas.split("\\r\\n"))
                .map(sentence -> sentence.endsWith("</p>") ? sentence : sentence + "<br/>")
                .collect(Collectors.joining(""));
    }
}
