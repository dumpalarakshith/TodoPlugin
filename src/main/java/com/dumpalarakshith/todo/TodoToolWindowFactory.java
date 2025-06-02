package com.dumpalarakshith.todo;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import javax.swing.*;
import java.util.Collections;
import java.util.List;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.ui.components.JBList;
import org.jetbrains.annotations.NotNull;


public class TodoToolWindowFactory implements ToolWindowFactory {
    private static JBList<String> todoList = new JBList<>();
    private static List<TodoItem> currentTodos = Collections.emptyList();
    private static TextEditor currentEditor;


    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        todoList = new JBList<>();
        panel.add(new JScrollPane(todoList));

        todoList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int index = todoList.getSelectedIndex();
                if (index >= 0 && currentEditor != null) {
                    TodoItem selectedTodo = currentTodos.get(index);
                    currentEditor.getEditor().getCaretModel().moveToOffset(selectedTodo.offset());
                    currentEditor.getEditor().getScrollingModel().scrollToCaret(com.intellij.openapi.editor.ScrollType.CENTER);
                }
            }
        });

        ContentFactory contentFactory = ContentFactory.getInstance();
        Content content = contentFactory.createContent(panel, "", false);
        toolWindow.getContentManager().addContent(content);
    }

    public static void updateTodos(List<TodoItem> todos, TextEditor editor) {
        currentTodos = todos;
        currentEditor = editor;
        if (todos.isEmpty()) {
            todoList.setListData(new String[]{"No TODOs found"});
        } else {
            todoList.setListData(todos.stream().map(TodoItem::toString).toArray(String[]::new));
        }
    }
}