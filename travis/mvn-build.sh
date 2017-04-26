#! /usr/bin/env bash

function snapshot() {
    echo "not building a tag -> Snapshot"
    mvn clean dokka:javadocJar install -V -U --settings travis/settings.xml
}

function release() {
    echo "building a tag -> Release"
}

function main() {
    if [[ $TRAVIS_TAG == Release* ]];
        then release;
        else snapshot;
    fi;
}

# Run main if this is not included
if ((${#BASH_SOURCE[@]} == 1)); then
    main "$@"
fi