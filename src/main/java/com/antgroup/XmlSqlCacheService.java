package com.antgroup;


import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;

@Service(Service.Level.PROJECT)
public final class XmlSqlCacheService {
    private final Project myProject;

    public XmlSqlCacheService(Project project) {
        myProject = project;
    }


}
