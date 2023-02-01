package com.dxvalley.crowdfunding.services;

import com.dxvalley.crowdfunding.email.EmailSender;
import com.dxvalley.crowdfunding.models.Campaign;
import com.dxvalley.crowdfunding.models.Role;
import com.dxvalley.crowdfunding.models.Users;
import com.dxvalley.crowdfunding.models.ConfirmationToken;
import com.dxvalley.crowdfunding.repositories.RoleRepository;
import com.dxvalley.crowdfunding.repositories.UserRepository;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserRegistrationService {
        private final ConfirmationTokenService confirmationTokenService;
        private final EmailSender emailSender;
        private final UserRepository userRepository;
        private final RoleRepository roleRepo;
        private final PasswordEncoder passwordEncoder;

        public ResponseEntity<?> register(Users tempUser) {
                var user = userRepository.findUser(tempUser.getUsername());
                System.out.println(user);
                if (user != null){
                        return new ResponseEntity<>("user already exists", HttpStatus.BAD_REQUEST);
                }

                // validate email and phone number before registration
                if (tempUser.getUsername().matches(".*[a-zA-Z]+.*")) {

                        // validate email
                } else {
                        if (tempUser.getUsername().length() < 9) {
                                createUserResponse response = new createUserResponse("error",
                                        "Please Inter a valid Mobile number!");
                                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
                        }
                }

                List<Role> roles = new ArrayList<Role>(1);
                roles.add(this.roleRepo.findByRoleName("user"));
                tempUser.setRoles(roles);
                tempUser.setPassword(passwordEncoder.encode(tempUser.getPassword()));
                tempUser.setIsEnabled(false);
                userRepository.save(tempUser);

                // if user used email send email else send message to phone
                String token = UUID.randomUUID().toString();
                if (tempUser.getUsername().matches(".*[a-zA-Z]+.*")) {

                        ConfirmationToken confirmationToken = new ConfirmationToken(
                                token,
                                LocalDateTime.now(),
                                LocalDateTime.now().plusMinutes(15),
                                tempUser);

                        confirmationTokenService.saveConfirmationToken(
                                confirmationToken);

                        String link = "http://localhost:8181/api/users/confirm?token=" + token;
                        emailSender.send(
                                tempUser.getUsername(),
                                buildEmail(tempUser.getFullName(), link));
                } else {
                        // send message here
                        ResponseEntity<String> res;
                        try {
                                String uri = "http://10.1.245.150:7080/v1/otp/";
                                RestTemplate restTemplate = new RestTemplate();

                                HttpHeaders headers = new HttpHeaders();
                                headers.setContentType(MediaType.APPLICATION_JSON);

                                // String requestBody = "{\"mobile\":\"251967434568\",\"text\":\"154624\"}";

                                String otp = getRandomNumberString();
                                String requestBody = "{\"mobile\":" + "\"" + tempUser.getUsername() + "\""
                                        + ",\"text\":" + "\"" + otp + "\"" + "}";

                                System.out.println(requestBody);
                                HttpEntity<String> request = new HttpEntity<String>(requestBody, headers);

                                res = restTemplate.exchange(uri, HttpMethod.POST, request, String.class);
                                System.out.println(res.getStatusCode().toString().trim().equals("200 OK"));

                                if (res.getStatusCode().toString().trim().equals("200 OK")) {
                                        // add otp to database
                                        tempUser.setOtp(otp);
                                        userRepository.save(tempUser);

                                        createUserResponse response = new createUserResponse("success",
                                                "Message sent to your phone number.");
                                        return new ResponseEntity<>(response, HttpStatus.OK);
                                } else {
                                        createUserResponse response = new createUserResponse("error",
                                                "Please inter valid Mobile number!");
                                        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
                                }

                        } catch (Exception e) {
                                e.printStackTrace();
                                createUserResponse response = new createUserResponse("error",
                                        "Please insert valid Mobile number!!");
                                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
                        }
                }

                return new ResponseEntity<>(
                        token,
                        HttpStatus.OK);
        }
        
        // "user created successfully! Please check your email to verify "
        @Transactional
        public String confirmToken(String token) {
                ConfirmationToken confirmationToken = confirmationTokenService
                        .getToken(token);

                if (confirmationToken == null) {
                        return "token not found";
                }

                if (confirmationToken.getConfirmedAt() != null) {
                        return "email already confirmed";
                }
                LocalDateTime expiredAt = confirmationToken.getExpiresAt();

                if (expiredAt.isBefore(LocalDateTime.now())) {
                        return "token expired";
                }
                confirmationTokenService.setConfirmedAt(token);
                var username = confirmationToken.getUser().getUsername();
                userRepository.enableUser(username);

                return emailConfirmed(confirmationToken.getUser().getFullName());
        }

        private String buildEmail(String name, String link) {
                return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n"
                        +
                        "\n" +
                        "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
                        "\n" +
                        "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n"
                        +
                        "    <tbody><tr>\n" +
                        "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
                        "        \n" +
                        "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n"
                        +
                        "          <tbody><tr>\n" +
                        "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
                        "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n"
                        +
                        "                  <tbody><tr>\n" +
                        "                    <td style=\"padding-left:10px\">\n" +
                        "                  \n" +
                        "                    </td>\n" +
                        "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n"
                        +
                        "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Confirm your email</span>\n"
                        +
                        "                    </td>\n" +
                        "                  </tr>\n" +
                        "                </tbody></table>\n" +
                        "              </a>\n" +
                        "            </td>\n" +
                        "          </tr>\n" +
                        "        </tbody></table>\n" +
                        "        \n" +
                        "      </td>\n" +
                        "    </tr>\n" +
                        "  </tbody></table>\n" +
                        "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n"
                        +
                        "    <tbody><tr>\n" +
                        "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                        "      <td>\n" +
                        "        \n" +
                        "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n"
                        +
                        "                  <tbody><tr>\n" +
                        "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
                        "                  </tr>\n" +
                        "                </tbody></table>\n" +
                        "        \n" +
                        "      </td>\n" +
                        "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                        "    </tr>\n" +
                        "  </tbody></table>\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n"
                        +
                        "    <tbody><tr>\n" +
                        "      <td height=\"30\"><br></td>\n" +
                        "    </tr>\n" +
                        "    <tr>\n" +
                        "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                        "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n"
                        +
                        "        \n" +
                        "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi "
                        + name
                        + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Thank you for registering. Please click on the below link to activate your account: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\""
                        + link
                        + "\">Activate Now</a> </p></blockquote>\n Link will expire in 15 minutes. <p>See you soon</p>"
                        +
                        "        \n" +
                        "      </td>\n" +
                        "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                        "    </tr>\n" +
                        "    <tr>\n" +
                        "      <td height=\"30\"><br></td>\n" +
                        "    </tr>\n" +
                        "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                        "\n" +
                        "</div></div>";
        }

        private String emailConfirmed(String name) {
                return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n"
                        +
                        "\n" +
                        "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
                        "\n" +
                        "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n"
                        +
                        "    <tbody><tr>\n" +
                        "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
                        "        \n" +
                        "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n"
                        +
                        "          <tbody><tr>\n" +
                        "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
                        "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n"
                        +
                        "                  <tbody><tr>\n" +
                        "                    <td style=\"padding-left:10px\">\n" +
                        "                  \n" +
                        "                    </td>\n" +
                        "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n"
                        +
                        "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Your email is successfully confirmed!</span>\n"
                        +
                        "                    </td>\n" +
                        "                  </tr>\n" +
                        "                </tbody></table>\n" +
                        "              </a>\n" +
                        "            </td>\n" +
                        "          </tr>\n" +
                        "        </tbody></table>\n" +
                        "        \n" +
                        "      </td>\n" +
                        "    </tr>\n" +
                        "  </tbody></table>\n" +
                        "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n"
                        +
                        "    <tbody><tr>\n" +
                        "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                        "      <td>\n" +
                        "        \n" +
                        "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n"
                        +
                        "                  <tbody><tr>\n" +
                        "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
                        "                  </tr>\n" +
                        "                </tbody></table>\n" +
                        "        \n" +
                        "      </td>\n" +
                        "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                        "    </tr>\n" +
                        "  </tbody></table>\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n"
                        +
                        "    <tbody><tr>\n" +
                        "      <td height=\"30\"><br></td>\n" +
                        "    </tr>\n" +
                        "    <tr>\n" +
                        "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                        "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n"
                        +
                        "        \n" +
                        "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hello again,"
                        + name
                        + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> "
                        +
                        "Thank you; your email has been verified. Your account is now active.\n" +
                        "\n" +
                        "Please use the link below to login to your account. </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"http://localhost:3000/login\">Login To Your Account\n</a> </p></blockquote>"
                        +
                        "        \n" +
                        "      </td>\n" +
                        "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                        "    </tr>\n" +
                        "    <tr>\n" +
                        "      <td height=\"30\"><br></td>\n" +
                        "    </tr>\n" +
                        "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                        "\n" +
                        "</div></div>";
        }
        public String buildEmailInvitation(String name,String senderName,String campaign, String link) {
                return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n"
                        +
                        "\n" +
                        "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
                        "\n" +
                        "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n"
                        +
                        "    <tbody><tr>\n" +
                        "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
                        "        \n" +
                        "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n"
                        +
                        "          <tbody><tr>\n" +
                        "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
                        "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n"
                        +
                        "                  <tbody><tr>\n" +
                        "                    <td style=\"padding-left:10px\">\n" +
                        "                  \n" +
                        "                    </td>\n" +
                        "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n"
                        +
                        "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Request for Collaboration\n</span>\n"
                        +
                        "                    </td>\n" +
                        "                  </tr>\n" +
                        "                </tbody></table>\n" +
                        "              </a>\n" +
                        "            </td>\n" +
                        "          </tr>\n" +
                        "        </tbody></table>\n" +
                        "        \n" +
                        "      </td>\n" +
                        "    </tr>\n" +
                        "  </tbody></table>\n" +
                        "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n"
                        +
                        "    <tbody><tr>\n" +
                        "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                        "      <td>\n" +
                        "        \n" +
                        "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n"
                        +
                        "                  <tbody><tr>\n" +
                        "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
                        "                  </tr>\n" +
                        "                </tbody></table>\n" +
                        "        \n" +
                        "      </td>\n" +
                        "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                        "    </tr>\n" +
                        "  </tbody></table>\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n"
                        +
                        "    <tbody><tr>\n" +
                        "      <td height=\"30\"><br></td>\n" +
                        "    </tr>\n" +
                        "    <tr>\n" +
                        "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                        "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n"
                        +
                        "        \n" +
                        "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hello "
                        + name
                        + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">" + senderName+ " has sent you an invitation to be his collaborator on the " + campaign + " campaign.\n" +
                        "Please click on the below link to view invitation detail: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\">" +
                        "<p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\""
                        + link
                        + "\">view invitation</a> </p></blockquote>\n Link will expire in 15 minutes. <p>See you soon</p>"
                        +
                        "        \n" +
                        "      </td>\n" +
                        "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                        "    </tr>\n" +
                        "    <tr>\n" +
                        "      <td height=\"30\"><br></td>\n" +
                        "    </tr>\n" +
                        "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                        "\n" +
                        "</div></div>";
        }
        public String invitationDetailPage(Campaign campaign, String link) {
                return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n"
                        +
                        "\n" +
                        "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
                        "\n" +
                        "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n"
                        +
                        "    <tbody><tr>\n" +
                        "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
                        "        \n" +
                        "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n"
                        +
                        "          <tbody><tr>\n" +
                        "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
                        "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n"
                        +
                        "                  <tbody><tr>\n" +
                        "                    <td style=\"padding-left:10px\">\n" +
                        "                  \n" +
                        "                    </td>\n" +
                        "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n"
                        +
                        "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Request for Collaboration\n</span>\n"
                        +
                        "                    </td>\n" +
                        "                  </tr>\n" +
                        "                </tbody></table>\n" +
                        "              </a>\n" +
                        "            </td>\n" +
                        "          </tr>\n" +
                        "        </tbody></table>\n" +
                        "        \n" +
                        "      </td>\n" +
                        "    </tr>\n" +
                        "  </tbody></table>\n" +

                        "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n"
                        +
                        "    <tbody><tr>\n" +
                        "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                        "      <td>\n" +
                        "        \n" +
                        "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n"
                        +
                        "                  <tbody><tr>\n" +
                        "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
                        "                  </tr>\n" +
                        "                </tbody></table>\n" +
                        "        \n" +
                        "      </td>\n" +
                        "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                        "    </tr>\n" +
                        "  </tbody></table>\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:80%;width:100%!important\" width=\"100%\">\n"
                        +
                        "  <tbody><tr>\n" +

                        "<td height=\"10\"><br></td></tr>\n" +
                        "<tr><td width=\"100% height=\"10\"> <b style=\"color:red;\">Campaign Name:</b> "+ campaign.getTitle() +"</td></tr>\n" +
                        "<td height=\"10\"><br></td></tr>\n" +
                        "<tr><td width=\"100% height=\"10\"> <b style=\"color:red;\">Campaign Description:</b> "+ campaign.getDescription() +"</td></tr>\n" +
                        "<td height=\"10\"><br></td></tr>\n" +
                        "<tr><td width=\"100% height=\"10\"> <b style=\"color:red;\">Project Type:</b> "+ campaign.getProjectType() +"</td></tr>\n" +
                        "<td height=\"10\"><br></td></tr>\n" +
                        "<tr><td width=\"100% height=\"10\"> <b style=\"color:red;\">Campaign Description:</b> "+ campaign.getDescription() +"</td></tr>\n" +
                        "<td height=\"10\"><br></td></tr>\n" +
                        "<tr><td width=\"100% height=\"10\"> <b style=\"color:red;\">Campaign Short-Description:</b> "+ campaign.getShortDescription() +"</td></tr>\n" +
                        "<td height=\"10\"><br></td></tr>\n" +
                        "<tr><td width=\"100% height=\"10\"> <b style=\"color:red;\">Campaign Category:</b> "+ campaign.getCampaignSubCategory().getCampaignCategory().getName() +"</td></tr>\n" +
                        "<td height=\"10\"><br></td></tr>\n" +
                        "<tr><td width=\"100% height=\"10\"> <b style=\"color:red;\">Campaign SubCategory:</b> "+ campaign.getCampaignSubCategory().getName() +"</td></tr>\n" +
                        "<td height=\"10\"><br></td></tr>\n" +
                        "<tr><td width=\"100% height=\"10\"> <b style=\"color:red;\">Campaign Category:</b> "+ campaign.getCampaignDuration() +"</td></tr>\n" +
                        "<td height=\"10\"><br></td></tr>\n" +
                        "<tr><td width=\"100% height=\"10\"> <b style=\"color:red;\">Goal Amount:</b> "+ campaign.getGoalAmount() +"</td></tr>\n" +
                        "<td height=\"10\"><br></td></tr>\n" +
                        "<tr><td width=\"100% height=\"10\"> <b style=\"color:red;\">Campaign Collaborators:</b> "+ campaign.getCollaborators() +"</td></tr>\n" +
                        "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n"
                        +
                        "    <tbody><tr>\n" +
                        "      <td height=\"10\"><br></td>\n" +
                        "    </tr>\n" +
                        "    <tr>\n" +
                        "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                        "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n"
                        +
                        "<p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> " +
                        "Please click on the below link to accept this invitation: " +
                        "</p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\""
                        + link +"/"+ 1
                        + "\">Accept Now</a> </p></blockquote></p>" +
                        "<p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Please click on the below link to reject this invitation: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\">" +
                        "<p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\""
                        + link +"/"+ 0
                        + "\">Reject Now</a> </p></blockquote>\n Link will expire in 1 Day. <p>See you soon</p>"
                        +
                        "        \n" +
                        "      </td>\n" +
                        "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                        "    </tr>\n" +
                        "    <tr>\n" +
                        "      <td height=\"30\"><br></td>\n" +
                        "    </tr>\n" +
                        "  </tbody></table>" +

                        "<div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                        "\n" +
                        "</div></div>";
        }
        public String invitationAccepted() {
                return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n"
                        +
                        "\n" +
                        "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
                        "\n" +
                        "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n"
                        +
                        "    <tbody><tr>\n" +
                        "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
                        "        \n" +
                        "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n"
                        +
                        "          <tbody><tr>\n" +
                        "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
                        "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n"
                        +
                        "                  <tbody><tr>\n" +
                        "                    <td style=\"padding-left:10px\">\n" +
                        "                  \n" +
                        "                    </td>\n" +
                        "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n"
                        +
                        "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Congratulations! You are collaborators.\n!</span>\n"
                        +
                        "                    </td>\n" +
                        "                  </tr>\n" +
                        "                </tbody></table>\n" +
                        "              </a>\n" +
                        "            </td>\n" +
                        "          </tr>\n" +
                        "        </tbody></table>\n" +
                        "        \n" +
                        "      </td>\n" +
                        "    </tr>\n" +
                        "  </tbody></table>\n" +
                        "<div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                        "\n" +
                        "</div></div>";
        }

        public String invitationRejected() {
                return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n"
                        +
                        "\n" +
                        "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
                        "\n" +
                        "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n"
                        +
                        "    <tbody><tr>\n" +
                        "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
                        "        \n" +
                        "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n"
                        +
                        "          <tbody><tr>\n" +
                        "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
                        "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n"
                        +
                        "                  <tbody><tr>\n" +
                        "                    <td style=\"padding-left:10px\">\n" +
                        "                  \n" +
                        "                    </td>\n" +
                        "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n"
                        +
                        "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Congratulations! You rejected this invention successfully.\n!</span>\n"
                        +
                        "                    </td>\n" +
                        "                  </tr>\n" +
                        "                </tbody></table>\n" +
                        "              </a>\n" +
                        "            </td>\n" +
                        "          </tr>\n" +
                        "        </tbody></table>\n" +
                        "        \n" +
                        "      </td>\n" +
                        "    </tr>\n" +
                        "  </tbody></table>\n" +
                        "<div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                        "\n" +
                        "</div></div>";
        }







        public String getRandomNumberString() {

                Random rnd = new Random();
                int number = rnd.nextInt(999999);

                return String.format("%06d", number);
        }
}

@Getter
@Setter
@AllArgsConstructor
@Data
class ESBStatus {
        @JsonProperty("Response")
        public String response;
        public String status;
        public String responseCode;
}

@Getter
@Setter
@AllArgsConstructor
@Data
class SMSGatewayResponse {
        @JsonProperty("ESBStatus")
        public ESBStatus eSBStatus;
}

@Getter
@Setter
@AllArgsConstructor
@Data
class createUserResponse {
        String status;
        String description;
}

