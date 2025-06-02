package com.dumpalarakshith.todo;

import org.jetbrains.annotations.NotNull;

public record TodoItem(String text, int lineNumber, int offset) {
    @SuppressWarnings("unused")
    public String text() {
        return text;
    }

    @SuppressWarnings("unused")
    public int lineNumber() {
        return lineNumber;
    }

    @SuppressWarnings("unused")
    public int offset() {
        return offset;
    }

    @Override
    @NotNull
    public String toString() {
        return "Line " + lineNumber + ": " + text;
    }
}