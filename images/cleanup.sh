#!/bin/bash

# remove dangling images (unused <none>:<none>)
docker rmi $(docker images --quiet --filter "dangling=true")
