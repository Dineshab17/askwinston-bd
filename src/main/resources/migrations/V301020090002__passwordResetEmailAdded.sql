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
					Your password for your {emailMask} account
					was changed on {formattedDateTime}.
				</p>
				<br />
				<p>
					If this was you, then you’re all set.
				</p>
				<br />
				</p>
					If this wasn’t you, your account has been compromised. Please follow these steps:
					<br />
                    1. <a href="{baseUrl}/profile/forgot-password">Reset your password</a>
				</p>
                <br />
				</p>
					2. <a href="{baseUrl}/profile/account-info">Review your personal information</a>
				</p>
				<br />
				</p>
					If you have any questions or need assistance,
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
</html>', 'Notify Patient About his Password was Successfully Reset', 'Password was reset', 'Password reset confirmation', 3,
        true)