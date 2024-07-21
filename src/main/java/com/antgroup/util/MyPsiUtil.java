package com.antgroup.util;

import com.intellij.psi.PsiElement;

public class MyPsiUtil {

    /**
     * 返回第一个叶子节点
     */
    public static PsiElement getFirstLeafNode(PsiElement element) {
        while (element != null && element.getFirstChild() != null) {
            element = element.getFirstChild();
        }
        return element;
    }
}
