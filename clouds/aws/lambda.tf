/**
 * AWS Lambda 
 */
// Function definition
resource "aws_lambda_function" "lambda" {
  runtime = "java8"

  function_name = "Nobel"

  filename = "${var.lambda}"

  source_code_hash = "${base64sha256(file(var.lambda))}}"

  handler = "co.aldrin.nobel.aws.Lambda::handleRequest"

  memory_size = 256

  timeout = 30

  role = "${aws_iam_role.lambda_role.arn}"

  vpc_config {
    subnet_ids         = ["${data.aws_subnet_ids.all.ids}"]
    security_group_ids = ["${data.aws_security_group.default.id}"]
  }

  environment {
    variables = {
      INFRA   = "${aws_instance.infra.private_ip}"
      SERVICE = "${aws_instance.service.private_ip}"
    }
  }
}

// IAM Role for the Lambda
resource "aws_iam_role" "lambda_role" {
  name = "nobel-lambda-role"

  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": "sts:AssumeRole",
      "Principal": {
        "Service": "lambda.amazonaws.com"
      },
      "Effect": "Allow",
      "Sid": ""
    }
  ]
}
EOF
}

// IAM Policy for the Lambda (Allow VPC Access)
data "aws_iam_policy" "lambda_policy" {
  arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaVPCAccessExecutionRole"
}

// Connect the Lambda Policy with the Lambda Role
resource "aws_iam_role_policy_attachment" "lambda-role-policy" {
  role       = "${aws_iam_role.lambda_role.name}"
  policy_arn = "${data.aws_iam_policy.lambda_policy.arn}"
}

// Get identifiers for all subnets in the VPC (see vpc_config above)
data "aws_subnet_ids" "all" {
  vpc_id = "${data.aws_vpc.default.id}"
}

// The default VPC
data "aws_vpc" "default" {
  default = true
}

// The default security group
data "aws_security_group" "default" {
  name = "default"
}
