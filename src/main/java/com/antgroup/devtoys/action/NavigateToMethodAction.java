package com.antgroup.devtoys.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class NavigateToMethodAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project currentProject = e.getProject();
        if (currentProject == null) return;

        // 获取当前编辑器中的文件和光标位置
        PsiFile psiFile = PsiDocumentManager.getInstance(currentProject).getPsiFile(e.getData(CommonDataKeys.EDITOR).getDocument());
        if (psiFile == null) return;

        PsiElement elementAtCaret = psiFile.findElementAt(e.getData(CommonDataKeys.EDITOR).getCaretModel().getOffset());
        if (elementAtCaret == null) return;

        // 查找方法引用
        PsiMethodCallExpression methodCall = PsiTreeUtil.getParentOfType(elementAtCaret, PsiMethodCallExpression.class);
        if (methodCall == null) return;

        PsiMethod method = methodCall.resolveMethod();
        if (method == null) return;

        // 遍历所有打开的项目
        Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
        List<String> foundProjects = new ArrayList<>();

        for (Project project : openProjects) {
            // 在项目中查找目标方法
            PsiClass targetClass = JavaPsiFacade.getInstance(project).findClass(method.getContainingClass().getQualifiedName(), GlobalSearchScope.projectScope(project));
            if (targetClass == null) continue;

            PsiMethod[] targetMethods = targetClass.findMethodsByName(method.getName(), false);

            System.out.println(method.getContainingClass().getQualifiedName() + "#" + method.getName());

            for (PsiMethod targetMethod : targetMethods) {
                if (targetMethod == null) continue;

                // 添加项目名称到列表中
                foundProjects.add(project.getName());

                // 打开目标文件并跳转
                VirtualFile targetFile = targetMethod.getContainingFile().getVirtualFile();
                if (targetFile == null) continue;

                OpenFileDescriptor descriptor = new OpenFileDescriptor(project, targetFile, targetMethod.getTextOffset());
                FileEditorManager.getInstance(project).openTextEditor(descriptor, true);
            }
        }

        // 显示包含项目名称的消息
        if (foundProjects.isEmpty()) {
            Messages.showMessageDialog(currentProject, "No implementations found in any open projects.", "Navigate to Method", Messages.getInformationIcon());
        } else {
            StringBuilder message = new StringBuilder("Found implementations in the following projects:\n");
            for (String projectName : foundProjects) {
                message.append(projectName).append("\n");
            }
            Messages.showMessageDialog(currentProject, message.toString(), "Navigate to Method", Messages.getInformationIcon());
        }
    }
}