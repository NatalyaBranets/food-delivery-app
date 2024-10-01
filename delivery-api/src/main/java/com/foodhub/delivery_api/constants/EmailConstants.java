package com.foodhub.delivery_api.constants;

public class EmailConstants {
    public static final String EMAIL_ACCOUNT_VERIFICATION_SUBJECT = "Account Verification for FoodHub application";
    public static final String MAIL_VERIFICATION_TEXT_HTML  =
            """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <title>Users Email Verification</title>
                        <style>
                            body {
                                font-family: Arial, sans-serif;
                                background-color: #f4f4f4;
                                text-align: left;
                                margin: 0;
                                padding: 0;
                            }

                            .container {
                                max-width: 600px;
                                margin: 0 auto;
                                padding: 20px;
                                background-color: #fff;
                                border-radius: 5px;
                                box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
                            }

                            p {
                                color: black;
                            }

                            .button-container {
                                margin: 20px 0;
                            }

                            .button {
                                display: inline-block;
                                padding: 10px 20px;
                                background-color: #007BFF;
                                color: #ffff;
                                text-decoration: none;
                                border-radius: 5px;
                                transition: background-color 0.3s ease;
                            }

                            .button:hover {
                                background-color: #0056b3;
                            }
                        </style>
                    </head>
                    <body>
                        <div class="container">
                            <p>Dear %s,</p>
                            <p>We're excited to have you get started. To verify your email, please click on the button below.</p>
                            <p>This verification link will expire in 3 minutes.</p>
                            <p>If you don't verify your registration within 3 minute, you will need to register again.</p>
                            <br>
                            <div class="button-container">
                                <a class="button" href="%s">Click to Confirm Email</a>
                            </div>
                            <br>
                            <p>Regards,</p>
                            <p>BRANETS Development Team</p>
                        </div>
                    </body>
                    </html>
                    """;

}
