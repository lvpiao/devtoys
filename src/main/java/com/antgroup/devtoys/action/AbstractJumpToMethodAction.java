package com.antgroup.devtoys.action;

import com.intellij.codeInsight.navigation.NavigationUtil;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractJumpToMethodAction extends AnAction {

    abstract PsiMethod getPsiMethod(AnActionEvent e);

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        PsiMethod method = getPsiMethod(e);
        if (method == null || e.getProject() == null) return;

        // 遍历所有打开的项目
        Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
        List<PsiMethod> foundMethods = new ArrayList<>();

        for (Project project : openProjects) {
            if (StringUtils.equals(project.getName(), e.getProject().getName())) {
                continue;
            }

            // 在项目中查找目标方法
            PsiClass targetClass = JavaPsiFacade.getInstance(project).findClass(method.getContainingClass().getQualifiedName(), GlobalSearchScope.projectScope(project));
            if (targetClass == null) continue;

            PsiMethod[] targetMethods = targetClass.findMethodsByName(method.getName(), false);

            for (PsiMethod targetMethod : targetMethods) {
                if (targetMethod == null) continue;

                // 添加找到的方法和项目到列表中
                foundMethods.add(targetMethod);
            }
        }

        // 显示包含项目名称和方法名的对话框
        if (foundMethods.isEmpty()) {
            Messages.showMessageDialog(e.getProject(), "No methods found in any open projects.", "Navigate to Method", Messages.getInformationIcon());
        } else {

            if (foundMethods.size() == 1) {
                // 只有一个方法时，直接导航
                NavigationUtil.activateFileWithPsiElement(foundMethods.get(0));
            } else {
                PsiMethod[] methods = foundMethods.toArray(PsiMethod[]::new);
                // 创建并显示弹出窗口
                JBPopup popup = NavigationUtil.getPsiElementPopup(methods, "Select a method to navigate to");
                Editor editor = e.getData(CommonDataKeys.EDITOR);
                if (editor == null) {
                    return;
                }
                popup.showInBestPositionFor(editor);
            }
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Presentation presentation = e.getPresentation();
        PsiMethod method = getPsiMethod(e);
        if (method == null) {
            presentation.setEnabledAndVisible(false);
            return;
        }

        // 遍历所有打开的项目，检查是否存在目标方法
        Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
        boolean found = false;

        for (Project project : openProjects) {
            PsiClass targetClass = JavaPsiFacade.getInstance(project).findClass(method.getContainingClass().getQualifiedName(), GlobalSearchScope.projectScope(project));
            if (targetClass != null) {
                PsiMethod[] targetMethods = targetClass.findMethodsByName(method.getName(), false);
                if (targetMethods.length > 0) {
                    found = true;
                    break;
                }
            }
        }

        presentation.setText("跳转到" + method.getName());
        presentation.setEnabledAndVisible(found);
    }


    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

}
