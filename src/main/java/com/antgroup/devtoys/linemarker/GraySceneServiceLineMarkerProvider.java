package com.antgroup.devtoys.linemarker;

import com.antgroup.devtoys.util.Constant;
import com.antgroup.devtoys.util.DrmUtil;
import com.antgroup.devtoys.util.MyIcons;
import com.google.common.collect.Lists;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlFile;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;


/**
 * 灰度场景的linemarker
 */
final class GraySceneServiceLineMarkerProvider extends RelatedItemLineMarkerProvider {

    // 日志
    private static final Logger LOG = Logger.getInstance(GraySceneServiceLineMarkerProvider.class);

    private static final String GRAY_SERVICE_KEYWORD = "graySceneService.";

    private static final String[] fromPackagesPrefix = {"com.alipay", "com.mybank"};


    private boolean isGrayServiceInMethod(PsiElement element) {
        if (!StringUtils.equals(JavaFileType.INSTANCE.getName(),
                element.getContainingFile().getFileType().getName())) {
            return false;
        }
        if (!(element instanceof PsiReferenceExpression)) {
            return false;
        }
        String identifier = element.getText();
        return StringUtils.startsWith(identifier, GRAY_SERVICE_KEYWORD);
    }

    private PsiIdentifier getGrayMethod(PsiElement element) {
        for (PsiElement nxt : element.getChildren()) {
            if (nxt instanceof PsiIdentifier) {
                return (PsiIdentifier) nxt;
            }
        }
        return null;
    }

    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
        if (!isGrayServiceInMethod(element)) {
            return;
        }

        PsiIdentifier identifier = getGrayMethod(element);
        if (identifier == null) {
            LOG.info("getGrayMethod: not found identifier");
            return;
        }
        String attribute = identifier.getText();
        boolean on = DrmUtil.isOn(element.getProject().getName(),
                Constant.GRAY_RESOURCE_ID,
                attribute,
                Constant.DEFAULT_ZONE_NAME,
                Constant.DEFAULT_VERSION);

        if (on) {
            NavigationGutterIconBuilder<PsiElement> builder = NavigationGutterIconBuilder.create(
                            MyIcons.GREEN_DOT)
                    .setTargets(element)
                    .setTooltipText("On");
            result.add(builder.createLineMarkerInfo(identifier));
        } else {
            NavigationGutterIconBuilder<PsiElement> builder2 = NavigationGutterIconBuilder.create(
                            MyIcons.BLUE_DOT)
                    .setTargets(element)
                    .setTooltipText("Off");
            result.add(builder2.createLineMarkerInfo(identifier));
        }
    }

    /**
     * 获取跳转目标
     *
     * @param fromElement
     * @return
     */
    private Collection<PsiElement> getJumpTargets(PsiReferenceExpression fromElement) {

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