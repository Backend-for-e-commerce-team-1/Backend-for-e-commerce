FROM ubuntu:latest
LABEL authors="goose"

ENTRYPOINT ["top", "-b"]