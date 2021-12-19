# Spring Data Elastic Search Example

---
### V1과 V2 차이점
#### 1. Tokenizer
- V1에서는 Standard Tokenizer를 사용하므로 한글 같은 텍스트는 제대로 토큰화 되지 않는다. 따라서 V2에서는 어간 추출이 가능한 nori tokenizer를 활용
```
 - Standard Tokenizer
  - Ex> "동해물과 백두산이" -> "동해물과", "백두산이"
 - Nori Tokenizer
  - Ex> "동해물과 백두산이" -> "동해", "물", "과", "백두", "산", "이"
```
#### 2. Index
- V1에서는 Entity 1개에 대해서만 Index를 구성.
- V2에서는 OneToMany, OneToOne등 검색할 Entity의 연관된 Entity가 많아 검색조건에 해당되는 필드들을 가진 Index 전용 class를 만들어 활용.

---
## [Version1](https://github.com/hwangyoungjin/Springboot-ElasticSearch/tree/main/v1#readme)
---
## [Version2](https://github.com/hwangyoungjin/Springboot-ElasticSearch/tree/main/v2#readme)