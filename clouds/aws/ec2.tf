/**
 * AWS EC2 Host Deployment
 */

// Infrastructure Host
resource "aws_instance" "infra" {
  ami = "${var.ami}"

  instance_type = "t2.micro"

  key_name = "${aws_key_pair.key.key_name}"

  monitoring = true

  tags {
    Name = "Infrastructure"
  }

  connection {
    type        = "ssh"
    user        = "ubuntu"
    private_key = "${file(var.private_key_path)}"
  }

  provisioner "file" {
    source      = "${var.infra}"
    destination = "~/docker-compose.yml"
  }

  provisioner "file" {
    source      = "${var.infra_provision}"
    destination = "~/provision.sh"
  }

  provisioner "remote-exec" {
    inline = "sudo bash ~/provision.sh"
  }
}

// Business logic Host
resource "aws_instance" "service" {
  ami = "${var.ami}"

  instance_type = "t2.micro"

  key_name = "${aws_key_pair.key.key_name}"

  monitoring = true

  tags {
    Name = "Business Logic"
  }

  connection {
    type        = "ssh"
    user        = "ubuntu"
    private_key = "${file(var.private_key_path)}"
  }

  provisioner "file" {
    source      = "${var.service}"
    destination = "~/backend.jar"
  }

  provisioner "file" {
    source      = "${var.service_provision}"
    destination = "~/provision.sh"
  }

  provisioner "remote-exec" {
    inline = "sudo bash ~/provision.sh ${aws_instance.infra.private_ip}"
  }
}
