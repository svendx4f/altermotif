package com.svend.dab.eda.events.projects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.svend.dab.core.beans.DabException;
import com.svend.dab.core.beans.DabPreConditionViolationException;
import com.svend.dab.core.beans.profile.UserProfile;
import com.svend.dab.core.beans.projects.Participant.ROLE;
import com.svend.dab.core.beans.projects.Participation;
import com.svend.dab.core.beans.projects.Project;
import com.svend.dab.core.dao.IProjectDao;
import com.svend.dab.core.dao.IProjectIndexDao;
import com.svend.dab.core.dao.IUserProfileDao;
import com.svend.dab.eda.IEventPropagator;

/**
 * @author Svend
 *
 */
@Component("projectCreatedPropagator")
public class ProjectCreatedPropagator implements IEventPropagator<ProjectCreated> {

	@Autowired
	private IUserProfileDao userProfileRepo;
	
	@Autowired
	private IProjectDao projetRepo;
	
	@Autowired
	private IProjectIndexDao projectIndexDao;
	
	public void propagate(ProjectCreated event) throws DabException {
		
		Project project = event.getCreatedProject();
		UserProfile creatorProfile = userProfileRepo.retrieveUserProfileById(event.getCreatorId());
		
		if (creatorProfile == null ) {
			throw new DabPreConditionViolationException("Cannot propagate ProjectCreated event: creator profile does not exist: " + event.getCreatorId());
		}
		
		project.addParticipant(ROLE.initiator, creatorProfile, "");
		projetRepo.save(project);
		userProfileRepo.addProjectParticipation(creatorProfile, new Participation(project, ROLE.initiator, true));
		
		projectIndexDao.updateIndex(event.getCreatedProject().getId(), false);
		
	}
}
