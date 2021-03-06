package com.svend.dab.core.beans.projects;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Strings;
import com.svend.dab.core.beans.Location;
import com.svend.dab.core.beans.profile.Photo;

/**
 * A {@link ProjectOverview} is not stored anywhere. It is computed by the DAO when searching for project: it contains just the subset of project information we are retrieving when searching for
 * projects
 * 
 * @author svend
 * 
 */
public class ProjectOverview {

	private String projectId;
	private String name;
	private String goal;
	private String description;

	private Date dueDate;
	private Date creationDate;

	private List<String> locationNames = new LinkedList<String>();

	private int numberOfMembers;

	private Photo mainThumb;

	public ProjectOverview() {
		super();
	}

	public ProjectOverview(Project project) {
		projectId = project.getId();
		name = project.getPdata().getName();
		goal = project.getPdata().getGoal();
		description = project.getPdata().getDescription();

		dueDate = project.getPdata().getDueDate();
		creationDate = project.getPdata().getCreationDate();

		numberOfMembers = project.getConfirmedActiveParticipants().size();
		mainThumb = project.getPhotoAlbum().getMainPhoto();

		if (project.getPdata().getLocations() != null) {
			for (Location location : project.getPdata().getLocations()) {
				locationNames.add(location.getLocation());
			}
		}
	}

	public boolean hasLocations() {
		return !locationNames.isEmpty();
	}

	public boolean hasMoreThanOneLocation() {
		return locationNames.size() > 1;
	}

	public void generatePhotoLinks(Date expirationdate) {
		if (mainThumb != null) {
			mainThumb.generatePresignedLinks(expirationdate, false, true);
		}
	}

	public void addLocation(String location) {
		if (!Strings.isNullOrEmpty(location)) {
			locationNames.add(location);
		}
	}

	public boolean hasAThumbPhoto() {
		return mainThumb != null && !mainThumb.isPhotoEmpty();
	}

	public String getMainPhotoThumbLink() {
		if (hasAThumbPhoto()) {
			return mainThumb.getThumbAddress();
		} else {
			return "";
		}
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGoal() {
		return goal;
	}

	public void setGoal(String goal) {
		this.goal = goal;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public int getNumberOfMembers() {
		return numberOfMembers;
	}

	public void setNumberOfMembers(int numberOfMembers) {
		this.numberOfMembers = numberOfMembers;
	}

	public Photo getMainThumb() {
		return mainThumb;
	}

	public void setMainThumb(Photo mainThumb) {
		this.mainThumb = mainThumb;
	}

}
