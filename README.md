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
  
  

