package com.svend.dab.core.dao;

import java.util.List;
import java.util.Set;

import com.svend.dab.core.beans.PhotoAlbum;
import com.svend.dab.core.beans.groups.GroupSummary;
import com.svend.dab.core.beans.profile.Photo;
import com.svend.dab.core.beans.profile.UserSummary;
import com.svend.dab.core.beans.projects.Asset;
import com.svend.dab.core.beans.projects.Participant;
import com.svend.dab.core.beans.projects.Participant.ROLE;
import com.svend.dab.core.beans.projects.Project;
import com.svend.dab.core.beans.projects.Project.STATUS;
import com.svend.dab.core.beans.projects.ProjectOverview;
import com.svend.dab.core.beans.projects.SearchQuery;
import com.svend.dab.core.beans.projects.SearchQuery.SORT_KEY;
import com.svend.dab.core.beans.projects.Task;

/**
 * @author Svend
 *
 */
public interface IProjectDao {
	void updateProjectParticipation(String projectId, UserSummary updatedSummary);

	Project findOne(String projectId);
	
	List<Project> loadAllProjects(Set<String> allIds, SORT_KEY sortkey);

	Set<String> getAllProjectIds();


	void save(Project project);

	void updateProjectPDataAndLinksAndTagsAndThemes(String id, Project project);
	
	void updateProjectStatus(String id, STATUS newStatus);
	
	List<ProjectOverview> searchProjects(SearchQuery request);

	// project photos

	
	/**
	 * Updates the complet photo album: usage of this is discourage: prefer the finer-grained photo related methods
	 * @param id
	 * @param photoAlbum
	 */
	void updatePhotoAlbum(String id, PhotoAlbum photoAlbum);
	
	void addOnePhoto(String id, Photo newPhoto);

	void removeOnePhoto(String id, Photo removed);

	void removeOnePhotoAndResetMainPhotoIndex(String id, Photo removed);
	
	void removeOnePhotoAndDecrementMainPhotoIndex(String id, Photo removed);
	
	void updatePhotoCaption(String id, String s3PhotoKey, String photoCaption);

	void movePhotoToFirstPosition(String id, int mainPhotoIndex);
	
	
	///////////////////////////
	// project participants

	void addOneParticipant(String projectId, Participant createdParticipant);

	void updateParticipantList(String projectId, List<Participant> newPList);

	void markParticipantAsAccepted(String projectId, String participantId);

	void removeParticipant(String projectId, String userId);

	void updateParticipantRole(String projectId, String userId, ROLE role);
	
	/**
	 * @param projectId
	 * @return a project with only the participants (if any)
	 */
	public Project loadProjectParticipants(String projectId);

	void updateOwnerShipProposed(String projectId, String userName, boolean b);
	
	////////////////////////////////////
	// tags
	
	public void launchCountProjectTagsJob();

	////////////////////////////////////
	// tasks and assets
	
	void addOrUpdateProjectTasks(String id, Task newOrUpdatedTask);

	void removeTaskFromProject(String id, String removedTasksId);

	void addOrUpdateProjectAsset(String id, Asset newOrUpdatedAsset);

	void removeAssetFromProject(String id, String removedAssetId);

	
	////////////////////////////////////
	// participation of this project in groups
	
	void addOneGroup(String projectId, GroupSummary groupSummary);

	void removeParticipationInGroup(String projectId, String groupId);

	void updateGroupSummaryOfAllProjectsPartOf(GroupSummary updatedSummary);

}
