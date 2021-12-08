# Spring Data Elastic Search Example
## SpringBoot, SpringData ElasticSearch, Kibana, Minikube, Docker
#### Ref.
 - [baeldung](https://www.baeldung.com/spring-data-elasticsearch-tutorial)
 - [springDataElasticSearch](https://tecoble.techcourse.co.kr/post/2021-10-19-elasticsearch/)

## Envirionment
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

#### 2. [Execute elasticsearch and kibana with docker](https://www.elastic.co/guide/en/kibana/current/docker.html)
```shell
$ docker network create elastic
# elasticsearch
$ docker pull docker.elastic.co/elasticsearch/elasticsearch:7.10.0
$ docker run --name es01-test --net elastic -d -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" docker.elastic.co/elasticsearch/elasticsearch:7.10.0
# kibana
$ docker pull docker.elastic.co/kibana/kibana:7.10.0
$ docker run --name kib01-test --net elastic -p 5601:5601 -e "ELASTICSEARCH_HOSTS=http://es01-test:9200" docker.elastic.co/kibana/kibana:7.10.0
```
#### 3. H2 DataBase 실행
```shell
# install h2 using homebrew
$ brew install h2
# start h2 db
$ h2
# DB 파일 생성
1. http://localhost:8082 접속
 - H2(Embedded)
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

#### 2. Add dependency ES in gradle
```gradle
//elasticSearch
implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")
```
#### 3. entity 추가
```kotlin
- User
- BasicProfile (Embedded)
```

#### 4. Reposirory 추가
 - @Query는 org.springframework.data.elasticsearch.annotations의 @Query를 사용

#### 5. ElasticSearch Config 설정
 - 이때 minikube 안에서 elasticsearch를 띄우고 있으므로
 - localhost:9200이 아닌 192.168.64.4(=minikube IP):9200으로 설정
 - localhost:9200으로 실행 시 => 'connection refused'

#### 6. Service 생성

#### 7. Controller 생성

#### 8. elasticsearch logger 설정 to application.properties
```properties
logging.level.org.springframework.data.elasticsearch.client.WIRE=TRACE
```

## Issue
#### 1.Spring Data JPA와 함께 사용하는 경우 ApplicationContext 로드에 실패
```kotlin
/**
* User 클래스를 위해 생성한 JPA용 Repository와 Elasticsearch용 Repository 인터페이스 모두
* PagingAndSortingRepository<T, ID>를 확장
* 그 결과 @EnableAutoConfiguration을 기반으로 Bean을 주입하는 과정에서 Bean 중복 문제가 발생
*/
// application.properties에 아래 부분 추가
spring.main.allow-bean-definition-overriding=true
```
#### 2. No property searchSimilar found for type User
```kotlin
/**
* @EnableJpaRepositories 애너테이션이 ElasticsearchRepository 인터페이스를 확장한
* Elasticsearch 관련 클래스를 스캐닝하면 위와 같은 에러가 발생
*/
// @EnableElasticsearchRepositories 애너테이션은 Elasticsearch 관련 Repository 클래스만 스캐닝하도록
@EnableElasticsearchRepositories(basePackageClasses = arrayOf(UserSearchRepository::class))
class ElasticConfig : AbstractElasticsearchConfiguration() {
 ...
}

// @EnableJpaRepositories 애너테이션은 JPA 관련 Repository 클래스만 스캐닝하도록
@EnableJpaRepositories(excludeFilters = @ComponentScan.Filter(
    type = FilterType.ASSIGNABLE_TYPE,
    classes = UserSearchRepository.class))
@SpringBootApplication
public class ElasticsearchApplication {
 ...
}
```

## Test

#### 1. ES index 확인
```shell
# 192.168.64.4 = minikube Ip
$ curl -XGET '192.168.64.4:9200/_cat/indices?v'
```

#### 2. Create User Using Postman
```api
 GET http://localhost:8080/api/users/hi
```

#### 3. Fetch all document using json Query DSL(Domain Specific Language) from kibana
- <img src="https://user-images.githubusercontent.com/60174144/145187371-18786d67-4416-48f7-a7f9-b4d7ae044ac9.png" width="50%" height="50%">







