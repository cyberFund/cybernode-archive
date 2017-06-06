
Cybernode is a **cluster** of software components that
provides full information about different blockchains.
These software **components** are:

  - [full blockchain nodes](fullnode) for different blockchains
  - exchange [crawler](crawler) that collects market data
  - [others](plzask)

Every component is wrapped into separate Docker **container**.
Different components can then be run on one VM or hardware
machine simultaneously.

To learn more about Docker, take a look at [Katacoda].

Hardware machines and VM shoud have Docker installed. We
use Ubuntu 16.04 as a base system for all our **node**s. Let's
clarify terminology:

  * **node** - VM or physical machine that runs containers
  * **component** - single service wrapped into container
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
[crawler]: https://github.com/cyberFund/xchange-crawler
[plzask]: https://github.com/cyberFund/cybernode/issues/new?title=docs/00intro.md:%20What%20are%20missing%20components?

[katacoda]: https://www.katacoda.com/courses/docker/
[styleguide]: https://kubernetes.io/docs/home/contribute/style-guide/#content-best-practices