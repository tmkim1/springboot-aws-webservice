# springboot-aws-webservice
스프링 부트와 aws로 혼자 구현하는 웹 서비스 스터디

--------------------

**@Entity**

- 테이블과 리크될 클래스임을 나타낸다.
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
자바빈 규악을 생각하면서 getter/setter를 무작정 생서하는 경우,
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
- 무료 프리티어 플랜에서의 사양: vCPU(가상 cpu) 1 Core, 메로리 1GB // 월 750 시간 제한 
- 리전: 서울 선택 

[고정 IP 할당 및 ssh key 복사 ]
- AWS의 고정 IP를 Elastic IP(EIP,탄력적 IP)라고 한다. 
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






