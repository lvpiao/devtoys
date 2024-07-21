package com.antgroup.listener;

import com.antgroup.service.ProjectCountingService;
import com.antgroup.util.EasyUtil;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.ProjectActivity;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProjectOpenedListener implements ProjectActivity {

    private static final Logger LOG = Logger.getInstance(ProjectOpenedListener.class);

    @Nullable
    @Override
    public Object execute(@NotNull Project project, @NotNull Continuation<? super Unit> continuation) {
        ProjectCountingService projectCountingService = EasyUtil.getService(ProjectCountingService.class);
        LOG.info("opened projects: " + projectCountingService.increaseOpenProjectCount());
        System.out.println("已经打开的项目数=" + projectCountingService.getOpenedProjectCount());
        return null;
    }
}
