package in.reqres;

import in.reqres.models.*;
import org.junit.jupiter.api.Test;

import static in.reqres.specs.UserDataRequestSpec.userDataRequestSpec;
import static in.reqres.specs.UserDataResponseSpec.userDataResponseSpec;
import static in.reqres.specs.RegRequestSpec.regRequestSpec;
import static in.reqres.specs.RegResponseSpec.regResponseSpec;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

public class SimpleApiTests {

    @Test
    void createUserTest() {
        CreateUserModel user = new CreateUserModel();
        user.setName("Mikhail");
        user.setJob("QA-Engineer");
        CreateUserResponseModel response = given()
                .spec(userDataRequestSpec)
                .when()
                .body(user)
                .post()
                .then()
                .spec(userDataResponseSpec)
                .extract().as(CreateUserResponseModel.class);
        assertThat(response.getName()).isEqualTo("Mikhail");
        assertThat(response.getJob()).isEqualTo("QA-Engineer");

    }

    @Test
    void regPositiveTest() {
        RegRequestModel regData = new RegRequestModel();
        regData.setEmail("eve.holt@reqres.in");
        regData.setPassword("randomPassword");
        RegResponseModel response = given()
                .spec(regRequestSpec)
                .when()
                .body(regData)
                .post()
                .then()
                .spec(regResponseSpec)
                .statusCode(200)
                .extract().as(RegResponseModel.class);
        assertThat(response.getId()).isEqualTo("4");
        assertThat(response.getToken()).isEqualTo("QpwL5tke4Pnpja7X4");
    }

    @Test
    void regWithoutAccessTest() {
        RegRequestModel regData = new RegRequestModel();
        regData.setEmail("testmail@gmail.com");
        regData.setPassword("randomPassword");
        given()
                .spec(regRequestSpec)
                .when()
                .body(regData)
                .post()
                .then()
                .spec(regResponseSpec)
                .statusCode(400)
                .body("error", is("Note: Only defined users succeed registration"));
    }

    @Test
    void regWithoutPasswordTest() {
        RegRequestModel regData = new RegRequestModel();
        regData.setEmail("eve.holt@reqres.in");
        given()
                .spec(regRequestSpec)
                .when()
                .body(regData)
                .post()
                .then()
                .spec(regResponseSpec)
                .statusCode(400)
                .body("error", is("Missing password"));
    }

    @Test
    void firstUserTest() {
        ListOfUsersModel listOfUsersModel = given()
                .spec(userDataRequestSpec)
                .when()
                .get()
                .then()
                .log().all()
                .extract().as(ListOfUsersModel.class);
        assertThat(listOfUsersModel.getData().get(0).getFirst_name()).isEqualTo("George");
        assertThat(listOfUsersModel.getData().get(0).getLast_name()).isEqualTo("Bluth");
    }

    @Test
    void singleUserTest() {
        given()
                .spec(userDataRequestSpec)
                .when()
                .get("/5")
                .then()
                .log().all()
                .body("data.last_name", is("Morris"));

    }

}
