# JRE가 이미 설치돼 있는 이클립스 테무린 배포판 우분투 베이스 이미지
FROM eclipse-temurin:17 AS builder
# 현재 작업 폴더를 workspace로 변경
WORKDIR workspace
# 프로젝트에서 애플리케이션 JAR 파일의 위치를 지정하는 빌드 인수
ARG JAR_FILE=build/libs/*.jar
# 애플리케이션 JAR 파일을 로컬 머신에서 이미지 안으로 복사한다.
COPY ${JAR_FILE} catalog-service.jar
# 계층 JAR 모드를 적용해 아카이브에서 계층을 추출한다.
RUN java -Djarmode=layertools -jar catalog-service.jar extract

FROM eclipse-temurin:17

# 'spring 이름의 유저를 만들고 현재 유저로 설정 (루트 권한을 갖지 않는 새로운 사용자로 애플리케이션을 실행 -> 보안 측면 강화)
RUN useradd spring
USER spring
WORKDIR workspace
# 첫 번째 단계에서 추출한 JAR 계층을 두번째 단계로 복사
COPY --from=builder workspace/dependencies/ ./
COPY --from=builder workspace/spring-boot-loader/ ./
COPY --from=builder workspace/snapshot-dependencies/ ./
COPY --from=builder workspace/application/ ./
# 애플리케이션을 실행하기 위한 컨테이너 진입점을 지정한다.
# 스프링 부트 런처를 사용해 우버 JAR 대신 계츠으로 애플리케이션을 시작한다. (레이어 캐시 사용)
ENTRYPOINT ["java", "org.pringframework.boot.loader.JarLauncher"]