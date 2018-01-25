
Cybernode is a **cluster** of software components that
provides full information about different blockchains
and tools to work with them. These **components** are:

  - [full blockchain nodes](fullnode) for different blockchains
  - market data collector and API [cyber-markets](markets)
  - blockchain search engine with API [cyber-search](search)
  - web app for services and status [cyber-ui](cyber-ui)
  - [others](plzask)

Every component is wrapped into separate Docker **container**,
so that different components can be run on one VM or hardware
machine simultaneously.

You should have Docker installed to run cybernode. To learn
Docker, take a look at [Katacoda].

We use Ubuntu 16.04 as a reference system for **node**s, with
some tests being done on Fedora 27+.


### Terminology

  * **node** - VM or physical machine that runs containers
  * **component** - some software wrapped into container
  * **cybernode** - cluster of nodes that connects
    containers with each other and provides external API


### Improving this documentation

You can help documenting `cybernode` and improve your
writing skills by editing
[this documentation on GitHub](https://github.com/cyberFund/cybernode/tree/master/docs)
and following
[best practice](https://kubernetes.io/docs/home/contribute/style-guide/#content-best-practices)
guidelines from Kubernetes.


[fullnode]: https://github.com/cyberFund/cybernode/issues?q=label%3Afullnode
[markets]: https://github.com/cyberFund/cyber-markets
[search]: https://github.com/cyberFund/cyber-search
[cyber-ui]: https://github.com/cyberFund/cyber-ui
[plzask]: https://github.com/cyberFund/cybernode/issues/new?title=docs/00intro.md:%20What%20are%20missing%20components?

[katacoda]: https://www.katacoda.com/courses/docker/
[styleguide]: https://kubernetes.io/docs/home/contribute/style-guide/#content-best-practices
