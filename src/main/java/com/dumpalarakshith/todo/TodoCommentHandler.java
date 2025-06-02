package com.dumpalarakshith.todo;

import com.intellij.notification.*;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.editor.markup.MarkupModel;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiComment;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import com.intellij.ui.JBColor;


import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
@Service(Service.Level.PROJECT)
public final class TodoCommentHandler {
    private final Project project;
    private static final NotificationGroup NOTIFICATION_GROUP =
            NotificationGroupManager.getInstance().getNotificationGroup("TodoPlugin");


    public TodoCommentHandler(Project project) {
        this.project = project;
        System.out.println("TodoCommentHandler instantiated for project: " + project.getName());
        Notification notification = NOTIFICATION_GROUP.createNotification(
                "Todo plugin initialized",
                "TodoCommentHandler service has been instantiated for project: " + project.getName(),
                NotificationType.INFORMATION
        );
        notification.notify(project);
        setupEditorListener();
    }

    private void setupEditorListener() {
        project.getMessageBus()
                .connect()
                .subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, new FileEditorManagerListener() {
                    @Override
                    public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
                        if (file.getExtension() == null || !file.getExtension().equals("kt")) {
                            return;
                        }

                        PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
                        if (psiFile == null) {
                            System.out.println("Could not find PSI file for " + file.getName());
                            return;
                        }

                        FileEditor editor = source.getSelectedEditor(file);
                        if (!(editor instanceof TextEditor)) {
                            System.out.println("Editor is not a text editor for " + file.getName());
                            return;
                        }

                        Document document = ((TextEditor) editor).getEditor().getDocument();
                        List<TodoItem> todos = scanTodos(psiFile, document);
                        System.out.println("Found " + todos.size() + " TODOs in " + file.getName());
                        for (TodoItem todo : todos) {
                            System.out.println(todo);
                        }

                        highlightTodos(((TextEditor) editor).getEditor().getMarkupModel(), todos);
                        TodoToolWindowFactory.updateTodos(todos, (TextEditor) editor);
                    }
                });
    }

    private List<TodoItem> scanTodos(PsiFile psiFile, Document document) {
        List<TodoItem> todos = new ArrayList<>();
        for (PsiComment comment : PsiTreeUtil.findChildrenOfType(psiFile, PsiComment.class)) {
            if (comment == null) continue;
            String commentText = comment.getText();
            if (commentText == null) continue;
            if (commentText.toUpperCase().contains("TODO")) {
                int lineNumber = document.getLineNumber(comment.getTextOffset()) + 1;
                int offset = comment.getTextOffset();
                todos.add(new TodoItem(commentText, lineNumber, offset));
            }
        }
        return todos;
    }

    private void highlightTodos(MarkupModel markupModel, List<TodoItem> todos) {
        if (markupModel == null) return;
        markupModel.removeAllHighlighters();
        for (TodoItem todo : todos) {
            if (todo == null) continue;
            TextAttributes attributes = new TextAttributes(null, JBColor.YELLOW, null, null, 0);
            markupModel.addRangeHighlighter(
                    todo.offset(), // Corrected from Offset() to offset()
                    todo.offset() + todo.text().length(),
                    HighlighterLayer.ADDITIONAL_SYNTAX,
                    attributes,
                    com.intellij.openapi.editor.markup.HighlighterTargetArea.EXACT_RANGE
            );
        }
    }
}