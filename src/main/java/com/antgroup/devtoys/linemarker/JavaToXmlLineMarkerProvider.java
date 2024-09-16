package com.antgroup.devtoys.linemarker;

import com.antgroup.devtoys.util.MyIcons;
import com.google.common.collect.Lists;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlFile;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;


final class JavaToXmlLineMarkerProvider extends RelatedItemLineMarkerProvider {

    // 日志
    private static final Logger LOG = Logger.getInstance(JavaToXmlLineMarkerProvider.class);


    private static final String[] fromPackagesPrefix = {"com.alipay", "com.mybank"};

    private boolean isJumpElement(PsiElement element) {
        if (!(element instanceof PsiIdentifier)) {
            return false;
        }
        PsiFile psiFile = element.getContainingFile();

        return true;
    }

    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
        if (!isJumpElement(element)) {
            return;
        }

        Collection<PsiElement> targets = getJumpTargets((PsiIdentifier) element);
        if (CollectionUtils.isEmpty(targets)) {
            return;
        }

        NavigationGutterIconBuilder<PsiElement> builder = NavigationGutterIconBuilder.create(
                        MyIcons.GREEN_DOT)
                .setTargets(targets)
                .setTooltipText("Jump to SQL");

        result.add(builder.createLineMarkerInfo(element));

        NavigationGutterIconBuilder<PsiElement> builder2 = NavigationGutterIconBuilder.create(
                        MyIcons.BLUE_DOT)
                .setTargets(targets)
                .setTooltipText("Jump to SQL");

        result.add(builder2.createLineMarkerInfo(element));
    }

    /**
     * @param fromElement
     * @return
     */
    private Collection<PsiElement> getJumpTargets(PsiIdentifier fromElement) {

        Project project = fromElement.getProject();
        Collection<VirtualFile> virtualFiles = FileTypeIndex.getFiles(XmlFileType.INSTANCE, GlobalSearchScope.projectScope(project));

        List<PsiElement> psiElements = Lists.newArrayList();
        for (VirtualFile virtualFile : virtualFiles) {
            XmlFile xmlFile = (XmlFile) PsiManager.getInstance(project).findFile(virtualFile);
            if (xmlFile == null) {
                continue;
            }
            String psiDirectory = xmlFile.getVirtualFile().getPath();
            if (!StringUtils.contains(psiDirectory, "src")) {
                continue;
            }

            psiElements.addAll(Lists.newArrayList(PsiTreeUtil.collectElements(xmlFile, (x) -> {
                if (!(x instanceof XmlAttribute xmlAttribute)) {
                    return false;
                }
                if (!x.isValid()) {
                    return false;
                }
                return StringUtils.equals(xmlAttribute.getValue(), fromElement.getText());
            })));

        }
        return psiElements;
    }

}