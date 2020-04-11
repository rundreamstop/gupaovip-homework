package com.gupaovip_homework.v3.webmvc.servlet;

import lombok.Data;

import java.io.File;

/**
 * @Author: Ray Allen  @Time:2020/4/10 0010
 */
@Data
public class RunViewResolver {

    private final String DEFAULT_TEMPLATE_SUFFIX = ".run";

    private File templateRootDir;

    public RunViewResolver(String templateRoot) {
        String templateRootPath = this.getClass().getClassLoader().
                getResource(templateRoot).getFile();
        templateRootDir = new File(templateRootPath);

    }

    public RunView resolverViewName(String viewName) {
        if (viewName == null || "".equals(viewName.trim())) {
            return null;
        }
        viewName = viewName.endsWith(DEFAULT_TEMPLATE_SUFFIX) ?
                viewName : (viewName + DEFAULT_TEMPLATE_SUFFIX);
        File templateFile = new File((templateRootDir.getPath() + "/" + viewName).
                replaceAll("/+", "/"));
        return new RunView(templateFile);
    }
}
