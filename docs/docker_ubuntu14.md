installing docker (as root)

`apt-get update`

`apt-get -y install docker.io`

`ln -sf /usr/bin/docker.io /usr/local/bin/docker` 

(symlink docker.io binary installed on prev step somewhere)


`curl -L https://raw.githubusercontent.com/docker/compose/$(docker-compose version --short)/contrib/completion/bash/docker-compose -o /etc/bash_completion.d/docker-compose`

(this one adds bash autocompletion for docker commands)

