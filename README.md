[![Travis branch](https://img.shields.io/travis/cyberFund/cybernode/master.svg)](https://travis-ci.org/cyberFund/cybernode)

**Cybernode** - polyblockchain full node (cluster with full nodes for multiple blockchains)

#### Architecture

![Architecture](https://rawgit.com/cyberFund/cybernode/master/cybernode_01.svg)
![architecture-description](https://github.com/cyberFund/cybernode/blob/13-cybernode-description/architecture-description.png)
![kenig-architecture](https://github.com/cyberFund/cybernode/blob/13-cybernode-description/kenig-architecture.png)
![state-monitor](https://github.com/cyberFund/cybernode/blob/13-cybernode-description/state-monitor.png)
![state-monitor-design](https://github.com/cyberFund/cybernode/blob/13-cybernode-description/state-monitor-design.png)

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
  1. Have same basic layer
  2. Conversation about ports of containers
  3. Custom general tweaks on a top of base layer for performance of container
  4. Prepared user control for security
  5. Conversation about side store of blockchain data
2. We are publish our containers in dockerhub.
3. We are controlling new tested releases of systems and updating our containers
