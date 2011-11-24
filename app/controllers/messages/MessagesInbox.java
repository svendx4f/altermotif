package controllers.messages;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import models.altermotif.messages.MessagesPage;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.svend.dab.core.beans.message.UserMessage;

import controllers.BeanProvider;
import controllers.DabLoggedController;

public class MessagesInbox extends DabLoggedController {

    public static void messagesInbox() {
        render();
    }
    
    
    
	public static void loadInboxMessages(int pageNumber) {

		List<UserMessage> inboxMessages = BeanProvider.getMessagesService().getReceivedMessages(getSessionWrapper().getLoggedInUserProfileId(), pageNumber);

		
		MessagesPage page = new MessagesPage();
		page.setMessages(inboxMessages);
		
		
		JsonSerializer<Date> serializer = new JsonSerializer<Date>() {
			@Override
			public JsonElement serialize(final Date date, Type arg1, JsonSerializationContext arg2) {
				return new JsonPrimitive(new SimpleDateFormat("dd/MM/yyyy - HH:mm").format(date));
			}
		};
		
		renderJSON(page, serializer);
		
		//
//		// automaticallly moves forward until the desired message id is displayed (if any)
//		// TODO: there must be a way to jump to the corrent message directly
//		if (getMessageSession().getToMessageId() != null) {
//			String mandatoryVisibleMessageId = getMessageSession().getToMessageId();
//			getMessageSession().setToMessageId(null);
//
//			while (!containsMessageId(inboxMessages, mandatoryVisibleMessageId) && inboxMessages.hasNextPage()) {
//				nextInboxMessagePage();
//			}
//		}

	}


}
