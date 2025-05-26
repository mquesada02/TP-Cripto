#!/bin/bash

properties=()

while [[ $# -gt 0 ]]; do
    key="$1"
    if [[ "$key" == -* ]]; then
        key="${key#-}"
        if [[ "$2" && "$2" != -* ]]; then
            value="$2"
            shift
        else
            value="true"
        fi
        properties+=("-D${key}=${value}")
    fi
    shift
done

mvn exec:java -Dexec.mainClass="ar.edu.itba.cys.Main" "${properties[@]}"