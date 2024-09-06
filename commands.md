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
    postgres:14.12

## ch6 스프링 부트 컨테이너화
### ch6.1 도커 컨테이너 이미지로 작업하기
  * my-java-image 디렉토리 생성 후 Dockerfile 작성
  * my-java-image 디렉토리에서 명령어 실행
    * docker build -t my-java-image:1.0.0 .
    * docker run --rm my-java-image:1.0.0
      * 컨테이너 제거

#### ch6.1.3 깃허브 컨테이너 저장소로 이미지 저장
  * 깃허브에서 토큰 발급
  * 터미널에서 깃허브 컨테이너 저장소로 인증을 실행
    * docker login ghrc.io
  * 깃허브 컨테이너 저장소에 저장하기 전에 이미지 이름을 완전하게 지정해야 함.
    * docker tag my-java-image:1.0.0 \
        ghcr.io/stk346/my-java-image:1.0.0
  * 깃허브 컨테이너 저장소로 이미지를 푸쉬
    * docker push ghcr.io/stk346/my-java-image:1.0.0
  * 깃허브 계정에서 profile 페이지로 이동 후 Package 섹션으로 가서 my-java-image 확인

### ch6.2 스프링 부트 애플리케이션을 컨테이너 이미지로 패키지
#### ch6.2.1 스프링 부트의 컨테이너화를 위한 준비
  * catalog-service와 postgreSQL이 IP주소나 호스트 이름 대신 컨테이너 이름을 사용해 연결할 수 있도록 네크워크 생성
    * docker network create catalog-network
  * 네트워크 생성 확인
    * docker network ls
  * postgres 컨테이너가 catalog-network를 사용하도록 설정
    * docker run -d \
      --name polar-postgres \
      --net catalog-network \
      -e POSTGRES_USER=user \
      -e POSTGRES_PASSWORD=password \
      -e POSTGRES_DB=polardb_catalog \
      -p 5432:5432 \
      postgres:14.12
#### ch6.2.2 도커파일로 스프링 부트 컨테이너화
  * catalog-service 루트 디렉토리에서 Dockerfile 생성
  * catalog-service 애플리케이션을 JAR 아티펙트로 빌드
    * ./gradlew clean bootJar
  * JAR 파일을 컨테이너 이미지로 만듦 (현재 디렉토리의 Dockerfile로 이미지 빌드)
    * docker build -t catalog-service .
  * application.yml의 설정 일부를 환경변수로 덮어씀
    * docker run -d \
      --name catalog-service \
      --net catalog-network \
      -p 9001:9001 \
      -e SPRING_DATASOURCE_URL=jdbc:postgresql://polar-postgres:5432/polardb_catalog \
      -e SPRING_PROFILES_ACTIVE=testdata \
      catalog-service
  * 제대로 작동하는지 확인
    * http :9001/books
  * (참고) 종료된 컨테이너 모두 삭제
    * docker container prune
  * (참고) postgres container 실행
  * 컨테이너 삭제
    * docker rm -f catalog-service polar-postgres

#### ch6.2.3 프로덕션을 위한 컨테이너 이미지 빌드
  * 새로운 Dockerfile로 새 컨테이너를 만듦
  * docker build -t catalog-service .
#### ch6.2.4 클라우드 네이티브 빌드팩을 이용한 스프링 부트 컨테이너화
  * 스프링 부트 플러그인에서 제공하는 빌드팩을 이용해 컨테이너 빌드
    * ./gradlew bootBuildImage
  * catalog-service를 빌드팩이 만든 이미지를 사용해 컨테이너 실행
    * docker run -d \
      --platform linux/amd64 \
      --name catalog-service \
      --net catalog-network \
      -p 9001:9001 \
      -e SPRING_DATASOURCE_URL=jdbc:postgresql://polar-postgres:5432/polardb_catalog \
      -e SPRING_PROFILES_ACTIVE=testdata \
      catalog-service
  * 컨테이너 및 네트워크 제거
    * docker rm -f catalog-service polar-postgres
    * docker network rm catalog-network
  * 스프링 부트 플러그인에서 제공하는 빌드팩을 이용해 이미지를 만들고 저장소에 저장
    * ./gradlew bootBuildImage \
      --imageName ghcr.io/<My_github_username>/catalog-service \
      --publishImage \
      -PregistryUrl=ghcr.io \
      -PregistryUsername=<My_github_username or organization_name> \
      -PregistryToken=<My_github_classic_token>
  * 깃허브 이미지 저장소에 catalog-service 이미지 저장된 것 확인 후 삭제

### ch6.3 도커 컴포즈를 통한 스프링 부트 컨테이너의 관리
#### ch6.3.1 도커 컴포즈를 통한 컨데이터 라이프사이클 관리
  * polar-deployment/docker/docker-compose.yml 파일 생성 후 코드 작성 후 실행
    * docker-compose up -d
#### ch6.3.2 스프링 부트 컨테이너 디버깅
  * 도커 컴포즈 파일 중 catalog-service 항목에 디버깅을 위한 설정 추가 후 해당 디렉토리에서 다시 실행
    * docker-compose up -d
  * 종료
    * docker-compose down
### ch6.4 배포 파이프라인: 패키지 및 등록
#### ch6.4.2 깃허브 액션을 통한 컨테이너 이미지 등록
  * github/workflows/commit-stage.yml 파일에 코드 입력
#### config-service도 같은 절차 적용하기

## ch7 스프링 부트를 위한 쿠퍼네티스 기초
### ch7.1 도커에서 쿠버네티스로의 이동
#### ch7.1.1 로컬 쿠버네티스 클러스터
  * 도커 위에 polar라는 이름으로 새로운 쿠버네티스 클러스터 생성, CPU와 메모리에 대한 리소스 제한
    * minikube start --cpus 2 --memory 4g --driver docker --profile polar
  * 현재 클러스터의 모든 노드 목록 얻기
    * kubectl get nodes
  * 상호작용할 수 있는 모든 컨텍스트 나열
    * kubectl config get-contexts
  * 현재 컨텍스트 확인
    * kubectl config current-context
  * 컨텍스트 변경
    * _kubectl config use-context polar
  * 클러스터 시작 및 중지, 삭제
    * minikube start --profile polar
    * minikube stop --profile polar
    * minikube delete --profile polar
#### ch7.1.2 로컬 클러스터에서 데이터 서비스 관리
  * 쿠버네티스로 PostgreSQL 배포
    * polar-deployement/kuberetes/platform/development/services/postgresql.yml 파일 작성
    * development 디렉토리에서 아래 명령어 실행
      * kubectl apply -f services
    * PostgreSQL 컨테이너를 실행하는 파드 확인
      * kubectl get pod
    * 배포 취소 (동일한 폴더에서 실행) (나중에 필요할 때 실행)
      * kubectl delete -f services
### ch7.2 스트링 부트를 위한 쿠버네티스 배포
#### ch7.2.1 컨테이너에서 파드로
  * 