Installation
============

1. Extract DataCompare_1.4.zip file e.g. C:\DataCompare

2. Open Compare.bat file and set JAVA_HOME environment variable pointing to JDK 1.8+

Setting up config.properties
============================

The config.properties is used to specify settings related to generating report at desired location.

1. Open the config.properties

2. Read the comments mentioned before each properties and set the values accordingly.


Setting up compare.properties
=============================

The compare.properties is used to specify settings related to source and target database connection details, entities, etc. which will be required to generate data comparison report.

1. Open the compare.properties

2. Read the comments mentioned before each properties and set the values accordingly.

Assumptions
===========

1. The entities should be of same structure in both source and target database
2. Entity should have at least one primary key or alternatively mention columns under pkeys which can be used to uniquely identify records

Known Issues or Limitations
===========================

1. Blob data comparison is not supported
2. Script generation does not handle single quotes within values
3. OutOfMemory errors observed when entities contains huge number of records
4. For large differences the data comparison report causes problem while opening in IE

Using the data comparison report
================================

Three level of data difference is available in data comparison report.
a. Different data
	In the report it will show two rows for difference between one record. 1st row represents the data of source and 2nd row represents the data of target. Different data in source is represented as blue bold colour and same in target is represented as green bold colour.

b. Missing data
	This is the data that is missing in source database.

c. New data
	This is the data that is missing in target database.

Generating data sync script via data comparison report
======================================================

Click on 'Generate Consolidated Data Sync Script' button to generate data sync script for all the data differences.

Click on 'Toggle Collapse' buttong to hide the differences for corresponding entity

Click on 'Generate Script' button to generate data sync script for corresponding entity. This will generate update script for different data, delete script for missing data and insert script for new data. You can execute this script on target database to make it sync with source database.

Click on 'Generate Reverse Script' button to generate data sync script for corresponding entity. This will generate update script for different data, delete script for new data and insert script for missing data. You can execute this script on source database to make it sync with target database.
