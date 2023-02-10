#!/usr/bin/env bash
rm -rf ./onetest.collector.tar.gz ./onetest.collector APP-META/docker-config/onetest.collector.tar.gz APP-META/docker-config/onetest.collector.zip
mvn  clean install -DskipTests

cd onetest-collector
mvn clean assembly:assembly -DskipTests
cd -

cp APP-META/docker-config/onetest.collector.tar.gz ./test-runner

