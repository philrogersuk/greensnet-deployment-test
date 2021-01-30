/*
 * Copyright (c) 2008, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * Entity class StaticPage
 *
 * @author Phil
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "STATICPAGE")
public class StaticPage {

    @Id
    private String id;
    private LocalDateTime lastUpdated;
    @Column(length = 50000)
    private String content;
    private String title;


    public String getContentFirst200() {
        return content.substring(0, Math.min(200, content.length()));
    }
}
