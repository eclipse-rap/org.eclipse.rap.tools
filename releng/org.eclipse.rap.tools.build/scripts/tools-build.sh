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

######################################################################
# Build RAP Tools

cd "$WORKSPACE/org.eclipse.rap.tools/releng/org.eclipse.rap.tools.build" || exit 1
echo "Running maven on $PWD, sign=$sign"
$MVN clean integration-test -Dsign=$sign || exit 1

######################################################################
# Rename zip file

VERSION=$(ls repository/target/repository/features/org.eclipse.rap.tools.feature_*.jar | sed 's/.*_\([0-9.-]\+\)\..*\.jar/\1/')
TIMESTAMP=$(ls repository/target/repository/features/org.eclipse.rap.tools.feature_*.jar | sed 's/.*\.\([0-9-]\+\)\.jar/\1/')
echo "Version is '$VERSION'"
echo "Timestamp is '$TIMESTAMP'"
test -n "$VERSION" || exit 1
test -n "$TIMESTAMP" || exit 1

# Example: rap-tools-2.2.0-N-20110814-2110.zip
mv repository/target/*.zip "$WORKSPACE/rap-tools-$VERSION-$BUILD_TYPE-$TIMESTAMP.zip"
