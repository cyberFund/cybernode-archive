1. Нужно попросить Леху склонировать ssh ключ

2. Потом открыть ssh тонель
```
ssh -f kong@api.cyber.fund -L 8088:localhost:8080 -N
ssh -f kong@api.cyber.fund -L 8001:localhost:8001 -N
```
3. Потом сходить на localhost:8088
4. Вбить настройку localhost:8001

5. Посмотреть насколько прикольная и простая тулза

Ендпоинт https://api.cyber.fund/
