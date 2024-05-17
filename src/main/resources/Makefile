.PHONY: build
build:
  ./gradlew bootJar
  docker build . -t 767397724469.dkr.ecr.ap-northeast-2.amazonaws.com/musicdiary:latest --platform linux/amd64

push: build
  $$(aws ecr get-login --no-include-email)
  docker push 767397724469.dkr.ecr.ap-northeast-2.amazonaws.com/musicdiary:latest