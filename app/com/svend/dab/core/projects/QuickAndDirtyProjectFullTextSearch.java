package com.svend.dab.core.projects;


import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import web.utils.Utils;

import com.google.common.base.Strings;
import com.svend.dab.core.beans.Config;
import com.svend.dab.core.beans.projects.IndexedProject;
import com.svend.dab.core.beans.projects.Project;
import com.svend.dab.core.beans.projects.ProjectOverview;
import com.svend.dab.core.beans.projects.ProjectSearchRequest;
import com.svend.dab.core.beans.projects.SelectedTheme;
import com.svend.dab.core.dao.IIndexedProjectDao;
import com.svend.dab.dao.mongo.IProjectDao;

/**
 * Quick and dirty (and I'm drunk...) version of a full text search index for projects.
 * 
 * to be replaced by a Solr index some time...
 * 
 * @author svend
 *
 */
@Service
public class QuickAndDirtyProjectFullTextSearch implements IProjectFTSService {

	
	@Autowired
	private IProjectDao projectDao;
	
	@Autowired
	private IIndexedProjectDao indexedProjectDao;
	
	@Autowired
	private Config config;


	private static Logger logger = Logger.getLogger(QuickAndDirtyProjectFullTextSearch.class.getName());
	
	
	@Override
	public void updateProjetIndex(String projectId, boolean immediate) {
		

		if (!immediate) {
			// increasing the chances to actually catch the updated data in db (you know, eventual consistency thingy...)
			// ...I said this class was quick and dirty....
			Utils.waitABit();
		}

		Project project = projectDao.findOne(projectId);
		
		if (project == null) {
			logger.log(Level.WARNING, "cannot update full text search of non existant project : " + projectId);
		} else {
			
			IndexedProject ip = new IndexedProject();
			ip.setProjectId(projectId);
			ip.setTags(project.getTags());
			
			if (project.getThemes() != null) {
				for (SelectedTheme theme : project.getThemes()) {
					ip.addSelectedTheme(theme);
				}
			}
			
			if (!Strings.isNullOrEmpty(project.getPdata().getName())) {
				StringTokenizer st = new StringTokenizer(project.getPdata().getName());
				while (st.hasMoreTokens()) {
					ip.addTerm(st.nextToken());
				}
			}

			if (!Strings.isNullOrEmpty(project.getPdata().getDescription())) {
				StringTokenizer st = new StringTokenizer(project.getPdata().getDescription());
				while (st.hasMoreTokens()) {
					ip.addTerm(st.nextToken());
				}
			}
			
			if (!Strings.isNullOrEmpty(project.getPdata().getGoal())) {
				StringTokenizer st = new StringTokenizer(project.getPdata().getGoal());
				while (st.hasMoreTokens()) {
					ip.addTerm(st.nextToken());
				}
			}
			
			indexedProjectDao.updateIndex(ip);
			
		}
	}

	
	@Override
	public List<ProjectOverview> searchForProjects(ProjectSearchRequest request) {
		
		List<IndexedProject> ips = indexedProjectDao.searchForProjects(request);
		
		
		Set<String> allIds = new HashSet<String>();
		
		for (IndexedProject ip : ips) {
			allIds.add(ip.getProjectId());
		}
		
		List<Project> projects = projectDao.loadAllProjects(allIds);
		
		List<ProjectOverview> projectOverview = new LinkedList<ProjectOverview>();
		
		if (projects != null) {
			for (Project project : projects) {
				projectOverview.add(new ProjectOverview(project));
			}
		}
		
		if (projectOverview != null) {
			Date expirationdate = new Date();
			expirationdate.setTime(expirationdate.getTime() + config.getCvExpirationDelayInMillis());
			for (ProjectOverview overview :projectOverview) {
				overview.generatePhotoLinks(expirationdate);
			}
		}
		
		return projectOverview;
		
	}

}