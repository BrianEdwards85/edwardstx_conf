#!/bin/bash

/usr/local/bin/lein uberjar

/usr/bin/docker build -t edwardstx/conf .

/usr/bin/docker run --restart always -d -p 127.0.0.1:5005:5005 -v /etc/service:/etc/service --name edwardstx_conf edwardstx/conf
