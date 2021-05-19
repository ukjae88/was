***
# WAS 구현   

## 사전세팅   
* was.jar 생성을 위한 maven 설치 필요   
* VirtualHost 기능을 위한 hosts 파일 내 아래 호스트 등록 필요   
  - 127.0.0.1       a.com   
  - 127.0.0.1       b.com   
   
## 실행설명   
* 압축파일명 : was.zip   
  1. 압출파일 해제 후, 해당 디렉토리 위치에서 mvn clean package 명령 실행 -> was.jar 파일 생성됨   
  2. was.jar 파일이 생성되면 java –jar was.jar 명령 실행   
  3. WAS 가 실행된다 (Config 설정 Port : 8080)   
  4. 아래 내용에 따라 WAS 동작을 확인한다   
   
## 스펙구현   
  1. 아래 서로 다른 호스트를 입력하는 경우, 서로 다른 페이지 제공 구현완료   
    - 브라우저에 URL 입력 : http://a.com:8080   
    - 브라우저에 URL 입력 : http://b.com:8080   
  2. config.json 파일 관리 구현완료   
    - 위치 : was/src/main/resources/config.json   
    - 내용 : port 설정정보, Host명 별 root_path, 오류 HTML 파일명   
    - 예시 : {"port":8080, "hosts":[{"name":"a.com", "root_path":"src/main/resources/a.com", "error403":"error403.html", "error404":"error404.html", "error500":"error500.html"}, {"name":"b.com", ...]}   
  3. 403, 404, 500 오류 처리 구현완료   
    - 리소스 파일을 못 찾은 경우, 응답 코드 404 반환   
    - 서버 처리 중 예외발생 시, 응답 코드 500 반환   
  4. 아래 규칙에 걸리면 응답 코드 403 을 반환 구현완료   
    - HTTP_ROOT 디렉터리의 상위 디렉터리에 접근할 때   
    - 확장자가 .exe 인 파일을 요청받았을 때   
  5. logback 을 사용하여 로깅 작업 구현완료   
    - 로그 파일을 하루 단위로 분리함 (was/logs 폴더의 logback.log 파일)   
    - 로그 내용에 따라 적절한 로그 레벨을 적용함(INFO, WARN, ERROR)   
    - 오류 발생 시, StackTrace 전체를 로그 파일에 남김   
  6. WAS 로직 구현완료   
    - 서로 다른 패키지에 존재하는 SimpleServlet 인터페이스 구현체 Hello.java 클래스 작성   
    - 요청에 대한 서블릿 매핑은 Map 형태로 관리하는 ServletMapping.java 클래스 작성 (예시. mappingInfo.put("/Greeting", "com.nhn.was.Hello") )   
    - 브라우저에 URL 입력 : http://a.com:8080/Greeting -> com.nhn.was 패키지의 Hello.java 로 매핑   
    - 브라우저에 URL 입력 : http://a.com:8080/super.Greeting -> com.nhn.was.service 패키지의 Hello.java 로 매핑   
  7. 현재 시각을 출력하는 Servlet 구현완료   
    - com.nhn.was.service 패키지의 CurrentTime.java 작성   
    - 브라우저에 URL 입력 : http://a.com:8080/time   
  8. JUnit4 을 이용하여 테스트 케이스 구현완료   
    - 테스트 폴더(src/test/java) com.nhn.was.service 패키지의 HelloTest 테스트 클래스 작성   
    - 테스트 내용 : com.nhn.was.service 패키지의 Hello.java 의 Body 정보가 일치하는지 테스트   
***