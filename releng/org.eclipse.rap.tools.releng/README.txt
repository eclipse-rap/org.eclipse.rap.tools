Building RAP Tools
==================

We use Tycho [1] to build RAP.

Prerequisites
-------------

* A Git client
* Maven 3.0
* A working Internet connection

Preparation
-----------

Clone the RAP Tools repository from the remote Git source code repository:

  git clone git://git.eclipse.org/gitroot/rap/org.eclipse.rap.tools.git

This will create a complete clone of the RAP Tools Git repository on your disk in a
new directory 'org.eclipse.rap.tools' with the latest HEAD version of the RAP Tools.

RAP Tooling
-----------

Run Maven in the RAP Tools build project:

  cd cd org.eclipse.rap.tools/releng/org.eclipse.rap.tools.releng/
  mvn clean package

A p2 repository with the RAP Tools will be created in repository/target

References
----------

[1] http://eclipse.org/tycho/
