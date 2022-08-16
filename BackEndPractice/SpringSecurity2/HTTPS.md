# HTTPS

HTTP+ Secure로  웹사이트 보안을 위해 만들어졌다.

인증서를 생성해서 HTTPS를 안전하게 만들어보자.

강의자료에서 복사해오겠다.

- keystore 만들기
    - keytool -genkey -alias [keystore 별칭] -keyalg RSA -storetype PKCS12 -keystore [keystore 파일]
    
    ```
    iyboklee@DESKTOP-6I1BVIA:/mnt/c/Users/iybok/stores$ ****keytool -genkey -alias prgrms_keystore -keyalg RSA -storetype PKCS12 -keystore prgrms_keystore.p12**
    Enter keystore password:
    Re-enter new password:
    What is your first and last name?
      [Unknown]:  localhost
    What is the name of your organizational unit?
      [Unknown]:  Prgrms
    What is the name of your organization?
      [Unknown]:  Prgrms
    What is the name of your City or Locality?
      [Unknown]:  Seoul
    What is the name of your State or Province?
      [Unknown]:  Seoul
    What is the two-letter country code for this unit?
      [Unknown]:  KR
    Is CN=localhost, OU=Prgrms, O=Prgrms, L=Seoul, ST=Seoul, C=KR correct?
      [no]:  y
    
    iyboklee@DESKTOP-6I1BVIA:/mnt/c/Users/iybok/stores$ ls
    **prgrms_keystore.p12**
    
    ```
    
- keystore 에서 인증서 추출하기
    - keytool -export -alias [keystore 별칭] -keystore [keystore 파일] -rfc -file [인증서 파일]
    
    ```
    iyboklee@DESKTOP-6I1BVIA:/mnt/c/Users/iybok/stores$ ****keytool -export -alias prgrms_keystore -keystore prgrms_keystore.p12 -rfc -file prgrms.cer**
    Enter keystore password:
    Certificate stored in file <prgrms.cer>
    iyboklee@DESKTOP-6I1BVIA:/mnt/c/Users/iybok/stores$ ls
    **prgrms.cer  prgrms_keystore.p12**
    
    ```
    
- trust-store 만들기
    - keytool -import -alias [trust keystore 별칭] -file [인증서 파일] -keystore [trust keystore 파일]
    
    ```java
    iyboklee@DESKTOP-6I1BVIA:/mnt/c/Users/iybok/stores$ **keytool -import -alias prgrms_truststore -file prgrms.cer -keystore prgrms_truststore.p12
    Enter keystore password:**
    Re-enter new password:
    Owner: CN=localhost, OU=Prgrms, O=Prgrms, L=Seoul, ST=Seoul, C=KR
    Issuer: CN=localhost, OU=Prgrms, O=Prgrms, L=Seoul, ST=Seoul, C=KR
    Serial number: 16cd5188
    Valid from: Thu Aug 19 19:37:07 KST 2021 until: Wed Nov 17 19:37:07 KST 2021
    Certificate fingerprints:
             MD5:  26:91:CD:3D:BC:B9:2E:C7:6B:23:2C:B0:3C:DF:E2:BB
             SHA1: 3E:85:57:2A:7B:51:2B:20:5A:F8:FB:92:41:87:6C:41:A4:1E:01:A5
             SHA256: 63:AD:A4:85:49:08:B7:01:75:36:34:A6:02:B6:2A:9B:1F:16:C0:5D:63:CE:F2:66:68:71:65:6E:31:1E:4B:D6
    Signature algorithm name: SHA256withRSA
    Subject Public Key Algorithm: 2048-bit RSA key
    Version: 3
    
    Extensions:
    
    #1: ObjectId: 2.5.29.14 Criticality=false
    SubjectKeyIdentifier [
    KeyIdentifier [
    0000: DB 81 03 CF 01 A9 25 34   70 46 F4 FF EF 8D BA 3D  ......%4pF.....=
    0010: 24 C7 3B 6C                                        $.;l
    ]
    ]
    
    Trust this certificate? [no]:  y
    Certificate was added to keystore
    iyboklee@DESKTOP-6I1BVIA:/mnt/c/Users/iybok/stores$ ls
    prgrms.cer  prgrms_keystore.p12  prgrms_truststore.p12
    ```
    
    위처럼 만들어주면 key가 생기고, keystore와 truststore를 복사해서 인텔리제이 resource에 붙여넣으면 완성된다.
    
    그리고 yml파일에
    
    ```yaml
    server:
      port: 443
      ssl:
        enabled: true
        key-alias: prgrms_keystore
        key-store: classpath:prgrms_keystore.p12
        key-store-password: prgrms123
        key-password: prgrms123
        trust-store: classpath:prgrms_truststore.p12
        trust-store-password: prgrms123
    ```
    
    위처럼 keystore에 대한 정보를 입력해주고, https는 Port를 443번을 쓰기 때문에 입력을 위처럼해준다.
    
    이렇게되면 https로 웹사이트가 실행된다.