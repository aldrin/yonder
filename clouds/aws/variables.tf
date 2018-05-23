/**
 * Deployment Parameters
 */

// Pick your region
variable "aws_region" {
  description = "AWS Region"
  default     = "us-east-1"
}

// Linux distribution for EC2 hosts
variable "ami" {
  description = "Ubuntu 16.04"
  default     = "ami-5c66ea23"
}

// SSH login public key
variable "public_key_path" {
  description = "SSH public key"
  default     = "~/.ssh/id_rsa.pub"
}

// SSH login private Key
variable "private_key_path" {
  description = "SSH private key"
  default     = "~/.ssh/id_rsa"
}

// Business logic service
variable "service" {
  description = "Backend service"
  default     = "../../application/server/target/nobel-server-0.0.1-SNAPSHOT.jar"
}

// Service provision script
variable "service_provision" {
  description = "Service provision script"
  default     = "provision-service.sh"
}

// Infrastructure services compose file
variable "infra" {
  description = "Backend Infrastructure"
  default     = "../../application/docker-compose.yml"
}

// Infrastructure provisioning script
variable "infra_provision" {
  description = "Infrastructure provision script"
  default     = "provision-infra.sh"
}

// Lambda code package
variable "lambda" {
  description = "Lambda"
  default     = "../../application/aws/target/nobel-aws-0.0.1-SNAPSHOT.jar"
}
