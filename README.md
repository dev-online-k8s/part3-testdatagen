# SNS Application Data Generator
Test Data Generator

## 사전준비
image 디렉토리에 테스트용 이미지를 저장합니다.

## 실행
```sh
# SNS_DATA_GENERATOR_BASEURL=[SNS앱 주소] java -jar TestDataGen.jar [생성할 데이터 수]
$ SNS_DATA_GENERATOR_BASEURL=http://my-sns-app-url.example.com java -jar TestDataGen.jar 100
```

- SNS_DATA_GENERATOR_BASEURL : SNS 애플리케이션의 주소를 환경변수 형태로 지정합니다. 지정하지 않을 경우 localhost에 데이터를 생성합니다.
  - SNS_DATA_GENERATOR_USER_SERVER : User Server의 주소를 개별 지정합니다.
  - SNS_DATA_GENERATOR_FEED_SERVER : Feed Server의 주소를 개별 지정합니다.
  - SNS_DATA_GENERATOR_IMAGE_SERVER : Image Server의 주소를 개별 지정합니다.
- SNS_DATA_GENERATOR_TELEPRESENCE_ENABLED : true로 지정하면 기본 주소를 telepresence를 이용한 주소로 사용합니다.
- java -jar TestDataGen.jar 테스트 데이터 생성기를 실행합니다. Java 19버전 이상의 런타임이 필요합니다.
- 생성할 데이터 수 : 지정한 숫자만큼의 데이터를 생성합니다. 생략할 경우 image 디렉토리에 있는 이미지 수 만큼 데이터를 생성합니다.

실행 시점에 임의의 사용자를 생성하고, 사용자마다 1개에서 6개 사이의 포스트를 생성합니다. 한 번의 실행에서는 중복된 사용자나 중복된 이미지가 생성되지 않지만, 여러 번 실행하거나 생성할 데이터의 수가 이미지보다 많은 경우 중복된 이미지와 사용자가 생성될 수 있습니다. 사용자가 중복될 경우 기존 계정에 추가로 포스트를 업로드합니다.

