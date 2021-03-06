package com.svend.dab.eda.events.s3;

import com.svend.dab.core.beans.DabIllegalFormatException;
import com.svend.dab.core.beans.aws.S3Link;
import com.svend.dab.core.beans.profile.ExternalLink;
import com.svend.dab.dao.aws.s3.DabS3AccessException;
import com.svend.dab.eda.Event;
import com.svend.dab.eda.IEventPropagator;
import com.svend.dab.eda.IEventPropagatorsContainer;

/**
 * @author svend
 * 
 */
public class BinaryNoLongerRequiredEvent extends Event {

	private String binaryBucket;

	private String binaryKey;

	

	// -----------------------------------------
	// -----------------------------------------

	public BinaryNoLongerRequiredEvent() {
	}

	public BinaryNoLongerRequiredEvent(ExternalLink cvLink) {
		if (cvLink == null) {
			throw new DabS3AccessException("cannot build a BinaryNoLongerRequiredEvent from null link");
		}
		
		if (cvLink instanceof S3Link) {
			binaryBucket = ((S3Link) cvLink).getS3BucketName();
			binaryKey = ((S3Link) cvLink).getS3Key();
		} else {
			throw new DabIllegalFormatException("Can only build a BinaryNoLongerRequiredEvent instance based on a S3Link (we need bucket and key name information");
		}
	}

	// -----------------------------------------
	// -----------------------------------------

	@Override
	public IEventPropagator<BinaryNoLongerRequiredEvent> selectEventProcessor(IEventPropagatorsContainer container) {
		return container.getPropagatorByName("binaryNoLongerRequiredPropagator");
	}

	public String getBinaryBucket() {
		return binaryBucket;
	}

	public void setBinaryBucket(String binaryBucket) {
		this.binaryBucket = binaryBucket;
	}

	public String getBinaryKey() {
		return binaryKey;
	}

	public void setBinaryKey(String binaryKey) {
		this.binaryKey = binaryKey;
	}

}
