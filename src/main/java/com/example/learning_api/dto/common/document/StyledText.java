package com.example.learning_api.dto.common.document;

public class StyledText {
    private String text;
    private TextStyle style;

    public StyledText(String text, TextStyle style) {
        this.text = text;
        this.style = style;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (style.isBold()) sb.append("**");
        if (style.isItalic()) sb.append("*");
        if (style.isUnderline()) sb.append("__");
        if (style.isStrikethrough()) sb.append("~~");
        sb.append(text);
        if (style.isStrikethrough()) sb.append("~~");
        if (style.isUnderline()) sb.append("__");
        if (style.isItalic()) sb.append("*");
        if (style.isBold()) sb.append("**");
        return sb.toString();
    }
}