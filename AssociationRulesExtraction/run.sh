#!/bin/bash

DIR=$(dirname $0)

java -jar ${DIR}/AssociationRulesExtraction-jar-with-dependencies.jar $@
