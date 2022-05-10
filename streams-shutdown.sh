#!/bin/bash

# Code adapted from: 
# https://stackoverflow.com/questions/192249/how-do-i-parse-command-line-arguments-in-bash

IP=localhost
PORT=""

while [[ $# -gt 0 ]]; do
    case $1 in
        --ip)
            IP="$2"
            shift # past argument
            shift # past value
            ;;
        -p|--port)
            PORT="$2"
            shift # past argument
            shift # past value
            ;;
        -h|--help)
            echo -e "Usage: shutdown-producer [OPTION...]\n"
            echo -e "  -h, --help                 show list of accepted arguments"
            echo -e "      --ip                   specify the IP address to connect to"
            echo -e "  -p, --port                 specify the port to connect to"
            exit 1
            ;;
        -*|--*)
            echo "Unknown option $1"
            exit 1
            ;;
    esac
done

if [ -z "$PORT" ]
then
    echo -e "Missing port. \nUse the -p option to specify a port or -h for more help"
    exit 1
fi

nc -zv $IP $PORT
echo -e "streams_command_shutdown" | nc $IP $PORT
