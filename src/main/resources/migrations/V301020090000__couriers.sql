update notification_template_persistence_dto
set html_template =  '<!DOCTYPE html>
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
                 <p>Our pharmacy partner has shipped your order. To track your shipment, please visit <a href="{courierLInk}" target="_blank">{courierShortName}</a> and input the tracking number below.</p>
                 <br>
                 <p class="bold">Shipping Information</p>
                 <br>
                 <p>Courier: {courier}</p>
                 <p>Tracking Number: {trackingNumber}</p>
                 <br>
                 <a href="{courierLInk}" target="_blank">Track Shipment</a>
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

 </html>'
where name = 'Notify Patient About Receipt';