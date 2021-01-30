package net.greensnet.news.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NewsResponseStatus {
    public Action action;
    public boolean success;

    public enum Action {
        CREATE,
        DELETE,
        EDIT
    }
}
