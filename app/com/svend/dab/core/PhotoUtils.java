package com.svend.dab.core;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.io.ByteStreams;
import com.svend.dab.core.beans.Config;
import com.svend.dab.core.beans.DabUploadFailedException;
import com.svend.dab.core.beans.DabUploadFailedException.failureReason;
import com.svend.dab.core.beans.aws.S3Link;
import com.svend.dab.core.beans.profile.Photo;
import com.svend.dab.dao.aws.s3.AwsS3CvDao;

@Component
public class PhotoUtils {

	private static Logger logger = Logger.getLogger(PhotoUtils.class.getName());

	public static String JPEG_MIME_TYPE = "image/jpeg";

	public static int NORMAL_PHOTO_MAX_GREATEST_DIMENSION = 800;

	// keeping 160px and not 80 in order to improve image quality of the thumbnail
	public static int THUMB_PHOTO_MAX_GREATEST_DIMENSION = 160;

	@Autowired
	private Config config;

	
	public Photo createOnePhotoPlaceholder(String photoRootFolder, String thumbRootFolder) {

		String photoId = UUID.randomUUID().toString().replace("-", "");

		S3Link normalPhotoLink = new S3Link();
		normalPhotoLink.setS3Key(photoRootFolder + photoId);
		normalPhotoLink.setS3BucketName(AwsS3CvDao.DEFAULT_S3_BUCKET);

		S3Link thumbPhotoLink = new S3Link();
		thumbPhotoLink.setS3Key(thumbRootFolder + photoId);
		thumbPhotoLink.setS3BucketName(AwsS3CvDao.DEFAULT_S3_BUCKET);

		return new Photo("", normalPhotoLink, thumbPhotoLink);
	}

	
	
//	/**
//	 * @param photoContent
//	 * @return
//	 * @throws DabUploadFailedException
//	 */
//	public byte[] readPhotoContent(File photoContent) throws DabUploadFailedException {
//
//		InputStream photoContentStream = null;
//
//		try {
//
//			photoContentStream = new BufferedInputStream(new FileInputStream(photoContent));
//
//			if (photoContentStream.available() == 0 || photoContentStream.available() > config.getMaxUploadedPhotoSizeInBytes()) {
//				throw new DabUploadFailedException("Photo size is too big", failureReason.fileTooBig);
//			}
//
//			byte[] receivedPhoto = new byte[photoContentStream.available()];
//			ByteStreams.readFully(photoContentStream, receivedPhoto);
//			return receivedPhoto;
//
//		} catch (IOException e) {
//			throw new DabUploadFailedException("Could not upload photo" + failureReason.fileFormatIncorrectError, e);
//		} finally {
//			try {
//				photoContentStream.close();
//			} catch (IOException e) {
//				logger.log(Level.WARNING, "Could not close uploaded content stream", e);
//			}
//		}
//
//	}

	public byte[] resizePhotoToThumbSize(BufferedImage photoContent) {
		return resizePhotoToTargetSize(photoContent, THUMB_PHOTO_MAX_GREATEST_DIMENSION);
	}

	public byte[] resizePhotoToNormalSize(BufferedImage photo) {
		return resizePhotoToTargetSize(photo, NORMAL_PHOTO_MAX_GREATEST_DIMENSION);
	}

	
	
	public byte[] resizePhotoToTargetSize(BufferedImage image, int targetMaxDimension) {
		
		ByteArrayOutputStream baos = null;
		Graphics2D graphic = null;
		
		try {
			
			if (image == null) {
				throw new DabUploadFailedException("cannot read this image", failureReason.fileFormatIncorrectError);
			}
			
			double scaleCoef = computeCoef(image.getWidth(), image.getHeight(), targetMaxDimension);
			
			int newWidth = (int) (image.getWidth() * scaleCoef);
			int newHeight = (int) (image.getHeight() * scaleCoef);
			
			int type = image.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : image.getType();
			BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, type);
			
			graphic = resizedImage.createGraphics();
			graphic.setComposite(AlphaComposite.Src);
			
			graphic.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			graphic.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			graphic.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			graphic.drawImage(image, 0, 0, newWidth, newHeight, null);
			
			baos = new ByteArrayOutputStream();
			ImageIO.write(resizedImage, "jpg", baos);
			byte [] result = baos.toByteArray(); 
			return result;
			
		} catch (IOException e) {
			throw new DabUploadFailedException("", failureReason.technicalError, e);
			
		} finally {
			
			if (graphic != null) {
				graphic.dispose();
			}

			if (baos != null) {
				try {
					baos.close();
				} catch (Exception e) {
					logger.log(Level.WARNING, "Error while trying to close stream, ignoring...", e);
				}
			}
			
			
		}
	}

	/**
	 * @param width
	 * @param height
	 * @param targetMaxDimension
	 * @return
	 */
	public double computeCoef(int width, int height, int targetMaxDimension) {

		if (width == 0 || height == 0) {
			// not scaling a 0 sized image
			return 1d;
		}

		if (width > targetMaxDimension || height > targetMaxDimension) {

			if (width > height) {
				return (double) targetMaxDimension / width;
			} else {
				return (double) targetMaxDimension / height;
			}

		} else {
			return 1d;
		}
	}

	public void setConfig(Config config) {
		this.config = config;
	}








}
