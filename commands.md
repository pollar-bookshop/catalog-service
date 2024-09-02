* 도커 이미지 빌드하기
  * ./gradlew bootBuildImage
    * rapository: catalog-service
    * tag: 0.0.1-SNAPSHOT 이미지 생성
* 이미지 실행
  * docker run --rm --name catalog-service -p 8080:8080 catalog-service:0.0.1-SNAPSHOT