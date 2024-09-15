package com.antgroup.devtoys.mybank;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;

@Service(Service.Level.PROJECT)
public final class DrmViewerService {

    private Project myProject;

    public DrmViewerService(Project project) {
        this.myProject = project;
    }
}
