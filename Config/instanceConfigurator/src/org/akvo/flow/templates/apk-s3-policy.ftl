{
  "Version": "${version}",
  "Statement": [
    {
      "Sid": "Stmt1",
      "Effect": "Allow",
      "Action": [
        "s3:PutObject"
      ],
      "Resource": [
        "arn:aws:s3:::${bucketName}/devicezip/*"
      ]
    },
    {
      "Sid": "Stmt2",
      "Effect": "Allow",
      "Action": [
        "s3:PutObject",
        "s3:PutObjectAcl"
      ],
      "Resource": [
        "arn:aws:s3:::${bucketName}/images/*"
      ]
    },
    {
      "Sid": "Stmt3",
      "Effect": "Allow",
      "Action": [
        "s3:GetObject"
      ],
      "Resource": [
        "arn:aws:s3:::${bucketName}/surveys/*"
      ]
    }
  ]
}