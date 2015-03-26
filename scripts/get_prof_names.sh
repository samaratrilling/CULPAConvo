#!/bin/bash
COUNTER=0
while [ $COUNTER -lt 113 ]; do
        curl http://api.culpa.info/professors/department_id/$COUNTER >> professors.txt
        let COUNTER=COUNTER+1
done
