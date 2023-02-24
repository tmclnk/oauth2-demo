.PHONY: help clean start

help: ## Print Help
	@egrep -h '\s##\s' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-20s\033[0m %s\n", $$1, $$2}'

clean: ## Clean
	./mvnw clean
	rm -f release.properties
	find . -name '*releaseBackup' -exec rm {} +

release: ## Prepare a Release to Github and M2
	./mvnw --batch-mode release:prepare
	./mvnw release:perform

package: ## Compile and Build JARs
	./mvnw package

start-client : clean ## Start Client App
	./mvnw -pl client spring-boot:run -Dspring-boot.run.jvmArguments="-ea"

start-rs: clean ## Start User Profile Service
	./mvnw -pl rs spring-boot:run -Dspring-boot.run.jvmArguments="-ea"
