
package com.dxvalley.crowdfunding.email;

import com.dxvalley.crowdfunding.models.Campaign;

public interface EmailSender {
     void send(String to, String email,String subject);
     String buildEmail(String name, String link);
     String emailBuilderForPasswordReset(String name, String link);
     String emailConfirmed(String name);
     String buildEmailInvitation(String name, String senderName, String campaign, String link);
     String invitationDetailPage(Campaign campaign, String link);
     String invitationAccepted();
     String invitationRejected();
}
