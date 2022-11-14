.PHONY: help clean start

help: ## Print Help
	@egrep -h '\s##\s' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-20s\033[0m %s\n", $$1, $$2}'

clean: ## Clean
	./mvnw clean
	rm release.properties
	find . -name '*releaseBackup' -exec rm {} +

prepare: ## Prepare a Release
	./mvnw --batch-mode release:prepare

package: ## Compile and Build JARs
	./mvnw package

start-client : clean ## Start Client App
	./mvnw -pl ce-client spring-boot:run -Dspring-boot.run.jvmArguments="-ea"

start-idp: clean ## Start IdP
	./mvnw -pl ce-idp spring-boot:run -Dspring-boot.run.jvmArguments="-ea"

start-rs-user-profile: clean ## Start User Profile Service
	./mvnw -pl ce-rs-user-profile spring-boot:run -Dspring-boot.run.jvmArguments="-ea"
