package com.reviewy.util;

import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXConstants;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MathRenderer {
    private static final Pattern BLOCK_MATH = Pattern.compile("\\$\\$(.*?)\\$\\$", Pattern.DOTALL);
    private static final Pattern INLINE_MATH = Pattern.compile("\\$(.*?)\\$");

    private static int counter = 0;

    public static String processMath(String text) {
        if (text == null) return null;

        // Process block math
        Matcher blockMatcher = BLOCK_MATH.matcher(text);
        StringBuilder sb = new StringBuilder();
        int lastPos = 0;
        while (blockMatcher.find()) {
            sb.append(text, lastPos, blockMatcher.start());
            sb.append("<div style='text-align: center;'>");
            sb.append(registerLatex(blockMatcher.group(1).trim(), 18, true));
            sb.append("</div>");
            lastPos = blockMatcher.end();
        }
        sb.append(text.substring(lastPos));
        text = sb.toString();

        // Process inline math
        Matcher inlineMatcher = INLINE_MATH.matcher(text);
        sb = new StringBuilder();
        lastPos = 0;
        while (inlineMatcher.find()) {
            sb.append(text, lastPos, inlineMatcher.start());
            sb.append(registerLatex(inlineMatcher.group(1).trim(), 14, false));
            lastPos = inlineMatcher.end();
        }
        sb.append(text.substring(lastPos));
        
        return sb.toString();
    }

    private static String registerLatex(String latex, float size, boolean block) {
        try {
            String key = "latex_" + (counter++) + ".png";
            TeXFormula formula = new TeXFormula(latex);
            BufferedImage image = (BufferedImage) formula.createBufferedImage(
                TeXConstants.STYLE_DISPLAY, size, Color.BLACK, null);
            
            MemoryURLStreamHandler.putImage(key, image);
            
            String verticalAlign = block ? "middle" : "baseline";
            return String.format("<img src='mem:%s' style='vertical-align: %s;'>", 
                key, verticalAlign);
        } catch (Exception e) {
            return "<span style='color: red;'>[Math Error]</span>";
        }
    }

    public static void clearCache() {
        MemoryURLStreamHandler.clear();
        counter = 0;
    }
}
