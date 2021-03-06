# Spring Data Elastic Search Example V1
## SpringBoot, SpringData ElasticSearch, Kibana, Minikube, Docker
#### Ref.
 - [baeldung](https://www.baeldung.com/spring-data-elasticsearch-tutorial)
 - [spring.doc](https://docs.spring.io/spring-data/elasticsearch/docs/current/reference/html/#reference)
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
```kotlin
- User
- BasicProfile (Embedded)
```

#### 4. Reposirory 추가 [코드 생략]
 - <img src="https://user-images.githubusercontent.com/60174144/145787471-16ddf4f6-3047-4ba3-b3bb-9c61d2941ba7.png" width="50%" height="50%">
 - @Query는 org.springframework.data.elasticsearch.annotations의 @Query를 사용

#### 5. ElasticSearch Config 설정
 - 이때 minikube 안에서 elasticsearch를 띄우고 있으므로
 - localhost:9200이 아닌 192.168.64.4(=minikube IP):9200으로 설정
 - localhost:9200으로 실행 시 => 'connection refused'
```kotlin
@Configuration
@EnableElasticsearchRepositories(basePackageClasses = arrayOf(UserSearchRepository::class))
class ElasticConfig : AbstractElasticsearchConfiguration() {
    override fun elasticsearchClient(): RestHighLevelClient {
        val clientConfiguration = ClientConfiguration.builder()
            .connectedTo("192.168.64.4:9200")
            .build()
        return RestClients.create(clientConfiguration).rest()
    }
}
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

#### 3.  No converter found capable of converting from type [java.lang.Long] to type [java.sql.Timestamp]
- [참조](https://stackoverflow.com/questions/62581249/spring-data-elasticsearch-no-converter-found-capable-of-converting-from-type)
```kotlin
갑 불러올때 아래 elasticsearchOperations.search() 부분에서 발생
class CustomUserSearchRepositoryImpl(
    private val elasticsearchOperations: ElasticsearchOperations
) : CustomUserSearchRepository {
    override fun searchByName(name: String, pageable: Pageable): List<User> {
        val criteria: Criteria = Criteria.where("basicProfile.name").contains(name)
        val query: Query = CriteriaQuery(criteria).setPageable(pageable)
        val search = elasticsearchOperations.search(query, User::class.java)
        return search.searchHits.map { it.content }
    }
}

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







