# springboot-aws-webservice
스프링 부트와 aws로 혼자 구현하는 웹 서비스 스터디

--------------------

**@Entity**

- 테이블과 링크될 클래스임을 나타낸다.
- 기본값으로 클래스의 카멜케이스 이름을 언더스코어 네이밍(_)으로 테이블 이름을 매칭한다.
- ex) SalesManager.java -> sales_manager table

**@Id**

- 해당 테이블의 PK 필드를 의미

**@GeneratedValue**

- PK의 생성 규칙을 나타낸다.
- 스프링 부트 2.0에서는 GenerationType.IDENTITY 옵션을 추가해야만 auto_incremenet가 된다

**@Column**

- 테이블의 칼럼을 나타내며 굳이 선언하지 않더라도 해당 클래스의 필드는 모두 칼럼이 된다.
- 사용하는 이유 → 기본값 외에 추가로 변경이 필요한 옵션이 있으면 사용
- 문자열의 경우 VARCHAR(255)가 기본값이지만 사이즈를 500으로 늘리고 싶은 경우 등

```
자바빈 규악을 생각하면서 getter/setter를 무작정 생성하는 경우,
해당 클래스의 인스턴스 값들이 언제 어디서 변해야 하는지 코드상으로 명확하게 구분할 수가 없어, 차후 기능 변경 시 매우 복잡해진다.

따라서 Entity 클래스에서는 절대 Setter 메소드를 만들지 않고,
그 목적과 의도를 나타내는 변경 메소드를 추가하여 사용한다.
```

```JAVA
public class Order{
  public void cancelOrder() {
    this.status = false;
  }
  
  public void 주문서비스의_취소이벤트() {
    order.cancelOrder();
  }
}

```

-------------------

# AWS

   클라우드 컴퓨팅이란 IT 리소스를 인터넷을 통해 온디맨드로 제공하고 사용한 만큼만 비용을 지불하는 것  을 말한다. 
   물리적 데이터 센터와 서버를 구입, 소유 및 유지 관리 대신 -> Amazon Web Services(AWS)와   같은 클라우드 공급자로부터 필요에 따라 컴퓨팅 파워, 스토리지, 데이터베이스와 같은 기술 서비스에 액세스할 수 있다. 

**회원가입** 

url: https://aws.amazon.com/ko/

- 가입 후, 기본 플랜 선택 

**EC2 인스턴스 생성** 

- EC2는 AWS에서 제공하는 성능, 용량 등을 유동적으로 사용할 수 있는 서버
- 무료 프리티어 플랜에서의 사양: vCPU(가상 cpu) 1 Core, 메모리 1GB // 월 750 시간 제한 
- 리전: 서울 선택 

[고정 IP 할당 및 ssh key 복사 ]
- AWS의 고정 IP를 Elastic IP(EIP, 탄력적 IP)라고 한다. 
- ssh 접속시: "ssh -i pem 키 위치 탄력적 IP 주소" 해당 명령어를 매번 수행해야함
  - pem 키 파일을 자동으로 읽어 오도록 ~/.ssh/ 위치에 해당 키 파일을 복사 (cp springboot-webservice.pem ~/.ssh) 
  - 탄력적 IP를 생성하고 할당하지 않은 경우 비용이 발생하기 때문에 꼭 할당하거나, 사용하지 않는다면 삭제 처리 

  - config 생성 
  ```shell
  # springboot-webservice
    Host springboot-webservice
    HostName 3.00.000.00 (본인 생성 ip)
    User ec2-user
    IdentityFile ~/.ssh/springboot-webservice.pem
  ```
  
[아마존 리눅스 서버 생성시 꼭 해야 할 설정들]
1. JAVA8 설치 
```shell
# 자바 1.8 설치 
sudo yum install -y java-1.8.0-openjdk-devel.x86_64
# 자바 버전 선택 
sudo /usr/sbin/alternatives --config java 
```
2. 타임존 변경: 미국 시간대 -> 한국 시간대 
```shell
sudo rm /etc/localtime
sudo ln -s /usr/share/zoneinfo/Asia/Seoul /etc/localtime
```
3. 호스트네임 변경: 현재 접속한 서버의 별명을 등록
```shell
sudo vim /etc/sysconfig/network
HOSTNAME=서비스명 기입 

sudo vim /etc/hosts
127.0.0.1 서비스명
```
**AWS 데이터이스 환경 셋팅**
- MariaDB (추후 AWS에서 운영하는 Aurora 교체 용이)
- 프리티어 템플릿 

[파라미터 그룹 생성]
- time_zone		Asia/Seoul	
- character_set_client		utf8mb4	
- character_set_connection		utf8mb4	
- character_set_database		utf8mb4	
- character_set_filesystem		utf8mb4	
- character_set_results		utf8mb4	
- character_set_server		utf8mb4	
- collation_connection		utf8mb4_general_ci	
- collation_server		utf8mb4_general_ci	
- max_connections 150

[데이터베이스 파라미터 값 직접 변경] 
```sql
ALTER DATABASE freelec
CHARACTER SET = 'utf8mb4'
COLLATE = 'utf8mb4_general_ci';
```

**EC2 서버에 프로젝트 배포**

```shell
#git 설치 
sudo yum install git

#git version 확인 
git --version 

#디렉토리 생성 
mkdir ~/app && mkdir ~/app/step1

#생성된 디렉토리로 이동 
cd ~/app/step1 

#project 복사 
git clone 깃 리파지토리 주소 

#test 
./gradlew test 
```

[배포 스크립트 만들기] 
- git clone혹은 git pull을 통해서 새 버전의 프로젝트 받음
- Gradle이나 Maven을 통해 프로젝트 테스트와 빌드
- EC2 서버에서 해당 프로젝트 실행 및 재실행 


```shell
#!/bin/bash

REPOSITORY=/home/ec2-user/app/step1
PROJECT_NAME=springboot-aws-webservice

cd $REPOSITORY/$PROJECT_NAME

echo ">Git Pullu"

git pull

echo "> 프로젝트 build 시작"

./gradlew build

echo "> step1 디렉토리 이동"

cd $REPOSITORY

echo "> Build 파일 복사"

cp $REPOSITORY/$PROJECT_NAME/build/libs/*.jar $REPOSITORY/

echo "> 현재 구동중인 애플리케이션 pid 확인"

CURRENT_PID=$(pgrep -f ${PROJECT_NAME}.*.jar)

echo "현재 구동중인 애플리케이션 pid: $CURRENT_PID"

if [ -z "$CURRENT_PID" ]; then
        echo "> 현재 구동 중인 애플리케이션이 없으므로 종료하지 않습니다."
else
        echo "> kill -15 $CURRENT_PID"
        kill -15 $CURRENT_PID
        sleep 5
fi

echo "> 새 애플리케이션 배포"

JAR_NAME=$(ls -tr $REPOSITORY/ | grep jar | tail -n 1)

echo "> JAR Name: $JAR_NAME"

sudo nohup java -jar \
        -Dspring.config.location=classpath:/application.properties,/home/ec2-user/app/application-oauth.properties,/home/ec2-user/app/application-real-db.properties,/classpath:/application-real.properties \
  -Dspring.profiles.active=real \
  $REPOSITORY/$JAR_NAME 2>&1 &

```

[EC2에서 소셜 로그인 연동]

1. EC2 보안 그룹 설정- 인바운드 규칙: 설정 사용 PORT 허용
2. EC2 퍼블릭 도메인 연동 (퍼블릭 DNS: 임시 자동으로 할당된 도메인, 인터넷이 되는 장소 어디나 이 주소를 입력하면 EC2 서버에 접근 가능)
3. 구글 클라우드 플랫폼 접속 (https://console.cloud.google.com/)
```
API 및 서비스 -> 사용자 인증 정보 -> OAuth 동의 화면 접속 
승인된 도메인란에 퍼블릭 DNS 주소 입력

사용자 인증 정보 
리디렉션 URL란에 퍼블릭 DNS 주소 + /login/oauth2/code/google 입력 
```
4. 네이버 개발자 센터 접속 (https://developers.naver.com/apps/#/myapps/)

```
내 애플리케이션 -> API설정 
서비스 URL
Callback URL: DNS 주소로 변경 
```

# Travis CI 배포 자동화 

[CI] 

Continuous Integration - 지속적 통합, 코드 버전 관리를 하는 VCS 시스템 (Git, SVC 등)에 PUSH가 되면 자동으로 테스트와 빌드가 수행되어 안정적인 배포 파일을 만드는 과정
CI 4가지 규칙 (마틴 파울러: http://bit.ly/2Yv0vFp)
- 모든 소스 코드가 살아 있고 (현재 실행 되고) 누구든 현재의 소스에 접근할 수 있는 단일 지점을 유지할 것
- 빌드 프로세스를 자동화해서 누구든 소스로부터 시스템을 빌드하는 단일 명령어를 사용할 수 있게 할 것
- 테스팅을 자동화해서 단일 명령어로 언제든지 시스템에 대한 건전한 테스트 수트를 실행할 수 있게 할 것 
- 누구나 현재 실행 파일을 얻으려면 지금까지 완전한 실행 파일을 얻었다는 확신을 하게 할 것 
 
[CD]

Continuous Deployment - 지속적인 배포, 빌드 결과를 자동으로 운영 서버에 무중단 배포까지 진행되는 과정

[Travis CI 연동하기] 

https://travis-ci.com/ 깃허브 계정 로그인 -> 계정명 Settings 클릭 -> CI 연동 활성화 할 프로젝트 선택 
Travis CI의 상세한 설정은 프로젝트에 존재하는 .travis.yml 파일로 관리

build.gradle과 같은 경로에 .travis.yml 파일 생성 

```yml
language: java
jdk:
  - openjdk8

branches:
  only:
    - master


# Travis CI 서버의 Home
cache:
  directories:
    - '$HOME/.m2/repository'
    - '$HOME/.gradle'

script: "./gradlew clean build"

# CI 실행 완료 시 메일로 알람
notifications: 
  email:
    recipients: 
      - 본인 메일 주소 
```

#Travis CI와 AWS S3 연동 
S3란 AWS에서 제공하는 일종의 파일 서버 

[AWS Key 발급]
IAM: AWS에서 제공하는 서비스의 접근 방식과 권한을 관리 (Travis가 AWS에 접근 가능하도록 설정 필요)  
  - 사용자 추가 (프로그래밍 방식 액세스, 기존 정책 직접 연결로 설정) 
  - s3full, codeDeployFull 정책 체크

생성된 키를 Travis 설정에 Environment Variable 항목에 추가 
  - AWS_ACCESS_KEY
  - AWS_SECRET_KEY

S3 버킷 생성 (모든 퍼블릭 액세스 차단) 

.travis.yml 코드 추가 

```yml

#Travis VI에서 빌드하여 만든 Jar파일 -> S3 업로드
before_deploy:
  - zip -r springboot-webservice
  - mkdir -p deploy
  - mv springboot-webservice.zip delpoy/springboot-webservice.zip

deploy:
  - provider: s3
    access_key_id: $AWS_ACCESS_KEY # Travis repo settings에 설정된 값
    secret_access_key: $AWS_SECRET_KEY # Travis repo settings에 설정된 값
    bucket: freelec-springboot-tm-build # S3 버킷
    region: ap-northeast-2
    skip_cleanup: true
    acl: private # zip 파일 접근을 private으로
    local_dir: deploy # before_deploy에서 생성한 디렉토리
    wait-until-deployed: true

```

IAM 역할 생성 (AWS 서비스 - EC2) 

인스턴스 -> 보안 -> IAM 역할 수정 -> 인스턴스 재부팅 

# Travis CI, S3, CodeDeploy 연동 

1. CodeDeploy 에이전트 설치 

``` shell
aws s3 cp s3://aws-codedeploy-ap-northeast-2/latest/install . --region ap-northeast-2

#실행 권한 부여
chmod +x ./install

#설치
sudo ./install auto

#상태 검사 
sudo service codedeploy-agent status

#아래와 같이 나온다면 정상
The AWS CodeDeploy agent is running as PID 8782

  루비가 없는 경우 ->  sudo yum install ruby

```

IAM 역할 생성 (AWS 서비스 - CodeDeploy) 
- CodeDeploy: AWS의 배포 삼형제 중 하나 
[배포 삼형제]
- Code Commit: 깃허브와 같은 코드 저장소의 역할을 함
- Code Build: Travis CI와 마찬가지로 빌드용 서비스, 규모가 있는 서비스에서는 대부분 젠킨스/팀시티 등을 이용하니 사용할 일이 거의 없음
- CodeDeploy: aws의 배포 서비스, 다른 대채재가 없음, 오토 스케일링 그룹 배포 외 많은 기능을 지원 
  
``` shell
#S3에 넘겨줄 zip 파일을 저장할 디렉토리 생성 
mkdir ~/app/step2 && mkdir ~/app/step2/zip
``` 


AWS Code Deploy의 설정은 appspec.yml로 진행 

```yml
version: 0.0
os: linux
files:
  - source: /
      destination: /home/ec2-user/app/step2/zip/
      overwrite: yes
```

.travis.yml <- CodeDeploy 내용 추가 
```yml 
  - provider: codedeploy
    access_key_id: $AWS_ACCESS_KEY # Travis repo settings에 설정된 값
    secret_access_key: $AWS_SECRET_KEY # Travis repo settings에 설정된 값
    bucket: freelec-springboot-tm-build # S3 버킷
    key: springboot-webservice.zip
    bundle_type: zip #압축 확장자 
    application: springboot-webservice
    deployment_group: springboot-webservice-group
    region: ap-northeast-2
    wait-until-deployed: true
```
deploy fail 나는 경우 

ec2 -> /var/log/aws/codedeploy-agent 해당 경로로 이동하여 로그 확인 

# 배포 자동화 구성 

[무중단 배포 방식]

1. AWS 블루 그린(Blue-Green)
2. 도커 웹 서비스 
3. 엔진엑스 리버스 프록시 

[엔진엑스 리버스 프록시]

- 리버스 프록시란 외부의 요청을 받아 백엔드 서보로 요청을 전달하는 행위 
- 엔진엑스를 이용하는 이유는 가장 저렴하고 쉽기 때문, 여유가 된다면 aws 블루 그린을 사용하면 됨

[사용방법]

- 기존에 쓰던 EC2에 그대로 적용하면 되므로 별도의 인스턴스가 필요하지 않음
- 하나의 EC2 혹은 리눅스 서버에 엔진엑스 1대와 스프링 부트 Jar 2대를 사용)
- 구조 
  - 엔진엑스는 80(http), 443(https) 포트를 할당
  - 스프링 부트1은 8081 포트로 실행
  - 스프링 부트2는 8082 포트로 실행 
- 운영 과정 
  - 사용자는 서비스 주소로 접속한다(80 혹은 443)
  - 엔진엑스는 사용자의 요청을 받아 현재 연결된 스프링 부트로 요청을 전달, ex) 스프링 부트1 (8081 포트)로 전달 가정
  - 스프링 부트2는 엔진엑스와 연결된 상태가 아니니 요청받지 못함 
  - 1.0 -> 1.1 버전으로 신규 배포가 필요하면, 엔진엑스와 연결되지 않은 스프링부트2(8082 포트)로 배포

- 배포 과정
  - 배포하는 동안에도 서비스는 중단되지 않는다 (엔진엑스는 스프링 부트1을 바라보기 때문)
  - 배포가 끝나고 정상적으로 스프링 부트2가 구동 중인지 확인
  - 스프링 부트2가 정상 구동 중이면 nginx reload 명령어를 통해 8081 대신에 8082를 바라보도록 
  - nginx reload는 0.1초 이내에 완료 됨 

[엔진엑스 설치와 스프링 부트 연동하기]

``` shell
#엔진엑스 설치
sudo yum install nginx

#Amazone Linux2인 경우
sudo amazon-linux-extras install nginx1

#엔진엑스 실행
sudo service nginx start 

```

정상 작동 시 -> Starting nginx: [ OK ] or Redirecting to /bin/systemctl start nginx.service

- AWS 보안 그룹 인바운드: Nginx가 사용하는 80포트 추가 

[엔진엑스와 스프링 부트 연동] 

엔진엑스가 현재 실행 중인 스프링 부트 프로젝트를 바라볼 수 있도록 프록시 설정 

``` shell
sudo vim /etc/nignx/nginx.conf

#server 설정 -> location 영역 추가 
server {
        listen       80;
        listen       [::]:80;
        server_name  _;
        root         /usr/share/nginx/html;

        # Load configuration files for the default server block.
        include /etc/nginx/default.d/*.conf;

        location / {
                proxy_pass http://localhost:8080;
                proxy_set_header X-Real-IP $remote_addr;
                proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
                proxy_set_header Host $http_host;
        }

        error_page 404 /404.html;
        location = /404.html {
        }

        error_page 500 502 503 504 /50x.html;
        location = /50x.html {
        }
    }
```

[무중단 배포 스크립트]

- profile API 추가 

``` java
package com.jojoidu.book.springboot.web;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class ProfileController {
    private final Environment env;

    @GetMapping("/profile")
    public String profile() {
        List<String> profiles = Arrays.asList(env.getActiveProfiles());
        List<String> realProfiles = Arrays.asList("real", "real1", "real2");
    String defaultProfile = profiles.isEmpty()? "default" : profiles.get(0);

    return profiles.stream()
            .filter(realProfiles::contains)
            .findAny()
            .orElse(defaultProfile);
    }
}

```









