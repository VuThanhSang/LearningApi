package com.example.learning_api.dto.common.document;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TextContent {
    private List<StyledText> styledTexts = new ArrayList<>();

    public void addStyledText(StyledText styledText) {
        styledTexts.add(styledText);
    }

    public boolean isEmpty() {
        return styledTexts.isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (StyledText styledText : styledTexts) {
            sb.append(styledText.toString());
        }
        return sb.toString();
    }
}