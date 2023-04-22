# docker exec -it <컨테이너 id> <명령어>

```console
docker run redis
```
```console
docker exec -it <컨테이너 id> redis-cli
```
### -it
* i : interactive
* t : terminal

docker exec 하고 나면 host terminal 로 돌아오는데 이를 방지

레디스 클라이언트를 실행하고자 하면
레디스 클라이언트도 컨테이너 안으로 들어가야 한다.

# docker exec -it <컨테이너 id> <sh | bash | zsh | powershell>

```console
docker run alpine ping localhost 
```

```console
docker exec -it <컨테이너 id> sh
```

참고로 나오는 건 ctrl+c 가 아니고 **ctrl+d** 이다 
