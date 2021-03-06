# Spring Data Elastic Search Example V2
#### V2는 기존 V1를 먼저 수행하고 본다면 더욱 이해가 쉽다

---
## SpringBoot, SpringData ElasticSearch, Kibana, Minikbe, Docker

### Ref.
 - [EsBook](https://esbook.kimjmin.net/)
 - [spring.doc](https://docs.spring.io/spring-data/elasticsearch/docs/current/reference/html/#reference)

## Envirionment [V1과 동일]
#### 1. Env
```gradle
- kotlin
- jdk11
- springboot
- h2 DataBase
```
## How To Start
#### 1. Install Minikube cluster with docker container engine [생략]
 - [Link](https://itnext.io/goodbye-docker-desktop-hello-minikube-3649f2a1c469)

#### 2. [Execute elasticsearch and kibana with docker]
1. nori Plugin 설치를 위한 ElasticSearch DockerFile 작성
```dockerfile
FROM docker.elastic.co/elasticsearch/elasticsearch:7.10.0
WORKDIR /usr/share/elasticsearch
# https://esbook.kimjmin.net/06-text-analysis/6.7-stemming/6.7.2-nori
RUN yum install -y wget && \
    ./bin/elasticsearch-plugin install analysis-nori
```
2. Build image from Dockerfile
```shell
$ docker build . --tag es-nori:latest
```
3. Execute
```shell
$ docker network create elastic
# elasticsearch
$ docker pull docker.elastic.co/elasticsearch/elasticsearch:7.10.0
$ docker run --name es01-test --net elastic -d -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" es-nori:latest
# kibana
$ docker pull docker.elastic.co/kibana/kibana:7.10.0
$ docker run --name kib01-test --net elastic -p 5601:5601 -e "ELASTICSEARCH_HOSTS=http://es01-test:9200" docker.elastic.co/kibana/kibana:7.10.0
```

#### 3. H2 DataBase 실행 [Server Mode](http://www.h2database.com/html/features.html)
```shell
# install h2 using homebrew
$ brew install h2
# start h2 db
$ h2
# DB 파일 생성
1. http://localhost:8082 접속
 - H2
 - JDBC URL : jdbc:h2:~/testdb
 - 사용자명 : sa
# mv.db 확인
$ cd ~
$ ls
> testdb.mv.db ...
```

## Spring Boot Dev
#### 1. application.properties의 h2 DB 설정
```properties
spring.datasource.url=jdbc:h2:tcp://localhost/~/testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.h2.console.enabled=true
```

#### 2. Add dependency ES in build.gradle.kts
```kotlin
dependencies {
    //elasticSearch
    implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")
}
```
#### 3. entity 추가 [코드 생략]
- Post(One) <-> Tag(Many)
- Post(Many) <-> Category(One)
- <img src="https://user-images.githubusercontent.com/89895898/146628729-d0391846-fe35-43db-bcf8-584d0c0ac1f5.png" width="50%" height="50%">
```kotlin
- Post.kt
- Category.kt
- Tag.kt
```
#### 4. Index용 PostDoc 추가
```kotlin
/**
* 검색의 사용되는 Entity Field만 추가
*/
@Document(indexName = "posts")
@Setting(settingPath = "/settings/elasticsearch-settings.json")
data class PostDoc(

    @Id //spring data annotation
    @Field(type = FieldType.Long)
    val postId: Long,
    @Field(type = FieldType.Text)
    val title: String? = null,
    @Field(type = FieldType.Text)
    val content: String? = null,
    @Field(type = FieldType.Keyword)
    val postStatus: PostStatus? = null,
    @Field(type = FieldType.Keyword)
    val categoryName: String? = null,
    @Field(type = FieldType.Text)
    val tagName: String? = null, // post.tags.map { it.name }.toList().joinToString { "," }
    @Field(type = FieldType.Date)
    val createdAt: String? = null // 검색 정렬시 사용
)
```

#### 5. Index Setting json File 추가 [Reference1](https://esbook.kimjmin.net/06-text-analysis/6.3-analyzer-1/6.4-custom-analyzer), [Reference2](https://javas.tistory.com/17)
```json
{
  "analysis": {
    "tokenizer": {
      "nori_none": {
        "type": "nori_tokenizer",
        "decompound_mode": "none"
        }
    },
    "analyzer": {
      "korean": {
        "type": "nori",
        "stopwords": "_korean_"
      }
    }
  }
}
```

#### 6. After Run Application,
- Check Tokenizer as Curl
```shell
curl -X GET "192.168.64.4:9200/_analyze?pretty" -H 'Content-Type: application/json' -d'
{
  "text" : "동해물과 백두산이",
  "tokenizer" : "nori_tokenizer"
}
'
```
- Check Tokenizer as API in Kibana
```curl
GET /_analyze
{  
  "text" : "동해물과 백두산이",
  "tokenizer": "nori_tokenizer"
}
```
- Response
```json
{
  "tokens" : [
    {
      "token" : "동해",
      "start_offset" : 0,
      "end_offset" : 2,
      "type" : "word",
      "position" : 0
    },
    {
      "token" : "물",
      "start_offset" : 2,
      "end_offset" : 3,
      "type" : "word",
      "position" : 1
    },
    {
      "token" : "과",
      "start_offset" : 3,
      "end_offset" : 4,
      "type" : "word",
      "position" : 2
    },
    {
      "token" : "백두",
      "start_offset" : 5,
      "end_offset" : 7,
      "type" : "word",
      "position" : 3
    },
    {
      "token" : "산",
      "start_offset" : 7,
      "end_offset" : 8,
      "type" : "word",
      "position" : 4
    },
    {
      "token" : "이",
      "start_offset" : 8,
      "end_offset" : 9,
      "type" : "word",
      "position" : 5
    }
  ]
}
```

- Check Index as Curl
```shell
curl -X GET "192.168.64.4:9200/_aliases?pretty=true"

or

curl "http://192.168.64.4:9200/_aliases?pretty=true"
```
- response
```json
{
  "posts" : {
    "aliases" : { }
  },
   ...
}
```

## Synchronize between Database and elasticsearch
- <img src="https://user-images.githubusercontent.com/89895898/146403905-5db5d739-65d3-45ad-8950-4a9a76aee0ac.png" width="50%" height="50%">

#### 1. Reposirory 추가 [코드 생략]
```kotlin
- PostRepository.kt
- PostSearchRepository.kt
```

#### 2. Service 생성 [코드 생략]
```kotlin
- PostSearvice
```

#### 3. Controller 생성 [코드 생략]
```kotlin
- PostController
```

#### 4. post 저장 후
- in h2 Database
- <img src="https://user-images.githubusercontent.com/60174144/146676218-87254798-4132-4cdb-a0fa-f00209a74fb1.png" width="50%" height="50%">

- in elasticSearch index
- <img src="https://user-images.githubusercontent.com/60174144/146676229-e0a02b5d-4529-49fc-b61f-e47b414a36e0.png" width="50%" height="50%">


## Search Post
- <img src="https://user-images.githubusercontent.com/89895898/146404147-6a4ad3da-83a6-46bf-893f-21b5e9bc221a.png" width="50%" height="50%">

#### 1. Custom Repository 추가
```kotlin
- CustomPostSearchRepository.kt
- CustomPostSearchRepositoryImpl.kt
```
#### 2. Service 수정 [코드 생략]
```kotlin
- PostSearvice
```

#### 3. Controller 수정 [코드 생략]
```kotlin
- PostController
```
#### 4. index에서 검색
```
- content: "스타트@업"인 post에서
- text: "스타트 업"으로 검색
- Using API in PostController
```
- <img src="https://user-images.githubusercontent.com/60174144/146676248-dc9f40ee-2d06-4c1e-9dbc-9b80d54860da.png" width="50%" height="50%">

#### 5. 참고: sql like 사용해서 '%스타트 업%'은 검색X
- <img src="https://user-images.githubusercontent.com/60174144/146676296-f0d82932-8a25-4640-ae5a-f2e237dc389f.png" width="50%" height="50%">










