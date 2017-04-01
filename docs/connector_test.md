##Тестирование bitcore коннектора

####Блокчейн
Для тестирования работы коннектора нам подойдёт блокчейн Голоса с небольшими изменениями.
1. Убрать проверку частоты публикации комментариев (постов). 
2. Убрать проверку на размер блока.

Берём блокчейн с этими изменениями. Собираем из исходников. Копируем нужные программы в папку golosnode

```
git clone https://github.com/nxtpool/golos.git
cd golos
git checkout origin/mytestnet
git submodule update --init --recursive && cmake -DCMAKE_BUILD_TYPE=Release .
make
cd ..
mkdir golosnode
cp programs/golosd/golosd ../golosnode/
cp programs/golosd/snapshot5392323.json ../golosnode/
cp programs/cli_wallet/cli_wallet ../golosnode/
```

####Запуск блокчейна

В файл config.ini вносим следующие изменения (вставляем верный приватный ключ вместо 5CYBERTESTERPRIVATEKEY):
```
seed-node = 127.0.0.1:8888
seed-node = 127.0.0.1:8889
rpc-endpoint = 127.0.0.1:8090
public-api = database_api login_api network_broadcast_api follow_api market_history_api tag_api
enable-plugin = witness account_history tags follow market_history
enable-stale-production = true
witness = "cybertester"
private-key = 5CYBERTESTERPRIVATEKEY
```

Запускаем блокчейн в фоновом режиме
```
screen -dmS golosd ./golosd
```

Запускаем cli_wallet. Используем следующие команды для импорта ключа
```
set_password verysecret
unlock verysecret
import_key 5CYBERTESTERPRIVATEKEY
save_wallet_file ""
```
Используем верный приватный ключ вместо 5CYBERTESTERPRIVATEKEY
Используем пароль для кошелька вместо verysecret. Его нужно будет потом указать в конфиге коннектора.

Выходем из cli_wallet и запускаем его в фоновом режиме

```
screen -dmS cliwallet ./cli_wallet --server-rpc-endpoint=ws://127.0.0.1:8090 --rpc-http-endpoint=127.0.0.1:8091 --rpc-http-allowip 127.0.0.1 -d
```

Проверяем что cli_wallet работает
```
curl -d '{"id":"1","method":"is_locked","params":[""]}' localhost:8091
```

####Запуск коннектора

```
git clone https://github.com/nxtpool/cybernode.git
```

Меняем если нужно порты и пароль config.json в папке connectors/bitcore

В папке install запускаем bitcore_connector_test.sh

Запустится докер контейнер с коннектором в тестовом режиме. Т.е. для скорости индексации будет сгенерено 1000 пользователей им выдано по 10000 силы. Блоки будут индексироваться без валидации содержимого блокчейна.   
