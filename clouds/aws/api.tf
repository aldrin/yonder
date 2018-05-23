/**
 * AWS API Gateway
 */

// Current account identity
data "aws_caller_identity" "current" {}

// The API
resource "aws_api_gateway_rest_api" "nobel" {
  name        = "Nobel API"
  description = "This is an API to fetch Nobel Laureates"
}

// The first level resource (year)
resource "aws_api_gateway_resource" "year" {
  rest_api_id = "${aws_api_gateway_rest_api.nobel.id}"
  parent_id   = "${aws_api_gateway_rest_api.nobel.root_resource_id}"

  path_part = "{year}"
}

// The second level resource (category)
resource "aws_api_gateway_resource" "category" {
  rest_api_id = "${aws_api_gateway_rest_api.nobel.id}"
  parent_id   = "${aws_api_gateway_resource.year.id}"

  path_part = "{category}"
}

// The HTTP method
resource "aws_api_gateway_method" "get" {
  rest_api_id = "${aws_api_gateway_rest_api.nobel.id}"
  resource_id = "${aws_api_gateway_resource.category.id}"

  http_method      = "GET"
  authorization    = "NONE"
  api_key_required = true
}

// The integration (connection) between API Gateway and Lambda
resource "aws_api_gateway_integration" "integration" {
  rest_api_id = "${aws_api_gateway_rest_api.nobel.id}"
  resource_id = "${aws_api_gateway_resource.category.id}"
  http_method = "${aws_api_gateway_method.get.http_method}"

  integration_http_method = "POST"
  type                    = "AWS_PROXY"
  uri                     = "arn:aws:apigateway:${var.aws_region}:lambda:path/2015-03-31/functions/${aws_lambda_function.lambda.arn}/invocations"
}

// Grant API Gateway the permission to invoke the lambda
resource "aws_lambda_permission" "allow_invoke_lambda" {
  statement_id = "AllowExecutionFromAPIGateway"
  principal    = "apigateway.amazonaws.com"
  action       = "lambda:InvokeFunction"

  function_name = "${aws_lambda_function.lambda.arn}"
  source_arn    = "arn:aws:execute-api:${var.aws_region}:${data.aws_caller_identity.current.account_id}:${aws_api_gateway_rest_api.nobel.id}/*/${aws_api_gateway_method.get.http_method}${aws_api_gateway_resource.category.path}"
}

// API deployment
resource "aws_api_gateway_deployment" "nobel" {
  stage_name = "nobel"

  rest_api_id = "${aws_api_gateway_rest_api.nobel.id}"
  depends_on  = ["aws_api_gateway_integration.integration"]
}

// API Usage Plan
resource "aws_api_gateway_usage_plan" "usage" {
  name = "Basic Usage"

  api_stages {
    api_id = "${aws_api_gateway_rest_api.nobel.id}"
    stage  = "${aws_api_gateway_deployment.nobel.stage_name}"
  }

  quota_settings {
    limit  = 1000
    period = "MONTH"
  }

  throttle_settings {
    burst_limit = 100
    rate_limit  = 10
  }
}

// API Access Key
resource "aws_api_gateway_api_key" "api_key" {
  name = "Basic Usage Client"
}

// Connect API key with the API usage plan
resource "aws_api_gateway_usage_plan_key" "main" {
  usage_plan_id = "${aws_api_gateway_usage_plan.usage.id}"
  key_id        = "${aws_api_gateway_api_key.api_key.id}"
  key_type      = "API_KEY"
}
