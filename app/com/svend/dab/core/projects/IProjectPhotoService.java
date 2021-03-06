package com.svend.dab.core.projects;

import java.io.File;

import com.svend.dab.core.beans.projects.Project;

/**
 * @author svend
 *
 */
public interface IProjectPhotoService {

	
	public abstract void addOnePhoto(String projectId, File photoContent);

	public abstract void removeProjectPhoto(Project project, int deletedPhotoIdx);

	public abstract void replacePhotoCaption(Project project, int photoIndex, String photoCaption);

	public abstract void putPhotoInFirstPositio(Project project, int photoIndex);

}
