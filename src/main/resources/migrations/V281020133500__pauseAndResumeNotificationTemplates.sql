insert into notification_template_persistence_dto (active, days_after, deferred, html_template, name,
                                                   notification_event_type_name, subject_template, target, actual)
values (true, 0, false,
        '<!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="utf-8" />
            <style>

                /*HEADER*/

                .container-wrapper {
                    width: 100%;
                    height: 100%;
                    margin: 0;
                    font-family: "Arial";
                    display: inline-block;
                }

                p {
                    margin: 0;
                    margin-top: 2px;
                    margin-bottom: 2px;
                }

                .bold {
                    font-weight: bold;
                    color: #bb9157;
                }

                .container {
                    width: 900px;
                    margin: 25px auto;
                    background-color: #ffffff;
                    padding: 10px;
                }

                a {
                    text-decoration: none;
                }

                .container .header  {
                    margin: 0 30px;
                    padding: 30px 0;
                    border-bottom: 1px solid #bb9157;
                    display: flex;
                }

                .header a {
                    display: block;
                    margin-left: 1vw;
                }

                .logo {
                    width: 65%;
                }

                .logo img{
                    height: 103px;
                    width: 164px;
                    margin-right: auto;
                }

                .address-block {
                    float: right;
                    margin-right: 0;
                    margin-left: auto;
                    width: 35%;
                }

                .web-address {
                    height: 25px;
                    width: 229px;
                    margin: 2vh;
                    display: flex;
                }


                .email-address {
                    height: 25px;
                    width: 230px;
                    margin: 2vh;
                    display: flex;
                }

                .email-address img {
                    display: block;
                    height: 25px;
                    width: 29px;
                    margin-right: 1vw;
                }

                .web-address img {
                    display: block;
                    height: 25px;
                    width: 29px;
                    margin-right: 1vw;
                }

                .address-block-text {
                    height: 25px;
                    width: 191px;
                    color: #000000;
                    font-family: Avenir;
                    font-size: 18px;
                    font-weight: 300;
                    letter-spacing: 0;
                    line-height: 25px;
                }

                /*MAIN*/

                .main {
                    width: 840px;
                    margin: 0 30px;
                    padding: 30px 0;
                    /*  height: 575px;*/
                    color: #000000;
                    font-family: Avenir;
                    font-size: 18px;
                    font-weight: 300;
                    /*letter-spacing: 0;*/
                    line-height: 25px;
                    word-wrap: break-word;
                }

            </style>
        </head>

        <body>
        <div class="container-wrapper">
            <div class="container">
                <div class="content">
                    <div class="header">
                        <div class="logo">
                            <img src="cid:logo" height="103" width="164" alt="" />
                        </div>
                        <div class="address-block">
                            <a href="www.askwinston.ca">
                                <div class="web-address">
                                    <img src="cid:webAddressIcon" height="25" width="29" alt="" />
                                    <span class="address-block-text">www.askwinston.ca</span>
                                </div>
                            </a>
                            <a href="mailto:Welisten@askwinston.ca">
                                <div class="email-address">
                                    <img src="cid:emailAddressIcon" height="25" width="29" alt="" />
                                    <span class="address-block-text">welisten@askwinston.ca</span>
                                </div>
                            </a>
                        </div>
                    </div>
                    <div class="main">

                        <p><span class="bold">You recently paused your subscription (ID {subscriptionId}) - {dragNameAndDosage}</span></p>
                        <br>
                        <p>Your subscription will be paused indefinitely until you re-activate within the expiration date. Your subscription is expiring on {prescriptionToDate}. </p>
                        <br>
                        <p>You can access your account at any time to re-activate your subscription.</p>
                        <br>
                        <table width="100%" border="0" cellspacing="0" cellpadding="0">
                            <tr>
                                <td>
                                    <table border="0" cellspacing="0" cellpadding="0">
                                        <tr>
                                            <td align="center" bgcolor="#B99055"><a href="{baseUrl}/login?logout=true" target="_blank" style="font-size: 16px; font-family: Helvetica, Arial, sans-serif; color: #ffffff; text-decoration: none; padding: 12px 18px; border: 1px solid #B99055; display: inline-block;">ACCESS ACCOUNT</a></td>
                                        </tr>
                                    </table>
                                </td>
                            </tr>
                        </table>
                        <br>
                        <p>If you didn’t make this change, or if you have any questions or need assistance, please contact us at <a href="mailto:WeListen@askwinston.ca">WeListen@askwinston.ca</a>.</p>
                        <br>
                        <p>Thanks, </p>
                        <br>
                        <p>The Winston Team</p>
                    </div>
                </div>
            </div>
        </div>
        </body>
        </html>', 'Notify patient about subscription pause', 'Subscription paused by patient', 'Subscription paused', 3,
        true),
       (true, 0, false,
        '<!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="utf-8" />
                <style>

                    /*HEADER*/

                    .container-wrapper {
                        width: 100%;
                        height: 100%;
                        margin: 0;
                        font-family: "Arial";
                        display: inline-block;
                    }

                    p {
                        margin: 0;
                        margin-top: 2px;
                        margin-bottom: 2px;
                    }

                    .bold {
                        font-weight: bold;
                        color: #bb9157;
                    }

                    .container {
                        width: 900px;
                        margin: 25px auto;
                        background-color: #ffffff;
                        padding: 10px;
                    }

                    a {
                        text-decoration: none;
                    }

                    .container .header  {
                        margin: 0 30px;
                        padding: 30px 0;
                        border-bottom: 1px solid #bb9157;
                        display: flex;
                    }

                    .header a {
                        display: block;
                        margin-left: 1vw;
                    }

                    .logo {
                        width: 65%;
                    }

                    .logo img{
                        height: 103px;
                        width: 164px;
                        margin-right: auto;
                    }

                    .address-block {
                        float: right;
                        margin-right: 0;
                        margin-left: auto;
                        width: 35%;
                    }

                    .web-address {
                        height: 25px;
                        width: 229px;
                        margin: 2vh;
                        display: flex;
                    }


                    .email-address {
                        height: 25px;
                        width: 230px;
                        margin: 2vh;
                        display: flex;
                    }

                    .email-address img {
                        display: block;
                        height: 25px;
                        width: 29px;
                        margin-right: 1vw;
                    }

                    .web-address img {
                        display: block;
                        height: 25px;
                        width: 29px;
                        margin-right: 1vw;
                    }

                    .address-block-text {
                        height: 25px;
                        width: 191px;
                        color: #000000;
                        font-family: Avenir;
                        font-size: 18px;
                        font-weight: 300;
                        letter-spacing: 0;
                        line-height: 25px;
                    }

                    /*MAIN*/

                    .main {
                        width: 840px;
                        margin: 0 30px;
                        padding: 30px 0;
                        /*  height: 575px;*/
                        color: #000000;
                        font-family: Avenir;
                        font-size: 18px;
                        font-weight: 300;
                        /*letter-spacing: 0;*/
                        line-height: 25px;
                        word-wrap: break-word;
                    }

                </style>
            </head>

            <body>
            <div class="container-wrapper">
                <div class="container">
                    <div class="content">
                        <div class="header">
                            <div class="logo">
                                <img src="cid:logo" height="103" width="164" alt="" />
                            </div>
                            <div class="address-block">
                                <a href="www.askwinston.ca">
                                    <div class="web-address">
                                        <img src="cid:webAddressIcon" height="25" width="29" alt="" />
                                        <span class="address-block-text">www.askwinston.ca</span>
                                    </div>
                                </a>
                                <a href="mailto:Welisten@askwinston.ca">
                                    <div class="email-address">
                                        <img src="cid:emailAddressIcon" height="25" width="29" alt="" />
                                        <span class="address-block-text">welisten@askwinston.ca</span>
                                    </div>
                                </a>
                            </div>
                        </div>
                        <div class="main">
                            <p><span class="bold">Customer {patientName} has paused their subscription (ID {subscriptionId}) - {dragNameAndDosage}</span></p>
                            <br>
                            <p>Their subscription will be paused indefinitely until they re-activate within the expiration date.</p>
                            <br>
                            <p>Expiration date: {prescriptionToDate}</p>
                        </div>
                    </div>
                </div>
            </div>
            </body>
            </html>', 'Notify Info about subscription pause by patient', 'Subscription paused by patient',
        'Subscription {subscriptionId} paused by patient', 0, true),
       (true, 0, false,
        '<!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="utf-8" />
            <style>

                /*HEADER*/

                .container-wrapper {
                    width: 100%;
                    height: 100%;
                    margin: 0;
                    font-family: "Arial";
                    display: inline-block;
                }

                p {
                    margin: 0;
                    margin-top: 2px;
                    margin-bottom: 2px;
                }

                .bold {
                    font-weight: bold;
                    color: #bb9157;
                }

                .container {
                    width: 900px;
                    margin: 25px auto;
                    background-color: #ffffff;
                    padding: 10px;
                }

                a {
                    text-decoration: none;
                }

                .container .header  {
                    margin: 0 30px;
                    padding: 30px 0;
                    border-bottom: 1px solid #bb9157;
                    display: flex;
                }

                .header a {
                    display: block;
                    margin-left: 1vw;
                }

                .logo {
                    width: 65%;
                }

                .logo img{
                    height: 103px;
                    width: 164px;
                    margin-right: auto;
                }

                .address-block {
                    float: right;
                    margin-right: 0;
                    margin-left: auto;
                    width: 35%;
                }

                .web-address {
                    height: 25px;
                    width: 229px;
                    margin: 2vh;
                    display: flex;
                }


                .email-address {
                    height: 25px;
                    width: 230px;
                    margin: 2vh;
                    display: flex;
                }

                .email-address img {
                    display: block;
                    height: 25px;
                    width: 29px;
                    margin-right: 1vw;
                }

                .web-address img {
                    display: block;
                    height: 25px;
                    width: 29px;
                    margin-right: 1vw;
                }

                .address-block-text {
                    height: 25px;
                    width: 191px;
                    color: #000000;
                    font-family: Avenir;
                    font-size: 18px;
                    font-weight: 300;
                    letter-spacing: 0;
                    line-height: 25px;
                }

                /*MAIN*/

                .main {
                    width: 840px;
                    margin: 0 30px;
                    padding: 30px 0;
                    /*  height: 575px;*/
                    color: #000000;
                    font-family: Avenir;
                    font-size: 18px;
                    font-weight: 300;
                    /*letter-spacing: 0;*/
                    line-height: 25px;
                    word-wrap: break-word;
                }

            </style>
        </head>

        <body>
        <div class="container-wrapper">
            <div class="container">
                <div class="content">
                    <div class="header">
                        <div class="logo">
                            <img src="cid:logo" height="103" width="164" alt="" />
                        </div>
                        <div class="address-block">
                            <a href="www.askwinston.ca">
                                <div class="web-address">
                                    <img src="cid:webAddressIcon" height="25" width="29" alt="" />
                                    <span class="address-block-text">www.askwinston.ca</span>
                                </div>
                            </a>
                            <a href="mailto:Welisten@askwinston.ca">
                                <div class="email-address">
                                    <img src="cid:emailAddressIcon" height="25" width="29" alt="" />
                                    <span class="address-block-text">welisten@askwinston.ca</span>
                                </div>
                            </a>
                        </div>
                    </div>
                    <div class="main">

                        <p><span class="bold">You recently re-activated your subscription (ID {subscriptionId}) - {dragNameAndDosage}</span></p>
                        <br>
                        <p>Your subscription will re-activate until it expires on  {prescriptionToDate}. </p>
                        <br>
                        <p>You can access your account at any time to check on the status of your refills, get an early refill, skip a refill, or pause.</p>
                        <br>
                        <table width="100%" border="0" cellspacing="0" cellpadding="0">
                            <tr>
                                <td>
                                    <table border="0" cellspacing="0" cellpadding="0">
                                        <tr>
                                            <td align="center" bgcolor="#B99055"><a href="{baseUrl}/login?logout=true" target="_blank" style="font-size: 16px; font-family: Helvetica, Arial, sans-serif; color: #ffffff; text-decoration: none; padding: 12px 18px; border: 1px solid #B99055; display: inline-block;">ACCESS ACCOUNT</a></td>
                                        </tr>
                                    </table>
                                </td>
                            </tr>
                        </table>
                        <br>
                        <p>If you didn’t make this change, or if you have any questions or need assistance, please contact us at <a href="mailto:WeListen@askwinston.ca">WeListen@askwinston.ca</a>.</p>
                        <br>
                        <p>Thanks, </p>
                        <br>
                        <p>The Winston Team</p>
                    </div>
                </div>
            </div>
        </div>
        </body>
        </html>', 'Notify patient about subscription resume', 'Subscription paused by patient resumed', 'Subscription resumed', 3,
        true)