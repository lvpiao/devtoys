package com.antgroup.devtoys.action;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;

public class JumpToImplementationAction extends AbstractJumpToMethodAction {

    @Override
    PsiMethod getPsiMethod(AnActionEvent e) {
        Project currentProject = e.getProject();
        if (currentProject == null) {
            return null;
        }
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (editor == null) {
            return null;
        }
        // 获取当前编辑器中的文件和光标位置
        PsiFile psiFile = PsiDocumentManager.getInstance(currentProject).getPsiFile(editor.getDocument());
        if (psiFile == null) {
            return null;
        }

        PsiElement elementAtCaret = psiFile.findElementAt(editor.getCaretModel().getOffset());
        if (elementAtCaret == null) {
            return null;
        }

        // 查找方法引用
        return PsiTreeUtil.getParentOfType(elementAtCaret, PsiMethod.class);
    }
}
