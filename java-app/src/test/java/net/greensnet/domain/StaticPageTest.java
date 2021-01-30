/*
 * Copyright (c) 2019, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StaticPageTest {

    @Test
    public void testGetContentFirst200() {
        StaticPage item = new StaticPage();
        item.setContent(
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed condimentum venenatis dolor eu eleifend. " +
                        "Nulla nec enim eget nulla pharetra ultricies. Morbi imperdiet sit amet ipsum sit amet " +
                        "vulputate. Praesent vitae turpis vel tellus aliquam fermentum ut in velit. Mauris purus " +
                        "sapien, pulvinar dapibus lacus eu, gravida lacinia arcu. Fusce pulvinar nulla a augue " +
                        "consequat tristique. Ut pretium neque eget diam pharetra, non aliquet tortor vehicula. " +
                        "Nunc vel eleifend nisl, vel lacinia eros. Curabitur eget aliquam nisl. Praesent venenatis " +
                        "leo et sagittis luctus.\r\n\r\n" +
                        "Nunc sodales interdum lectus quis venenatis. Nulla non justo a diam posuere maximus. " +
                        "Pellentesque venenatis purus sed ullamcorper scelerisque. Aliquam sed tellus vitae leo " +
                        "mollis efficitur sit amet vitae nibh. Cras id lacinia nulla. Phasellus ligula ex, congue nec " +
                        "nisi a, porttitor blandit purus. Integer faucibus magna orci, at ullamcorper eros maximus " +
                        "eget.\r\n\r\n" +
                        "Cras a mauris eget nulla bibendum facilisis non at sem. In malesuada lacus non tincidunt " +
                        "gravida. Aenean purus augue, vulputate sed fermentum id, gravida ac metus. Proin gravida " +
                        "porttitor magna, in lobortis ipsum lacinia ut. Curabitur congue massa nec dui blandit, sit " +
                        "amet consectetur lectus congue. Etiam efficitur, leo ut imperdiet eleifend, ante leo " +
                        "condimentum libero, ut cursus ante elit eu ligula. Etiam vitae quam eget tortor mollis " +
                        "dapibus. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac " +
                        "turpis egestas. Nam vel molestie nulla. Ut bibendum nulla congue arcu blandit vestibulum. " +
                        "Cras a ultricies mi. Praesent condimentum nibh in nulla lacinia, vitae tristique velit " +
                        "fermentum. Nulla auctor lacus vel porta malesuada. Duis ac risus risus.");
        assertEquals("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed condimentum venenatis " +
                        "dolor eu eleifend. Nulla nec enim eget nulla pharetra ultricies. Morbi imperdiet sit amet " +
                        "ipsum sit amet vulputate. P",
                item.getContentFirst200());
    }
}
