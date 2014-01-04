#!/bin/bash
#
# This script is used to trigger the tools build with parameters passed by Hudson.
# All values are retrieved trough system variables set by Hudson.
# See Job -> Configure... -> This build is parameterized

MVN=${MVN:-"/opt/public/common/apache-maven-3.0.4/bin/mvn"}

if [ "${BUILD_TYPE:0:1}" == "S" ]; then
  sign=true
else
  sign=false
fi

cd "$WORKSPACE/org.eclipse.rap.tools/releng/org.eclipse.rap.tools.build" || exit 1
echo "Running maven on $PWD, sign=$sign"
$MVN clean integration-test -Dsign=$sign || exit 1

mv repository/target/*.zip "$WORKSPACE"
