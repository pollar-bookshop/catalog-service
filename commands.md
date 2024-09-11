# 1부 클라우드 네이티브 개요
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

# 2부 클라우드 네이티브 개발
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
#### ch7.2.3 스프링 부트 애플리케이션을 위한 배포 객체 생성
  * catalog-service/k8s/deployment.yml 생성 후 코드 작성
  * 카탈로그 서비스를 위한 이미지 하나 더 만들기
    * ./gradlew bootBuildImage
  * 수동으로 로컬 클러스터에 이미지 불러오기 (기본적으로 미니큐브는 로컬 컨테이너 이미지에 엑세스할 수 없음)
    * minikube image load catalog-service --profile polar
  * 매니페스트에서 배포 객체를 생성
    * kubectl apply -f k8s/deployment.yml
  * 어떤 객체가 만들어졌는지 확인
    * kubectl get all -l app=catalog-service
  * catalog-service가 올바르게 시작됐는지 확인하기 위해 배포 로그 확인
    * kubectl logs deployment/catalog-service
  * 오류에 대한 자세한 정보 얻기
    * kubectl describe pod <pod_name>
  * 파드 인스턴스에서 애플리케이션 로그 보기
    * kubectl logs <pod_name>

### ch7.3 서비스 검색 및 부하 분산
#### ch7.3.4 쿠버네티스 서비스를 통한 스프링 부트 애플리케이션 노출
  * catalog-service/k8s.service.yml 파일 추가
  * 매니페스트를 사용한 서비스 객체 생성 (루트 객체에서 아래 코드 실행)
    * kubectl apply -f k8s/service.yml
  * 결과 확인
    * kubectl get svc -l app=catalog-service
  * 로컬 컴퓨터에 포트 노출(포트 포워딩)
    * kubectl port-forward service/catalog-service 9001:80
  * 결과 확인
    * localhost:9001/books
### ch7.4 확장성과 일회성
#### ch7.4.2 일회성을 위한 조건: 우아한 종료
  * src/main/recources/application.yml 파일에 코드 추가
  * 애플리케이션 소스 코드를 수정했기 때문에 새로운 컨테이너 이미지를 만들어 미니큐브에 로드
    * ./gradlew bootBuildImage
    * minikube image load catalog-service --profile polar
  * k8s/deployment.yml에 설정 추가
  * 수정한 배포 객체를 적용 (기존 파드를 제거하고 우아한 종료가 설정된 새로운 파드를 생성)
    * kubectl apply -f k8s/deployment.yml
#### ch7.4.3 스프링 부트 애플리케이션 확장
  * k8s/deployment.yml 파일 수정 (복제본 2개로 설정)
  * 수정 내용 적용
    * kubectl apply -f k8s/deployment.yml
  * 결과 확인 (복제본 2개)
    * kubectl get pods -l app=catalog-service
  * 복제본 두 개중 하나 삭제 시도
    * kubectl delete pod <pod-name>
  * 결과 확인 (복제본 2개)
    * kubectl get pods -l app=catalog-service
  * k8s/deployment.yml 파일 수정 (다시 복제본 1개로 설정)
  * 클러스터 정리 (catalog-service에서 실행)
    * kubectl delete -f k8s
  * PostgreSQL 삭제 (polar-deployment/kubernetes/platform/development에서 실행)
    * kubectl delete -f services
### ch7.5 틸트를 사용한 로컬 쿠버네티스 개발
#### ch7.5.1 틸트를 사용한 내부 개발 루트
  * 틸트 설치
    * brew install tilt-dev/tap/tilt
  * 아래의 단계를 자동화 하는 워크플로를 설계
    * 클라우드 네이티브 빌드팩을 사용해 스프링 부트 애플리케이션을 컨테이너 이미지로 패키징
    * 이미지를 쿠버네티스 클러스터에 업로드
    * YAML 매니페스트에 선언된 모든 쿠버네티스 객체를 적용한다.
    * 로컬 컴퓨터에서 애플리케이션에 엑세스할 수 있도록 포트 전달을 활성화한다.
    * 클러스터에서 실행 중인 애플리케이션의 로그에 쉽게 엑세스한다.
  * 로컬 쿠버네티스에서 PostgreSQL 인스턴스 실행
    * polar-deployment/kubernetes/platform/development 이동 후 아래 코드 실행
      * kubectl apply -f services
  * catalog-service/Tiltfile 생성
  * catalog-service 루트 폴더에서 아래 코드 실행
    * tilt up
  * 실행 확인
    * http://localhost:10350
    * http :9001/books
  * 배포된 애플리케이션 중지
    * tile down
#### ch7.5.2 옥탄트를 사용한 쿠버네티스 워크로드 시각화
  * 옥탄트 설치
    * 아래 페이지에서 octant_0.25.0_macOS-arm64.tar.gz 파일 다운 후 압축 풀기
      * https://github.com/vmware-archive/octant/releases/v0.25.0
    * 옥탄트 파일 실행 가능한 위치로 옮기기
      * sudo mv octant /usr/local/bin
    * octant 명령어로 실행 확인
    * 프로젝트 종료 및 클러스터 종료
      * tilt down
      * polar-deployment/kubernetes/platform/development에서 커맨드 실행
        * kubectl delete -f services
      * minikube stop --profile polar
### ch7.6 배포 파이프라인: 쿠버네티스 매니페스트 유효성 검사
#### ch7.6.1 커밋 단계에서 쿠버네티스 매니페스트 검증
  * 큐비발(kubeval) 설치
    * https://github.com/instrumenta/kubeval/releases 에서 kubeval-darwin-amd64.tar.gz 파일 다운 후 압축 풀기
    * 큐비발 파일 실행 가능한 위치로 옮기기
      * sudo mv kubeval /usr/local/bin
    * catalog-service 루트 폴더에서 아래 명령어 실행
      * kubeval --strict -d k8s
        * k8s 디렉터리 내의 쿠버네티스 매니페스트가 유효한지 검사한다.
#### ch7.6.2 깃허브 액션을 통한 쿠버네티스 매티페스트 유효성 검사 자동화
  * catalog-service/github/workflow/commit-stage.yml 파일 수정
  * main 브랜치에 푸쉬 후 워크플로 성공하는지 확인

# 3부 클라우드 네이티브 분산 시스템
## ch8 리액티브 스프링: 복원력과 확장성
### ch8.2 스프링 웹플럭스와 스프링 데이터 R2DBC를 갖는 리액티브 서버
#### ch8.2.1 스프링 부트를 통한 리액티브 애플리케이션 부트스트래핑
  * order-service/src/main/resources/application.yml 파일 작성
#### ch8.2.2 스프링 데이터 R2DBC를 사용한 리액티브 데이터 지속성
  * polar-deployment/docker/postgresql/init.sql 파일 추가
  * polar-deployment/docker/docker-compose.yml 파일 수정
  * polar-deployment/docker 디렉터리 이동 후 아래 코드 실행
    * docker-compose up -d polar-postgres
#### ch.8.2.3 리액티브 스트림을 이용한 비즈니스 로직 구현
#### ch8.2.4 스프링 웹플럭스로 REST API 노출
  * 주문 요청
    * http POST :9002/orders isbn=1234567890 quantity=3
### ch8.3 스프링 웹 클라이언트를 사용한 리액티브 클라이언트
#### ch8.3.3 웹 클라이언트를 통한 REST 클라이언트 구현
  * catalog-service 실행
  * 주문 요청 보내기
    * http POST :9002/orders isbn=1234567891 quantity=3
### ch8.4 리액티브 스프링을 통한 복원력 높은 애플리케이션
### ch8.5 스프링, 리액터, 테스트컨테이너를 이용한 리액티브 애플리케이션의 테스트
#### ch8.5.3 @WebFluxTest를 이용한 REST 컨트롤러 테스트

## ch9 API 게이트웨이와 서킷 브레이커
### ch9.1 에지 서버와 스프링 클라우드 게이트웨이
#### ch9.1.2 경로와 술어 정의
  * application.yml 파일 작성 후 도커로 서비스, PostgreSQL 실행, 에지 서버는 로컬 JVM으로 실행
    * 각 프로젝트 루트 폴더에서 ./gradlew bootBuildImage 실행해 컨테이너 이미지로 패키징
    * polar-deployment/docker 폴더에서 아래 코드 실행 (order-service에 깃허브에 이미지 등록하기 위한 yml파일 작성돼 있고, 이미지가 push돼 있어야 함)
    * docker-compose up -d catalog-service order-service
      * 위 서비스는 PostgreSQL을 사용하기 때문에 도커 컴포즈는 PostgreSQL 컨테이너도 실행한다.
    * edge-service 루트에서 아래 코드 실행
      * .gradlew bootRun
  * 책과 주문에 대한 요청을 API게이트웨이로 전송
    * http :9000/books
    * http :9000/orders
#### ch9.1.3 필터를 통한 요청 및 응답 처리

### ch9.2 스프링 클라우드 서킷 브레이커와 Resilience5J로 내결함성 개선하기
#### ch9.2.4 서킷 브레이커, 재시도 및 시간 제한의 결합
  * 서킷브레이커 테스트
    * catalog-service, order-service가 실행되지 않은 상태에서 아래 코드 실행
      * ./gradlew bootRun
      * 21개의 POST 요청
        * ab -n 21 -c 1 -m POST http://localhost:9000/orders

### ch9.3 스프링 클라우드 게이트웨이와 레디스를 통한 요청 사용률 제한
#### ch9.3.3 요청 사용률 제한 설정
  * 필요한 설정, 클래스 생성 완료 후 아래 커맨드 실행 후 응답 확인
    * http :9000/books
      * X-RateLimit-Burst-Capacity: 20
      * X-RateLimit-Remaining: 19
      * X-RateLimit-Replenish-Rate: 10
      * X-RateLimit-Requested-Tokens: 1

### ch9.4 레디스를 통한 분산 세션 관리
#### ch9.4.1 스프링 세션 데이터 레디스를 통한 세션 처리

### ch9.5 쿠버네티스 인그레스를 통한 외부 엑세스 관리
#### ch9.5.1 인그레스 API와 인그레스 컨트롤러 이해
  * 인그레스 NGINX를 쿠버네티스에 적용
    * 미니큐브로 polar 클러스터 실행
      * minikube start --cpus 2 --memory 4g --driver docker --profile polar
    * ingress 애드온 활성화 -> 인그레스 NGINX가 로컬 클러스터에 배포됨
      * minikube addons enable ingress --profile polar
    * 배포된 ingress-nginx 구성 요소 확인
      * kubectl get all -n ingress-nginx
        * -n ingress-nginx: ingress-nginx라는 네임스페이스 내에 존재하는 모든 객체를 가져옴
    * polar-bookshop 애플리케이션이 사용하는 지원 서비스 배포
      * polar-deployment/kubernetes/platform/development에 파일 작성
      * 해당 디렉터리에서 아래 명령어 실행
        * kubectl apply -f services
      * 결과 확인
        * kubectl get deployment
      * 에지 서비스를 컨테이너 이미지로 패키징 및 쿠버네티스 클러스터에 아티펙트를 로드
        * edge-service 루트 디렉터리에서 아래 코드 실행
          * ./gradlew bootBuildImage
          * minikube image load edge-service --profile polar
#### ch9.5.2 인그레스 객체 사용
  * 미니큐브 클러스터에 할당된 ip주소 검색
    * minikube ip --profile polar
  * 맥에서는 애드온이 미니큐브 클러스터의 IP 주소를 사용하는 것을 지원하지 않음.
    * 클러스터를 로컬 환경에 노출한 후에 127.0.0.1 주소를 통해 클러스터 호출
    * sudo minikube tunnel --profile polar
  * 인그레스 객체 정의 (edge-service/k8s/ingress.yml 파일 작성)
  * edge-service에서 아래 코드 실행
    * kubectl apply -f k8s
  * 인그레스 객체가 올바르게 생성됐는지 확인
    * kubectl get ingress
  * 로컬 쿠버네티스 클러스터 중지 및 삭제
    * minikube stop --profile polar
    * minikube delete --profile polar

## ch10 이벤트 중심 애플리케이션과 함수
### ch10.1 이벤트 중심 아키텍처
### ch10.2 메시지 브로커와 래빗MQ
#### ch10.2.2 발행/구독 통신을 위한 래빗MQ 사용
  * rabbitmq 컨테이너 정의
    * polar-deployment/docker/docker-compose.yml에 코드 추가
    * polar-deployment/docker/rabbitmq/rabbitmq.conf 파일 추가
    * polar-deployment/docker에서 아래 코드 실행
      * docker-compose up -d polar-rabbitmq
    * 래빗MQ 관리 콘솔 확인
      * http://localhost:15672
    * 컨테이너 종료
      * docker-compose down

### ch10.3 스프링 클라우드 함수를 통한 함수
#### ch10.3.2 함수의 합성 및 통합: REST, 서버리스, 데이터 스트림
  * 자바는 andThen()이나 compose()를 사용해 Function 객체를 순서대로 합성하는 기능을 제공한다.
  * 문제는 첫 번째 함수의 출력 유형이 두 번째 함수의 입력 유형과 같을 때만 사용할 수 있다는 것이다.
  * 스프링 클라우드 함수는 이러한 문제에 대한 해결책을 제공한다.
  * application.yml파일에 코드 작성
#### ch10.3.3 @FunctionalSpringBootTest를 통한 통합 테스트
### 10.4 스프링 클라우드 스트림을 통한 메시지 처리


