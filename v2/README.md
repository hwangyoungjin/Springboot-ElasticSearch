# Spring Data Elastic Search Example V2
#### V2는 기존 V1를 먼저 수행하고 본다면 더욱 이해가 쉽습니다.
---
## SpringBoot, SpringData ElasticSearch, Kibana, Minikbe, Docker
---
### V1과 V2 차이점
#### 1. Tokenizer
- V1에서는 Standard Tokenizer를 사용하므로 한글 같은 텍스트는 제대로 토큰화 되지 않습니다. 따라서 V2에서는 어간 추출이 가능한 nori tokenizer를 활용했습니다.
```
 - Standard Tokenizer
  - Ex> "동해물과 백두산이" -> "동해물과", "백두산이"
 - Nori Tokenizer
  - Ex> "동해물과 백두산이" -> "동해", "물", "과", "백두", "산", "이"
```
#### 2. Index
- V1에서는 Entity 1개에 대해서만 Index를 구성하였습니다.
- V2에서는 OneToMany, OneToOne등 검색할 Entity의 연관된 Entity가 많아 검색조건에 해당되는 필드들을 가진 Index 전용 class를 만들어 활용하였습니다.

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
```docker
FROM docker.elastic.co/elasticsearch/elasticsearch:7.10.0
WORKDIR /usr/share/elasticsearch
# https://esbook.kimjmin.net/06-text-analysis/6.7-stemming/6.7.2-nori
RUN yum install -y wget && \
    ./bin/elasticsearch-plugin install analysis-nori
```
2. Build image from Dockerfile
```shell
docker build . --tag es-nori:latest
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

#### 2. Add dependency ES and queryDsl in gradle
```gradle
dependencies {
    //elasticSearch
    implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")
}
```
#### 3. entity 추가 [코드 생략]
- Post(One) <-> Tag(Many)
- Post(Many) <-> Category(One)
```kotlin
- Post
- Category
- Tag
```
#### 4. Index용 PostDoc 추가
```kotlin
/**
* 검색의 사용되는 Entity Field만 추가
**/

```

#### 4. Reposirory 추가 [코드 생략]
```

#### 6. Service 생성 [코드 생략]
```kotlin
- UserSearvice
```

#### 7. Controller 생성 [코드 생략]
```kotlin
- UserController
```

#### 8. elasticsearch logger 설정 to application.properties
```properties
logging.level.org.springframework.data.elasticsearch.client.WIRE=TRACE
```

#### 9. [Database와 elasticsearch Sync 맞추기](https://medium.com/@yassine.s.sabri/how-to-synchronize-mysql-database-with-elasticsearch-and-perform-data-querying-in-a-spring-boot-829ff7717380)
##### 1. 스프링 스케쥴러 설정
```kotlin
@EnableScheduling
class SpringElasticSearchApplication

fun main(args: Array<String>) {
	runApplication<SpringElasticSearchApplication>(*args)
}
```

##### 2. User Entity의 Modified field 추가
```kotlin
@Document(indexName = "users")
@Entity
class User (

    ...

    @Field(type = FieldType.Date, format = [], pattern = ["uuuu-MM-dd HH:mm:ss"])
    var modificationDate : LocalDateTime? = null

)
```
##### 3. scheduler service 생성
```kotlin
@Service
class ElasticSynchronizer(
    private val userSearchRepository: UserSearchRepository,
    private val userRepository: UserRepository,
) {

    @Scheduled(cron = "*/60 * * * * *") //60초 마다 실행
    @Transactional
    fun sync(){
        logger.info("Start Syncing - {}", LocalDateTime.now())
        syncUsers()
        logger.info("end Syncing - {}", LocalDateTime.now())
    }

    private fun syncUsers(){
        //최근 2시간 동안 갱신된 Post 찾아서 ElasticSearch 갱신
        val startDate = LocalDateTime.now().minusHours(2)
        val endDate = LocalDateTime.now()

        val userList: List<User> = if (userSearchRepository.count() == 0L) {
            userRepository.findAll()
        } else {
            userRepository.findAllByModificationDate(startDate,endDate)
        }
        for (user in userList) {
            logger.info("Syncing User - {}", user.id)
            userSearchRepository.save(user)
        }
    }
}
```

## Issue
#### 1.

기존에 추가했던 UserEntity modifiedDate 필드 변경
 -> elasticsearch annotations 추가
/** 기존 **/
@UpdateTimestamp
val modifiedAt : Timestamp

/** 변경 **/
@Field(type = FieldType.Date, format = [], pattern = ["uuuu-MM-dd HH:mm:ss"])
var modificationDate : LocalDateTime? = null
```

## Test







