package controllers.projects;

import models.altermotif.projects.WebSearchRequest;
import controllers.BeanProvider;
import controllers.DabController;

/**
 * @author svend
 *
 */
public class ProjectsResults extends DabController {

	public static void projectsResults(WebSearchRequest r) {
		renderArgs.put("projectsSummaries", BeanProvider.getProjectService().searchForProjects(r.toBackendRequest()));
		render();
	}

}