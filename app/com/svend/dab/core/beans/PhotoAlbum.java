package com.svend.dab.core.beans;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.springframework.data.annotation.Transient;

import play.mvc.Router;
import play.vfs.VirtualFile;

import com.svend.dab.core.beans.profile.Photo;

public class PhotoAlbum {

	private List<Photo> photos;

	private final String photoS3RootFolder;
	private final String thumbS3RootFolder;
	private int mainPhotoIndex;

	
	////////////////////////////
	@Transient
	public int maxNumberOfPhotos = 20;

	@Transient
	private String defaultMainPhoto;
	
	// this arrays is guaranteed to contain exactly "maxNumberOfPhotos" photos => the "photos" contain less, we just top up with empty photos
	// (but only the "photos" are persisted: no need to persist the supplementary placeholders
	@Transient
	private List<Photo> photoCompleteList;


	public PhotoAlbum(String photoS3RootFolder, String thumbS3RootFolder) {
		super();
		this.photoS3RootFolder = photoS3RootFolder;
		this.thumbS3RootFolder = thumbS3RootFolder;
	}

	// ////////////////////////////////////
	//

	public String getMainPhotoAddress() {

		if (getMainPhoto().getNormalPhotoAddress() == null) {
			return Router.reverse(VirtualFile.fromRelativePath(defaultMainPhoto), false);
		} else {
			return getMainPhoto().getNormalPhotoAddress();
		}

	}

	/**
	 * @return
	 */
	public Photo getMainPhoto() {

		if (photos != null && !photos.isEmpty() ) {
			
			if (mainPhotoIndex < photos.size()) {
				return photos.get(mainPhotoIndex);
			} else {
				// TODO: log warning here
				return new Photo();
			}
		}

		return new Photo();
	}

	public List<Photo> getCompletList() {
		
		if (photoCompleteList == null) {
			photoCompleteList = new LinkedList<Photo>();
	
			if (photos != null) {
				for (int copiedPhotoIndex = 0 ; copiedPhotoIndex < maxNumberOfPhotos && copiedPhotoIndex < photos.size(); copiedPhotoIndex++) {
					photoCompleteList.add(photos.get(copiedPhotoIndex));
				}
			}
	
			// filling up to "maxNumberOfPhotos" (if required)
			while (photoCompleteList.size() < maxNumberOfPhotos) {
				photoCompleteList.add(new Photo());
			}
		}

		return photoCompleteList;
	}
	
	
	public void generatePhotoLinks(Date expirationdate) {
		for (Photo photo : getCompletList()) {
			photo.generatePresignedLinks(expirationdate, true, true);
		}
	}


	public boolean isFull() {
		return photos != null && photos.size() > maxNumberOfPhotos;
	}
	
	
	public boolean hashMoreThanOnePhoto() {
		return photos != null && photos.size() > 1;
	}


	public String getPhotoS3RootFolder() {
		return photoS3RootFolder;
	}

	public String getThumbsS3RootFolder() {
		return thumbS3RootFolder;
	}

	public boolean isPhotoPackEmpty() {
		return photos == null || photos.size() == 0;
	}

	public Photo getPhoto(int photoIdx) {
		if (isPhotoPackEmpty() || photos.size() <= photoIdx) {
			return null;
		}
		return photos.get(photoIdx);
	}

	// //////////////////////////////////////
	// ////////////////////////////////////////

	public int getMaxNumberOfPhotos() {
		return maxNumberOfPhotos;
	}

	public void setMaxNumberOfPhotos(int maxNumberOfPhotos) {
		this.maxNumberOfPhotos = maxNumberOfPhotos;
	}

	public String getDefaultMainPhoto() {
		return defaultMainPhoto;
	}

	public void setDefaultMainPhoto(String defaultMainPhoto) {
		this.defaultMainPhoto = defaultMainPhoto;
	}

	public int getMainPhotoIndex() {
		return mainPhotoIndex;
	}

	public void setMainPhotoIndex(int mainPhotoIndex) {
		this.mainPhotoIndex = mainPhotoIndex;
	}

	public String getThumbS3RootFolder() {
		return thumbS3RootFolder;
	}

	public List<Photo> getPhotos() {
		return photos;
	}

	public void setPhotos(List<Photo> photos) {
		this.photos = photos;
	}



}
