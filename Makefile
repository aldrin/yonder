cloud ?= aws

help:
	@echo "Usage: make <deploy|undeploy>"

init:
	cd clouds/$(cloud) && terraform init

deploy:
	mvn -q -f application/pom.xml clean package
	cd clouds/$(cloud) && terraform apply

undeploy:
	cd clouds/$(cloud) && terraform destroy
