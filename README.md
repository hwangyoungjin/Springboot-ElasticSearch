# Spring Data Elastic Search Example
### Ref.
 - [baeldung](https://www.baeldung.com/spring-data-elasticsearch-tutorial)
 - [springDataElasticSearch](https://tecoble.techcourse.co.kr/post/2021-10-19-elasticsearch/)
## envirionment
```gradle
- kotlin
- jdk11
- springboot
```
## 1. Add dependency in gradle
```gradle
//elasticSearch
implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")
```
## 2. entity 추가
```kotlin
- User
- BasicProfile (Embedded)
```

## 3. Reposirory 추가
 - @Query는 org.springframework.data.elasticsearch.annotations의 @Query를 사용

## 4. ElasticSearch Config 설정
 - 이때 minikube 안에서 elasticsearch를 띄우고 있으므로
 - localhost:9200이 아닌 minikubeIP:9200으로 설정
 - localhost:9200으로 실행 시 => 'connection refused'

## 5. Service 생성

## 6. Execute elasticsearch in docker
```shell
$ docker pull docker.elastic.co/elasticsearch/elasticsearch:7.10.0
$ docker run -d -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" docker.elastic.co/elasticsearch/elasticsearch:7.10.0
```

## 7. logger 설정 to application.properties
```properties
logging.level.org.springframework.data.elasticsearch.client.WIRE=TRACE
```

## 8. Issue
### 1.Spring Data JPA와 함께 사용하는 경우 ApplicationContext 로드에 실패
```
User 클래스를 위해 생성한 JPA용 Repository와 Elasticsearch용 Repository 인터페이스 모두
PagingAndSortingRepository<T, ID>를 확장하고 있습니다.
그 결과 @EnableAutoConfiguration을 기반으로 Bean을 주입하는 과정에서 Bean 중복 문제가 발생합니다.

application.properties에 아래 부분 추가
spring.main.allow-bean-definition-overriding=true
```
### 2. No property searchSimilar found for type User
```
@EnableJpaRepositories 애너테이션이 ElasticsearchRepository 인터페이스를 확장한
Elasticsearch 관련 클래스를 스캐닝하면 위와 같은 에러가 발생

@EnableElasticsearchRepositories 애너테이션은 Elasticsearch 관련 Repository 클래스만 스캐닝하도록
@EnableElasticsearchRepositories(basePackageClasses = arrayOf(UserSearchRepository::class))
class ElasticConfig : AbstractElasticsearchConfiguration() {
 ...
}

@EnableJpaRepositories 애너테이션은 JPA 관련 Repository 클래스만 스캐닝하도록 합니다.
@EnableJpaRepositories(excludeFilters = @ComponentScan.Filter(
    type = FilterType.ASSIGNABLE_TYPE,
    classes = UserSearchRepository.class))
@SpringBootApplication
public class ElasticsearchApplication {
```

