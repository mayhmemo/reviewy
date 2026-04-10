package com.reviewy.util;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

public class MarkdownParser {
    private static final Parser parser = Parser.builder().build();
    private static final HtmlRenderer renderer = HtmlRenderer.builder().build();

    public static String toHtml(String markdown) {
        if (markdown == null) return "";
        String processed = MathRenderer.processMath(markdown);
        Node document = parser.parse(processed);
        return "<html><body style='font-family: sans-serif; font-size: 14px;'>" + renderer.render(document) + "</body></html>";
    }
}
