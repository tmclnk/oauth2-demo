.PHONY: help clean start

help: ## Print Help
	@egrep -h '\s##\s' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-20s\033[0m %s\n", $$1, $$2}'

clean: ## Clean
	./mvnw clean

prepare: ## Build and Tag a Release
	./mvnw --batch-mode release:prepare

start-client : clean ## Start Client App
	./mvnw -pl ce-client spring-boot:run -Dspring-boot.run.jvmArguments="-ea"

start-idp: clean ## Start IdP
	./mvnw -pl ce-idp spring-boot:run -Dspring-boot.run.jvmArguments="-ea"

start-rs-user-profile: clean ## Start User Profile Service
	./mvnw -pl ce-rs-user-profile spring-boot:run -Dspring-boot.run.jvmArguments="-ea"
