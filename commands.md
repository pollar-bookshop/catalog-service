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