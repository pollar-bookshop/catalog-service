## ch2
  * 도커 이미지 빌드하기
    * ./gradlew bootBuildImage
      * rapository: catalog-service
      * tag: 0.0.1-SNAPSHOT 이미지 생성
  * 이미지 실행
    * docker run --rm --name catalog-service -p 8080:8080 catalog-service:0.0.1-SNAPSHOT
  * 이미지로 카탈로그 서비스를 배포하도록 쿠버네티스에 명령
    * minikube image load catalog-service:0.0.1-SNAPSHOT
  * 배포 리소스 생성
    * kubectl create deployment catalog-service --image=catalog-service:0.0.1-SNAPSHOT
  * 배포가 잘 생성됐는지 확인
    * kubectl get deployment
  * 서비스 리소스를 통해 카탈로그 서비스를 클러스터에 노출 (기본 설정이 파드로는 실행중인 애플리케이션에 접근할 수 없기 때문)
    * kubectl expose deployment catalog-service --name=catalog-service --port=8080
  * 서비스가 잘 생성됐는지 확인
    * kubectl get service catalog-service
  * 포트포워딩 (로컬호스트 포트:서비 포트)
    * kubectl port-forward service/catalog-service 8000:8080  
  
  * 서비스 삭제
    * kubectl delete service catalog-service
  * 배포 객체 삭제
    * kubectl delete deployment catalog-service
  * 클러스터 중지
    * minikube stop

## ch3
  * ./gradlew bootJar
    * build/libs/catalog-service-0.0.1-SNAPSHOT.jar 파일 생성
  * java -jar build/libs/catalog-service-0.0.1-SNAPSHOT.jar  
  
  * 북 생성 요청
    * http POST :9001/books author="Lyra Silverstar" title="Northern Lights" isbn="1234567891" price=9.90
  * 잘못된 북 생성 요청
    * http POST :9001/books author="John Snow" title="" isbn="123ABC456Z" price=9.90

### ch3.4 스프링 RESTful 애플리케이션 테스트
#### ch3.4.1 JUnit5를 이용한 단위테스트
  * BookValidationTests 테스트
    * ./gradlew test --tests BookValidationTests

#### ch3.4.2 SpringBootTest를 통한 통합테스트
  * 테스트 진행
    * ./gradlew test --tests CatalogServiceApplicationTests

#### ch3.4.3 @WebMvcTest를 사용한 REST 컨트롤러의 테스트
  * ./gradlew test --tests BookControllerMvcTests

#### ch3.4.4 @JsonTest를 사용한 JSON 직렬화 테스트
  * ./gradlew test --tests BookJsonTests

### ch3.5 배포 파이프라인: 빌드 및 테스트
#### ch3.5.2 깃허브 액션을 이용한 커밋 단계 구현
  * 깃허브 액션은 직접 소프트웨어 워크플로우를 자동화할 수 있다. 워크플로는 깃허브 저장소 루트의 .github/workflow 폴더에 YAML 형식으로 기술돼야 한다.

### ch4. 외부화 설정 관리
#### ch4.3.1  스크링 클라우드 컨피그 서버로 중앙식 설정 관리하기
  * 깃 저장소에 환경설정용 리파지터리 만들기
    * 아래 파일 생성 후 환경설정
      * catalog-service.yml
      * catalog-service-prod.yml
#### ch4.3.2~3 환경 설정 서버 구성
  * 환경설정용 서버 구성 후 양식에 맞게 yml파일 작성 (포트=8888)
  * 아래 명령어로 api 요청 후 데이터 확인
    * http :8888/catalog-service/default
    * http :8888/catalog-service/prod

#### ch4.4.1 설정 클라이언트 구축
  * catalog-service 에서 yml파일에 필요한 데이터 입력
  * config서버 실행
  * catalog 루트에서 jar파일 생성 후 실행 및 api 요청
    * ./gradlew bootJar
    * java -jar build/libs/catalog-service-0.0.1-SNAPSHOT.jar
    * http :9001/
      * Welcome to the catalog from config server
    * java -jar build/libs/catalog-service-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
    * http :9001/
      * Welcome to the production catalog from the config sercer
#### ch4.4.3 런타임 시 설정 새로고침
  * catalog-service에서 actuator 관련 설정
  * config-repo에서 yml 설정 변경 후 리프레쉬 요청 및 새로운 설정 데이터 확인
    * http POST :9001/actuator/refresh
    * http :9001/
      * Welcome to the catalog from fresh config server

## ch5 클라우드에서 데이터 저장과 관리
### ch5.1 클라우드 네이티브 시스템을 위한 데이터베이스
#### ch5.1.2 PostgreSQL을 컨테이너로 실행
  * docker run -d \
    -e POSTGRES_USER=user \
    -e POSTGRES_PASSWORD=password \
    -e POSTGRES_DB=polardb_catalog \
    -p 5432:5432 \
    postgres:14.4

#### ch5.4.1 PostgreSQL을 위한 테스트컨테이너 설정
  * 
