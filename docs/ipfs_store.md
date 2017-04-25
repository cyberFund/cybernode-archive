#IPFS Store

IPFS + OrbitDB + RPC api инкеапсулирован в модуль ipfs. Модуль ipfs поднимается в отдельном докер контейнере устанавливать IPFS на хост машине не нужно.

###API

```
insertBlock(JSON block, String system)
insertTx(JSON tx, String system)
getHeight(String system)
getBlockByHeight(int Height, String system)
getBlockByHash(String hash, String system)
getTxByTxid(String txid, String system)
```

system = ["btc"]

JSON документ block обязан содержать следующие поля: {String hash, int height}
JSON документ tx обязан содержать следующие поля: {String txid}

