output "Login to Infrastructure Host" {
  value = "ssh -i ${var.private_key_path} ubuntu@${aws_instance.infra.public_ip}"
}

output "Login to Backend Service" {
  value = "ssh -i ${var.private_key_path} ubuntu@${aws_instance.service.public_ip}"
}

output "Invoke the API" {
  value = "curl -H x-api-key:${aws_api_gateway_api_key.api_key.value} ${aws_api_gateway_deployment.nobel.invoke_url}/1979/peace"
}
