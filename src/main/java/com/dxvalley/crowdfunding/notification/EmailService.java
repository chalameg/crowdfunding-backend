package com.dxvalley.crowdfunding.notification;

public interface EmailService {
     Boolean send(String to, String email,String subject);
     String emailBuilderForUserConfirmation(String name, String link);
     String emailBuilderForPasswordReset(String name, String link);
     String emailBuilderForCollaborationInvitation(String name, String senderName, String campaign, String link);
}
