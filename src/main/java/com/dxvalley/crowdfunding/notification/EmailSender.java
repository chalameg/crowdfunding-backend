
package com.dxvalley.crowdfunding.notification;

import com.dxvalley.crowdfunding.models.Campaign;

public interface EmailSender {
     Boolean send(String to, String email,String subject);
     String buildEmail(String name, String link);
     String emailBuilderForPasswordReset(String name, String link);
     String buildEmailInvitation(String name, String senderName, String campaign, String link);
     String invitationDetailPage(Campaign campaign, String link);
     String invitationAccepted();
     String invitationRejected();
}
