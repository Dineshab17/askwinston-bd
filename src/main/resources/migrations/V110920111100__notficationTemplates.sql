delete from notification_persistence_dto;
delete from notification_template_persistence_dto;
alter table notification_template_persistence_dto AUTO_INCREMENT = 1;
insert into notification_template_persistence_dto (active, days_after, deferred, html_template, name, notification_event_type_name, subject_template, target, actual)
values
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
                <p>An appointment has been scheduled for : {appointmentDate}</p>
                <br>
                <p>Patient:</p>
                <p>{patientInfo}</p>
            </div>
        </div>
    </div>
</div>
</body>

</html>', 'Notify Info About New Appointment', 'New appointment', 'New appointment', 0, true),
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
                 <p class="bold">Subscription Confirmation</p>
                 <br>
                 <p>Thank you for choosing Winston!  Please see your subscription details below.  If you have any questions or concerns, please contact us at <a href="mailto:WeListen@askwinston.ca">WeListen@askwinston.ca</a></p>
                 <br>
                 <p>Your receipt and shipment tracking information will be sent to you once your order has shipped.</p>
                 <br>
                 <p>Thanks, </p>
                 <br>
                 <p>The Winston Team</p>
                 <br>
                 <br>
                 <p>Subscription number: {subscriptionId}</p>
                 <br>
                 <p>Date: {subscriptionDate} <br>Billing Info: {billingInfo}</p>
                 <br>
                 <p>Name: {patientName}</p>
                 <p>Address: <br>{shippingInfo}</p>
                 <br>
                 <p>{productsInfo}</p>
                 <br>
                 <p>Total price: ${orderPrice}</p>
             </div>
         </div>
     </div>
 </div>
 </body>

 </html>', 'Notify Patient About Subscription Approval', 'Subscription approval', 'Winston Order Confirmation: {subscriptionId}', 3, true),
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
                <p>New subscription</p>
                <br>
                <p>Subscription number: {subscriptionId}</p>
                <br>
                <p>Date: {subscriptionDate}</p>
                <br>
                <p>Patient ID: {patientId}</p>
                <br>
                <p>Patient name: {patientName}</p>
                <br>
                <p>Billing info: {billingInfo}</p>
                <br>
                <p>Shipping info: <{shippingInfo}</p>
                <br />
                <p>{productsInfo}</p>
            </div>
        </div>
    </div>
</div>
</body>

</html>', 'Notify Info About New Subscription', 'New subscription', 'New Subscription: {subscriptionId}', 0, true),
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
                 <p class="bold">Your subscription has been cancelled.</p>
                 <br>
                 <p>We regret to inform you that we are are unable to process your subscription at this time due to medical reasons. If you have any questions about why your subscription has been cancelled, please reach out to us at <a href="mailto:Welisten@askwinston.ca">WeListen@askwinston.ca</a>.</p>
                 <br>
                 <p>Thanks, </p>
                 <br>
                 <p>The Winston Team</p>
             </div>
         </div>
     </div>
 </div>
 </body>

 </html>', 'Notify Patient About Subscription Rejection', 'Subscription rejection', 'Your order cannot be processed.', 3, true),
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
                 <p class="bold">Your order has been cancelled.</p>
                 <br>
                 <p>We regret to inform you that we are are unable to process your order at this time due to medical reasons. If you have any questions about why your order has been cancelled, please reach out to us at <a href="mailto:Welisten@askwinston.ca">WeListen@askwinston.ca</a>.</p>
                 <br>
                 <p>Thanks, </p>
                 <br>
                 <p>The Winston Team</p>
             </div>
         </div>
     </div>
 </div>
 </body>

 </html>', 'Notify Patient About Order Rejection', 'Order rejection', 'Your order cannot be processed.', 3, true),
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
             font-family: "Avenir";
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
             font-family: "Avenir";
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
             font-family: "Avenir";
             font-size: 18px;
             font-weight: 300;
             /*letter-spacing: 0;*/
             line-height: 25px;
             word-wrap: break-word;
         }

         .button {
             box-sizing: border-box;
             height: 47px;
             width: 246px;
             border: 2px solid #B99055;
             background-color: #B99055;
             margin: 2vh 0
         }

         .button-text {
             height: 22px;
             width: 206px;
             color: #FFFFFF;
             font-family: "Avenir";
             font-size: 18px;
             font-weight: 300;
             letter-spacing: 0;
             line-height: 25px;
             text-align: center;
         }
         .border-top {
             border-top: 1px solid #bb9157;
         }

         .logo-2 {
             width: 840px
         }

         .logo-2 img {
             float: right;
         }

         table.pharm {
             margin: 0 30px;
             border-collapse: collapse;
             background-color: #EFEFEF;
         }

         td.pharm, th.pharm{
             text-align: center;
             height: 47px;
             width: 140px;
             border: 2px solid #FFFFFF;
             background-color: #EFEFEF;
         }

         td.dosage, th.dosage{
             width: 280px;
         }

         th.pharm {
             color: #FFFFFF;
             background-color: #B99055;
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
                 <p class="bold">Congratulations, your order has shipped!</p>
                 <br>
                 <p>Our pharmacy partner has shipped your order.  To track your shipment, please visit canadapost.ca and input the tracking number below.</p>
                 <br>
                 <p class="bold">Shipping Information</p>
                 <br>
                 <p>Courier: {courier}</p>
                 <p>Service: {service}</p>
                 <p>Tracking Number: {trackingNumber}</p>
                 <br>
                 <a href="canadapost.ca" target="_blank">Track Shipment</a>
                 <br>
                 <br>
                 <p>Please contact our pharmacy partner at 1-888-404-4002 with any questions regarding your medication.</p>
                 <br>
                 <p>We would love to hear from you. Please reach out to us with any questions, comments, or general feedback at WeListen@askwinston.ca.</p>
                 <br>
                 <br>
                 <p>Thanks, </p>
                 <br>
                 <p>The Winston Team</p>
             </div>
             <div class="main border-top">
                 <div class="logo logo-2">
                     <img src="cid:logo" height="103" width="164" alt="" />
                 </div>
                 <p class="bold">Receipt</p>
                 <br>
                 <br>
                 <p>Doctor Information:</p>
                 <p>Name: {doctorName}</p>
                 <p>Date of Prescription: {dateOfPrescription}</p>
                 <br>
                 <p>Patient Information:</p>
                 <p>Name: {patientName}</p>
                 <p>Date of Birth: {patientBirthday}</p>
                 <p>Address: {patientAddress}</p>
             </div>
             <table class="pharm">
                 <thead class="pharm">
                 <tr class="pharm">
                     <th class="pharm">Product</th>
                     <th class="pharm dosage">Dosage</th>
                     <th class="pharm">Refills remaining</th>
                     <th class="pharm">Total</th>
                 </tr>
                 </thead>
                 <tbody class="pharm">
                 {productTable}
                 </tbody>
             </table>
             <div class="main">
                 <p>Subtotal:  ${subTotal}</p>
                 {coveredByInsurance}<p>Tax:  {tax}</p>
                 <p>Grand Total: ${coPayText}</p>
             </div>
             <div class="main border-top">
                 <p>Note: The total cost includes the pharmacy dispensing fee of $12</p>
             </div>
         </div>
     </div>
 </div>
 </body>

 </html>', 'Notify Patient About Receipt', 'Receipt', 'Receipt for order #{orderId}', 3, true),
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
                 <p class="bold">Congratulations!</p>
                 <br>
                 <p>You have successfully registered!</p>
                 <br>
                 <p>You can access your account at any time to check on the status of your next refill, skip a refill, request your next refill early, add new prescriptions or consult with a doctor.</p>
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
                 <br>
                 <p>If you have any questions or need assistance, please contact us at <a href="mailto:WeListen@askwinston.ca">WeListen@askwinston.ca</a></p>
                 <br>
                 <p>Thanks, </p>
                 <br>
                 <p>The Winston Team</p>
             </div>
         </div>
     </div>
 </div>
 </body>

 </html>', 'Notify Patient About Registration', 'Registration', 'Thank you for registering with Winston', 3, true),
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
                <p>You`re all set for your free consultation with the doctor.
                    You will receive a call from one of our Doctors at the phone number you provided on:
                </p>
                <br>
                <p>{appointmentDate}</p>
                <br>
                <p>Please note: The doctor may call from a private number so please ensure your phone settings do not block these types of calls.</p>
                <br>
                <p>If you have any questions, concerns or to change your appointment, please reach out to us at <a href="mailto:WeListen@askwinston.ca">WeListen@askwinston.ca</a>.</p>
                <br>
                <p>Thanks, </p>
                <br>
                <p>The Winston Team</p>
            </div>
        </div>
    </div>
</div>
</body>

</html>', 'Notify Patient About New Appointment', 'New appointment', 'Your doctor consultation has been confirmed!', 3, true),
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
            font-family: "Avenir";
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
            font-family: "Avenir";
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
            font-family: "Avenir";
            font-size: 18px;
            font-weight: 300;
            /*letter-spacing: 0;*/
            line-height: 25px;
            word-wrap: break-word;
        }

        .button {
            box-sizing: border-box;
            height: 47px;
            width: 246px;
            border: 2px solid #B99055;
            background-color: #B99055;
            margin: 2vh 0
        }

        .button-text {
            height: 22px;
            width: 206px;
            color: #FFFFFF;
            font-family: "Avenir";
            font-size: 18px;
            font-weight: 300;
            letter-spacing: 0;
            line-height: 25px;
            text-align: center;
        }
        .border-top {
            border-top: 1px solid #bb9157;
        }

        .logo-2 {
            width: 840px
        }

        .logo-2 img {
            float: right;
        }

        table.pharm {
            margin: 0 30px;
            border-collapse: collapse;
            background-color: #EFEFEF;
        }

        td.pharm, th.pharm{
            text-align: center;
            height: 47px;
            width: 140px;
            border: 2px solid #FFFFFF;
            background-color: #EFEFEF;
        }

        td.dosage, th.dosage{
            width: 280px;
        }

        th.pharm {
            color: #FFFFFF;
            background-color: #B99055;
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
                <p>Order #{orderId} shipped</p>
                <br/>
                <p>Order ID: {orderId}</p>
                <p>Date: {orderDate}</p>
                <p>Patient ID: {patientId}</p>
                <p>Patient name: {patientName}</p>
                <p>Billing info: {billingInfo}</p>
                <p>Shipping info: {shippingInfo}</p>
                <br/>
                <p>{productsInfo}</p>
                <p>Subtotal:  ${subTotal}</p>
                {coveredByInsurance}<p>Tax:  {tax}</p>
                <p>Grand Total: ${coPayText}</p>
            </div>
        </div>
    </div>
</div>
</body>

</html>', 'Notify Info About Order Checkout', 'Order checkout', 'Order #{orderId} shipped', 0, true),
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
             font-family: "Avenir";
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
             font-family: "Avenir";
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
             font-family: "Avenir";
             font-size: 18px;
             font-weight: 300;
             /*letter-spacing: 0;*/
             line-height: 25px;
             word-wrap: break-word;
         }

         .button {
             box-sizing: border-box;
             height: 47px;
             width: 246px;
             border: 2px solid #B99055;
             background-color: #B99055;
             margin: 2vh 0
         }

         .button-text {
             height: 22px;
             width: 206px;
             color: #FFFFFF;
             font-family: "Avenir";
             font-size: 18px;
             font-weight: 300;
             letter-spacing: 0;
             line-height: 25px;
             text-align: center;
         }
         .border-top {
             border-top: 1px solid #bb9157;
         }

         .logo-2 {
             width: 840px
         }

         .logo-2 img {
             float: right;
         }

         table.pharm {
             margin: 0 30px;
             border-collapse: collapse;
             background-color: #EFEFEF;
         }

         td.pharm, th.pharm{
             text-align: center;
             height: 47px;
             width: 140px;
             border: 2px solid #FFFFFF;
             background-color: #EFEFEF;
         }

         td.dosage, th.dosage{
             width: 280px;
         }

         th.pharm {
             color: #FFFFFF;
             background-color: #B99055;
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
                 <p>Pharmacy RX num: <span class="bold">{pharmacyId}</span></p>
                 <p>Date: <span class="bold">{orderDate}</span></p>
                 <p>Patient name: {patientName}</p>
                 <p>Winston order number: #{orderId}</p>
                 <p>Refills left: <span class="bold">{refillsLeft}</span></p>
             </div>
             <table class="pharm">
                 <thead class="pharm">
                 <tr class="pharm">
                     <th class="pharm">Product</th>
                     <th class="pharm dosage">Dosage</th>
                     <th class="pharm">Refills remaining</th>
                     <th class="pharm">Total</th>
                 </tr>
                 </thead>
                 <tbody class="pharm">
                 {productTable}
                 </tbody>
             </table>
             <div class="main">
                 <p>Shipping details: {shippingInfo}</p>
             </div>
         </div>
     </div>
 </div>
 </body>

 </html>', 'Notify Pharmacy About Refill', 'Order refill', 'Winston order #{orderId}', 2, true),
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
                 <p>Thanks for choosing Winston.  Our goal is to help you be the best version of you.</p>
                 <br>
                 <p>We noticed you didn’t include an image of your valid photo identification when you setup your account.</p>
                 <br>
                 <p class="bold">Not sure what counts as valid identification? The following list includes some examples: </p>
                 <p>- Drivers License <br>
                     - Health Card with Photo <br>
                     - Passport <br>
                     - Citizenship Card <br>
                     - Provincial Photo Card</p>
                 <br>
                 <p>To ensure we are filling prescriptions for the person on the account we need to be able to validate your identity through photo identification.  Without this we are not able to complete your order.</p>
                 <br>
                 <p>Please login to your winston account and access the profile page to upload your ID.</p>
                 <br>
                 <table width="100%" border="0" cellspacing="0" cellpadding="0">
                     <tr>
                         <td>
                             <table border="0" cellspacing="0" cellpadding="0">
                                 <tr>
                                     <td align="center" bgcolor="#B99055"><a href="{baseUrl}/login?logout=true" target="_blank" style="font-size: 16px; font-family: Helvetica, Arial, sans-serif; color: #ffffff; text-decoration: none; padding: 12px 18px; border: 1px solid #B99055; display: inline-block;">UPLOAD IMAGE</a></td>
                                 </tr>
                             </table>
                         </td>
                     </tr>
                 </table>
                 <br>
                 <br>
                 <p>Thanks, </p>
                 <br>
                 <p>The Winston Team</p>
             </div>
         </div>
     </div>
 </div>
 </body>

 </html>', 'Remind Patient To Download Id', 'ID download reminder', 'Reminder from Winston', 3, true),
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
                 <p>We have received a request to skip your next refill and have updated your order accordingly.  Your next refill will now be {nextOrderDate}.</p>
                 <br>
                 <p>If you have any questions about your order or need further assistance you can contact us anytime at <a href="mailto:WeListen@askwinston.ca">WeListen@askwinston.ca</a>.</p>
                 <br>
                 <p>Thanks again for choosing Winston.  Our goal is to help you be the best version of you.</p>
                 <br>
                 <p>Thanks, </p>
                 <br>
                 <p>The Winston Team</p>
             </div>
         </div>
     </div>
 </div>
 </body>

 </html>', 'Notify Patient Auto Refill Skip Confirmation', 'Auto-Refill-Skip', 'Winston Auto-Refill Skip Confirmation', 3, true),
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
                 <p class="bold">Complete the process</p>
                 <br>
                 <p>Thanks for your order with Winston.  We are ready to complete your order but to do that we need you to consult with one of our doctors. If you have not done so already, please click the link below to start your consultation.</p>
                 <br>
                 <p>You can access your account at any time to check on the status of your next refill, skip a refill, request your next refill early, add new prescriptions or consult with a doctor.</p>
                 <br>
                 <table width="100%" border="0" cellspacing="0" cellpadding="0">
                     <tr>
                         <td>
                             <table border="0" cellspacing="0" cellpadding="0">
                                 <tr>
                                     <td align="center" bgcolor="#B99055"><a href="https://xyhealth.inputhealth.com" target="_blank" style="font-size: 16px; font-family: Helvetica, Arial, sans-serif; color: #ffffff; text-decoration: none;padding: 12px 18px; border: 1px solid #B99055; display: inline-block;">START ONLINE VISIT</a></td>
                                 </tr>
                             </table>
                         </td>
                     </tr>
                 </table>
                 <br>
                 <br>
                 <p>Once your medical consultation is completed your order will be processed and shipped</p>
                 <br>
                 <p>Thanks, </p>
                 <br>
                 <p>The Winston Team</p>
             </div>
         </div>
     </div>
 </div>
 </body>

 </html>', 'Notify Patient About New Subscription', 'Consultation reminder', 'Consultation reminder from Winston', 3, true),
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
                 <p>Hello,</p>
                 <br>
                 <p>Thank you for placing your order with Winston. Unfortunately, the payment method provided has been declined. To finalize your order please login to your Winston account and provide an alternate payment method.</p>
                 <br>
                 <p>Subscription number: {subscriptionId}</p>
                 <br>
                 <p>Billing Info: {billingInfo}</p>
                 <p>Shipping info: <br>{shippingInfo}</p>
                 <br>
                 <p>{productsInfo}</p>
             </div>
         </div>
     </div>
 </div>
 </body>

 </html>', 'Notify Patient About Payment Failure', 'Subscription paused', 'Payment Failure', 3, false),
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
                 <p>New Contact Us request</p>
                 <br>
                 <p>Email: {email}</p>
                 <br>
                 <p>Name: {name}</p>
                 <br>
                 <p>Phone: {phone}</p>
                 <br>
                 <p>Message: {message}</p>
             </div>
         </div>
     </div>
 </div>
 </body>

 </html>', 'Notify Info about new contact us request', 'New contact us request', 'New Contact Us Request', 0, false),
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
                 <p>Hello,</p>
                 <br>
                 <p>Your prescription transfer request has been submitted and is in progress.  We may reach out to you if there are outstanding questions or details required to complete this process.</p>
                 <br>
                 <p>Once the transfer has been completed you will also receive an email notification once the order has been shipped.
                     If you have any questions please reach out to us via our live chat on the website or by email at <a href="mailto:WeListen@askwinston.ca">WeListen@askwinston.ca</a>.</p>
                 <br>
                 <p>Thanks, </p>
                 <br>
                 <p>The Winston Team</p>
             </div>
         </div>
     </div>
 </div>
 </body>

 </html>', 'Pharmacist submitted RX', 'Pharmacist approved RX', 'Prescription transfer confirmation', 3, true)