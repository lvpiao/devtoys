// Copyright 2000-2024 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.

package com.antgroup.devtoys.listener;

import com.antgroup.devtoys.service.ProjectCountingService;
import com.antgroup.devtoys.util.EasyUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import org.jetbrains.annotations.NotNull;

/**
 * Listener to detect project closing.
 */
final class ProjectCloseListener implements ProjectManagerListener {

    @Override
    public void projectClosed(@NotNull Project project) {
        // Get the counting service
        ProjectCountingService projectCountingService = EasyUtil.getService(ProjectCountingService.class);
        System.out.println("Project closed, left opened projects: " + projectCountingService.decreaseOpenProjectCount());
    }

}