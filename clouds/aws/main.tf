/**
 * AWS Deployment
 */

provider "aws" {
  region = "${var.aws_region}"
}

resource "aws_key_pair" "key" {
  key_name   = "Deployment Key"
  public_key = "${file(var.public_key_path)}"
}
