[![Travis branch](https://img.shields.io/travis/cyberFund/cybernode/master.svg)](https://travis-ci.org/cyberFund/cybernode)

**Cybernode** - polyblockchain full node (cluster with full nodes for multiple blockchains)

#### Architecture

![Architecture](https://rawgit.com/cyberFund/cybernode/master/cybernode_01.svg)
![architecture-description](https://github.com/cyberFund/cybernode/raw/master/architecture-description.png)
![kenig-architecture](https://github.com/cyberFund/cybernode/raw/master/kenig-architecture.png)
![state-monitor](https://github.com/cyberFund/cybernode/raw/master/state-monitor-design.png)
![state-monitor-design](https://github.com/cyberFund/cybernode/raw/master/state-monitor.png)

#### Cyberchain comment data structure:

**Block comment**

    title – block hash
    permlink – block hash
    parent_permlink (main tag) – cryptocurrency code
    body – block data including transactions hash list in JSON format
    json_metadata – JSON object:
      tags – all tags we need
	    height – bock height
	    data_type = ‘block’

**Transaction comment**

    title – tx id (or hash depends on cryptocurrency)
    permlink* – tx id (or hash depends on cryptocurrency)
    parent_permlink (main tag) – cryptocurrency code
    body – tx data in JSON format
    json_metadata – JSON object:
        tags – all tags we need
        height – bock height
        data_type - ’tx’

#### Blockchain systems in containers

We are collect blockchain systems an put them to docker containers:
1. All containers build with the same standard and specification
2. Have same basic layer
3. Conversation about ports of containers
4. Custom general tweaks on a top of base layer for performance of container
5. Prepared user control for security
6. Conversation about side store of blockchain data
7. We are publish our containers in dockerhub.
8. We are controlling new tested releases of systems and updating our containers

#### Flow of integrations by prioritized groups (dockerization and indexing too)

```
Bitcoin <-
Litecoin
Dash
```

```
Ethereum (ERC20+) <-
Ethereum Classic
```

```
BitShares <-
Steem
Golos
```

```
Ripple (client) <-
Stellar
```

```
NEM <-
NXT
Monero
DAGs (!)
```
