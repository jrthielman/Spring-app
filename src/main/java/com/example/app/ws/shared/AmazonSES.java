package com.example.app.ws.shared;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;
import com.example.app.ws.shared.dto.UserDto;

public class AmazonSES {

    private final String url = "http://localhost:4200/verify_email";

    private final String FROM = "j.thielman@workingspirit.nl";

    private final String SUBJECT = "One last step to complete your registration with this app";

    private final String HTMLBODY = "<h1>Please verify your email address</h1>" +
            "<p>Thank you for registering with our app. To complete registration process and be able to log in please" +
            " click on the following link: </p>" +
            "<a href='http://localhost:4200/verify_email/$emailToken'>"+
            "Final step to complete your registration" + "</a> <br><br>" +
            "<p>Or fill in your token manually by following the link below</p>"+
            "<p>Your verification token:</p> <b>$emailToken</b><br>" +
            "<a href='http://localhost:4200/verify_email/nothing'>Verify manually</a><br>"+
            "Have a nice day!";

    private final String TEXTBODY = "Please verify your email address."
            + "Thank you for registering with our app. To complete the registration process and be able to log in"
            + ", open the following URL in your browser. "
            + "Auto verification URL: http://localhost:4200/verify_email/$emailToken "
            + "Or enter the token manually at the URL below the token."
            + "Token: $emailToken "
            + "Manual verification URL: http://localhost:4200/verify_email/nothing "
            + "Have a nice day!";

    public void verifyEmail(UserDto userDto){
        AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard().withRegion(Regions.EU_WEST_1)
                .build();

        String htmlBodyWithToken = this.HTMLBODY.replace("$emailToken", userDto.getEmailVerificationToken());
        String textBodyWithToken = this.TEXTBODY.replace("$emailToken", userDto.getEmailVerificationToken());

        SendEmailRequest request = new SendEmailRequest()
                .withDestination(new Destination().withToAddresses(userDto.getEmail()))
                .withMessage(new Message().withBody(new Body().withHtml(new Content()
                .withCharset("UTF-8").withData(htmlBodyWithToken))
                .withText(new Content().withCharset("UTF-8").withData(textBodyWithToken)))
                .withSubject(new Content().withCharset("UTF-8").withData(this.SUBJECT)))
                .withSource(this.FROM);

        client.sendEmail(request);

    }

}
