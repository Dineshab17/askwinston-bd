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
                <p class="bold">
                    Your prescription auto-refills for (ID {subscriptionId}) -
                    {productsInfo} has been cancelled
                </p>
                <br />
                <p>
                    If cancelled after your recent refill shipment, this will take effect
                    immediately and you will no longer be receiving your following refills.
                </p>
                <br />
                </p>
                    If cancelled within # days or less by your upcoming refill date, then the
                    cancellation will take effect for the next refill order and all future refills.
                    Order refill processing takes # days prior to the refill date to ensure we can
                    ship your order in a timely manner.
                </p>
                <br />
                </p>
                    If this wasn’t you, then please contact us
                    at <a href="mailto:welisten@askwinston.ca">WeListen@askwinston.ca</a> and we can assist you.
                </p>
                <br />
                <br />
                <br />
                </p>
                    Thanks,
                    <br />
                    <br />
                    The Winston Team
                </p>
            </div>
        </div>
    </div>
</div>
</body>
</html>', 'Notify Patient About his Subscription is Canceled', 'Subscription canceled by patient', 'Subscription canceled', 3,
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
                <p class="bold">
                    You recently submitted a transfer of your prescription
                    for (ID {subscriptionId}) - {productsInfo}
                </p>
                <br />
                <p>
                    We will begin processing your transfer request. In the event we require further information
                    or need to verify any details, we will contact you at the email and/or phone number you provided to us.
                </p>
                <br />
                </p>
                    You can also access your account to view the status of your prescription.
                </p>
                <br />

                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                        <td>
                            <table border="0" cellspacing="0" cellpadding="0">
                                <tr>
                                    <td align="center" bgcolor="#B99055">
                                        <a href="{baseUrl}/login?logout=true" target="_blank"
                                           style="font-size: 16px; font-family: Helvetica, Arial, sans-serif;
                                                  color: #ffffff; text-decoration: none; padding: 10px 40px;
                                                  border: 1px solid #B99055; display: inline-block;">
                                            ACCESS ACCOUNT
                                        </a>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>

                <br />
                </p>
                    If you have any questions or need assistance, please contact us at
                    <a href="mailto:welisten@askwinston.ca">WeListen@askwinston.ca</a>.
                </p>
                <br />
                </p>
                    Thanks,
                    <br />
                    <br />
                    The Winston Team
                </p>
            </div>
        </div>
    </div>
</div>
</body>
</html>', 'Notify Patient About his Transfer RX is Submitted', 'Transfer RX submitted', 'Transfer RX submitted', 3,
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
        .strong {
            font-weight: 600;
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
                <p class="bold">
                    You recently requested an early refill for your prescription
                    auto-refill (ID {subscriptionId}) - {productsInfo}
                </p>
                <br />
                <p>
                    The pharmacist will review and once approved we will process and ship your next refill.
                    You will receive an email confirmation once your order has shipped.
                </p>
                <br />
                </p>
                    <span class="strong">Tip:</span> If you find your medication isn’t lasting you until your next scheduled refill,
                    you may want to increase your order to a larger size if this option is available
                    for your choice of medication.
                </p>
                </p>
                    You can access your account at any time to view the status of your prescriptions.
                </p>
                <br />

                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                        <td>
                            <table border="0" cellspacing="0" cellpadding="0">
                                <tr>
                                    <td align="center" bgcolor="#B99055">
                                        <a href="{baseUrl}/login?logout=true" target="_blank"
                                           style="font-size: 16px; font-family: Helvetica, Arial, sans-serif;
                                                  color: #ffffff; text-decoration: none; padding: 10px 40px;
                                                  border: 1px solid #B99055; display: inline-block;">
                                            ACCESS ACCOUNT
                                        </a>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>

                <br />
                </p>
                    If you didn’t make this change, or if you have any questions or need assistance,
                    please contact us at <a href="mailto:welisten@askwinston.ca">WeListen@askwinston.ca</a>.
                </p>
                <br />
                </p>
                    Thanks,
                    <br />
                    <br />
                    The Winston Team
                </p>
            </div>
        </div>
    </div>
</div>
</body>
</html>', 'Notify Patient About his Early Refill is Submitted', 'Early refill submitted', 'Early Refill confirmation', 3,
        true)