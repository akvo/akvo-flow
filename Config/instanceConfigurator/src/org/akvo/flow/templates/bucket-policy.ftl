{
	"Version": "2012-10-17",
	"Statement": {
		"Sid": "Stmt1",
		"Effect": "Allow",
		"Principal": {
			"AWS": "arn:aws:iam::179767337522:user/FlowAdmin"
		},
		"Action": "s3:*",
		"Resource": [
			"arn:aws:s3:::${bucketName}",
			"arn:aws:s3:::${bucketName}/*",
		]
	}
}