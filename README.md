# Cybernode

Polyblockchain full node

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
