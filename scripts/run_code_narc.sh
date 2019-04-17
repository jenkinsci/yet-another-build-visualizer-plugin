#!/bin/bash

docker run --rm -v `pwd`:/ws -u `id -u`:`id -g` \
    edupo/codenarc \
    -rulesetfiles=file:scripts/codenarc_rules.txt \
    -basedir=. \
    -includes='**/*.groovy' \
    -excludes='**/target/**' \
    -report=html:CodeNarcReports/CodeNarcReport.html
