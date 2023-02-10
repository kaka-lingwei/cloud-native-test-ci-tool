#!/bin/sh
#
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

export M2_HOME=/opt/apache-maven-3.8.7
export MAVEN_HOME=/opt/apache-maven-3.8.7
export PATH=${M2_HOME}/bin:${PATH}
echo export PATH="${PATH}">> ~/.bashrc
source ~/.bashrc

cd ~
mkdir -p logs
mkdir -p onetest/jacoco_coverage/exec_data


echo '=================Start Clone Case Code========================='
git clone --depth=1 $CODE -b $BRANCH code
echo '=================Finish Clone Case Code========================='

echo '=================Start Execute Test Case========================'
cd code
cd $CODE_PATH
$CMD > /root/testlog.txt
echo '=================FINISH Execute Test Case========================'

cd ~
cd onetest/jacoco_coverage/
echo '=================Start Clone Source Code========================='
git clone --depth=1  $SOURCE_CODE -b $SOURCE_BRANCH sourceCode
echo '=================Finish Clone Source Code========================='
cd sourceCode

echo '=================Start Compile Source Code========================='
mvn -Prelease-all -DskipTests -Dspotbugs.skip=true clean install -U
echo '=================Finish Compile Source Code========================='

cd ~

echo '=================Start Collector Coverage========================='
nohup java -Xmx512m -DALL_IP=$ALL_IP -DPORT="2023" -DCommitId=$CommitId -Dlogging.path="root/logs" -cp "/root/onetest.collector/lib/*" shell.OneTestShell > /root/logs/collectCoverage.out
echo '=================Finish Collector Coverage========================='


res=$?
# wait for result collect
touch /root/testdone
sleep 60
exit $res
